package org.sqlunet.concurrency;

public abstract class ObservedTask<Params, Progress extends Number, Result> extends Task<Params, Progress, Result>
{
	private final TaskObserver.Listener<Progress> observer;

	public ObservedTask(final TaskObserver.Listener<Progress> observer)
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
		this.observer.taskUpdate(values[0], values[1]); // current, total
	}
}
