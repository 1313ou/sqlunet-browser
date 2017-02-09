package org.sqlunet.browser.config;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * SimpleDownloader task
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class SimpleDownloader extends AsyncTask<Void, Integer, Boolean>
{
	static private final String TAG = "SimpleDownloader";

	/**
	 * Buffer size
	 */
	static private final int CHUNK_SIZE = 8192;

	/**
	 * From URL
	 */
	private final String fromUrl;

	/**
	 * To file
	 */
	private final String toFile;

	/**
	 * Download code
	 */
	private final int code;

	/**
	 * Exception while executing
	 */
	private Exception exception;

	/**
	 * ResultListener
	 */
	private final Listener listener;

	/**
	 * Constructor
	 *
	 * @param from     from-url
	 * @param to       to-file
	 * @param code     code
	 * @param listener listener
	 */
	SimpleDownloader(final String from, final String to, int code, final Listener listener)
	{
		this.fromUrl = from;
		this.toFile = to;
		this.code = code;
		this.exception = null;
		this.listener = listener;
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		if (this.listener != null)
		{
			this.listener.onDownloadStart();
		}
	}

	@Override
	protected void onProgressUpdate(final Integer... params)
	{
		super.onProgressUpdate();
		if (this.listener != null)
		{
			this.listener.onDownloadUpdate(params[0], params[1]);
		}
	}

	@Override
	protected void onPostExecute(final Boolean result)
	{
		Log.d(TAG, "Completed " + result);
		super.onPostExecute(result);

		// fire event
		if (this.listener != null)
		{
			this.listener.onDownloadFinish(this.code, result);
		}
	}

	@Override
	protected void onCancelled(final Boolean result)
	{
		Log.d(TAG, "Cancelled " + result);
		super.onCancelled(result);

		// clean up
		File file = new File(this.toFile);
		if (file.exists())
		{
			//noinspection ResultOfMethodCallIgnored
			file.delete();
		}

		// fire event
		if (this.listener != null)
		{
			this.listener.onDownloadFinish(this.code, result);
		}
	}

	@SuppressWarnings("boxing")
	@Override
	protected Boolean doInBackground(final Void... params)
	{
		prerequisite();

		InputStream input = null;
		OutputStream output = null;
		try
		{
			// connect
			final URL url = new URL(this.fromUrl);
			Log.d(TAG, "Get " + url.toString());
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

			// getting file length
			int total = connection.getContentLength();

			// input stream toFile read file - with 8k buffer
			input = new BufferedInputStream(connection.getInputStream(), CHUNK_SIZE);

			// output stream toFile write file
			output = new FileOutputStream(this.toFile);

			// copy streams
			final byte[] buffer = new byte[1024];
			int downloaded = 0;
			int count;
			while ((count = input.read(buffer)) != -1)
			{
				downloaded += count;

				// publishing the progress (onProgressUpdate will be called)
				publishProgress(downloaded, total);

				// writing data toFile file
				output.write(buffer, 0, count);

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
			output.flush();
			return true;
		}
		catch (final InterruptedException e)
		{
			this.exception = e;
			Log.d(TAG, e.toString());
			return false;
		}
		catch (final Exception e)
		{
			this.exception = e;
			Log.e(TAG, "while downloading", e);
			return false;
		}
		finally
		{
			if (output != null)
			{
				try
				{
					output.close();
				}
				catch (final IOException e)
				{
					Log.e(TAG, "while closing output", e);
				}
			}
			if (input != null)
			{
				try
				{
					input.close();
				}
				catch (final IOException e)
				{
					this.exception = e;
					Log.e(TAG, "while closing input", e);
				}
			}
		}
	}

	/**
	 * Prerequisite
	 */
	private void prerequisite()
	{
		final File dir = new File(this.toFile).getParentFile();
		if (!dir.exists())
		{
			//noinspection ResultOfMethodCallIgnored
			dir.mkdirs();
		}
	}

	/**
	 * Get Exception
	 *
	 * @return exception
	 */
	synchronized public Exception getException()
	{
		final Exception result = this.exception;
		this.exception = null;
		return result;
	}

	/**
	 * SimpleDownloader listener
	 */
	public interface Listener
	{
		/**
		 * Start
		 */
		void onDownloadStart();

		/**
		 * Finish
		 *
		 * @param code   download code
		 * @param result true if success
		 */
		void onDownloadFinish(int code, boolean result);

		/**
		 * Intermediate progress notification
		 *
		 * @param downloaded byte count
		 * @param total      progress
		 */
		void onDownloadUpdate(long downloaded, long total);
	}
}
