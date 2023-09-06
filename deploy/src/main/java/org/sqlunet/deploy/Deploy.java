/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.deploy;

import android.net.Uri;
import android.util.Log;

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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
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

	// E X P A N D

	/**
	 * Expand file to path
	 *
	 * @param fromPath zip source path
	 * @param toPath   destination path
	 * @param flatten  flatten hierarchy (may lead to overwrites)
	 * @param getter   input stream getter
	 * @return true if successful
	 */
	static public boolean expandZip(final String fromPath, @NonNull final String toPath, final boolean flatten, @NonNull final InputStreamGetter getter)
	{
		final File toDir = new File(toPath);
		//noinspection ResultOfMethodCallIgnored
		toDir.mkdirs();
		try (InputStream is = getter.get(fromPath))
		{
			expandZipStream(is, null, toDir, flatten);
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
	 * @param is               zip file input stream
	 * @param pathPrefixFilter path prefix filter on entries
	 * @param toDir            destination dir
	 * @param flatten          flatten hierarchy (may lead to overwrites)
	 * @return dest dir
	 */
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	static private File expandZipStream(@NonNull final InputStream is, @SuppressWarnings("SameParameterValue") final String pathPrefixFilter, @NonNull final File toDir, final boolean flatten) throws IOException
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
		try (ZipInputStream zis = new ZipInputStream(is))
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
		return toDir;
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
		try ( //
		      InputStream is = getter.get(fromPath); //
		      OutputStream os = new FileOutputStream(toPath) //
		)
		{
			copyStreams(is, os);
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
		try ( //
		      InputStream is = new FileInputStream(fromPath); //
		      OutputStream os = new FileOutputStream(toPath)) //
		{
			copyStreams(is, os);
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
	 * @param is in stream
	 * @param os out stream
	 * @throws IOException io exception
	 */
	@SuppressWarnings("WeakerAccess")
	static private void copyStreams(@NonNull final InputStream is, @NonNull final OutputStream os) throws IOException
	{
		final byte[] buffer = new byte[1024];
		int read;
		while ((read = is.read(buffer)) != -1)
		{
			os.write(buffer, 0, read);
		}
	}

	// M D 5

	static private final int MD5_CHUNK_SIZE = 1024;

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
	public static String computeDigest(@NonNull final String path)
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
	public static String digestToString(@Nullable byte... byteArray)
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
}
