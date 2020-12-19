/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.sqlunet.concurrency.Task;

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
public class FileDataDownloader extends Task<String, Void, FileData>
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

	static public void start(@NonNull final Activity activity, @Nullable final String name, @Nullable final String downloadSourceUrl0, final String downloadDest, final String cache)
	{
		// download source data
		if (name == null || downloadSourceUrl0 == null || downloadSourceUrl0.isEmpty())
		{
			final String message = activity.getString(R.string.status_download_error_unavailable_download_url);
			activity.runOnUiThread(() -> Toast.makeText(activity, message, Toast.LENGTH_LONG).show());
			return;
		}
		final String downloadSourceUrl = Settings.Downloader.zipDownloaderSource(activity, downloadSourceUrl0);

		// download source data (acquired by task)
		final FileDataDownloader task = new FileDataDownloader(srcData -> {

			// in-use downstream data
			final long downDateValue = Settings.getDbDate(activity);
			final long downSizeValue = Settings.getDbSize(activity);
			final Date downDate = downDateValue == -1 ? null : new Date(downDateValue);
			final Long downSize = downSizeValue == -1 ? null : downSizeValue;
			final long downSourceDateValue = Settings.getDbSourceDate(activity);
			final long downSourceSizeValue = Settings.getDbSourceSize(activity);
			final String downSource = Settings.getDbSource(activity);
			final Date downSourceDate = downSourceDateValue == -1 ? null : new Date(downSourceDateValue);
			final Long downSourceSize = downSourceSizeValue == -1 ? null : downSourceSizeValue;

			// upstream data
			final Date srcDate = srcData == null ? null : srcData.getDate();
			final Long srcSize = srcData == null ? null : srcData.getSize();

			// newer
			final boolean newer = srcDate == null || downDate == null || srcDate.compareTo(downDate) > 0;
			final boolean newer2 = srcDate == null || downSourceDate == null || srcDate.compareTo(downSourceDate) > 0;

			// start update activity
			final Intent intent = new Intent(activity, UpdateActivity.class);
			// result
			intent.putExtra(UpdateFragment.UP_SOURCE_ARG, downloadSourceUrl);
			intent.putExtra(UpdateFragment.UP_DATE_ARG, srcDate == null ? "n/a" : srcDate.toString());
			intent.putExtra(UpdateFragment.UP_SIZE_ARG, srcSize == null ? "n/a" : srcSize.toString() + " bytes");
			intent.putExtra(UpdateFragment.DOWN_ARG, name);
			intent.putExtra(UpdateFragment.DOWN_TARGET_ARG, cache + '/' + name);
			intent.putExtra(UpdateFragment.DOWN_DATE_ARG, downDate == null ? "n/a" : downDate.toString());
			intent.putExtra(UpdateFragment.DOWN_SIZE_ARG, downSize == null ? "n/a" : downSize.toString() + " bytes");
			intent.putExtra(UpdateFragment.DOWN_SOURCE_ARG, downSource);
			intent.putExtra(UpdateFragment.DOWN_SOURCE_DATE_ARG, downSourceDate == null ? "n/a" : downSourceDate.toString());
			intent.putExtra(UpdateFragment.DOWN_SOURCE_SIZE_ARG, downSourceSize == null ? "n/a" : downSourceSize.toString() + " bytes");
			intent.putExtra(UpdateFragment.NEWER_ARG, newer2);
			// to do if confirmed
			intent.putExtra(DOWNLOAD_FROM_ARG, downloadSourceUrl);
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
