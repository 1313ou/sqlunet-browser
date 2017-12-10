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
		if (/*downloadUrl == null ||*/ downloadUrl.isEmpty())
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
			public void onDone(final FileData srcData)
			{
				// actual data
				final String to = StorageSettings.getDbDownloadTarget(activity);
				final File actualFile = new File(to);
				final String actual = actualFile.getAbsolutePath();
				final FileData actualData = FileData.makeFileDataFrom(actualFile);
				final Date actualDate = actualData == null ? null : actualData.getDate();
				final Long actualSize = actualData == null ? null : actualData.getSize();

				// current data
				final FileData currentData = FileData.getCurrent(activity);
				final Date currentDate = currentData == null ? null : currentData.getDate();
				final Long currentSize = currentData == null ? null : currentData.getSize();

				// src data
				final Date srcDate = srcData == null ? null : srcData.getDate();
				final Long srcSize = srcData == null ? null : srcData.getSize();

				// newer
				final Date refDate = currentDate != null ? currentDate : actualDate;
				@SuppressWarnings("SimplifiableConditionalExpression") final boolean newer = (srcDate == null || refDate == null) ? false : srcDate.compareTo(refDate) > 0;

				final Intent intent = new Intent(activity, UpdateActivity.class);
				intent.putExtra(UpdateFragment.CURRENT_DATE_ARG, currentDate == null ? "n/a" : currentDate.toString());
				intent.putExtra(UpdateFragment.CURRENT_SIZE_ARG, currentSize == null ? "n/a" : currentSize.toString() + " bytes");

				intent.putExtra(UpdateFragment.FROM_ARG, downloadUrl);
				intent.putExtra(UpdateFragment.FROM_DATE_ARG, srcDate == null ? "n/a" : srcDate.toString());
				intent.putExtra(UpdateFragment.FROM_SIZE_ARG, srcSize == null ? "n/a" : srcSize.toString() + " bytes");

				intent.putExtra(UpdateFragment.TO_ARG, actual);
				intent.putExtra(UpdateFragment.TO_DATE_ARG, actualDate == null ? "n/a" : actualDate.toString());
				intent.putExtra(UpdateFragment.TO_SIZE_ARG, actualSize == null ? "n/a" : actualSize.toString() + " bytes");

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
