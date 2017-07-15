package org.sqlunet.browser.config;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.sqlunet.browser.common.R;
import org.sqlunet.settings.StorageSettings;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

/**
 * File data downloader
 */
public class FileDataDownloader extends AsyncTask<String, Void, FileDataDownloader.FileData>
{
	static private final String TAG = "FileDataDownloader";

	/**
	 * Result
	 */
	static public class FileData
	{
		private final long size;

		private final long date;

		public FileData(final long date, final long size)
		{
			this.size = size;
			this.date = date;
		}

		public FileData(final File file)
		{
			if (file.exists())
			{
				this.size = file.length();
				this.date = file.lastModified();
			}
			else
			{
				this.size = -1;
				this.date = -1;
			}
		}

		public Long getSize()
		{
			return this.size == -1 ? null : this.size;
		}

		public Date getDate()
		{
			return this.date == -1 ? null : new Date(this.date);
		}
	}

	/**
	 * Result listener
	 */
	private final Listener listener;

	/**
	 * Constructor
	 *
	 * @param listener listener
	 */
	FileDataDownloader(final Listener listener)
	{
		this.listener = listener;
	}

	@Override
	protected FileData doInBackground(final String... params)
	{
		final String urlString = params[0];

		HttpURLConnection httpConnection = null;
		try
		{
			// url
			final URL url = new URL(urlString);
			Log.d(TAG, "Get " + url.toString());

			// connect
			URLConnection connection = url.openConnection();
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
				long date = httpConnection.getLastModified(); // new Date(date));
				int size = httpConnection.getContentLength();
				return new FileData(date, size);
			}
		}
		catch (final Exception e)
		{
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
	protected void onPostExecute(final FileData result)
	{
		Log.d(TAG, "Completed " + result);
		this.listener.onDone(result);
	}

	@Override
	protected void onCancelled(final FileData result)
	{
		Log.d(TAG, "Cancelled " + result);
		this.listener.onDone(result);
	}

	/**
	 * SizeDownloader listener
	 */
	public interface Listener
	{
		/**
		 * Done
		 *
		 * @param result file data
		 */
		void onDone(final FileData result);
	}

	static public void start(final Activity activity)
	{
		// download source data
		final String downloadUrl = StorageSettings.getDbDownloadSource(activity);
		if (downloadUrl == null || downloadUrl.isEmpty())
		{
			final String message = activity.getString(R.string.status_error_null_download_url);
			activity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
				}
			});
			return;
		}

		// download source data (acquired by task)
		final FileDataDownloader task = new FileDataDownloader(new FileDataDownloader.Listener()
		{
			@Override
			public void onDone(final FileDataDownloader.FileData srcData)
			{
				// dest data
				final String to = StorageSettings.getDbDownloadTarget(activity);
				final File destFile = new File(to);
				final FileDataDownloader.FileData destData = new FileDataDownloader.FileData(destFile);

				final String dest = destFile.getAbsolutePath();
				final Long destSize = destData.getSize();
				final Date destDate = destData.getDate();

				// src data
				final String src = downloadUrl;
				final Long srcSize = srcData == null ? null : srcData.getSize();
				final Date srcDate = srcData == null ? null : srcData.getDate();

				final boolean newer = srcDate == null || destDate == null ? false : srcDate.compareTo(destDate) > 0;

				final Intent intent = new Intent(activity, UpdateActivity.class);
				intent.putExtra(UpdateFragment.FROM_ARG, src);
				intent.putExtra(UpdateFragment.FROM_DATE_ARG, srcDate == null ? "n/a" : srcDate.toString());
				intent.putExtra(UpdateFragment.FROM_SIZE_ARG, srcSize == null ? "n/a" : srcSize.toString());
				intent.putExtra(UpdateFragment.TO_ARG, dest);
				intent.putExtra(UpdateFragment.TO_DATE_ARG, destDate == null ? "n/a" : destDate.toString());
				intent.putExtra(UpdateFragment.TO_SIZE_ARG, destSize == null ? "n/a" : destSize.toString());
				intent.putExtra(UpdateFragment.NEWER_ARG, newer);

				activity.startActivity(intent);
			}
		});
		task.execute(downloadUrl);
	}

	/*
	long getCreateTime(File file)
	{
		java.nio.Path file = java.nio.Paths.get(file.getAbsoluteFile().toURI());
		java.nio.BasicFileAttributes attr = java.nio.Files.readAttributes(file, java.nio.BasicFileAttributes.class);

		System.out.println("creationTime: " + attr.creationTime());
		System.out.println("lastAccessTime: " + attr.lastAccessTime());
		System.out.println("lastModifiedTime: " + attr.lastModifiedTime());
	}
	*/
}
