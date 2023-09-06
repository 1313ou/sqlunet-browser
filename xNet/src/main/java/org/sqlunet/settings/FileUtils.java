/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.settings;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * File utilities
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FileUtils
{
	// static private final String TAG = "FileUtils";
	// C O P Y A S S E T

	/**
	 * Copy asset file
	 *
	 * @param context  context
	 * @param fileName file in assets
	 * @return uri of copied file
	 */
	@Nullable
	static public Uri copyAssetFile(@NonNull final Context context, @NonNull final String fileName)
	{
		try (AssetManager assetManager = context.getAssets())
		{
			final File dir = Storage.getSqlUNetStorage(context);

			//noinspection ResultOfMethodCallIgnored
			dir.mkdirs();
			final File file = new File(dir, fileName);
			if (FileUtils.copyAsset(assetManager, fileName, file.getAbsolutePath()))
			{
				return Uri.fromFile(file);
			}
			return null;
		}
	}

	/**
	 * Copy asset file to path
	 *
	 * @param assetManager asset manager
	 * @param assetPath    asset path
	 * @param toPath       destination path
	 * @return destination path
	 */
	static private boolean copyAsset(@NonNull final AssetManager assetManager, @NonNull final String assetPath, @NonNull final String toPath)
	{
		try (InputStream is = assetManager.open(assetPath))
		{
			final File f = new File(toPath);
			if (!f.createNewFile())
			{
				return false;
			}
			try (OutputStream os = new FileOutputStream(f))
			{
				FileUtils.copyFile(is, os);
				return true;
			}
		}
		catch (@NonNull final Exception e)
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
	static private void copyFile(@NonNull final InputStream is, @NonNull final OutputStream os) throws IOException
	{
		final byte[] buffer = new byte[1024];
		int read;
		while ((read = is.read(buffer)) != -1)
		{
			os.write(buffer, 0, read);
		}
	}

	// E X P A N D A S S E T

	/**
	 * Expand asset file
	 *
	 * @param context  context
	 * @param fileName zip file in assets
	 * @return uri of dest dir
	 */
	@Nullable
	static public Uri expandZipAssetFile(@NonNull final Context context, @NonNull final String fileName)
	{
		try (AssetManager assetManager = context.getAssets())
		{
			assert assetManager != null;
			final File dir = Storage.getSqlUNetStorage(context);

			//noinspection ResultOfMethodCallIgnored
			dir.mkdirs();
			if (FileUtils.expandZipAsset(assetManager, fileName, dir.getAbsolutePath()))
			{
				return Uri.fromFile(dir);
			}
			return null;
		}
	}

	/**
	 * Expand asset file to path
	 *
	 * @param assetManager asset manager
	 * @param assetPath    asset path
	 * @param toPath       destination path
	 * @return true if successful
	 */
	static private boolean expandZipAsset(@NonNull final AssetManager assetManager, @NonNull final String assetPath, @NonNull final String toPath)
	{
		try (InputStream is = assetManager.open(assetPath))
		{
			FileUtils.expandZip(is, null, new File(toPath));
			return true;
		}
		catch (@NonNull final Exception e)
		{
			return false;
		}
		//
	}

	/**
	 * Expand zip stream to dir
	 *
	 * @param is                zip file input stream
	 * @param pathPrefixFilter0 path prefix filter on entries
	 * @param destDir           destination dir
	 * @return dest dir
	 */
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	static private File expandZip(@NonNull final InputStream is, @SuppressWarnings("SameParameterValue") final String pathPrefixFilter0, @NonNull final File destDir) throws IOException
	{
		// prefix
		String pathPrefixFilter = pathPrefixFilter0;
		if (pathPrefixFilter != null && !pathPrefixFilter.isEmpty() && pathPrefixFilter.charAt(0) == File.separatorChar)
		{
			pathPrefixFilter = pathPrefixFilter.substring(1);
		}

		// create output directory if not exists
		//noinspection ResultOfMethodCallIgnored
		destDir.mkdir();

		// read and expandContainer entries
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
					if (!entryName.endsWith("MANIFEST.MF")) //
					{
						if (pathPrefixFilter == null || pathPrefixFilter.isEmpty() || entryName.startsWith(pathPrefixFilter))
						{
							// flatten zip hierarchy
							final File outFile = new File(destDir + File.separator + new File(entryName).getName());

							// create all non exists folders else you will hit FileNotFoundException for compressed folder
							final String parent = outFile.getParent();
							if (parent != null)
							{
								final File dir = new File(parent);
								/* boolean created = */
								//noinspection ResultOfMethodCallIgnored
								dir.mkdirs();
							}

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

		return destDir;
	}
}
