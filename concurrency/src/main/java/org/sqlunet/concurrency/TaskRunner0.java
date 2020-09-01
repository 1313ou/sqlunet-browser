/*
 * Copyright (c) 2020. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.concurrency;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskRunner0
{
	private final Handler handler = new Handler(Looper.getMainLooper());

	private final ExecutorService executor = Executors.newSingleThreadExecutor(); // change according to your requirements

	@FunctionalInterface
	public interface Callback<R>
	{
		void onComplete(R result);
	}

	public <Result> Future<?> execute(final Callable<Result> callable, final Callback<Result> callback)
	{
		return this.executor.submit(() -> {
			try
			{
				final Result result = callable.call();
				this.handler.post(() -> callback.onComplete(result));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		});
	}
}
