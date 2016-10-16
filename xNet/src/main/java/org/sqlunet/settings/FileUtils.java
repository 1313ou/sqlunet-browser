package org.sqlunet.settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;

@SuppressWarnings("unused")
class FileUtils
{
	// private static final String TAG = "FileUtils"; //$NON-NLS-1$

	// C O P Y A S S E T

	/**
	 * Copy asset file
	 *
	 * @param context
	 *            context
	 * @param fileName
	 *            file in assets
	 * @return uri of copied file
	 */
	public static Uri copyAssetFile(final Context context, final String fileName)
	{
		AssetManager assetManager = null;
		try
		{
			assetManager = context.getAssets();
			final File dir = Storage.getSqlUNetStorage(context);
			//noinspection ResultOfMethodCallIgnored
			dir.mkdirs();
			final File file = new File(dir, fileName);
			if (FileUtils.copyAsset(assetManager, fileName, file.getAbsolutePath()))
				return Uri.fromFile(file);
			return null;
		}
		finally
		{
			assert assetManager != null;
			assetManager.close();
		}
	}

	/**
	 * Copy asset file to path
	 *
	 * @param assetManager
	 *            asset manager
	 * @param assetPath
	 *            asset path
	 * @param toPath
	 *            destination path
	 * @return destination path
	 */
	private static boolean copyAsset(final AssetManager assetManager, final String assetPath, final String toPath)
	{
		InputStream in = null;
		OutputStream out = null;
		try
		{
			in = assetManager.open(assetPath);
			final File f = new File(toPath);
			if (!f.createNewFile())
				return false;
			out = new FileOutputStream(f);
			FileUtils.copyFile(in, out);
			return true;
		}
		catch (final Exception e)
		{
			return false;
		}
		finally
		{
			if (out != null)
			{
				try
				{
					out.close();
				}
				catch (final IOException e)
				{
					//
				}
			}
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (final IOException e)
				{
					//
				}
			}
		}
	}

	/**
	 * Copy instream to outstream
	 *
	 * @param in
	 *            instream
	 * @param out
	 *            outstream
	 * @throws IOException
	 */
	private static void copyFile(final InputStream in, final OutputStream out) throws IOException
	{
		final byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1)
		{
			out.write(buffer, 0, read);
		}
	}

	// E X P A N D A S S E T

	/**
	 * Expand asset file
	 *
	 * @param context
	 *            context
	 * @param fileName
	 *            zip file in assets
	 * @return uri of dest dir
	 */
	public static Uri expandZipAssetFile(final Context context, final String fileName)
	{
		AssetManager assetManager = null;
		try
		{
			assetManager = context.getAssets();
			assert assetManager != null;
			final File dir = Storage.getSqlUNetStorage(context);
			//noinspection ResultOfMethodCallIgnored
			dir.mkdirs();
			if (FileUtils.expandZipAsset(assetManager, fileName, dir.getAbsolutePath()))
				return Uri.fromFile(dir);
			return null;
		}
		finally
		{
			if(assetManager != null)
				assetManager.close();
		}
	}

	/**
	 * Expand asset file to path
	 *
	 * @param assetManager
	 *            asset manager
	 * @param assetPath
	 *            asset path
	 * @param toPath
	 *            destination path
	 * @return true if successful
	 */
	private static boolean expandZipAsset(final AssetManager assetManager, final String assetPath, final String toPath)
	{
		InputStream in = null;
		try
		{
			in = assetManager.open(assetPath);
			FileUtils.expandZip(in, null, new File(toPath));
			return true;
		}
		catch (final Exception e)
		{
			return false;
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (final IOException e)
				{
					//
				}
			}
		}
	}

	/**
	 * Expand zip stream to dir
	 *
	 * @param in
	 *            zip file input stream
	 * @param pathPrefixFilter0
	 *            path prefix filter on entries
	 * @param destDir
	 *            destination dir
	 * @return dest dir
	 */
	@SuppressWarnings("UnusedReturnValue")
	static private File expandZip(final InputStream in, @SuppressWarnings("SameParameterValue") final String pathPrefixFilter0, final File destDir) throws IOException
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

		// read and expand entries
		final ZipInputStream zis = new ZipInputStream(in);

		//noinspection TryFinallyCanBeTryWithResources
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
					if (!entryName.endsWith("MANIFEST.MF")) //$NON-NLS-1$
					{
						if (pathPrefixFilter == null || pathPrefixFilter.isEmpty() || entryName.startsWith(pathPrefixFilter))
						{
							// flatten zip hierarchy
							final File outFile = new File(destDir + File.separator + new File(entryName).getName());

							// create all non exists folders else you will hit FileNotFoundException for compressed folder
							//noinspection ResultOfMethodCallIgnored
							new File(outFile.getParent()).mkdirs();

							// output
							final FileOutputStream os = new FileOutputStream(outFile);

							// copy

							//noinspection TryFinallyCanBeTryWithResources
							try
							{
								int len;
								while ((len = zis.read(buffer)) > 0)
								{
									os.write(buffer, 0, len);
								}
							}
							finally
							{
								try { os.close(); } catch(IOException ignored){}
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
			try { zis.close(); } catch(IOException ignored){}
			try { in.close(); } catch(IOException ignored){}
		}

		return destDir;
	}
}
