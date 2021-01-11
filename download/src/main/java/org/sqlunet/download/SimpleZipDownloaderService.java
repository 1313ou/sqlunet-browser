/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * SimpleDownloader service
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SimpleZipDownloaderService extends SimpleDownloaderService
{
	static private final String TAG = "SimpleZipDownloaderS";

	static public final String ARG_ENTRY = "entry";

	/**
	 * Zip entry
	 */
	@Nullable
	private String entry;

	/**
	 * Constructor
	 */
	public SimpleZipDownloaderService()
	{
		super();
	}

	// J O B

	/**
	 * Download job
	 */
	@Override
	protected void job() throws Exception
	{
		// first
		prerequisite();
		final PowerManager.WakeLock wakelock = wakelock();

		// dest file
		final File outFile = new File(this.toFile + ".part");
		long date = -1;
		long size = -1;
		@SuppressWarnings("UnusedAssignment") long zDate = -1;
		@SuppressWarnings("UnusedAssignment") long zSize = -1;
		String zEtag = null;
		String zVersion = null;
		String zStaticVersion = null;

		boolean done = false;
		HttpURLConnection httpConnection = null;
		try
		{
			// connection
			final URL url = new URL(this.fromUrl);
			Log.d(TAG, "Get " + url.toString());
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(TIMEOUT_S * 1000);
			// connection.addRequestProperty("If-None-Match", "*"); // returns HTTP 304 Not Modified

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
					// zDate = connection.getLastModified(); // new Date(date));
					// zSize = connection.getContentLength();
					zEtag = connection.getHeaderField("etag");
					zVersion = connection.getHeaderField("x-version");
					zStaticVersion = connection.getHeaderField("x-static-version");

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

			// getting zip file length
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
			zDate = connection.getLastModified(); // new Date(date));
			zSize = connection.getContentLength();

			if (zEtag == null)
			{
				zEtag = connection.getHeaderField("etag");
			}
			if (zVersion == null)
			{
				zVersion = connection.getHeaderField("x-version");
			}
			if (zStaticVersion == null)
			{
				zStaticVersion = connection.getHeaderField("x-static-version");
			}

			// streams
			try (ZipInputStream zInput = new ZipInputStream(connection.getInputStream()); OutputStream output = new FileOutputStream(outFile))
			{
				// get the entry
				ZipEntry entry;
				while ((entry = zInput.getNextEntry()) != null)
				{
					if (!entry.isDirectory())
					{
						final String entryName = entry.getName();
						if (entryName.equals(this.entry))
						{
							size = entry.getSize();
							date = entry.getTime();

							// copy
							copyStreams(zInput, output, size);
							zInput.closeEntry();
							done = true;
							break;
						}
						//else
						// consume
						//{
						//	copyStreams(zInput, null, zSize);
						//}
					}
					zInput.closeEntry();
				}
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

		// rename
		if (done)
		{
			install(outFile, date, size);
			Settings.recordDbSource(this, this.fromUrl, zDate, zSize, zEtag, zVersion, zStaticVersion);
		}
		else
		{
			throw new RuntimeException("Entry not found " + this.entry);
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
		int id = super.unmarshal(intent);

		// entry argument
		this.entry = intent.getStringExtra(SimpleZipDownloaderService.ARG_ENTRY);
		if (this.entry == null)
		{
			if (this.fromUrl != null)
			{
				int lastSlash = this.fromUrl.lastIndexOf('/');
				if (lastSlash != -1)
				{
					this.entry = this.fromUrl.substring(lastSlash + 1);
					if (this.entry.endsWith(".zip"))
					{
						this.entry = this.entry.substring(0, this.entry.length() - 4);
					}
				}
			}
		}
		return id;
	}

	// S T A R T

	/**
	 * Convenience method for enqueuing work in to this service.
	 */
	public static void enqueueWork(@NonNull final Context context, @NonNull final Intent work)
	{
		enqueueWork(context, SimpleZipDownloaderService.class, JOB_ID, work);
	}
}
