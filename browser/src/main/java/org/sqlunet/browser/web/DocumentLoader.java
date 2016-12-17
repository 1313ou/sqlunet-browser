package org.sqlunet.browser.web;

import android.annotation.TargetApi;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Build;
import android.os.CancellationSignal;

import org.sqlunet.browser.BuildConfig;
import org.sqlunet.xml.Validate;
import org.w3c.dom.Document;

/**
 * Abstract document loader
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class DocumentLoader extends AsyncTaskLoader<Document>
{
	/**
	 * Document
	 */
	private Document document;

	/**
	 * Cancellation signal
	 */
	@SuppressWarnings("WeakerAccess")
	protected CancellationSignal cancellationSignal;

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	@SuppressWarnings("unused")
	public DocumentLoader(final Context context)
	{
		super(context);
	}

	/* Runs on a worker thread */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
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
			final Document result = getDoc();
			if (BuildConfig.DEBUG)
			{
				final String xml = XSLTransformer.docToXml(result);
				final String log = XSLTransformer.writeLog(xml, false);
				Validate.validateDocs(XSLTransformer.class.getResource("/org/sqlunet/dom/SqlUNet.xsd"), result);
			}
			return result;
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
	abstract protected Document getDoc();

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
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

	@Override
	public void deliverResult(final Document document)
	{
		// runs on the UI thread

		if (isReset())
		{
			// an async query came in while the loader is stopped
			return;
		}

		this.document = document;

		if (isStarted())
		{
			super.deliverResult(document);
		}
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
		// attempt to cancel the current load task if possible.
		cancelLoad();
	}

	@Override
	public void onCanceled(final Document document)
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
