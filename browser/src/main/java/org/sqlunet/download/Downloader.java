package org.sqlunet.download;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Downloader task
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Downloader extends AsyncTask<Void, Integer, Boolean>
{
	static private final String TAG = "Downloader";
	/**
	 * From URL
	 */
	private final String from;

	/**
	 * To file
	 */
	private final String to;

	/**
	 * Download code
	 */
	private final int code;
	/**
	 * Listener
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
	public Downloader(final String from, final String to, int code, final Listener listener)
	{
		this.from = from;
		this.to = to;
		this.code = code;
		this.listener = listener;
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		this.listener.downloadStart();
	}

	@Override
	protected void onProgressUpdate(final Integer... progress)
	{
		super.onProgressUpdate();
		this.listener.downloadUpdate(progress[0], progress[1]);
	}

	@Override
	protected void onPostExecute(final Boolean result)
	{
		Log.d(TAG, "Completed " + result);
		super.onPostExecute(result);
		this.listener.downloadFinish(this.code, result);
	}

	@Override
	protected void onCancelled(final Boolean result)
	{
		Log.d(TAG, "Cancelled, result=" + result);
		super.onCancelled(result);
		this.listener.downloadFinish(this.code, result);
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
			final URL url = new URL(this.from);
			Log.d(TAG, "Get " + url.toString());
			final URLConnection connection = url.openConnection();
			connection.connect();

			// getting file length
			int length = connection.getContentLength();

			// input stream to read file - with 8k buffer
			input = new BufferedInputStream(connection.getInputStream(), 8192);

			// output stream to write file
			output = new FileOutputStream(this.to);

			// copy streams
			final byte[] buffer = new byte[1024];
			long counter = 0;
			int count;
			while ((count = input.read(buffer)) != -1)
			{
				counter += count;

				// publishing the progress (onProgressUpdate will be called)
				publishProgress(length == -1 ? -1 : (int) (counter * 100 / length), (int) counter);

				// writing data to file
				output.write(buffer, 0, count);

				// interrupted
				if (Thread.interrupted())
				{
					throw new InterruptedException("While copying download stream");
				}
			}
			output.flush();
			return true;
		}
		catch (final InterruptedException e)
		{
			Log.d(TAG, "Interrupted downloading");
		}
		catch (final Exception e)
		{
			Log.e(TAG, "While downloading", e);
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
					Log.e(TAG, "While closing input", e);
				}
			}
		}
		return false;
	}

	/**
	 * Prerequisite
	 */
	private void prerequisite()
	{
		final File dir = new File(this.to).getParentFile();
		if (!dir.exists())
		{
			//noinspection ResultOfMethodCallIgnored
			dir.mkdirs();
		}
	}

	/**
	 * Downloader listener
	 */
	public interface Listener
	{
		/**
		 * Start
		 */
		void downloadStart();

		/**
		 * Finish
		 *
		 * @param code   download code
		 * @param result true if success
		 */
		void downloadFinish(int code, boolean result);

		/**
		 * Intermediate progress notification
		 *
		 * @param progress progress
		 * @param bytes    byte count
		 */
		void downloadUpdate(int progress, int bytes);
	}
}
