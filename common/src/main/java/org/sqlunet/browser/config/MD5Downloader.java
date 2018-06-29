package org.sqlunet.browser.config;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * SimpleDownloader task
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class MD5Downloader extends AsyncTask<String, Void, String>
{
	static private final String TAG = "MD5Downloader";

	/**
	 * ResultListener
	 */
	private final Listener listener;

	/**
	 * Exception while executing
	 */
	@Nullable
	private Exception exception;

	/**
	 * Constructor
	 *
	 * @param listener listener
	 */
	MD5Downloader(final Listener listener)
	{
		this.listener = listener;
		this.exception = null;
	}

	@Nullable
	@Override
	protected String doInBackground(final String... params)
	{
		final String md5Arg = params[0];
		final String targetArg = params[1];
		InputStream input = null;
		BufferedReader reader = null;
		try
		{
			// connect
			final URL url = new URL(md5Arg);
			Log.d(MD5Downloader.TAG, "Get " + url.toString());
			final URLConnection connection = url.openConnection();
			connection.connect();

			// expect HTTP 200 OK, so we don't mistakenly save error report instead of the file
			if (connection instanceof HttpURLConnection)
			{
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
				{
					final String message = "server returned HTTP " + httpConnection.getResponseCode() + " " + httpConnection.getResponseMessage();
					throw new RuntimeException(message);
				}
			}

			// open the reader
			input = connection.getInputStream();
			final InputStreamReader isr = new InputStreamReader(input);
			reader = new BufferedReader(isr);

			// read
			String line;
			while ((line = reader.readLine()) != null)
			{
				if (line.contains(targetArg))
				{
					final String[] fields = line.split("\\s+");
					return fields[0].trim();
				}
				// interrupted
				if (Thread.interrupted())
				{
					final InterruptedException ie = new InterruptedException("interrupted while downloading");
					this.exception = ie;
					throw ie;
				}

				if (isCancelled())
				{
					//noinspection BreakStatement
					throw new InterruptedException("cancelled");
				}
			}
			return null;
		}
		catch (@NonNull final InterruptedException e)
		{
			this.exception = e;
			Log.d(MD5Downloader.TAG, e.toString());
		}
		catch (@NonNull final Exception e)
		{
			this.exception = e;
			Log.e(MD5Downloader.TAG, "While downloading", e);
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (@NonNull final IOException e)
				{
					this.exception = e;
					Log.e(TAG, "While closing reader", e);
				}
			}
			if (input != null)
			{
				try
				{
					input.close();
				}
				catch (@NonNull final IOException e)
				{
					this.exception = e;
					Log.e(TAG, "While closing input", e);
				}
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(final String result)
	{
		Log.d(TAG, "Completed " + result);
		this.listener.onDone(result);
	}

	@Override
	protected void onCancelled(final String result)
	{
		Log.d(TAG, "Cancelled " + result);
		this.listener.onDone(result);
	}

	/**
	 * Get Exception
	 *
	 * @return exception
	 */
	@Nullable
	@SuppressWarnings("unused")
	synchronized public Exception getException()
	{
		final Exception result = this.exception;
		this.exception = null;
		return result;
	}

	/**
	 * MD5Downloader listener
	 */
	public interface Listener
	{
		/**
		 * Done
		 *
		 * @param result md5 digest
		 */
		void onDone(final String result);
	}
}
