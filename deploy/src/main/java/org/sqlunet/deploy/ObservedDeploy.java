/*
 * Copyright (c) 2020. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.deploy;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import org.sqlunet.concurrency.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public class ObservedDeploy
{
	static private final String TAG = "ObservedDeploy";

	/**
	 * Buffer size
	 */
	static private final int CHUNK_SIZE = 1024;

	@FunctionalInterface
	public interface Publisher
	{
		void publish(long current, long total);
	}

	/**
	 * Copy from file
	 *
	 * @param srcUri      source uri
	 * @param resolver    content resolver
	 * @param destFile    destination file
	 * @param task        async task
	 * @param publisher   publisher
	 * @param publishRate publish rate
	 * @return true if successful
	 */
	static synchronized public boolean copyFromUri(@NonNull final Uri srcUri, @NonNull final ContentResolver resolver, @NonNull final String destFile, @NonNull final Task<Uri, Number, Boolean> task, @NonNull final Publisher publisher, final int publishRate)
	{
		Log.d(ObservedDeploy.TAG, "Copy from " + srcUri + " to " + destFile);

		try (InputStream in = resolver.openInputStream(srcUri); FileOutputStream out = new FileOutputStream(destFile))
		{
			final byte[] buffer = new byte[CHUNK_SIZE];
			long byteCount = 0;
			int chunkCount = 0;
			int readCount;
			while ((readCount = in.read(buffer)) != -1)
			{
				// write
				out.write(buffer, 0, readCount);

				// count
				byteCount += readCount;
				chunkCount++;

				// publish
				if ((chunkCount % publishRate) == 0)
				{
					publisher.publish(byteCount, -1);
				}

				// cancel hook
				if (task.isCancelled())
				{
					//noinspection BreakStatement
					break;
				}
			}
			publisher.publish(byteCount, -1);
			return true;
		}
		catch (@NonNull final Exception e)
		{
			Log.e(TAG, "While copying", e);
		}
		return false;
	}

	/**
	 * Copy from file
	 *
	 * @param srcFile     source file
	 * @param destFile    destination file
	 * @param task        async task
	 * @param publisher   publisher
	 * @param publishRate publish rate
	 * @return true if successful
	 */
	static synchronized public boolean copyFromFile(@NonNull final String srcFile, @NonNull final String destFile, @NonNull final Task<String, Number, Boolean> task, @NonNull final Publisher publisher, final int publishRate)
	{
		Log.d(ObservedDeploy.TAG, "Copy from " + srcFile + " to " + destFile);

		File sourceFile = new File(srcFile);
		long length = sourceFile.length();

		try (FileInputStream in = new FileInputStream(srcFile); FileOutputStream out = new FileOutputStream(destFile))
		{
			final byte[] buffer = new byte[CHUNK_SIZE];
			long byteCount = 0;
			int chunkCount = 0;
			int readCount;
			while ((readCount = in.read(buffer)) != -1)
			{
				// write
				out.write(buffer, 0, readCount);

				// count
				byteCount += readCount;
				chunkCount++;

				// publish
				if ((chunkCount % publishRate) == 0)
				{
					publisher.publish(byteCount, length);
				}

				// cancel hook
				if (task.isCancelled())
				{
					//noinspection BreakStatement
					break;
				}
			}
			publisher.publish(byteCount, length);
			return true;
		}
		catch (@NonNull final Exception e)
		{
			Log.e(TAG, "While copying", e);
		}
		return false;
	}

	/**
	 * Unzip entries from archive file
	 *
	 * @param srcArchive  source archive file
	 * @param destDir     destination dir
	 * @param task        async task
	 * @param publisher   publisher
	 * @param publishRate publish rate
	 * @return true if successful
	 */
	static synchronized public boolean unzipFromArchiveFile(@NonNull final String srcArchive, @NonNull final String destDir, @NonNull final Task<String, Number, Boolean> task, @NonNull final Publisher publisher, final int publishRate)
	{
		Log.d(ObservedDeploy.TAG, "Expand from " + srcArchive + " to " + destDir);

		ZipFile zipFile = null;

		try
		{
			zipFile = new ZipFile(srcArchive);
			Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
			while (zipEntries.hasMoreElements())
			{
				final ZipEntry zipEntry = zipEntries.nextElement();
				//Log.d(Deploy.TAG, "Expand zip entry  " + zipEntry.getName());
				if (zipEntry.isDirectory())
				{
					continue;
				}

				// out
				final File outFile = new File(destDir + '/' + zipEntry.getName());
				// Log.d(TAG, outFile + " exist=" + outFile.exists());

				// create all non exists folders else you will hit FileNotFoundException for compressed folder
				final String parent = outFile.getParent();
				if (parent != null)
				{
					final File dir = new File(parent);
					boolean created = dir.mkdirs();
					Log.d(TAG, dir + " created=" + created + " exists=" + dir.exists());
				}

				// input
				try (InputStream in = zipFile.getInputStream(zipEntry); FileOutputStream out = new FileOutputStream(outFile))
				{
					long length = zipEntry.getSize();

					// copy
					final byte[] buffer = new byte[CHUNK_SIZE];
					long byteCount = 0;
					int chunkCount = 0;
					int readCount;
					while ((readCount = in.read(buffer)) != -1)
					{
						// write
						out.write(buffer, 0, readCount);

						// count
						byteCount += readCount;
						chunkCount++;

						// publish
						if ((chunkCount % publishRate) == 0)
						{
							publisher.publish(byteCount, length);
						}

						// cancel hook
						if (task.isCancelled())
						{
							//noinspection BreakStatement
							break;
						}
					}
					out.flush();
					publisher.publish(byteCount, length);
				}
				catch (IOException e1)
				{
					Log.e(TAG, "While executing from archive", e1);
				}

				if (outFile.exists())
				{
					Log.d(TAG, outFile + " exist=" + outFile.exists());
					long stamp = zipEntry.getTime();
					//noinspection ResultOfMethodCallIgnored
					outFile.setLastModified(stamp);
				}
			}
			return true;
		}
		catch (IOException e1)
		{
			Log.e(TAG, "While executing from archive", e1);
		}
		finally
		{
			if (zipFile != null)
			{
				try
				{
					zipFile.close();
				}
				catch (IOException e)
				{
					Log.e(TAG, "While closing archive", e);
				}
			}
		}
		return false;
	}

	/**
	 * Unzip entries from archive uri
	 *
	 * @param srcUri      source archive uri
	 * @param destDir     destination dir
	 * @param task        async task
	 * @param publisher   publisher
	 * @param publishRate publish rate
	 * @return true if successful
	 */
	static synchronized public boolean unzipFromArchiveUri(@NonNull final Uri srcUri, @NonNull final ContentResolver resolver, @NonNull final String destDir, @NonNull final Task<Uri, Number, Boolean> task, @NonNull final Publisher publisher, final int publishRate)
	{
		Log.d(ObservedDeploy.TAG, "Expand from " + srcUri + " to " + destDir);

		try (ZipInputStream zipIn = (ZipInputStream) resolver.openInputStream(srcUri))
		{
			ZipEntry zipEntry;
			while ((zipEntry = zipIn.getNextEntry()) != null)
			{
				//Log.d(Deploy.TAG, "Expand zip entry  " + zipEntry.getName());
				if (zipEntry.isDirectory())
				{
					continue;
				}

				// out
				final File outFile = new File(destDir + '/' + zipEntry.getName());
				// Log.d(TAG, outFile + " exist=" + outFile.exists());

				// create all non exists folders else you will hit FileNotFoundException for compressed folder
				final String parent = outFile.getParent();
				if (parent != null)
				{
					final File dir = new File(parent);
					boolean created = dir.mkdirs();
					Log.d(TAG, dir + " created=" + created + " exists=" + dir.exists());
				}

				// input
				try (FileOutputStream out = new FileOutputStream(outFile))
				{
					long length = zipEntry.getSize();

					// copy
					final byte[] buffer = new byte[CHUNK_SIZE];
					long byteCount = 0;
					int chunkCount = 0;
					int readCount;
					while ((readCount = zipIn.read(buffer)) != -1)
					{
						// write
						out.write(buffer, 0, readCount);

						// count
						byteCount += readCount;
						chunkCount++;

						// publish
						if ((chunkCount % publishRate) == 0)
						{
							publisher.publish(byteCount, length);
						}

						// cancel hook
						if (task.isCancelled())
						{
							//noinspection BreakStatement
							break;
						}
					}
					out.flush();
					publisher.publish(byteCount, length);
				}
				catch (IOException e1)
				{
					Log.e(TAG, "While executing from archive", e1);
				}

				if (outFile.exists())
				{
					Log.d(TAG, outFile + " exist=" + outFile.exists());
					long stamp = zipEntry.getTime();
					//noinspection ResultOfMethodCallIgnored
					outFile.setLastModified(stamp);
				}
			}
			return true;
		}
		catch (IOException e1)
		{
			Log.e(TAG, "While executing from archive", e1);
		}
		return false;
	}

	/**
	 * Unzip entry from archive file
	 *
	 * @param srcArchive  source archive file
	 * @param srcEntry    source entry
	 * @param destFile    destination file
	 * @param task        async task
	 * @param publisher   publisher
	 * @param publishRate publish rate
	 * @return true if successful
	 */
	static synchronized public boolean unzipEntryFromArchiveFile(@NonNull final String srcArchive, @NonNull final String srcEntry, @NonNull final String destFile, @NonNull final Task<String, Number, Boolean> task, @NonNull final Publisher publisher, final int publishRate)
	{
		Log.d(ObservedDeploy.TAG, "Expand from " + srcArchive + " (entry " + srcEntry + ") to " + destFile);

		ZipFile zipFile = null;
		try
		{
			zipFile = new ZipFile(srcArchive);

			// entry
			final ZipEntry zipEntry = zipFile.getEntry(srcEntry);
			if (zipEntry == null)
			{
				throw new IOException("Zip entry not found " + srcEntry);
			}

			// out
			final File outFile = new File(destFile);
			// Log.d(TAG, outFile + " exist=" + outFile.exists());

			// create all non exists folders else you will hit FileNotFoundException for compressed folder
			final String parent = outFile.getParent();
			if (parent != null)
			{
				final File dir = new File(parent);
				boolean created = dir.mkdirs();
				Log.d(TAG, dir + " created=" + created + " exists=" + dir.exists());
			}

			// unzip
			try (InputStream in = zipFile.getInputStream(zipEntry); FileOutputStream out = new FileOutputStream(outFile))
			{
				long length = zipEntry.getSize();

				final byte[] buffer = new byte[CHUNK_SIZE];
				long byteCount = 0;
				int chunkCount = 0;
				int readCount;
				while ((readCount = in.read(buffer)) != -1)
				{
					// write
					out.write(buffer, 0, readCount);

					// count
					byteCount += readCount;
					chunkCount++;

					// publish
					if ((chunkCount % publishRate) == 0)
					{
						publisher.publish(byteCount, length);
					}

					// cancel hook
					if (task.isCancelled())
					{
						//noinspection BreakStatement
						break;
					}
				}
				out.flush();
				publisher.publish(byteCount, length);
			}
			catch (IOException e1)
			{
				Log.e(TAG, "While executing from archive", e1);
			}

			// inherit time stamp and return
			if (outFile.exists())
			{
				Log.d(TAG, outFile + " exist=" + outFile.exists());
				long stamp = zipEntry.getTime();
				//noinspection ResultOfMethodCallIgnored
				outFile.setLastModified(stamp);
				return true;
			}
		}
		catch (IOException e)
		{
			return false;
		}
		finally
		{
			if (zipFile != null)
			{
				try
				{
					zipFile.close();
				}
				catch (IOException e)
				{
					Log.e(TAG, "While closing archive", e);
				}
			}
		}
		return false;
	}

	/**
	 * Unzip entry from archive uri
	 *
	 * @param srcUri      source archive uri
	 * @param srcEntry    source entry
	 * @param destFile    destination file
	 * @param task        async task
	 * @param publisher   publisher
	 * @param publishRate publish rate
	 * @return true if successful
	 */
	static synchronized public boolean unzipEntryFromArchiveUri(@NonNull final Uri srcUri, @NonNull final String srcEntry, @NonNull final ContentResolver resolver, @NonNull final String destFile, @NonNull final Task<Uri, Number, Boolean> task, @NonNull final Publisher publisher, final int publishRate)
	{
		Log.d(ObservedDeploy.TAG, "Expand from " + srcUri + " (entry " + srcEntry + ") to " + destFile);

		try (ZipInputStream zipIn = (ZipInputStream) resolver.openInputStream(srcUri))
		{
			ZipEntry zipEntry;
			while ((zipEntry = zipIn.getNextEntry()) != null)
			{
				//Log.d(Deploy.TAG, "Expand zip entry  " + zipEntry.getName());
				// entry
				if (zipEntry.isDirectory())
				{
					continue;
				}
				if (!zipEntry.getName().equals(srcEntry))
				{
					continue;
				}

				// out
				final File outFile = new File(destFile);
				// Log.d(TAG, outFile + " exist=" + outFile.exists());

				// create all non exists folders else you will hit FileNotFoundException for compressed folder
				final String parent = outFile.getParent();
				if (parent != null)
				{
					final File dir = new File(parent);
					boolean created = dir.mkdirs();
					Log.d(TAG, dir + " created=" + created + " exists=" + dir.exists());
				}

				// unzip
				try (FileOutputStream out = new FileOutputStream(destFile))
				{
					long length = zipEntry.getSize();

					// copy
					final byte[] buffer = new byte[CHUNK_SIZE];
					long byteCount = 0;
					int chunkCount = 0;
					int readCount;
					while ((readCount = zipIn.read(buffer)) != -1)
					{
						// write
						out.write(buffer, 0, readCount);

						// count
						byteCount += readCount;
						chunkCount++;

						// publish
						if ((chunkCount % publishRate) == 0)
						{
							publisher.publish(byteCount, length);
						}

						// cancel hook
						if (task.isCancelled())
						{
							//noinspection BreakStatement
							break;
						}
					}
					out.flush();
					publisher.publish(byteCount, length);
				}
				catch (IOException e1)
				{
					Log.e(TAG, "While executing from archive", e1);
				}

				// inherit time stamp and return
				if (outFile.exists())
				{
					Log.d(TAG, outFile + " exist=" + outFile.exists());
					long stamp = zipEntry.getTime();
					//noinspection ResultOfMethodCallIgnored
					outFile.setLastModified(stamp);
					return true;
				}
			}
		}
		catch (IOException e)
		{
			return false;
		}
		return false;
	}

	/**
	 * MD5 from file
	 *
	 * @param srcFile     source file
	 * @param task        async task
	 * @param publisher   publisher
	 * @param publishRate publish rate
	 * @return true if successful
	 */
	@Nullable
	static synchronized public String md5FromFile(@NonNull final String srcFile, @NonNull final Task<String, Number, String> task, @NonNull final Publisher publisher, final int publishRate)
	{
		Log.d(TAG, "Md5 " + srcFile);
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			File sourceFile = new File(srcFile);
			long length = sourceFile.length();
			try (FileInputStream fis = new FileInputStream(srcFile); DigestInputStream dis = new DigestInputStream(fis, md))
			{
				final byte[] buffer = new byte[CHUNK_SIZE];
				long byteCount = 0;
				int chunkCount = 0;
				@SuppressWarnings("UnusedAssignment") int readCount = 0;

				// read decorated stream (dis) to EOF as normal
				while ((readCount = dis.read(buffer)) != -1)
				{
					// count
					byteCount += readCount;
					chunkCount++;

					// publish
					if ((chunkCount % publishRate) == 0)
					{
						publisher.publish(byteCount, length);
					}

					// cancel hook
					if (task.isCancelled())
					{
						//noinspection BreakStatement
						break;
					}
				}
				byte[] digest = md.digest();
				return Deploy.digestToString(digest);
			}
		}
		catch (@NonNull NoSuchAlgorithmException | IOException e)
		{
			Log.e(TAG, srcFile, e);
			return null;
		}
	}

	/**
	 * MD5 from uri
	 *
	 * @param uri         uri
	 * @param task        async task
	 * @param publisher   publisher
	 * @param publishRate publish rate
	 * @return true if successful
	 */
	@Nullable
	static synchronized public String md5FromUri(@NonNull final Uri uri, @NonNull final ContentResolver resolver, @NonNull final Task<Uri, Number, String> task, @NonNull final Publisher publisher, final int publishRate)
	{
		Log.d(TAG, "Md5 uri " + uri);
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			try (InputStream in = resolver.openInputStream(uri); DigestInputStream dis = new DigestInputStream(in, md))
			{
				final byte[] buffer = new byte[CHUNK_SIZE];
				long byteCount = 0;
				int chunkCount = 0;
				@SuppressWarnings("UnusedAssignment") int readCount = 0;

				// read decorated stream (dis) to EOF as normal
				while ((readCount = dis.read(buffer)) != -1)
				{
					// count
					byteCount += readCount;
					chunkCount++;

					// publish
					if ((chunkCount % publishRate) == 0)
					{
						publisher.publish(byteCount, -1);
					}

					// cancel hook
					if (task.isCancelled())
					{
						//noinspection BreakStatement
						break;
					}
				}
				byte[] digest = md.digest();
				return Deploy.digestToString(digest);
			}
		}
		catch (@NonNull NoSuchAlgorithmException | IOException e)
		{
			Log.e(TAG, "input stream", e);
			return null;
		}
	}
}
