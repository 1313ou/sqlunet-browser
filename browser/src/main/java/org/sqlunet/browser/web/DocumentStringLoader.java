package org.sqlunet.browser.web;

import android.annotation.SuppressLint;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.CancellationSignal;

//TODO
abstract class DocumentStringLoader extends AsyncTaskLoader<String>
{

	private CancellationSignal cancellationSignal;

	private String document;

	/* Runs on a worker thread */
	@SuppressLint("NewApi")
	@Override
	public String loadInBackground()
	{
		synchronized (this)
		{
			// TODO
			// if (isLoadInBackgroundCanceled())
			// {
			// throw new OperationCanceledException();
			// }
			this.cancellationSignal = new CancellationSignal();
		}
		try
		{
			return getDoc();
		}
		finally
		{
			synchronized (this)
			{
				this.cancellationSignal = null;
			}
		}
	}

	abstract protected String getDoc();

	@SuppressLint("NewApi")
	@Override
	public boolean cancelLoad()
	{
		synchronized (this)
		{
			if (this.cancellationSignal != null)
			{
				this.cancellationSignal.cancel();
			}
			return super.cancelLoad();
		}
	}

	/* Runs on the UI thread */
	@Override
	public void deliverResult(final String document0)
	{
		if (isReset())
			// An async query came in while the loader is stopped
			return;

		this.document = document0;

		if (isStarted())
		{
			super.deliverResult(document0);
		}
	}

	/**
	 * Creates an empty unspecified CursorLoader. You must follow this with calls to setUri(Uri), setSelection(String), etc to specify the
	 * query to perform.
	 */
	public DocumentStringLoader(final Context context)
	{
		super(context);
	}

	/**
	 * Starts an asynchronous load of the contacts list data. When the result is ready the callbacks will be called on the UI thread. If a previous load has
	 * been completed and is still valid the result may be passed to the callbacks immediately. Must be called from the UI thread
	 */
	@Override
	protected void onStartLoading()
	{
		if (this.document != null)
		{
			deliverResult(this.document);
		}
		if (takeContentChanged() || this.document == null)
		{
			forceLoad();
		}
	}

	/**
	 * Must be called from the UI thread
	 */
	@Override
	protected void onStopLoading()
	{
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	@Override
	public void onCanceled(final String document0)
	{
		// Do nothing
	}

	@Override
	protected void onReset()
	{
		super.onReset();

		// ensure the loader is stopped
		onStopLoading();

		this.document = null;
	}
}
