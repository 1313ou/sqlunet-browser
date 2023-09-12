/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.download;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Download Zip Core
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DownloadZipCore extends DownloadCore
{
	static private final String TAG = "DownloadDelegateZip";

	/**
	 * Zip entry
	 */
	@Nullable
	private String entry;

	/**
	 * Constructor
	 *
	 * @param progressConsumer progress consumer
	 */
	public DownloadZipCore(@NonNull final BiConsumer<Long, Long> progressConsumer)
	{
		super(progressConsumer);
	}

	// W O R K

	/**
	 * Work
	 *
	 * @param fromUrl    zip source	 * @param toFile  destination file
	 *                   url
	 * @param renameFrom rename source
	 * @param renameTo   rename destination
	 * @param entry      zip source entry
	 * @return download data
	 */
	@Override
	public DownloadData work(@NonNull final String fromUrl, @NonNull final String toFile, @Nullable final String renameFrom, @Nullable final String renameTo, @Nullable final String entry) throws Exception
	{
		this.entry = entry;
		return super.work(fromUrl, toFile, renameFrom, renameTo, null);
	}

	// C O R E W O R K

	/**
	 * Download job
	 *
	 * @return download data
	 */
	@Override
	protected DownloadData job() throws Exception
	{
		// first
		prerequisite();

		// dest file
		assert this.toFile != null;
		final File outFile = new File(this.toFile);
		long date;
		long size;
		long zDate;
		long zSize;
		String zEtag = null;
		String zVersion = null;
		String zStaticVersion = null;

		boolean done = false;
		HttpURLConnection httpConnection = null;
		try
		{
			// connection
			final URL url = new URL(this.fromUrl);
			Log.d(TAG, "Getting " + url);
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
			try ( //
			      InputStream is = connection.getInputStream(); //
			      ZipInputStream zis = new ZipInputStream(is) //
			)
			{
				// get the entry
				ZipEntry entry;
				while ((entry = zis.getNextEntry()) != null)
				{
					if (!entry.isDirectory())
					{
						final String entryName = entry.getName();

						// accept if filter unspecified
						if (this.entry == null || entryName.matches(this.entry))
						{
							size = entry.getSize();
							date = entry.getTime();

							// copy
							File tempOutFile = new File(outFile, entryName + ".part");
							File entryOutFile = new File(outFile, entryName);

							//noinspection IOStreamConstructor
							try (OutputStream os = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ? Files.newOutputStream(tempOutFile.toPath()) : new FileOutputStream(tempOutFile))
							{
								copyStreams(zis, os, size);
							}

							// rename temp to target
							//noinspection ResultOfMethodCallIgnored
							tempOutFile.renameTo(entryOutFile);

							// optional rename
							if (entryName.equals(this.renameFrom) && this.renameTo != null)
							{
								File renamed = new File(outFile, this.renameTo);
								//noinspection ResultOfMethodCallIgnored
								entryOutFile.renameTo(renamed);
							}

							// date
							setDate(entryOutFile, date);

							zis.closeEntry();
							done = true;
						}
					}
					zis.closeEntry();
				}
			}
		}
		finally
		{
			if (httpConnection != null)
			{
				httpConnection.disconnect();
			}
		}
		Log.d(TAG, "Downloaded " + outFile.getAbsolutePath());

		// tail
		if (done)
		{
			return new DownloadData(this.fromUrl, this.toFile, zDate, zSize, zEtag, zVersion, zStaticVersion);
		}
		else
		{
			throw new RuntimeException("Entry not found " + this.entry);
		}
	}
}
