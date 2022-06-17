/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * Download service
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DownloadService extends JobIntentService
{
	static private final String TAG = "DownloadS";

	static public final String MAIN_INTENT_FILTER = "intent_filter_downloader_main";

	static public final String UPDATE_INTENT_FILTER = "intent_filter_downloader_update";

	static public final String ACTION_DOWNLOAD = "download";

	public static final String ACTION_DOWNLOAD_CANCEL = "cancel_download";

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

	static public final String EVENT_FINISH_CAUSE = "finish_cause";

	/**
	 * Unique job ID for this service.
	 */
	static final int JOB_ID = 1313;

	/**
	 * Buffer size
	 */
	static protected final int CHUNK_SIZE = 8192;

	/**
	 * Publish granularity (number of chunks) for update = 1MB x 10 = 10MB
	 * 1MB = 8192 chunk size x 128 chunks
	 */
	static protected final int PUBLISH_UPDATE_GRANULARITY = 128 * 10;

	/**
	 * Publish granularity for (number of chunks) main = 1MB x 16 = 16MB
	 * 1MB = 8192 chunk size x 128 chunks
	 */
	static protected final int PUBLISH_MAIN_GRANULARITY = 128 * 16;

	/**
	 * Timeout in seconds
	 */
	static protected final int TIMEOUT_S = 15;

	/**
	 * Intent filter
	 */
	static private final IntentFilter intentFilter = new IntentFilter();

	static
	{
		intentFilter.addAction(ACTION_DOWNLOAD);
		intentFilter.addAction(ACTION_DOWNLOAD_CANCEL);
		intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
	}

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
	 * Cancel broadcast receiver
	 */
	private final BroadcastReceiver receiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(final Context context, final Intent intent)
		{

			String action = intent.getAction();
			//Log.d(TAG, "Broadcast receiver caught " + action);
			if (action.equals(ACTION_DOWNLOAD_CANCEL))
			{
				Log.d(TAG, "Cancel flagged through broadcast");
				DownloadService.this.cancel = true;
			}
		}
	};

	/**
	 * Constructor
	 */
	public DownloadService()
	{
		super();
	}

	// L I F E C Y C L E

	/**
	 * OnCreate
	 */
	@Override
	public void onCreate()
	{
		super.onCreate();

		// cancel receiver
		LocalBroadcastManager.getInstance(this).registerReceiver(this.receiver, DownloadService.intentFilter);
		Iterator<String> it = intentFilter.actionsIterator();
		Log.d(TAG, "Register " + this.receiver + " filter=" + it.next() + " filter=" + it.next());
	}

	/**
	 * OnDestroy
	 * Called by context.stopService()
	 */
	@Override
	public void onDestroy()
	{
		super.onDestroy();

		// cancel flag
		Log.d(TAG, "Cancel flagged in onDestroy");
		this.cancel = true;

		// unregister cancel receiver
		LocalBroadcastManager.getInstance(this).unregisterReceiver(this.receiver);
		Log.d(TAG, "Unregister " + this.receiver);
	}

	// M A I N   E N T R Y

	@Override
	protected void onHandleWork(@Nullable final Intent intent)
	{
		if (intent != null)
		{
			final String action = intent.getAction();
			if (DownloadService.ACTION_DOWNLOAD.equals(action))
			{
				// arguments
				int id = unmarshal(intent);

				// fire start event
				broadcast(MAIN_INTENT_FILTER, //
						EVENT, EVENT_START);

				// do job
				this.cancel = false;
				this.exception = null;
				try
				{
					job();
					Log.d(TAG, "Completed cancel=" + this.cancel);
					broadcast(MAIN_INTENT_FILTER, //
							EVENT, EVENT_FINISH, //
							EVENT_FINISH_ID, id, //
							EVENT_FINISH_RESULT, !this.cancel);
				}
				catch (@NonNull final InterruptedException ie)
				{
					this.exception = ie;
					cleanup();
					Log.d(TAG, "Interrupted while downloading, " + ie.getMessage());
					broadcast(MAIN_INTENT_FILTER, //
							EVENT, EVENT_FINISH, //
							EVENT_FINISH_ID, id, //
							EVENT_FINISH_RESULT, false, //
							EVENT_FINISH_EXCEPTION, exception.getMessage());
				}
				catch (@NonNull final SocketTimeoutException ste)
				{
					this.exception = ste;
					cleanup();
					Log.d(TAG, "Timeout while downloading, " + ste.getMessage());
					broadcast(MAIN_INTENT_FILTER, //
							EVENT, EVENT_FINISH, //
							EVENT_FINISH_ID, id, //
							EVENT_FINISH_RESULT, false, //
							EVENT_FINISH_EXCEPTION, exception.getMessage());
				}
				catch (@NonNull final Exception e)
				{
					this.exception = e;
					cleanup();
					Log.e(TAG, "Exception while downloading, " + e.getMessage());
					broadcast(MAIN_INTENT_FILTER, //
							EVENT, EVENT_FINISH, //
							EVENT_FINISH_ID, id, //
							EVENT_FINISH_RESULT, false, //
							EVENT_FINISH_EXCEPTION, exception.getMessage());
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

		@SuppressWarnings("UnusedAssignment") long date = -1;
		@SuppressWarnings("UnusedAssignment") long size = -1;
		String etag = null;
		String version = null;
		String staticVersion = null;

		final File outFile = new File(this.toFile + ".part");
		HttpURLConnection httpConnection = null;
		try
		{
			// connection
			final URL url = new URL(this.fromUrl);
			Log.d(TAG, "Get " + url);
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(TIMEOUT_S * 1000);

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
					// date = connection.getLastModified(); // new Date(date));
					// size = connection.getContentLength();
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
			Log.d(TAG, "Connecting");
			connection.connect();
			Log.d(TAG, "Connected");

			// getting file length
			// expect HTTP 200 OK, so we don't mistakenly save error report instead of the file
			if (connection instanceof HttpURLConnection)
			{
				if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
				{
					final String message = "server returned HTTP " + httpConnection.getResponseCode() + " " + httpConnection.getResponseMessage();
					throw new RuntimeException(message);
				}
			}

			// headers
			Log.d(TAG, "Headers " + connection.getHeaderFields());
			date = connection.getLastModified(); // new Date(date));
			size = connection.getContentLength();
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

			try ( //
			      InputStream is = new BufferedInputStream(connection.getInputStream(), CHUNK_SIZE); //
			      OutputStream os = new FileOutputStream(outFile)) //
			{
				copyStreams(is, os, size);
			}
		}
		finally
		{
			if (httpConnection != null)
			{
				httpConnection.disconnect();
			}
			wakelock.release();
		}

		// install
		Log.d(TAG, "Download done " + outFile.getAbsolutePath());
		if (!this.cancel)
		{
			Log.d(TAG, "Install " + outFile.getAbsolutePath());
			install(outFile, date, size);
			Settings.recordDbSource(this, this.fromUrl, date, size, etag, version, staticVersion);
		}
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
		this.fromUrl = intent.getStringExtra(DownloadService.ARG_FROM_URL);
		this.toFile = intent.getStringExtra(DownloadService.ARG_TO_FILE);

		// id
		return intent.getIntExtra(DownloadService.ARG_CODE, 0);
	}

	/**
	 * Copy streams or consume input stream
	 *
	 * @param is    input stream
	 * @param os    output stream
	 * @param total expected total length
	 * @throws InterruptedException interrupted exception
	 * @throws IOException          io exception
	 */
	protected void copyStreams(@NonNull final InputStream is, @Nullable final OutputStream os, final long total) throws InterruptedException, IOException
	{
		// copy streams
		final byte[] buffer = new byte[1024];
		long downloaded = 0;
		int chunks = 0;
		int count;
		while ((count = is.read(buffer)) != -1)
		{
			downloaded += count;

			// publishing the progress
			if ((chunks % PUBLISH_MAIN_GRANULARITY) == 0)
			{
				broadcast(MAIN_INTENT_FILTER, //
						EVENT, EVENT_UPDATE, //
						EVENT_UPDATE_DOWNLOADED, downloaded, //
						EVENT_UPDATE_TOTAL, total);
			}
			if ((chunks % PUBLISH_UPDATE_GRANULARITY) == 0)
			{
				broadcast(UPDATE_INTENT_FILTER, //
						EVENT, EVENT_UPDATE, //
						EVENT_UPDATE_DOWNLOADED, downloaded, //
						EVENT_UPDATE_TOTAL, total);
			}
			chunks++;

			// writing data toFile file
			if (os != null)
			{
				os.write(buffer, 0, count);
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
				final InterruptedException ie = new InterruptedException("cancelled");
				this.exception = ie;
				throw ie;
			}
		}
		if (os != null)
		{
			os.flush();
		}
	}

	/**
	 * Install
	 *
	 * @param outFile temporary file
	 * @param date    date stamp
	 * @param size    expected size
	 */
	protected void install(@NonNull final File outFile, final long date, final long size)
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

	// B R O A D C A S T   E V E N T S

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

	// C A N C E L

	/**
	 * Kill by broadcasting cancel action in intent
	 *
	 * @param context context
	 */
	static public void kill(final Context context)
	{
		final Intent broadcastIntent = new Intent();
		broadcastIntent.setPackage(context.getPackageName());
		broadcastIntent.setAction(ACTION_DOWNLOAD_CANCEL);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);

		Log.d(TAG, "Send kill " + broadcastIntent);
		LocalBroadcastManager.getInstance(context).sendBroadcastSync(broadcastIntent);
	}

	// S T A R T

	/**
	 * Convenience method for enqueuing work in to this service.
	 */
	public static void enqueueWork(@NonNull final Context context, @NonNull final Intent work)
	{
		enqueueWork(context, DownloadService.class, JOB_ID, work);
	}
}
