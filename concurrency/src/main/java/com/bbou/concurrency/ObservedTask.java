/*
 * Copyright (c) 2023. Bernard Bou
 */

package com.bbou.concurrency;

public abstract class ObservedTask<Params, Progress extends Number, Result> extends Task<Params, Progress, Result>
{
	private final TaskObserver.Observer<Progress> observer;

	public ObservedTask(final TaskObserver.Observer<Progress> observer)
	{
		this.observer = observer;
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		this.observer.taskStart(this);
	}

	@Override
	protected void onPostExecute(final Result result)
	{
		super.onPostExecute(result);
		this.observer.taskFinish(true);
	}

	@Override
	protected void onCancelled(final Result result)
	{
		super.onCancelled(result);
		this.observer.taskFinish(false);
	}

	@Override
	protected void onCancelled()
	{
		super.onCancelled();
		this.observer.taskFinish(false);
	}

	@Override
	protected void onProgressUpdate(final Progress[] values)
	{
		super.onProgressUpdate(values);
		this.observer.taskProgress(values[0], values[1], null); // current, total
	}
}
