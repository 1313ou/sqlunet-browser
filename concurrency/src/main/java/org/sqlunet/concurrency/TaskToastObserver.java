/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.concurrency;

import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

/**
 * Task toast observer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("WeakerAccess")
public class TaskToastObserver<Progress extends Number> extends TaskObserver.BaseObserver<Progress>
{
	// static private final String TAG = "TaskTObserver";

	/**
	 * Cached context
	 */
	@NonNull
	final protected Context appContext;

	TaskToastObserver(@NonNull final Context appContext)
	{
		super();
		this.appContext = appContext;
	}

	@SuppressWarnings("WeakerAccess")
	@Override
	public void taskStart(@NonNull final Cancelable task)
	{
		super.taskStart(task);
		Toast.makeText(this.appContext, R.string.status_task_start_toast, Toast.LENGTH_SHORT).show();
	}

	@SuppressWarnings("WeakerAccess")
	@Override
	public void taskFinish(boolean result)
	{
		super.taskFinish(result);
		Toast.makeText(this.appContext, R.string.status_task_done_toast, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Toast + TextView observer
	 */
	static public class WithStatus<Progress extends Number> extends TaskToastObserver<Progress>
	{
		@NonNull
		final private TextView status;

		public WithStatus(@NonNull final Context appContext, @NonNull final TextView status)
		{
			super(appContext);
			this.status = status;
		}

		@Override
		public void taskStart(@NonNull final Cancelable task)
		{
			super.taskStart(task);
			this.status.setText(R.string.status_task_running);
		}

		@Override
		public void taskUpdate(@NonNull final String message)
		{
			super.taskUpdate(message);
			this.status.setText(message);
		}

		@Override
		public void taskFinish(final boolean result)
		{
			super.taskFinish(result);
			this.status.setText(result ? R.string.status_task_done : R.string.status_task_failed);
		}
	}

	/**
	 * Toast + Progress bar observer
	 */
	static public class WithProgress<Progress extends Number> extends TaskToastObserver<Progress>
	{
		private final ProgressBar progress;

		/**
		 * Constructor
		 *
		 * @param appContext app context
		 * @param progress   progress bar
		 */
		WithProgress(@NonNull final Context appContext, final ProgressBar progress)
		{
			super(appContext);
			this.progress = progress;
		}

		@Override
		public void taskStart(@NonNull Cancelable task)
		{
			super.taskStart(task);
			this.progress.setIndeterminate(true);
		}

		@Override
		public void taskProgress(@NonNull final Progress progress, @NonNull final Progress length)
		{
			super.taskProgress(progress, length);
			boolean indeterminate = length.longValue() == -1L;
			this.progress.setIndeterminate(indeterminate);
			if (!indeterminate)
			{
				this.progress.setMax(length.intValue());
				this.progress.setProgress(progress.intValue());
			}
		}

		@Override
		public void taskFinish(boolean result)
		{
			super.taskFinish(result);
			int done = result ? 0 : 100;
			this.progress.setMax(done);
			this.progress.setProgress(done);
		}
	}
}
