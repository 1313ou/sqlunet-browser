/*
 * Copyright (c) 2020. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.concurrency;

import android.os.AsyncTask;

@SuppressWarnings("deprecation")
abstract public class Task<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>
{
	public Task()
	{
		super();
	}

	// job

	@SuppressWarnings("unchecked")
	abstract protected Result job(final Params... params);

	// action

	public static void run(final Runnable runnable)
	{
		execute(runnable);
	}

	@SafeVarargs
	public final Task<Params, Progress, Result> run(final Params... params)
	{
		execute(params);
		return this;
	}

	public final boolean cancelJob(final boolean mayInterruptIfRunning)
	{
		return cancel(mayInterruptIfRunning);
	}

	@SafeVarargs
	protected final void pushProgress(final Progress... values)
	{
		publishProgress(values);
	}

	// listen callbacks

	@SuppressWarnings("EmptyMethod")
	protected void onPre()
	{
	}

	@SuppressWarnings("EmptyMethod")
	protected void onJobComplete(final Result result)
	{
	}

	@SuppressWarnings("EmptyMethod")
	protected void onJobCancelled(final Result result)
	{
	}

	@SuppressWarnings("EmptyMethod")
	protected void onJobCancelled()
	{
	}

	@SuppressWarnings({"EmptyMethod", "unchecked"})
	protected void onProgress(final Progress... values)
	{
	}

	// state

	public final boolean jobIsCancelled()
	{
		return isCancelled();
	}

	// I M P L E M E N T A T I O N

	// job

	@SafeVarargs
	@Override
	protected final Result doInBackground(final Params... params)
	{
		return job(params);
	}

	// pre

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		onPre();
	}

	// completion

	@Override
	protected void onPostExecute(final Result result)
	{
		super.onPostExecute(result);
		onJobComplete(result);
	}

	// cancellation

	@Override
	protected void onCancelled(final Result result)
	{
		super.onCancelled(result);
		onJobCancelled(result);
	}

	@Override
	protected void onCancelled()
	{
		super.onCancelled();
		onJobCancelled();
	}

	// progress

	@SuppressWarnings("unchecked")
	@Override
	protected void onProgressUpdate(final Progress... values)
	{
		super.onProgressUpdate(values);
		onProgress(values);
	}
}