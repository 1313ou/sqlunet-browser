/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.download;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bbou.concurrency.Task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Resources downloader task
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class ResourcesDownloader extends Task<String, Void, Collection<String[]>>
{
	static private final String TAG = "ResourcesDownloader";

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
	ResourcesDownloader(final Listener listener)
	{
		this.listener = listener;
		this.exception = null;
	}

	@Nullable
	@Override
	protected Collection<String[]> doInBackground(final String... params)
	{
		final String resArg = params[0];
		final String lineFilter = params.length < 2 ? null : params[1];
		HttpURLConnection httpConnection = null;
		try
		{
			// connect
			final URL url = new URL(resArg);
			Log.d(TAG, "Get " + url);
			final URLConnection connection = url.openConnection();
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
			}

			// open the reader
			try (InputStream is = connection.getInputStream(); InputStreamReader isr = new InputStreamReader(is); BufferedReader reader = new BufferedReader(isr))
			{
				// read
				Collection<String[]> lines = null;
				String line;
				while ((line = reader.readLine()) != null)
				{
					line = line.trim();
					if (!line.isEmpty() && !line.startsWith("#"))
					{
						if (lineFilter != null && !line.matches(lineFilter))
						{
							continue;
						}

						final String[] fields = line.split("\\s+");
						if (lines == null)
						{
							lines = new ArrayList<>();
						}
						lines.add(fields);
					}
					// cooperative exit
					if (isCancelled())
					{
						Log.d(TAG, "Cancelled!");
						throw new InterruptedException("cancelled");
					}
					if (Thread.interrupted())
					{
						Log.d(TAG, "Interrupted!");
						final InterruptedException ie = new InterruptedException("interrupted while downloading");
						this.exception = ie;
						throw ie;
					}
				}
				return lines;
			}
		}
		catch (@NonNull final InterruptedException e)
		{
			this.exception = e;
			Log.d(TAG, e.toString());
		}
		catch (@NonNull final Exception e)
		{
			this.exception = e;
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
	protected void onPostExecute(final Collection<String[]> result)
	{
		Log.d(TAG, "Completed " + result);
		this.listener.onDone(result);
	}

	@Override
	protected void onCancelled(final Collection<String[]> result)
	{
		Log.d(TAG, "Cancelled " + result);
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
	 * Resources downloader listener
	 */
	@FunctionalInterface
	public interface Listener
	{
		/**
		 * Done
		 *
		 * @param result resources
		 */
		void onDone(final Collection<String[]> result);
	}

	// Get resources from resource directory

	/**
	 * Display resources
	 */
	public static void showResources(@NonNull final Activity activity)
	{
		final String url = activity.getString(R.string.resources_directory);
		final String filter = activity.getString(R.string.resources_directory_filter);

		new ResourcesDownloader(resources -> {

			if (activity.isFinishing() || activity.isDestroyed())
			{
				return;
			}

			if (resources == null)
			{
				new AlertDialog.Builder(activity) // guarded, level 2
						.setTitle(activity.getString(R.string.action_directory) + " of " + url) //
						.setMessage(R.string.status_task_failed) //
						.show();
			}
			else
			{
				if (!activity.isFinishing() && !activity.isDestroyed())
				{
					final SpannableStringBuilder sb = new SpannableStringBuilder();
					sb.append('\n');

					for (String[] row : resources)
					{
						sb.append(String.join(" ", row));
						sb.append('\n');
						sb.append('\n');
					}

					new AlertDialog.Builder(activity) // guarded, level 3
							.setTitle(activity.getString(R.string.resource_directory) + ' ' + url) //
							.setMessage(sb) //
							.show();
				}
			}
		}).execute(url, filter);
	}

	/**
	 * Populate radio group with resources
	 */
	public static void populateLists(@NonNull final Context context, @NonNull final BiConsumer<List<String>, List<String>> consumer)
	{
		final String url = context.getString(R.string.resources_directory);
		final String filter = context.getString(R.string.resources_directory_filter);

		new ResourcesDownloader(resources -> {

			if (resources != null)
			{
				@NonNull final List<String> values = new ArrayList<>();
				@NonNull final List<String> labels = new ArrayList<>();
				for (String[] row : resources)
				{
					// ewn	OEWN	2023	Bitbucket	https://bitbucket.org/semantikos2/semantikos22/raw/53e04fe21bc901ee15631873972445c2c8725652	zipped
					final String value = row[4];
					final String label = String.format("%s %s (%s %s)", row[1], row[2], row[3], row[5]);
					values.add(value);
					labels.add(label);
				}
				consumer.accept(values, labels);
			}
		}).execute(url, filter);
	}

	/**
	 * Populate radio group with resources
	 */
	public static void populateRadioGroup(@NonNull final Context context, @NonNull final RadioGroup optionsView)
	{
		populateLists(context, (values, labels) -> {

			int n = Math.min(values.size(), labels.size());
			for (int i = 0; i < n; i++)
			{
				final CharSequence value = values.get(i);
				final CharSequence label = labels.get(i);
				final RadioButton radioButton = new RadioButton(context);
				radioButton.setText(label);
				radioButton.setTag(value);
				radioButton.setEnabled(true);
				optionsView.addView(radioButton);
			}
		});
	}
}
