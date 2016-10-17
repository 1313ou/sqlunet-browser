package org.sqlunet.browser.web;

import android.annotation.SuppressLint;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.CancellationSignal;

import org.w3c.dom.Document;

//TODO
abstract public class DocumentLoader extends AsyncTaskLoader<Document>
{
	private CancellationSignal cancellationSignal;

	private Document document;

	/* Runs on a worker thread */
	@SuppressLint("NewApi")
	@Override
	public Document loadInBackground()
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
			return getDoc(this.cancellationSignal);
		}
		finally
		{
			synchronized (this)
			{
				this.cancellationSignal = null;
			}
		}
	}

	abstract protected Document getDoc(CancellationSignal mCancellationSignal2);

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
	public void deliverResult(final Document document0)
	{
		if (isReset())
		// An async query came in while the loader is stopped
		{
			return;
		}

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
	@SuppressWarnings("unused")
	public DocumentLoader(final Context context)
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
	public void onCanceled(final Document document0)
	{
		// Do nothing
	}

	@Override
	protected void onReset()
	{
		super.onReset();

		// Ensure the loader is stopped
		onStopLoading();

		this.document = null;
	}
}
