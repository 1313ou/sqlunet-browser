/*
 * Copyright (c) 2020. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.concurrency;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Task2<Result>
{
	@FunctionalInterface
	public interface Callback<Result>
	{
		void onComplete(Result result);
	}

	static class Runner
	{
		private final Handler handler = new Handler(Looper.getMainLooper());

		private final ExecutorService executor = Executors.newSingleThreadExecutor();

		private <Result> Future<?> execute(final Task2<Result> task)
		{
			return this.executor.submit(() -> {
				try
				{
					final Result result = task.job.call();
					this.handler.post(() -> task.callback.onComplete(result));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			});
		}
	}

	private final Callable<Result> job;

	private final Callback<Result> callback;

	private final Runner runner;

	private Future<?> future;

	public Task2(final Runner runner, final Callable<Result> job, final Callback<Result> callback)
	{
		this.runner = runner;
		this.job = job;
		this.callback = callback;
	}

	public Task2(final Callable<Result> job, final Callback<Result> callback)
	{
		this(new Runner(), job, callback);
	}

	public void run()
	{
		this.future = this.runner.execute(this);
	}

	public void cancel()
	{
		this.future.cancel(true);
	}
}
