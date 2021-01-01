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
		long zDate;
		long zSize;

		boolean done = false;
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

			// getting zip file length
			// expect HTTP 200 OK, so we don't mistakenly save error report instead of the file
			if (connection instanceof HttpURLConnection)
			{
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
				{
					final String message = "server returned HTTP " + httpConnection.getResponseCode() + " " + httpConnection.getResponseMessage();
					throw new RuntimeException(message);
				}
				zDate = httpConnection.getLastModified(); // new Date(date));
				zSize = httpConnection.getContentLength();
			}
			else
			{
				zDate = connection.getDate();
				zSize = connection.getContentLength();
			}

			// streams
			try (ZipInputStream zinput = new ZipInputStream(connection.getInputStream()); OutputStream output = new FileOutputStream(outFile))
			{
				// get the entry
				ZipEntry entry;
				while ((entry = zinput.getNextEntry()) != null)
				{
					if (!entry.isDirectory())
					{
						final String entryName = entry.getName();
						if (entryName.equals(this.entry))
						{
							size = entry.getSize();
							date = entry.getTime();

							// copy
							copyStreams(zinput, output, size);
							zinput.closeEntry();
							done = true;
							break;
						}
						//else
						// consume
						//{
						//	copyStreams(zinput, null, zsize);
						//}
					}
					zinput.closeEntry();
				}
			}
		}
		finally
		{
			wakelock.release();
		}

		// rename
		if (done)
		{
			install(outFile, date, size);
			Settings.recordDbSource(this, this.fromUrl, zDate, zSize);
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
