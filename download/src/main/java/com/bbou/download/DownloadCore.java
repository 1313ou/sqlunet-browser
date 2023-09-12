/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.download;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.function.BiConsumer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Download delegate
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DownloadCore
{
	static private final String TAG = "DownloadDelegate";

	public static class DownloadData
	{
		public final String fromUrl;
		public final String toFile;
		public final long date;
		public final long size;
		public final String etag;
		public final String version;
		public final String staticVersion;

		public DownloadData(final String fromUrl, final String toFile, final long date, final long size, final String etag, final String version, final String staticVersion)
		{
			this.fromUrl = fromUrl;
			this.toFile = toFile;
			this.date = date;
			this.size = size;
			this.etag = etag;
			this.version = version;
			this.staticVersion = staticVersion;
		}
	}

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
	 * Rename source
	 */
	@Nullable
	protected String renameFrom;

	/**
	 * Rename dest
	 */
	@Nullable
	protected String renameTo;

	/**
	 * Cancel
	 */
	protected boolean cancel;

	/**
	 * Progress consumer
	 */
	@NonNull
	protected final BiConsumer<Long, Long> progressConsumer;

	/**
	 * Constructor
	 *
	 * @param progressConsumer progress consumer
	 */
	public DownloadCore(@NonNull final BiConsumer<Long, Long> progressConsumer)
	{
		this.progressConsumer = progressConsumer;
	}

	// W O R K

	/**
	 * Work
	 *
	 * @param fromUrl    source url
	 * @param toFile     destination file
	 * @param renameFrom rename source
	 * @param renameTo   rename destination
	 * @return download data
	 */
	public DownloadData work(@NonNull final String fromUrl, @NonNull final String toFile, @Nullable final String renameFrom, @Nullable final String renameTo, @Nullable final String unused) throws Exception
	{
		// arguments
		this.fromUrl = fromUrl;
		this.toFile = toFile;
		this.renameFrom = renameFrom;
		this.renameTo = renameTo;

		// do job
		this.cancel = false;
		try
		{
			DownloadData data = job();
			Log.d(TAG, "Completed");
			return data;
		}
		catch (@NonNull final Exception e)
		{
			cleanup();
			throw e;
		}
	}

	// C O R E W O R K

	/**
	 * Cancel
	 */
	public void cancel()
	{
		this.cancel = true;
	}

	/**
	 * Download job
	 *
	 * @return download data
	 */
	protected DownloadData job() throws Exception
	{
		// first
		prerequisite();

		long date;
		long size;
		String etag = null;
		String version = null;
		String staticVersion = null;

		assert this.toFile != null;
		final File outFile = new File(this.toFile + ".part");
		final File tempOutFile = new File(this.toFile);
		HttpURLConnection httpConnection = null;
		try
		{
			// connection
			final URL url = new URL(this.fromUrl);
			Log.d(TAG, "Getting " + url);
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

					// date = connection.getLastModified();
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

			//noinspection IOStreamConstructor
			try ( //
			      InputStream is = new BufferedInputStream(connection.getInputStream(), CHUNK_SIZE); //
			      OutputStream os = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ? Files.newOutputStream(tempOutFile.toPath()) : new FileOutputStream(tempOutFile)) //
			{
				copyStreams(is, os, size);
			}

			// rename temp to target
			//noinspection ResultOfMethodCallIgnored
			tempOutFile.renameTo(outFile);

			// optional rename
			if (this.renameFrom!= null && this.renameTo != null)
			{
				File renamed = new File(outFile.getParent(), this.renameTo);
				//noinspection ResultOfMethodCallIgnored
				outFile.renameTo(renamed);
			}

			// date
			setDate(outFile, date);
		}
		finally
		{
			if (httpConnection != null)
			{
				httpConnection.disconnect();
			}
		}
		Log.d(TAG, "Downloaded " + outFile.getAbsolutePath());

		// install
		Log.d(TAG, "Installing " + outFile.getAbsolutePath());
		install(outFile, date, size);
		return new DownloadData(this.fromUrl, this.toFile, date, size, etag, version, staticVersion);
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
	 * Copy streams or consume input stream
	 *
	 * @param is    input stream
	 * @param os    output stream
	 * @param total expected total length
	 * @throws IOException io exception
	 */
	protected void copyStreams(@NonNull final InputStream is, @Nullable final OutputStream os, final long total) throws IOException, InterruptedException
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
				progressConsumer.accept(downloaded, total);
			}
			if ((chunks % PUBLISH_UPDATE_GRANULARITY) == 0)
			{
				progressConsumer.accept(downloaded, total);
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
				throw new InterruptedException("interrupted");
			}

			if (this.cancel)
			{
				throw new InterruptedException("cancelled");
			}
		}
		if (os != null)
		{
			os.flush();
		}
	}

	// U T I L S

	/**
	 * Install
	 *
	 * @param outFile temporary file
	 * @param date    date stamp
	 * @param size    expected size
	 */
	protected void install(@NonNull final File outFile, final long date, final long size)
	{
		if (this.toFile != null)
		{
			final File newFile = new File(this.toFile);
			boolean success = false;
			if (outFile.exists())
			{
				// rename
				if (newFile.exists())
				{
					//noinspection ResultOfMethodCallIgnored
					newFile.delete();
				}
				success = outFile.renameTo(newFile);

				// date
				setDate(newFile, date);

				// size check
				if (size != -1 && newFile.length() != size)
				{
					throw new RuntimeException("Size do not match");
				}
			}
			Log.d(TAG, "Renamed " + outFile + " to " + newFile + ' ' + success);
		}
	}

	/**
	 * Set date
	 *
	 * @param outFile file
	 * @param date    date stamp
	 */
	protected void setDate(@NonNull final File outFile, final long date)
	{
		if (date != -1)
		{
			//noinspection ResultOfMethodCallIgnored
			outFile.setLastModified(date);
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
}
