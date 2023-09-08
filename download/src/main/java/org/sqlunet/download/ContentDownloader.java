package org.sqlunet.download;

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

import org.sqlunet.download.R;
import org.sqlunet.concurrency.Task;

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

import static org.sqlunet.download.AbstractDownloadFragment.DOWNLOAD_DOWNLOADER_ARG;
import static org.sqlunet.download.AbstractDownloadFragment.DOWNLOAD_FROM_ARG;
import static org.sqlunet.download.AbstractDownloadFragment.DOWNLOAD_TO_ARG;
import static org.sqlunet.download.AbstractDownloadFragment.UNZIP_TO_ARG;

/**
 * Content Downloader task
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
	protected String[] doInBackground(final String... params)
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
			Log.d(TAG, "Interrupted " +  e);
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
	 * MD5Downloader listener
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
	 * Content
	 */
	static public void content(@NonNull Activity activity)
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
			@SuppressLint("InflateParams") View header = inflater.inflate(R.layout.content_header, null);
			alert.setCustomTitle(header);
			final TextView sourceView = header.findViewById(R.id.source);
			sourceView.setText(targetFile);

			if (result == null)
			{
				alert.setIcon(R.drawable.ic_error);
				alert.setMessage(R.string.status_task_failed);
			}
			else
			{
				alert.setItems(result, (dialog, which) -> {
					final String item = result[which];
					startDownload(activity2, item);
				});
			}
			alert.show();
		}).execute(sourceFile, targetFile);
	}

	/**
	 * Start download
	 *
	 * @param context context
	 * @param target  target
	 */
	static private void startDownload(@NonNull final Context context, final String target)
	{
		final String repo = Settings.getRepoPref(context);
		final String cache = Settings.getCachePref(context);
		final String modeldir = Settings.getModelDir(context);
		final String downloader = Settings.getDownloaderPref(context);

		final Intent intent = new Intent(context, DownloadActivity.class);
		intent.putExtra(DOWNLOAD_FROM_ARG, repo + '/' + target + ".zip");
		intent.putExtra(DOWNLOAD_TO_ARG, cache + '/' + target + ".zip");
		intent.putExtra(UNZIP_TO_ARG, modeldir);
		intent.putExtra(DOWNLOAD_DOWNLOADER_ARG, downloader);
		context.startActivity(intent);
	}
}
