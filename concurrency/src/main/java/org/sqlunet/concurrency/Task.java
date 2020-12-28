package org.sqlunet.concurrency;

import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.util.Pair;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

abstract public class Task<Params, Progress, Result> implements Cancelable
{
	private static final String LOG_TAG = "AsyncTask";

	// E X E C U T O R

	private static final int CORE_POOL_SIZE = 5;

	private static final int MAXIMUM_POOL_SIZE = 128;

	private static final int KEEP_ALIVE = 1;

	private static final ThreadFactory THREAD_FACTORY = new ThreadFactory()
	{
		private final AtomicInteger count = new AtomicInteger(1);

		@NonNull
		public Thread newThread(@NonNull Runnable runnable)
		{
			return new Thread(runnable, "ModernAsyncTask #" + this.count.getAndIncrement());
		}
	};

	private static final BlockingQueue<Runnable> POOL_WORK_QUEUE = new LinkedBlockingQueue<>(10);

	@NonNull
	public static final Executor THREAD_POOL_EXECUTOR;

	static
	{
		THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, POOL_WORK_QUEUE, THREAD_FACTORY);
		defaultExecutor = THREAD_POOL_EXECUTOR;
	}

	private static volatile Executor defaultExecutor;

	public static void setDefaultExecutor(Executor exec)
	{
		defaultExecutor = exec;
	}

	// H A N D L E R

	private static final int MESSAGE_POST_RESULT = 1;

	private static final int MESSAGE_POST_PROGRESS = 2;

	private static class MultiplexingHandler<Params, Progress, Result> extends Handler
	{
		MultiplexingHandler()
		{
			super(Looper.getMainLooper());
		}

		@SuppressWarnings("unchecked")
		public void handleMessage(@NonNull Message msg)
		{
			switch (msg.what)
			{
				case MESSAGE_POST_RESULT:
				{
					final Pair<Task<Params, Progress, Result>, Result> payload = (Pair<Task<Params, Progress, Result>, Result>) msg.obj;
					if (payload.first.isCancelled())
					{
						payload.first.onCancelled(payload.second);
					}
					else
					{
						payload.first.onPostExecute(payload.second);
					}
					payload.first.status = Task.Status.FINISHED;
					break;
				}
				case MESSAGE_POST_PROGRESS:
				{
					final Pair<Task<Params, Progress, Result>, Progress[]> payload2 = (Pair<Task<Params, Progress, Result>, Progress[]>) msg.obj;
					payload2.first.onProgressUpdate(payload2.second);
					break;
				}
			}
		}
	}

	private static MultiplexingHandler<?, ?, ?> handler;

	private static Handler getHandler()
	{
		synchronized (Task.class)
		{
			if (handler == null)
			{
				handler = new MultiplexingHandler<>();
			}
			return handler;
		}
	}

	// W O R K E R

	private abstract static class WorkerRunnable<Params, Result> implements Callable<Result>
	{
		Params[] params;

		WorkerRunnable()
		{
		}
	}

	@NonNull
	private final WorkerRunnable<Params, Result> worker;

	// F U T U R E

	@NonNull
	private final FutureTask<Result> future;

	// S T A T U S

	public enum Status
	{
		PENDING, RUNNING, FINISHED;

		Status()
		{
		}
	}

	private volatile Status status;

	public final Status getStatus()
	{
		return this.status;
	}

	@NonNull
	final AtomicBoolean cancelled;

	@NonNull
	final AtomicBoolean taskInvoked;

	private Task<?, ?, ?> forward;

	// C O N S T R U C T

	public Task()
	{
		this.forward = this;
		this.status = Status.PENDING;
		this.cancelled = new AtomicBoolean();
		this.taskInvoked = new AtomicBoolean();

		// build worker
		this.worker = new WorkerRunnable<Params, Result>()
		{
			@Nullable
			public Result call()
			{
				Task.this.taskInvoked.set(true);
				Result result = null;

				try
				{
					Process.setThreadPriority(10);
					result = Task.this.doInBackground(this.params);
					Binder.flushPendingCommands();
				}
				catch (Throwable t)
				{
					Task.this.cancelled.set(true);
					throw t;
				}
				finally
				{
					Task.this.postResult(result);
				}

				return result;
			}
		};

		// future
		this.future = new FutureTask<Result>(this.worker)
		{
			@Override
			protected void done()
			{
				try
				{
					Result result = this.get();
					Task.this.postResultIfNotInvoked(result);
				}
				catch (InterruptedException ie)
				{
					Log.w("AsyncTask", ie);
				}
				catch (ExecutionException ee)
				{
					throw new RuntimeException("An error occurred while executing background job", ee.getCause());
				}
				catch (CancellationException ce)
				{
					Task.this.postResultIfNotInvoked(null);
				}
				catch (Throwable t)
				{
					throw new RuntimeException("An error occurred while executing background job", t);
				}
			}
		};
	}

	// D E L E G A T I O N

	/**
	 * Set this task as delegate and forward termination and progress signals to delegating task
	 *
	 * @param delegating delegating task
	 */
	protected final void setForward(Task<?, ?, ?> delegating)
	{
		this.forward = delegating;
	}

	// O V E R R I D A B L E

	/**
	 * Background job
	 *
	 * @param params parameters
	 */
	@Nullable
	protected abstract Result doInBackground(Params[] params);

	protected void onPreExecute()
	{
	}

	protected void onPostExecute(Result result)
	{
	}

	// C A N C E L

	protected void onCancelled(Result result)
	{
		this.onCancelled();
	}

	protected void onCancelled()
	{
	}

	public final boolean isCancelled()
	{
		return this.cancelled.get() || this.future.isCancelled();
	}

	@Override
	public final boolean cancel(boolean mayInterruptIfRunning)
	{
		this.cancelled.set(true);
		return this.future.cancel(mayInterruptIfRunning);
	}

	// G E T

	public final Result get() throws InterruptedException, ExecutionException
	{
		return this.future.get();
	}

	public final Result get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
	{
		return this.future.get(timeout, unit);
	}

	// P R O G R E S S

	/**
	 * Progress update callback
	 *
	 * @param values progress values
	 */
	protected void onProgressUpdate(Progress[] values)
	{
	}

	@SafeVarargs
	protected final void publishProgress(Progress... values)
	{
		if (!this.isCancelled())
		{
			getHandler().obtainMessage(MESSAGE_POST_PROGRESS, new Pair<>(this.forward, values)).sendToTarget();
		}
	}

	// E X E C U T E

	@NonNull
	@SafeVarargs
	public final Task<Params, Progress, Result> execute(Params... params)
	{
		return this.executeOnExecutor(defaultExecutor, params);
	}

	@NonNull
	@SafeVarargs
	public final Task<Params, Progress, Result> executeOnExecutor(@NonNull Executor exec, Params... params)
	{
		if (this.status != Task.Status.PENDING)
		{
			switch (this.status)
			{
				case RUNNING:
					throw new IllegalStateException("Cannot execute task: the task is already running.");
				case FINISHED:
					throw new IllegalStateException("Cannot execute task: the task has already been executed (a task can be executed only once)");
				default:
					throw new IllegalStateException("We should never reach this state");
			}
		}
		else
		{
			this.status = Task.Status.RUNNING;
			this.onPreExecute();
			this.worker.params = params;
			exec.execute(this.future);
			return this;
		}
	}

	public static void execute(Runnable runnable)
	{
		defaultExecutor.execute(runnable);
	}

	private void postResultIfNotInvoked(Result result)
	{
		boolean wasTaskInvoked = this.taskInvoked.get();
		if (!wasTaskInvoked)
		{
			this.postResult(result);
		}
	}

	private void postResult(Result result)
	{
		Message message = getHandler().obtainMessage(MESSAGE_POST_RESULT, new Pair<>(this.forward, result));
		message.sendToTarget();
	}
}
