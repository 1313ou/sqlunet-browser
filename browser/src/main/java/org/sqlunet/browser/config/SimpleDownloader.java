package org.sqlunet.browser.config;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
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
	 * Timeout in seconds
	 */
	static private final int TIMEOUT_S = 15;

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
	 * Result listener
	 */
	private Listener listener;

	/**
	 * Context
	 */
	private final Context context;

	/**
	 * Constructor
	 *
	 * @param context context
	 * @param from     from-url
	 * @param to       to-file
	 * @param code     code
	 */
	SimpleDownloader(final Context context, final String from, final String to, int code)
	{
		this.context = context;
		this.fromUrl = from;
		this.toFile = to;
		this.code = code;
		this.exception = null;
	}

	/**
	 * Set listener
	 *
	 * @param listener listener
	 */
	void setListener(final Listener listener)
	{
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
			this.listener.onDownloadFinish(this.code, result == null ? false : result);
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
			this.listener.onDownloadFinish(this.code, result == null ? false : result);
		}
	}

	@SuppressWarnings("boxing")
	@Override
	protected Boolean doInBackground(final Void... params)
	{
		prerequisite();

		// wake lock
		final PowerManager powerManager = (PowerManager) this.context.getSystemService(Context.POWER_SERVICE);
		final PowerManager.WakeLock wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DownloaderService");
		wakelock.acquire();

		final File outFile = new File(this.toFile + ".part");
		InputStream input = null;
		OutputStream output = null;
		try
		{
			// connection
			final URL url = new URL(this.fromUrl);
			Log.d(TAG, "Get " + url.toString());
			final URLConnection connection = url.openConnection();
			connection.setConnectTimeout(TIMEOUT_S * 1000);

			// connect
			Log.d(TAG, "Connecting");
			connection.connect();
			Log.d(TAG, "Connected");

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
			output = new FileOutputStream(outFile);

			// copy streams
			final byte[] buffer = new byte[1024];
			int downloaded = 0;
			int count;
			while ((count = input.read(buffer)) != -1)
			{
				downloaded += count;

				// progress
				boolean isInteractive = Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT ? powerManager.isInteractive() : powerManager.isScreenOn();
				if (isInteractive)
				{
					// publishing the progress (onProgressUpdate will be called)
					publishProgress(downloaded, total);
				}

				// writing data toFile file
				output.write(buffer, 0, count);

				// interrupted
				if (Thread.interrupted())
				{
					final InterruptedException ie = new InterruptedException("interrupted");
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

			if (outFile.exists())
			{
				outFile.renameTo(new File(this.toFile));
			}
			return true;
		}
		catch (final InterruptedException ie)
		{
			this.exception = ie;
			Log.d(TAG, "While downloading, " + ie.getMessage());
			return false;
		}
		catch (SocketTimeoutException ste)
		{
			Log.d(TAG, "Timeout");
			this.exception = ste;
			Log.d(TAG, "While downloading, " + ste.getMessage());
			return false;
		}
		catch (final Exception e)
		{
			this.exception = e;
			Log.e(TAG, "While downloading, " + e.getMessage());
			return false;
		}
		finally
		{
			// wake lock
			wakelock.release();

			if (output != null)
			{
				try
				{
					output.close();
				}
				catch (final IOException e)
				{
					Log.e(TAG, "While closing output", e);
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
					Log.e(TAG, "While closing input", e);
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
