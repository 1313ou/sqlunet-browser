/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.download;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bbou.concurrency.Task;
import com.bbou.deploy.Deploy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.bbou.download.AbstractDownloadFragment.DOWNLOAD_FROM_ARG;
import static com.bbou.download.AbstractDownloadFragment.DOWNLOAD_MODE_ARG;
import static com.bbou.download.AbstractDownloadFragment.DOWNLOAD_TARGET_FILE_ARG;
import static com.bbou.download.DownloadFragment.DOWNLOAD_TO_FILE_ARG;

/**
 * Content downloader task
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class ContentDownloader extends Task<String, Void, String[]>
{
	static private final String TAG = "ContentDownloader";

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
	@SuppressWarnings("WeakerAccess")
	public ContentDownloader(final Listener listener)
	{
		super();
		this.listener = listener;
		this.exception = null;
	}

	@Nullable
	@Override
	protected String[] doInBackground(@NonNull final String... params)
	{
		final String srcArg = params[0];
		try
		{
			// connect
			final URL url = new URL(srcArg);
			Log.d(TAG, "Getting " + url);
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

			try (InputStream input = connection.getInputStream(); InputStreamReader isr = new InputStreamReader(input); BufferedReader reader = new BufferedReader(isr))
			{
				// read
				List<String> items = new ArrayList<>();
				String line;
				while ((line = reader.readLine()) != null)
				{
					items.add(line);

					// interrupted
					if (Thread.interrupted())
					{
						final InterruptedException ie = new InterruptedException("interrupted while downloading");
						this.exception = ie;
						throw ie;
					}

					// cancelled
					if (isCancelled())
					{
						throw new InterruptedException("cancelled");
					}
				}
				return items.toArray(new String[0]);
			}
		}
		catch (@NonNull final InterruptedException e)
		{
			this.exception = e;
			Log.d(TAG, "Interrupted " + e);
		}
		catch (@NonNull final Exception e)
		{
			this.exception = e;
			Log.e(TAG, "While downloading", e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(final String[] result)
	{
		Log.d(TAG, "Completed");
		this.listener.onDone(result);
	}

	@Override
	protected void onCancelled(final String[] result)
	{
		Log.d(TAG, "Cancelled");
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
	 * Content downloader listener
	 */
	@SuppressWarnings("WeakerAccess")
	public interface Listener
	{
		/**
		 * Done
		 *
		 * @param result content items
		 */
		void onDone(final String[] result);
	}

	static private final String CONTENT_FILE = "content";


	/**
	 * @param context                           context
	 * @param name                              name
	 * @param downloadIntent                    download intent
	 * @param downloadBroadcastAction           download broadcast action
	 * @param downloadBroadcastRequestKey       download broadcast request key
	 * @param downloadBroadcastKillRequestValue download broadcast kill request value
	 * @param downloadBroadcastNewRequestValue  download broadcast new  request value
	 */
	static private void startDownload(@NonNull final Context context, @NonNull final String name, @NonNull final Intent downloadIntent, @NonNull final String downloadBroadcastAction, @NonNull final String downloadBroadcastRequestKey, @NonNull final String downloadBroadcastKillRequestValue, @NonNull final String downloadBroadcastNewRequestValue)
	{
		final String target = name.endsWith(Deploy.ZIP_EXTENSION) ? name : name + Deploy.ZIP_EXTENSION;
		addTargetToIntent(context, downloadIntent, target);
		downloadIntent.putExtra(AbstractDownloadFragment.BROADCAST_ACTION, downloadBroadcastAction);
		downloadIntent.putExtra(AbstractDownloadFragment.BROADCAST_REQUEST_KEY, downloadBroadcastRequestKey);
		downloadIntent.putExtra(AbstractDownloadFragment.BROADCAST_KILL_REQUEST_VALUE, downloadBroadcastKillRequestValue);
		downloadIntent.putExtra(AbstractDownloadFragment.BROADCAST_NEW_REQUEST_VALUE, downloadBroadcastNewRequestValue);

		context.startActivity(downloadIntent);
	}

	/** @noinspection UnusedReturnValue*/
	@NonNull
	public static Intent addTargetToIntent(@NonNull final Context context, @NonNull final Intent intent, @NonNull final String name)
	{
		final String mode = intent.getStringExtra(DOWNLOAD_MODE_ARG);
		assert mode != null;
		if (mode.equals(Settings.Mode.DOWNLOAD.toString()))
		{
			return addTargetToIntentPlainDownload(context, intent, name);
		}
		else if (mode.equals(Settings.Mode.DOWNLOAD_ZIP.toString()))
		{
			return addTargetToIntentZipDownload(context, intent, name);
		}
		else if (mode.equals(Settings.Mode.DOWNLOAD_ZIP_THEN_UNZIP.toString()))
		{
			return addTargetToIntentZipDownloadThenDeploy(context, intent, name);
		}
		throw new RuntimeException(mode);
	}

	@NonNull
	public static Intent addTargetToIntentPlainDownload(@NonNull final Context context, @NonNull final Intent intent, @NonNull final String name)
	{
		String target = name;
		final String repo = Settings.getRepoPref(context);
		target = repo + '/' + target;
		final String dest = Settings.getDatapackDir(context);
		target = dest + '/' + target;
		intent.putExtra(DOWNLOAD_FROM_ARG, target); // source archive
		intent.putExtra(DOWNLOAD_TO_FILE_ARG, dest); // dest file
		intent.putExtra(DOWNLOAD_TARGET_FILE_ARG, dest); // target file
		return intent;
	}

	@NonNull
	public static Intent addTargetToIntentZipDownload(@NonNull final Context context, @NonNull final Intent intent, @NonNull final String name)
	{
		String target = name;
		if (!target.endsWith(Deploy.ZIP_EXTENSION))
		{
			target += Deploy.ZIP_EXTENSION;
		}
		final String repo = Settings.getRepoPref(context);
		target = repo + '/' + target;
		intent.putExtra(DOWNLOAD_FROM_ARG, target); // source archive
		intent.putExtra(DOWNLOAD_TARGET_FILE_ARG, name); // target file
		return intent;
	}

	@NonNull
	public static Intent addTargetToIntentZipDownloadThenDeploy(@NonNull final Context context, @NonNull final Intent intent, @NonNull final String name)
	{
		String target = name;
		if (!target.endsWith(Deploy.ZIP_EXTENSION))
		{
			target += Deploy.ZIP_EXTENSION;
		}
		final String repo = Settings.getRepoPref(context);
		final String repoTarget = repo + '/' + target;
		final String cache = Settings.getCachePref(context);
		final String cachedTarget = cache + '/' + target;

		intent.putExtra(DOWNLOAD_FROM_ARG, repoTarget); // source archive
		intent.putExtra(DOWNLOAD_TO_FILE_ARG, cachedTarget); // destination archive
		intent.putExtra(AbstractDownloadFragment.DOWNLOAD_TARGET_FILE_ARG, cachedTarget); // target file
		return intent;
	}

	/**
	 * Content
	 */
	static public void content(@NonNull final Activity activity, @NonNull final Intent downloadIntent, @NonNull final String downloadBroadcastAction, @NonNull final String downloadBroadcastRequestKey, @NonNull final String downloadBroadcastKillRequestValue, @NonNull final String downloadBroadcastNewRequestValue)
	{
		Toast.makeText(activity, R.string.status_download_downloadable_content, Toast.LENGTH_SHORT).show();

		final WeakReference<Activity> activityReference = new WeakReference<>(activity);
		final String repo = Settings.getRepoPref(activity);
		if (repo == null)
		{
			Toast.makeText(activity, R.string.status_download_error_null_download_url, Toast.LENGTH_SHORT).show();
			return;
		}
		final String dest = Settings.getCachePref(activity);
		final String sourceFile = repo + '/' + CONTENT_FILE;
		final String targetFile = dest + '/' + CONTENT_FILE;

		new ContentDownloader(result -> {

			final Activity activity2 = activityReference.get();
			if (activity2 == null || activity2.isFinishing() || activity2.isDestroyed())
			{
				return;
			}
			final AlertDialog.Builder alert = new AlertDialog.Builder(activity2); // guarded, level 2
			LayoutInflater inflater = LayoutInflater.from(activity2);
			@SuppressLint("InflateParams") final View header = inflater.inflate(R.layout.content_header, null);
			alert.setCustomTitle(header);
			final TextView sourceView = header.findViewById(R.id.source);
			sourceView.setText(targetFile);

			if (result == null)
			{
				alert.setIconAttribute(android.R.attr.alertDialogIcon) //
						.setMessage(R.string.status_task_failed);
			}
			else
			{
				alert.setItems(result, (dialog, which) -> {
					final String item = result[which];
					startDownload(activity2, item, downloadIntent, downloadBroadcastAction, downloadBroadcastRequestKey, downloadBroadcastKillRequestValue, downloadBroadcastNewRequestValue);
				});
			}
			alert.show();
		}).execute(sourceFile, targetFile);
	}
}
