package org.sqlunet.browser.config;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
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

/**
 * SimpleDownloader service
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SimpleDownloaderService extends IntentService
{
	static private final String TAG = "SimpleDownloader";

	static public final String INTENT_FILTER = "intent_filter_downloader";

	static public final String ACTION_DOWNLOAD = "download";

	static public final String ARG_FROMURL = "from_url";

	static public final String ARG_TOFILE = "to_file";

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
	 * Buffer size
	 */
	static private final int CHUNK_SIZE = 8192;

	/**
	 * Timeout in seconds
	 */
	static private final int TIMEOUT_S = 15;

	/**
	 * From URL
	 */
	private String fromUrl;

	/**
	 * To file
	 */
	private String toFile;

	/**
	 * Id
	 */
	private int id;

	/**
	 * Cancel
	 */
	private boolean cancel;

	/**
	 * Exception while executing
	 */
	private Exception exception;

	/**
	 * Constructor
	 */
	public SimpleDownloaderService()
	{
		super("SimpleDownloaderService");
		this.cancel = false;
		this.exception = null;
	}

	// M A I N   E N T R Y

	@Override
	protected void onHandleIntent(final Intent intent)
	{
		if (intent != null)
		{
			final String action = intent.getAction();
			if (SimpleDownloaderService.ACTION_DOWNLOAD.equals(action))
			{
				/*
				TODO
				final IntentFilter filter = new IntentFilter(StopReceiver.ACTION_STOP);
				filter.addCategory(Intent.CATEGORY_DEFAULT);
				StopReceiver receiver = new StopReceiver();
				registerReceiver(receiver, filter);
				*/

				// arguments
				this.fromUrl = intent.getStringExtra(SimpleDownloaderService.ARG_FROMURL);
				this.toFile = intent.getStringExtra(SimpleDownloaderService.ARG_TOFILE);
				this.id = intent.getIntExtra(SimpleDownloaderService.ARG_CODE, 0);

				// fire start event
				broadcast(EVENT, EVENT_START);

				// do job
				try
				{
					job();
					Log.d(TAG, "Completed successfully");
					broadcast(EVENT, EVENT_FINISH, EVENT_FINISH_ID, this.id, EVENT_FINISH_RESULT, true);
				}
				catch (final InterruptedException ie)
				{
					this.exception = ie;

					// clean up
					File file = new File(this.toFile);
					if (file.exists())
					{
						//noinspection ResultOfMethodCallIgnored
						file.delete();
					}

					Log.d(TAG, "Interrupted while downloading, " + ie.getMessage());
					broadcast(EVENT, EVENT_FINISH, EVENT_FINISH_ID, this.id, EVENT_FINISH_RESULT, false, EVENT_FINISH_EXCEPTION, exception.getMessage());
				}
				catch (final SocketTimeoutException ste)
				{
					this.exception = ste;
					Log.d(TAG, "Timeout while downloading, " + ste.getMessage());
					broadcast(EVENT, EVENT_FINISH, EVENT_FINISH_ID, this.id, EVENT_FINISH_RESULT, false, EVENT_FINISH_EXCEPTION, exception.getMessage());
				}
				catch (final Exception e)
				{
					this.exception = e;
					Log.e(TAG, "Exception while downloading, " + e.getMessage());
					broadcast(EVENT, EVENT_FINISH, EVENT_FINISH_ID, this.id, EVENT_FINISH_RESULT, false, EVENT_FINISH_EXCEPTION, exception.getMessage());
				}
				finally
				{
					/*
					unregisterReceiver(receiver);
					stopSelf();
					*/
				}
			}
		}
	}

	// J O B

	/**
	 * Download job
	 *
	 * @throws Exception exception
	 */
	@SuppressWarnings("boxing")
	private void job() throws Exception
	{
		prerequisite();

		InputStream input = null;
		OutputStream output = null;
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

			// getting file length
			int total = connection.getContentLength();

			// input stream toFile read file - with 8k buffer
			input = new BufferedInputStream(connection.getInputStream(), CHUNK_SIZE);

			// output stream toFile write file
			output = new FileOutputStream(this.toFile);

			// copy streams
			final byte[] buffer = new byte[1024];
			int downloaded = 0;
			int chunks = 0;
			int count;
			while ((count = input.read(buffer)) != -1)
			{
				downloaded += count;

				// publishing the progress
				if ((chunks % 50) == 0)
				{
					broadcast(EVENT, EVENT_UPDATE, EVENT_UPDATE_DOWNLOADED, downloaded, EVENT_UPDATE_TOTAL, total);
				}
				chunks++;

				// writing data toFile file
				output.write(buffer, 0, count);

				// interrupted
				if (Thread.interrupted())
				{
					final InterruptedException ie = new InterruptedException("interrupted");
					this.exception = ie;
					throw ie;
				}

				if (this.cancel)
				{
					//noinspection BreakStatement
					throw new InterruptedException("cancelled");
				}
			}
			output.flush();
		}
		finally
		{
			if (output != null)
			{
				try
				{
					output.close();
				}
				catch (final IOException e)
				{
					Log.e(TAG, "While closing output", e);
				}
			}
			if (input != null)
			{
				try
				{
					input.close();
				}
				catch (final IOException e)
				{
					this.exception = e;
					Log.e(TAG, "While closing input", e);
				}
			}
		}
	}

	/**
	 * Prerequisite
	 */
	private void prerequisite()
	{
		final File dir = new File(this.toFile).getParentFile();
		if (!dir.exists())
		{
			//noinspection ResultOfMethodCallIgnored
			dir.mkdirs();
		}
	}

	// F I R E   E V E N T S

	private void broadcast(final Object... args)
	{
		final Intent broadcastIntent = new Intent(INTENT_FILTER);
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
			// boolean
			else if (value instanceof Boolean)
			{
				broadcastIntent.putExtra(key, (boolean) value);
			}
			// Handler
			/*
			else if (value instanceof Handler)
			{
				broadcastIntent.putExtra(key, (Handler) value);
			}
			*/
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

	// A L T
	// TODO

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
		context.sendBroadcast(intent);
	}
}
