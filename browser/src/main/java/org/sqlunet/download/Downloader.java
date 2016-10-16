package org.sqlunet.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;
import android.util.Log;

public class Downloader extends AsyncTask<Void, Integer, Boolean>
{
	private static final String TAG = "Downloader"; //$NON-NLS-1$

	private final String from;

	private final String to;

	private final int code;

	/**
	 * Downloader listener
	 */
	public interface Listener
	{
		void downloadStart();

		void downloadFinish(int code, boolean result);

		void downloadUpdate(int progress, int bytes);
	}

	private final Listener listener;

	/**
	 * Constructor
	 *
	 * @param listener
	 *            listener
	 */
	/**
	 * @param from
	 *            from-url
	 * @param to
	 *            to-file
	 * @param code
	 *            code
	 * @param listener
	 *            listener
	 */
	public Downloader(final String from, final String to, int code, final Listener listener)
	{
		this.from = from;
		this.to = to;
		this.code = code;
		this.listener = listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		this.listener.downloadStart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@SuppressWarnings("boxing")
	@Override
	protected void onProgressUpdate(final Integer... progress)
	{
		super.onProgressUpdate();
		this.listener.downloadUpdate(progress[0], progress[1]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@SuppressWarnings("boxing")
	@Override
	protected void onPostExecute(final Boolean result)
	{
		Log.d(TAG, "Completed " + result); //$NON-NLS-1$
		super.onPostExecute(result);
		this.listener.downloadFinish(this.code, result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onCancelled(java.lang.Object)
	 */
	@SuppressWarnings("boxing")
	@Override
	protected void onCancelled(final Boolean result)
	{
		Log.d(TAG, "Cancelled, result=" + result); //$NON-NLS-1$
		super.onCancelled(result);
		this.listener.downloadFinish(this.code, result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
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
			Log.d(TAG, "Get " + url.toString()); //$NON-NLS-1$
			final URLConnection connection = url.openConnection();
			connection.connect();

			// getting file length
			int length = connection.getContentLength();

			// input stream to read file - with 8k buffer
			input = new BufferedInputStream(connection.getInputStream(), 8192);

			// output stream to write file
			output = new FileOutputStream(this.to);

			// copy streams
			final byte buffer[] = new byte[1024];
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
					throw new InterruptedException();
			}
			output.flush();
			return true;
		}
		catch (final InterruptedException e)
		{
			Log.d(TAG, "Interrupted downloading"); //$NON-NLS-1$
		}
		catch (final Exception e)
		{
			Log.e(TAG, "While downloading", e); //$NON-NLS-1$
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
					e.printStackTrace();
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
					e.printStackTrace();
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
}
