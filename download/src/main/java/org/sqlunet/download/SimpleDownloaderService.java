/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * SimpleDownloader service
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SimpleDownloaderService extends JobIntentService
{
	static private final String TAG = "SimpleDownloaderS";

	static public final String MAIN_INTENT_FILTER = "intent_filter_downloader_main";

	static public final String UPDATE_INTENT_FILTER = "intent_filter_downloader_update";

	static public final String ACTION_DOWNLOAD = "download";

	static public final String ARG_FROM_URL = "from_url";

	static public final String ARG_TO_FILE = "to_file";

	static public final String ARG_CODE = "code";

	static public final String EVENT = "event";

	static public final String EVENT_START = "start";

	static public final String EVENT_UPDATE = "update";

	static public final String EVENT_UPDATE_DOWNLOADED = "update_downloaded";

	static public final String EVENT_UPDATE_TOTAL = "update_total";

	static public final String EVENT_FINISH = "finish";

	static public final String EVENT_FINISH_RESULT = "finish_result";

	static public final String EVENT_FINISH_ID = "finish_id";

	static public final String EVENT_FINISH_EXCEPTION = "finish_exception";

	/**
	 * Unique job ID for this service.
	 */
	static final int JOB_ID = 1313;

	/**
	 * Buffer size
	 */
	static protected final int CHUNK_SIZE = 8192;

	/**
	 * Publish granularity for update = 8192 x 128 = 1MB
	 */
	static protected final int PUBLISH_UPDATE_GRANULARITY = 128;

	/**
	 * Publish granularity for main = 1MB x 10 = 10MB
	 */
	static protected final int PUBLISH_MAIN_GRANULARITY = PUBLISH_UPDATE_GRANULARITY * 10;

	/**
	 * Timeout in seconds
	 */
	static protected final int TIMEOUT_S = 15;

	/**
	 * From URL
	 */
	@Nullable
	protected String fromUrl;

	/**
	 * To file
	 */
	@Nullable
	protected String toFile;

	/**
	 * Cancel
	 */
	protected boolean cancel;

	/**
	 * Exception while executing
	 */
	@Nullable
	protected Exception exception;

	/**
	 * Constructor
	 */
	public SimpleDownloaderService()
	{
		super();
		this.cancel = false;
		this.exception = null;
	}

	// M A I N   E N T R Y

	@Override
	protected void onHandleWork(@Nullable final Intent intent)
	{
		if (intent != null)
		{
			final String action = intent.getAction();
			if (SimpleDownloaderService.ACTION_DOWNLOAD.equals(action))
			{
				/*
				final IntentFilter filter = new IntentFilter(StopReceiver.ACTION_STOP);
				filter.addCategory(Intent.CATEGORY_DEFAULT);
				StopReceiver receiver = new StopReceiver();
				registerReceiver(receiver, filter);
				*/

				// arguments
				int id = unmarshal(intent);

				// fire start event
				broadcast(MAIN_INTENT_FILTER, EVENT, EVENT_START);

				// do job
				try
				{
					job();
					Log.d(TAG, "Completed successfully");
					broadcast(MAIN_INTENT_FILTER, EVENT, EVENT_FINISH, EVENT_FINISH_ID, id, EVENT_FINISH_RESULT, true);
				}
				catch (@NonNull final InterruptedException ie)
				{
					this.exception = ie;
					cleanup();
					Log.d(TAG, "Interrupted while downloading, " + ie.getMessage());
					broadcast(MAIN_INTENT_FILTER, EVENT, EVENT_FINISH, EVENT_FINISH_ID, id, EVENT_FINISH_RESULT, false, EVENT_FINISH_EXCEPTION, exception.getMessage());
				}
				catch (@NonNull final SocketTimeoutException ste)
				{
					this.exception = ste;
					cleanup();
					Log.d(TAG, "Timeout while downloading, " + ste.getMessage());
					broadcast(MAIN_INTENT_FILTER, EVENT, EVENT_FINISH, EVENT_FINISH_ID, id, EVENT_FINISH_RESULT, false, EVENT_FINISH_EXCEPTION, exception.getMessage());
				}
				catch (@NonNull final Exception e)
				{
					this.exception = e;
					cleanup();
					Log.e(TAG, "Exception while downloading, " + e.getMessage());
					broadcast(MAIN_INTENT_FILTER, EVENT, EVENT_FINISH, EVENT_FINISH_ID, id, EVENT_FINISH_RESULT, false, EVENT_FINISH_EXCEPTION, exception.getMessage());
				}
			}
		}
	}

	// J O B

	/**
	 * Download job
	 */
	protected void job() throws Exception
	{
		// first
		prerequisite();
		final PowerManager.WakeLock wakelock = wakelock();

		long date = -1;
		long size;

		final File outFile = new File(this.toFile + ".part");
		try
		{
			// connection
			final URL url = new URL(this.fromUrl);
			Log.d(TAG, "Get " + url.toString());
			final URLConnection connection = url.openConnection();
			connection.setConnectTimeout(TIMEOUT_S * 1000);

			// connect
			Log.d(TAG, "Connecting");
			connection.connect();
			Log.d(TAG, "Connected");

			// getting file length
			// expect HTTP 200 OK, so we don't mistakenly save error report instead of the file
			if (connection instanceof HttpURLConnection)
			{
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
				{
					final String message = "server returned HTTP " + httpConnection.getResponseCode() + " " + httpConnection.getResponseMessage();
					throw new RuntimeException(message);
				}
				date = httpConnection.getLastModified(); // new Date(date));
				size = httpConnection.getContentLength();
			}
			else
			{
				size = connection.getContentLength();
			}

			try (InputStream input = new BufferedInputStream(connection.getInputStream(), CHUNK_SIZE); OutputStream output = new FileOutputStream(outFile))
			{
				copyStreams(input, output, size);
			}
		}
		finally
		{
			wakelock.release();
		}

		// install
		install(outFile, date, size);
		Settings.recordDbSource(this, this.fromUrl, date, size);
	}

	/**
	 * Prerequisite
	 */
	protected void prerequisite()
	{
		if (this.toFile == null)
		{
			return;
		}

		final File dir = new File(this.toFile).getParentFile();
		if (dir != null && !dir.exists())
		{
			//noinspection ResultOfMethodCallIgnored
			dir.mkdirs();
		}
	}

	/**
	 * Unmarshal arguments from intent
	 *
	 * @param intent intent passed to service
	 * @return unmarshalled id
	 */
	protected int unmarshal(@NonNull final Intent intent)
	{
		// arguments
		this.fromUrl = intent.getStringExtra(SimpleDownloaderService.ARG_FROM_URL);
		this.toFile = intent.getStringExtra(SimpleDownloaderService.ARG_TO_FILE);

		// id
		return intent.getIntExtra(SimpleDownloaderService.ARG_CODE, 0);
	}

	/**
	 * Copy streams or consume input stream
	 *
	 * @param input  input stream
	 * @param output output stream
	 * @param total  expected total length
	 * @throws InterruptedException interrupted exception
	 * @throws IOException          io exception
	 */
	protected void copyStreams(final InputStream input, @Nullable final OutputStream output, final long total) throws InterruptedException, IOException
	{
		// copy streams
		final byte[] buffer = new byte[1024];
		long downloaded = 0;
		int chunks = 0;
		int count;
		while ((count = input.read(buffer)) != -1)
		{
			downloaded += count;

			// publishing the progress
			if ((chunks % PUBLISH_MAIN_GRANULARITY) == 0)
			{
				broadcast(MAIN_INTENT_FILTER, EVENT, EVENT_UPDATE, EVENT_UPDATE_DOWNLOADED, downloaded, EVENT_UPDATE_TOTAL, total);
			}
			if ((chunks % PUBLISH_UPDATE_GRANULARITY) == 0)
			{
				broadcast(UPDATE_INTENT_FILTER, EVENT, EVENT_UPDATE, EVENT_UPDATE_DOWNLOADED, downloaded, EVENT_UPDATE_TOTAL, total);
			}
			chunks++;

			// writing data toFile file
			if (output != null)
			{
				output.write(buffer, 0, count);
			}

			// interrupted
			if (Thread.interrupted())
			{
				final InterruptedException ie = new InterruptedException("interrupted");
				this.exception = ie;
				throw ie;
			}

			if (this.cancel)
			{
				throw new InterruptedException("cancelled");
			}
		}
		if (output != null)
		{
			output.flush();
		}
	}

	/**
	 * Install
	 *
	 * @param outFile temporary file
	 * @param date    date stamp
	 * @param size    expected size
	 */
	protected void install(final File outFile, final long date, final long size)
	{
		// rename
		if (this.toFile != null)
		{
			final File newFile = new File(this.toFile);
			boolean success = false;
			if (outFile.exists())
			{
				if (newFile.exists())
				{
					//noinspection ResultOfMethodCallIgnored
					newFile.delete();
				}
				success = outFile.renameTo(newFile);
				if (date != -1)
				{
					//noinspection ResultOfMethodCallIgnored
					newFile.setLastModified(date);
				}
				if (size != -1 && newFile.length() != size)
				{
					throw new RuntimeException("Size do not match");
				}
			}
			Log.d(TAG, "Rename " + outFile + " to " + newFile + ' ' + success);
		}
	}

	/**
	 * Cleanup
	 */
	protected void cleanup()
	{
		if (this.toFile != null)
		{
			File file = new File(this.toFile);
			if (file.exists())
			{
				//noinspection ResultOfMethodCallIgnored
				file.delete();
			}
			file = new File(this.toFile + ".part");
			if (file.exists())
			{
				//noinspection ResultOfMethodCallIgnored
				file.delete();
			}
		}
	}

	/**
	 * Wake lock
	 */
	protected PowerManager.WakeLock wakelock()
	{
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		assert pm != null;
		final PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "org.sqlunet:DownloaderService");
		wakelock.acquire(30 * 60 * 1000L /*30 minutes*/);
		return wakelock;
	}

	// F I R E   E V E N T S

	/**
	 * Broadcast message
	 *
	 * @param intentFilter intent filter
	 * @param args         arguments
	 */
	protected void broadcast(final String intentFilter, @NonNull final Object... args)
	{
		final Intent broadcastIntent = new Intent(intentFilter);
		broadcastIntent.setPackage(this.getPackageName());
		for (int i = 0; i < args.length; i = i + 2)
		{
			final String key = (String) args[i];
			final Object value = args[i + 1];

			// string
			if (value instanceof String)
			{
				broadcastIntent.putExtra(key, (String) value);
			}
			// int
			else if (value instanceof Integer)
			{
				broadcastIntent.putExtra(key, (int) value);
			}
			// long
			else if (value instanceof Long)
			{
				broadcastIntent.putExtra(key, (long) value);
			}
			// boolean
			else if (value instanceof Boolean)
			{
				broadcastIntent.putExtra(key, (boolean) value);
			}
		}
		LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
	}

	/**
	 * Cancel
	 */
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		this.cancel = true;
	}

	// S T A R T

	/**
	 * Convenience method for enqueuing work in to this service.
	 */
	public static void enqueueWork(@NonNull final Context context, @NonNull final Intent work)
	{
		enqueueWork(context, SimpleDownloaderService.class, JOB_ID, work);
	}

	// A L T
	/*
	public class StopReceiver extends BroadcastReceiver
	{
		public static final String ACTION_STOP = "stop";

		@Override
		public void onReceive(final Context context, Intent intent)
		{
			SimpleDownloaderService.this.cancel = true;
		}
	}

	static public void kill(final Context context)
	{
		final Intent intent = new Intent();
		intent.setAction(StopReceiver.ACTION_STOP);
		LocalBroadcastManager.getInstance(this.context).sendBroadcast(intent);
	}
	*/
}
