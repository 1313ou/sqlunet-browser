package org.sqlunet.browser.web;

import android.content.Context;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.loader.content.AsyncTaskLoader;

/**
 * Document string loader
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract class DocumentStringLoader extends AsyncTaskLoader<String>
{
	/**
	 * String document
	 */
	@Nullable
	private String document;

	/**
	 * Cancellation signal
	 */
	@Nullable
	private CancellationSignal cancellationSignal;

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	DocumentStringLoader(@NonNull final Context context)
	{
		super(context);
	}

	/* Runs on a worker thread */
	@Override
	public String loadInBackground()
	{
		synchronized (this)
		{
			if (isLoadInBackgroundCanceled())
			{
				throw new OperationCanceledException("Canceled");
			}
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

	/**
	 * Get document
	 *
	 * @return document
	 */
	@Nullable
	abstract protected String getDoc();

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
	public void deliverResult(final String document)
	{
		if (isReset())
		// An async query came in while the loader is stopped
		{
			return;
		}

		this.document = document;

		if (isStarted())
		{
			super.deliverResult(document);
		}
	}

	/**
	 * Starts an asynchronous load of the contacts list data. When the progressMessage is ready the callbacks will be called on the UI thread. If a previous load has
	 * been completed and is still valid the progressMessage may be passed to the callbacks immediately. Must be called from the UI thread
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
		// attempt to cancel the current load task if possible.
		cancelLoad();
	}

	@Override
	public void onCanceled(final String document)
	{
		// do nothing
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
