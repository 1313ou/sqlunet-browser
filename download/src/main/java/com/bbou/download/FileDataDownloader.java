/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.download;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.bbou.concurrency.Task;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
	protected FileData doInBackground(@NonNull final String... params)
	{
		final String urlString = params[0];

		long date = -1;
		long size = -1;
		String etag = null;
		String version = null;
		String staticVersion = null;

		HttpURLConnection httpConnection = null;
		try
		{
			// url
			final URL url = new URL(urlString);
			Log.d(TAG, "Getting " + url);

			// connection
			URLConnection connection = url.openConnection();

			// handle redirect
			if (connection instanceof HttpURLConnection)
			{
				httpConnection = (HttpURLConnection) connection;
				httpConnection.setInstanceFollowRedirects(false);
				HttpURLConnection.setFollowRedirects(false);

				int status = httpConnection.getResponseCode();
				Log.d(TAG, "Response Code ... " + status);
				if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER)
				{
					// headers
					date = connection.getLastModified(); // new Date(date));
					size = connection.getContentLength();
					etag = connection.getHeaderField("etag");
					version = connection.getHeaderField("x-version");
					staticVersion = connection.getHeaderField("x-static-version");

					// get redirect url from "location" header field
					String newUrl = httpConnection.getHeaderField("Location");

					// close
					httpConnection.getInputStream().close();

					// disconnect
					httpConnection.disconnect();

					// open the new connection again
					connection = httpConnection = (HttpURLConnection) new URL(newUrl).openConnection();
					httpConnection.setInstanceFollowRedirects(true);
					HttpURLConnection.setFollowRedirects(true);
					Log.d(TAG, "Redirect to URL : " + newUrl);
				}
			}

			// connect
			connection.connect();

			// expect HTTP 200 OK, so we don't mistakenly save error report instead of the file
			if (connection instanceof HttpURLConnection)
			{
				if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
				{
					final String message = "server returned HTTP " + httpConnection.getResponseCode() + " " + httpConnection.getResponseMessage();
					throw new RuntimeException(message);
				}
			}
			final String name = url.getFile();
			if (date <= 0)
			{
				date = connection.getLastModified(); // new Date(date));
			}
			if (size <= 0)
			{
				size = connection.getContentLength();
			}
			if (etag == null)
			{
				etag = connection.getHeaderField("etag");
			}
			if (version == null)
			{
				version = connection.getHeaderField("x-version");
			}
			if (staticVersion == null)
			{
				staticVersion = connection.getHeaderField("x-static-version");
			}

			// close
			connection.getInputStream().close();

			return new FileData(name, date, size, etag, version, staticVersion);
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
	 * File data downloader listener
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

	/**
	 * Start file data downloading
	 *
	 * @param activity       launching activity
	 * @param downloadIntent download intent (activity launched if update is requested)
	 */
	static public void start(@NonNull final Activity activity, @NonNull final Intent downloadIntent)
	{
		// unmarshal arguments
		@Nullable final String downloadSourceUrl = downloadIntent.getStringExtra(AbstractDownloadFragment.DOWNLOAD_FROM_ARG);
		@Nullable final String name = Uri.parse(downloadSourceUrl).getLastPathSegment();

		// download source data
		if (downloadSourceUrl == null || downloadSourceUrl.isEmpty() || name == null)
		{
			final String message = activity.getString(R.string.no_url);
			activity.runOnUiThread(() -> Toast.makeText(activity, message, Toast.LENGTH_LONG).show());
			return;
		}

		// download source data (acquired by task)
		final FileDataDownloader task = new FileDataDownloader(srcData -> {

			// in-use downstream data
			final long downDateValue = Settings.getDatapackDate(activity);
			final long downSizeValue = Settings.getDatapackSize(activity);
			final Date downDate = downDateValue == -1 || downDateValue == 0 ? null : new Date(downDateValue);
			final Long downSize = downSizeValue == -1 ? null : downSizeValue;

			// in-use downstream recorded source data
			final String downSource = Settings.getDatapackSource(activity);
			final long downSourceDateValue = Settings.getDatapackSourceDate(activity);
			final long downSourceSizeValue = Settings.getDatapackSourceSize(activity);
			final Date downSourceDate = downSourceDateValue == -1 || downSourceDateValue == 0 ? null : new Date(downSourceDateValue);
			final Long downSourceSize = downSourceSizeValue == -1 ? null : downSourceSizeValue;
			final String downSourceEtag = Settings.getDatapackSourceEtag(activity);
			final String downSourceVersion = Settings.getDatapackSourceVersion(activity);
			final String downSourceStaticVersion = Settings.getDatapackSourceStaticVersion(activity);

			// upstream data from http connection
			final Date srcDate = srcData == null ? null : srcData.getDate();
			final Long srcSize = srcData == null ? null : srcData.getSize();
			final String srcEtag = srcData == null ? null : srcData.getEtag();
			final String srcVersion = srcData == null ? null : srcData.getVersion();
			final String srcStaticVersion = srcData == null ? null : srcData.getStaticVersion();

			// newer
			final boolean same = (srcEtag != null && srcEtag.equals(downSourceEtag)) || (srcVersion != null && srcVersion.equals(downSourceVersion)) || (srcStaticVersion != null && srcStaticVersion.equals(downSourceStaticVersion)); // one match
			final boolean newer = srcDate == null || downDate == null || srcDate.compareTo(downDate) > 0; // upstream src date from http connection is newer than recorded date
			final boolean srcNewer = srcDate == null || downSourceDate == null || srcDate.compareTo(downSourceDate) > 0; // upstream src date from http connection is newer than recorded source date

			// start update activity
			final Intent intent = new Intent(activity, UpdateActivity.class);

			// result
			final String na = activity.getString(R.string.na);
			final String bytes = activity.getString(R.string.bytes);
			intent.putExtra(UpdateFragment.UP_SOURCE_ARG, downloadSourceUrl);
			intent.putExtra(UpdateFragment.UP_DATE_ARG, srcDate == null ? na : srcDate.toString());
			intent.putExtra(UpdateFragment.UP_SIZE_ARG, srcSize == null ? na : srcSize + " " + bytes);
			intent.putExtra(UpdateFragment.UP_ETAG_ARG, srcEtag == null ? na : srcEtag);
			intent.putExtra(UpdateFragment.UP_VERSION_ARG, srcVersion == null ? na : srcVersion);
			intent.putExtra(UpdateFragment.UP_STATIC_VERSION_ARG, srcStaticVersion == null ? na : srcStaticVersion);

			intent.putExtra(UpdateFragment.DOWN_NAME_ARG, name);
			intent.putExtra(UpdateFragment.DOWN_DATE_ARG, downDate == null ? na : downDate.toString());
			intent.putExtra(UpdateFragment.DOWN_SIZE_ARG, downSize == null ? na : downSize + " " + bytes);
			intent.putExtra(UpdateFragment.DOWN_SOURCE_ARG, downSource == null ? activity.getString(R.string.unsourced) : downSource);
			intent.putExtra(UpdateFragment.DOWN_SOURCE_DATE_ARG, downSourceDate == null ? na : downSourceDate.toString());
			intent.putExtra(UpdateFragment.DOWN_SOURCE_SIZE_ARG, downSourceSize == null ? na : downSourceSize + " " + bytes);
			intent.putExtra(UpdateFragment.DOWN_SOURCE_ETAG_ARG, downSourceEtag == null ? na : downSourceEtag);
			intent.putExtra(UpdateFragment.DOWN_SOURCE_VERSION_ARG, downSourceVersion == null ? na : downSourceVersion);
			intent.putExtra(UpdateFragment.DOWN_SOURCE_STATIC_VERSION_ARG, downSourceVersion == null ? na : downSourceStaticVersion);

			intent.putExtra(UpdateFragment.NEWER_ARG, !same && (newer || srcNewer));

			// to do if confirmed
			intent.putExtra(UpdateFragment.DOWNLOAD_INTENT_ARG, downloadIntent);

			activity.startActivity(intent);
		});
		task.execute(downloadSourceUrl);
	}
}
