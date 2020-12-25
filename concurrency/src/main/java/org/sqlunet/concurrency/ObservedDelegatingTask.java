package org.sqlunet.concurrency;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ObservedDelegatingTask<Params, Progress extends Number, Result> extends Task<Params, Progress, Result>
{
	private final Task<Params, Progress, Result> delegate;

	private final TaskObserver.Observer<Progress> observer;

	public ObservedDelegatingTask(final Task<Params, Progress, Result> delegate, final TaskObserver.Observer<Progress> observer)
	{
		this.delegate = delegate;
		this.delegate.setForward(this);
		this.observer = observer;
	}

	@NonNull
	protected Task<Params, Progress, Result> getTask() { return this; }

	@Nullable
	@Override
	protected Result doInBackground(final Params[] params)
	{
		return this.delegate.doInBackground(params);
	}

	@Override
	protected void onPreExecute()
	{
		this.delegate.onPreExecute();
		this.observer.taskStart(this);
	}

	@Override
	protected void onPostExecute(final Result result)
	{
		this.delegate.onPostExecute(result);
		this.observer.taskFinish(true);
	}

	@Override
	protected void onCancelled(final Result result)
	{
		this.delegate.onCancelled(result);
		this.observer.taskFinish(false);
	}

	@Override
	protected void onCancelled()
	{
		this.delegate.onCancelled();
		this.observer.taskFinish(false);
	}

	@Override
	protected void onProgressUpdate(final Progress[] values)
	{
		this.delegate.onProgressUpdate(values);
		this.observer.taskUpdate(values[0], values[1]); // current, total
	}
}
