/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.download;

import android.util.Log;

import com.bbou.concurrency.Task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * MD5 downloader task
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class MD5Downloader extends Task<String, Void, String>
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
		HttpURLConnection httpConnection = null;
		try
		{
			// connect
			final URL url = new URL(md5Arg);
			Log.d(TAG, "Getting " + url);
			final URLConnection connection = url.openConnection();
			connection.connect();

			// expect HTTP 200 OK, so we don't mistakenly save error report instead of the file
			if (connection instanceof HttpURLConnection)
			{
				httpConnection = (HttpURLConnection) connection;
				if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
				{
					final String message = "server returned HTTP " + httpConnection.getResponseCode() + " " + httpConnection.getResponseMessage();
					throw new RuntimeException(message);
				}
			}

			// open the reader
			try (InputStream is = connection.getInputStream(); InputStreamReader isr = new InputStreamReader(is); BufferedReader reader = new BufferedReader(isr))
			{
				// read
				String line;
				while ((line = reader.readLine()) != null)
				{
					if (line.contains(targetArg))
					{
						final String[] fields = line.split("\\s+");
						return fields[0].trim();
					}
					// cooperative exit
					if (isCancelled())
					{
						Log.d(TAG, "Cancelled!");
						throw new InterruptedException("cancelled");
					}
					if (Thread.interrupted())
					{
						Log.d(TAG, "Interrupted!");
						final InterruptedException ie = new InterruptedException("interrupted while downloading");
						this.exception = ie;
						throw ie;
					}
				}
			}
			return null;
		}
		catch (@NonNull final InterruptedException e)
		{
			this.exception = e;
			Log.d(TAG, e.toString());
		}
		catch (@NonNull final Exception e)
		{
			this.exception = e;
			Log.e(TAG, "While downloading", e);
		}
		finally
		{
			if (httpConnection != null)
			{
				httpConnection.disconnect();
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
	synchronized public Exception getException()
	{
		final Exception result = this.exception;
		this.exception = null;
		return result;
	}

	/**
	 * MD5Downloader listener
	 */
	@FunctionalInterface
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
