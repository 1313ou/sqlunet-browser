/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static org.sqlunet.download.BaseDownloadFragment.DOWNLOAD_FROM_ARG;
import static org.sqlunet.download.BaseDownloadFragment.DOWNLOAD_TO_ARG;

/**
 * File data downloader
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FileDataDownloader extends AsyncTask<String, Void, FileData>
{
	static private final String TAG = "FileDataDownloader";

	/**
	 * Result listener
	 */
	private final Listener listener;

	/**
	 * Constructor
	 *
	 * @param listener listener
	 */
	private FileDataDownloader(final Listener listener)
	{
		this.listener = listener;
	}

	@Nullable
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
				final String name = url.getFile();
				final long date = httpConnection.getLastModified(); // new Date(date));
				final int size = httpConnection.getContentLength();
				return new FileData(name, date, size);
			}
		}
		catch (@NonNull final Exception e)
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
	@FunctionalInterface
	interface Listener
	{
		/**
		 * Done
		 *
		 * @param result file data
		 */
		void onDone(final FileData result);
	}

	static public void start(@NonNull final Activity activity, final String name, final String downloadSourceUrl, final String downloadDest, final String cache)
	{
		// download source data
		if (name == null || downloadSourceUrl == null || downloadSourceUrl.isEmpty())
		{
			final String message = activity.getString(R.string.status_download_error_unavailable_download_url);
			activity.runOnUiThread(() -> Toast.makeText(activity, message, Toast.LENGTH_LONG).show());
			return;
		}

		// download source data (acquired by task)
		final FileDataDownloader task = new FileDataDownloader(srcData -> {

			// actual data
			final long actualDateValue = Settings.getDbDate(activity);
			final long actualSizeValue = Settings.getDbSize(activity);
			final Date actualDate = actualDateValue == -1 ? null : new Date(actualDateValue);
			final Long actualSize = actualSizeValue == -1 ? null : actualSizeValue;

			// src data
			final Date srcDate = srcData == null ? null : srcData.getDate();
			final Long srcSize = srcData == null ? null : srcData.getSize();

			// newer
			final boolean newer = srcDate == null || actualDate == null || srcDate.compareTo(actualDate) > 0;

			// cache
			String downloadFromArg = downloadSourceUrl + '/' + name;

			final Intent intent = new Intent(activity, UpdateActivity.class);
			intent.putExtra(UpdateFragment.FROM_ARG, downloadSourceUrl + '/' + name);
			intent.putExtra(UpdateFragment.FROM_DATE_ARG, srcDate == null ? "n/a" : srcDate.toString());
			intent.putExtra(UpdateFragment.FROM_SIZE_ARG, srcSize == null ? "n/a" : srcSize.toString() + " bytes");
			intent.putExtra(UpdateFragment.TO_ARG, name);
			intent.putExtra(UpdateFragment.TO_DEST_ARG, cache + '/' + name);
			intent.putExtra(UpdateFragment.TO_DATE_ARG, actualDate == null ? "n/a" : actualDate.toString());
			intent.putExtra(UpdateFragment.TO_SIZE_ARG, actualSize == null ? "n/a" : actualSize.toString() + " bytes");
			intent.putExtra(UpdateFragment.NEWER_ARG, newer);
			intent.putExtra(DOWNLOAD_FROM_ARG, downloadFromArg);
			intent.putExtra(DOWNLOAD_TO_ARG, downloadDest);

			activity.startActivity(intent);
		});

		task.execute(downloadSourceUrl);
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
