/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet;

import android.net.Uri;
import android.util.Log;

import org.sqlunet.concurrency.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public class Deploy
{
	static private final String TAG = "Deploy";

	@FunctionalInterface
	public interface InputStreamGetter
	{
		@NonNull
		InputStream get(String path);
	}

	/**
	 * Fast check data storage (throws RuntimeException if operation cannot succeed)
	 *
	 * @param toDir dir
	 */
	static public void fastCheck(@NonNull final File toDir)
	{
		if (!toDir.exists())
		{
			throw new RuntimeException("Does not exist " + toDir.getAbsolutePath());
		}
		if (!toDir.isDirectory())
		{
			throw new RuntimeException("Is not a directory " + toDir.getAbsolutePath());
		}
		final String[] dirContent = toDir.list();
		if (dirContent == null || dirContent.length == 0)
		{
			throw new RuntimeException("Is empty " + toDir.getAbsolutePath());
		}
	}

	/**
	 * Check data storage (throws RuntimeException if operation cannot succeed)
	 *
	 * @param toDir dir
	 */
	static synchronized public void check(@NonNull final File toDir)
	{
		fastCheck(toDir);
		md5(toDir);
	}

	/**
	 * Deploy to data storage (throws RuntimeException if operation cannot succeed)
	 *
	 * @param toDir  dir
	 * @param lang   default language, used if asset has to be expanded
	 * @param getter input stream getter, used if asset has to be expanded
	 * @return dir
	 */
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	static synchronized public File deploy(@NonNull final File toDir, @SuppressWarnings("SameParameterValue") final String lang, @NonNull final InputStreamGetter getter)
	{
		if (!toDir.exists())
		{
			//noinspection ResultOfMethodCallIgnored
			toDir.mkdirs();
		}
		if (toDir.isDirectory())
		{
			final String[] dirContent = toDir.list();
			if (dirContent == null || dirContent.length == 0)
			{
				// expand asset
				expandZipFile(lang + ".zip", toDir, false, getter);
			}

			// check
			md5(toDir);
			return toDir;
		}
		throw new RuntimeException("Inconsistent dir " + toDir);
	}

	/**
	 * Redeploy to data storage
	 *
	 * @param toDir  dir
	 * @param lang   default language, used if asset has to be expanded
	 * @param getter input stream getter, used if asset has to be expanded
	 */
	static synchronized public void redeploy(@NonNull final File toDir, final String lang, @NonNull final InputStreamGetter getter)
	{
		emptyDirectory(toDir);
		File[] dirContent = toDir.listFiles();
		if (dirContent == null)
		{
			throw new RuntimeException("Null directory");
		}
		if (dirContent.length != 0)
		{
			throw new RuntimeException("Incomplete removal of previous data");
		}

		deploy(toDir, lang, getter);
		dirContent = toDir.listFiles();
		if (dirContent == null)
		{
			throw new RuntimeException("Null directory");
		}
		if (dirContent.length == 0)
		{
			throw new RuntimeException("Failed deployment of data for " + lang);
		}
		File tag = new File(toDir, lang);
		if (!tag.exists())
		{
			throw new RuntimeException("Incomplete data (lang tag missing)" + lang);
		}
	}

	// E X P A N D

	/**
	 * Expand asset file
	 *
	 * @param fromPath zip source path
	 * @param flatten  flatten hierarchy (may lead to overwrites)
	 * @param getter   input stream getter
	 * @return uri of dest dir
	 */
	@Nullable
	@SuppressWarnings({"UnusedReturnValue"})
	static private Uri expandZipFile(final String fromPath, @NonNull final File toDir, @SuppressWarnings("SameParameterValue") final boolean flatten, @NonNull final InputStreamGetter getter)
	{
		//noinspection ResultOfMethodCallIgnored
		toDir.mkdirs();
		if (expandZip(fromPath, toDir.getAbsolutePath(), flatten, getter))
		{
			return Uri.fromFile(toDir);
		}
		return null;
	}

	/**
	 * Expand file to path
	 *
	 * @param fromPath zip source path
	 * @param toPath   destination path
	 * @param flatten  flatten hierarchy (may lead to overwrites)
	 * @param getter   input stream getter
	 * @return true if successful
	 */
	static private boolean expandZip(final String fromPath, @NonNull final String toPath, final boolean flatten, @NonNull final InputStreamGetter getter)
	{
		try (InputStream in = getter.get(fromPath))
		{
			expandZipStream(in, null, new File(toPath), flatten);
			return true;
		}
		catch (@NonNull final Exception ignored)
		{
			return false;
		}
	}

	/**
	 * Expand zip stream to dir
	 *
	 * @param in               zip file input stream
	 * @param pathPrefixFilter path prefix filter on entries
	 * @param toDir            destination dir
	 * @param flatten          flatten hierarchy (may lead to overwrites)
	 * @return dest dir
	 */
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	static private File expandZipStream(@NonNull final InputStream in, @SuppressWarnings("SameParameterValue") final String pathPrefixFilter, @NonNull final File toDir, final boolean flatten) throws IOException
	{
		// prefix
		String filter = pathPrefixFilter;
		if (filter != null && !filter.isEmpty() && filter.charAt(0) == File.separatorChar)
		{
			filter = filter.substring(1);
		}

		// create output directory if not exists
		//noinspection ResultOfMethodCallIgnored
		toDir.mkdir();

		// read and expand entries
		final ZipInputStream zis = new ZipInputStream(in);
		try
		{
			// get the zipped file list entry
			final byte[] buffer = new byte[1024];
			ZipEntry entry = zis.getNextEntry();
			while (entry != null)
			{
				if (!entry.isDirectory())
				{
					final String entryName = entry.getName();
					if (!entryName.endsWith("MANIFEST.MF"))
					{
						if (filter == null || filter.isEmpty() || entryName.startsWith(filter))
						{
							// flatten zip hierarchy
							final File outFile = flatten ? new File(toDir + File.separator + new File(entryName).getName()) : new File(toDir + File.separator + entryName);

							// create all non exists folders else you will hit FileNotFoundException for compressed folder
							final String parent = outFile.getParent();
							if (parent != null)
							{
								File dir = new File(parent);
								boolean created = dir.mkdirs();
								Log.d(TAG, dir + " created=" + created + " exists=" + dir.exists());
							}
							Log.d(TAG, "Unzipped : " + outFile);

							// output

							// copy
							try (FileOutputStream os = new FileOutputStream(outFile))
							{
								int len;
								while ((len = zis.read(buffer)) > 0)
								{
									os.write(buffer, 0, len);
								}
							}
						}
					}
				}
				zis.closeEntry();
				entry = zis.getNextEntry();
			}
		}
		finally
		{
			try
			{
				zis.close();
			}
			catch (IOException ignored)
			{
			}

			try
			{
				in.close();
			}
			catch (IOException ignored)
			{
			}
		}

		return toDir;
	}

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
	 * @param srcFile     source file
	 * @param destFile    destination file
	 * @param task        async task
	 * @param publisher   publisher
	 * @param publishRate publish rate
	 * @return true if successful
	 */
	static synchronized public boolean copyFromFile(final String srcFile, final String destFile, @NonNull final Task<String, Long, Boolean> task, @NonNull final Publisher publisher, final int publishRate)
	{
		Log.d(Deploy.TAG, "Copy from " + srcFile + " to " + destFile);

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
				if (task.jobIsCancelled())
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
	 * Unzip entries from archive
	 *
	 * @param srcArchive  source archive
	 * @param destDir     destination dir
	 * @param task        async task
	 * @param publisher   publisher
	 * @param publishRate publish rate
	 * @return true if successful
	 */
	static synchronized public boolean unzipFromArchive(final String srcArchive, final String destDir, @NonNull final Task<String, Long, Boolean> task, @NonNull final Publisher publisher, final int publishRate)
	{
		Log.d(Deploy.TAG, "Expand from " + srcArchive + " to " + destDir);

		ZipFile zipFile = null;

		try
		{
			// zip
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
						if (task.jobIsCancelled())
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
	 * Unzip entry from archive
	 *
	 * @param srcArchive  source archive
	 * @param srcEntry    source entry
	 * @param destFile    destination file
	 * @param task        async task
	 * @param publisher   publisher
	 * @param publishRate publish rate
	 * @return true if successful
	 */
	static synchronized public boolean unzipEntryFromArchive(final String srcArchive, final String srcEntry, final String destFile, @NonNull final Task<String, Long, Boolean> task, @NonNull final Publisher publisher, final int publishRate)
	{
		Log.d(Deploy.TAG, "Expand from " + srcArchive + " (entry " + srcEntry + ") to " + destFile);

		ZipFile zipFile = null;
		try
		{
			zipFile = new ZipFile(srcArchive);
			final ZipEntry zipEntry = zipFile.getEntry(srcEntry);
			if (zipEntry == null)
			{
				throw new IOException("Zip entry not found " + srcEntry);
			}

			try (InputStream in = zipFile.getInputStream(zipEntry); FileOutputStream out = new FileOutputStream(destFile))
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
					if (task.jobIsCancelled())
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

			File outFile = new File(destFile);
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
	 * MD5 from file
	 *
	 * @param srcFile     source file
	 * @param task        async task
	 * @param publisher   publisher
	 * @param publishRate publish rate
	 * @return true if successful
	 */
	@Nullable
	static synchronized public String md5FromFile(final String srcFile, @NonNull final Task<String, Long, String> task, @NonNull final Publisher publisher, final int publishRate)
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
					if (task.jobIsCancelled())
					{
						//noinspection BreakStatement
						break;
					}
				}
				byte[] digest = md.digest();
				return digestToString(digest);
			}
		}
		catch (@NonNull NoSuchAlgorithmException | IOException e)
		{
			return null;
		}
	}

	// C O P Y

	/**
	 * Copy file
	 *
	 * @param fileName file name
	 * @param fromDir  source dir
	 * @param toDir    dest dir
	 * @param getter   input stream getter
	 * @return uri of copied file
	 */
	@Nullable
	static synchronized public Uri copyFile(@NonNull final String fileName, @NonNull final File fromDir, @NonNull final File toDir, @NonNull final InputStreamGetter getter)
	{
		//noinspection ResultOfMethodCallIgnored
		toDir.mkdirs();
		final File fromFile = new File(fromDir, fileName);
		final File toFile = new File(toDir, fileName);
		if (copy(fromFile.getAbsolutePath(), toFile.getAbsolutePath(), getter))
		{
			return Uri.fromFile(toFile);
		}
		return null;
	}

	/**
	 * Copy file to path
	 *
	 * @param fromPath source path
	 * @param toPath   destination path
	 * @param getter   input stream getter
	 * @return true if successful
	 */
	static private boolean copy(@NonNull final String fromPath, @NonNull final String toPath, @NonNull final InputStreamGetter getter)
	{
		try
		{
			//noinspection ResultOfMethodCallIgnored
			new File(toPath).createNewFile();
		}
		catch (IOException e)
		{
			return false;
		}
		try (InputStream in = getter.get(fromPath); OutputStream out = new FileOutputStream(toPath))
		{
			copyStreams(in, out);
			return true;
		}
		catch (@NonNull final Exception ignored)
		{
			return false;
		}
	}

	/**
	 * Copy file to path
	 *
	 * @param fromPath source path
	 * @param toPath   destination path
	 * @return true if successful
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	static synchronized public boolean copyFile(@NonNull final String fromPath, @NonNull final String toPath)
	{
		try
		{
			new File(toPath).createNewFile();
		}
		catch (IOException e)
		{
			return false;
		}
		try (InputStream in = new FileInputStream(fromPath); OutputStream out = new FileOutputStream(toPath))
		{
			copyStreams(in, out);
			return true;
		}
		catch (@NonNull final Exception ignored)
		{
			return false;
		}
	}

	/**
	 * Copy in stream to out stream
	 *
	 * @param in  in stream
	 * @param out out stream
	 * @throws IOException io exception
	 */
	@SuppressWarnings("WeakerAccess")
	static private void copyStreams(@NonNull final InputStream in, @NonNull final OutputStream out) throws IOException
	{
		final byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1)
		{
			out.write(buffer, 0, read);
		}
	}

	// D E L E T E

	/**
	 * Empty directory recursively.
	 *
	 * @param dir file.
	 */
	static synchronized public void emptyDirectory(@NonNull final File dir)
	{
		if (dir.isDirectory())
		{
			File[] childFiles = dir.listFiles();
			if (childFiles != null && childFiles.length > 0)
			{
				// Directory has other files. Need to delete them first
				for (File childFile : childFiles)
				{
					// Recursively delete the files
					zap(childFile);
				}
			}
		}
		File[] dirContent = dir.listFiles();
		if (dirContent == null)
		{
			throw new RuntimeException("Null directory");
		}
		if (dirContent.length != 0)
		{
			throw new RuntimeException("Cannot empty " + dir);
		}
	}

	/**
	 * Delete this file or dir recursively.
	 *
	 * @param file file.
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	static private void zap(@NonNull final File file)
	{
		if (file.isDirectory())
		{
			File[] childFiles = file.listFiles();
			if (childFiles != null && childFiles.length > 0)
			{
				// Directory has other files. Need to delete them first
				for (File childFile : childFiles)
				{
					// Recursively delete the files
					zap(childFile);
				}
			}
			// Directory is empty.
		}
		file.delete();
	}

	// M D 5

	static private final int MD5_CHUNK_SIZE = 1024;

	// M D 5

	static private final Pattern md5LinePattern = Pattern.compile("([a-f0-9]+)\\s*(.*)");

	/**
	 * Scan directory recursively (throws RuntimeException if anything goes wrong)
	 *
	 * @param dir dir.
	 */
	static private void md5(@NonNull final File dir)
	{
		Map<String, String> map = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new FileReader(new File(dir, "md5sum.txt"))))
		{
			String line;
			while ((line = br.readLine()) != null)
			{
				final Matcher matcher = md5LinePattern.matcher(line);
				if (matcher.find())
				{
					final String digest = matcher.group(1);
					String name = matcher.group(2);
					if (name == null)
					{
						continue;
					}
					if (name.startsWith("./"))
					{
						name = name.substring(2);
					}
					final String path = new File(dir, name).getAbsolutePath();
					map.put(path, digest);
				}
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException("Can't read 'md5sum.txt'.");
		}

		md5Scan(dir, map);
	}

	/**
	 * Empty directory recursively (throws RuntimeException if anything goes wrong)
	 *
	 * @param file file.
	 */
	static private void md5Scan(@NonNull final File file, @NonNull final Map<String, String> map)
	{
		if (file.isDirectory())
		{
			File[] childFiles = file.listFiles();
			if (childFiles != null && childFiles.length > 0)
			{
				// Directory has other files. Need to delete them first
				for (File childFile : childFiles)
				{
					// Recursively compute
					md5Scan(childFile, map);
				}
			}
		}
		else if (file.length() > 0 && !file.getName().equals("md5sum.txt"))
		{
			final String path = file.getAbsolutePath();
			final String digest = computeDigest(path);
			// Log.d(TAG, path + " " + digest);
			if (!map.containsKey(path))
			{
				//Log.e(TAG, "Missing digest for " + path);
				//throw new RuntimeException("Missing digest for " + path);
				// not deemed critical by distributor
				Log.d(TAG, "No digest for " + path + " skipped");
				return;
			}
			String refDigest = map.get(path);
			if (refDigest == null)
			{
				Log.e(TAG, "Null digest for " + path);
				throw new RuntimeException("Null digest for " + path);
			}
			if (!refDigest.equals(digest))
			{
				Log.e(TAG, "Altered " + path);
				throw new RuntimeException("Altered " + path);
			}
			Log.d(TAG, "MD5 of " + path + " ok");
		}
	}

	@Nullable
	@SuppressWarnings("StatementWithEmptyBody")
	static public String computeDigest(@NonNull final String path)
	{
		// Log.d(TAG, "MD5 " + path);
		MessageDigest md;
		try
		{
			md = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e)
		{
			return null;
		}
		try (FileInputStream fis = new FileInputStream(path); DigestInputStream dis = new DigestInputStream(fis, md))
		{

			final byte[] buffer = new byte[MD5_CHUNK_SIZE];
			while (dis.read(buffer) != -1)
			{
			}
			byte[] digest = md.digest();
			return digestToString(digest);
		}
		catch (IOException e)
		{
			return null;
		}
	}

	@Nullable
	static private String digestToString(@Nullable byte... byteArray)
	{
		if (byteArray == null)
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (byte b : byteArray)
		{
			sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}
}
