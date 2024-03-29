/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.settings

import android.content.Context
import android.content.res.AssetManager
import android.net.Uri
import android.os.Build
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.util.zip.ZipInputStream

/**
 * File utilities
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal object FileUtils {

    // C O P Y   A S S E T

    /**
     * Copy asset file
     *
     * @param context  context
     * @param fileName file in assets
     * @return uri of copied file
     */
    fun copyAssetFile(context: Context, fileName: String): Uri? {
        context.assets.use {
            val dir = Storage.getSqlUNetStorage(context)
            dir.mkdirs()
            val file = File(dir, fileName)
            return if (copyAsset(it, fileName, file.absolutePath)) {
                Uri.fromFile(file)
            } else null
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
    private fun copyAsset(assetManager: AssetManager, assetPath: String, toPath: String): Boolean {
        try {
            assetManager.open(assetPath).use { `is` ->
                val f = File(toPath)
                if (!f.createNewFile()) {
                    return false
                }
                val os = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Files.newOutputStream(f.toPath()) else FileOutputStream(f)
                os.use {
                    copyFile(`is`, it)
                    return true
                }
            }
        } catch (_: Exception) {
        }
        return false
    }

    /**
     * Copy in stream to out stream
     *
     * @param is in stream
     * @param os out stream
     * @throws IOException io exception
     */
    @Throws(IOException::class)
    private fun copyFile(`is`: InputStream, os: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while (`is`.read(buffer).also { read = it } != -1) {
            os.write(buffer, 0, read)
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
    fun expandZipAssetFile(context: Context, fileName: String): Uri? {
        context.assets.use {
            val dir = Storage.getSqlUNetStorage(context)
            dir.mkdirs()
            return if (expandZipAsset(it, fileName, dir.absolutePath)) {
                Uri.fromFile(dir)
            } else null
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
    private fun expandZipAsset(assetManager: AssetManager, assetPath: String, toPath: String): Boolean {
        try {
            assetManager.open(assetPath).use {
                expandZip(it, null, File(toPath))
                return true
            }
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * Expand zip stream to dir
     *
     * @param is                zip file input stream
     * @param pathPrefixFilter0 path prefix filter on entries
     * @param destDir           destination dir
     * @return dest dir
     */
    @Throws(IOException::class)
    private fun expandZip(`is`: InputStream, pathPrefixFilter0: String?, destDir: File): File {
        // prefix
        var pathPrefixFilter = pathPrefixFilter0
        if (!pathPrefixFilter.isNullOrEmpty() && pathPrefixFilter[0] == File.separatorChar) {
            pathPrefixFilter = pathPrefixFilter.substring(1)
        }

        // create output directory if not exists
        destDir.mkdir()
        ZipInputStream(`is`).use { zis ->
            // get the zipped file list entry
            val buffer = ByteArray(1024)
            var entry = zis.getNextEntry()
            while (entry != null) {
                if (!entry.isDirectory) {
                    val entryName = entry.name
                    if (!entryName.endsWith("MANIFEST.MF")) {
                        if (pathPrefixFilter.isNullOrEmpty() || entryName.startsWith(pathPrefixFilter)) {
                            // flatten zip hierarchy
                            val outFile = File(destDir.toString() + File.separator + File(entryName).getName())

                            // create all non exists folders else you will hit FileNotFoundException for compressed folder
                            val parent = outFile.getParent()
                            if (parent != null) {
                                val dir = File(parent)
                                /* boolean created = */dir.mkdirs()
                            }
                            FileOutputStream(outFile).use { os ->
                                var len: Int
                                while (zis.read(buffer).also { len = it } > 0) {
                                    os.write(buffer, 0, len)
                                }
                            }
                        }
                    }
                }
                zis.closeEntry()
                entry = zis.getNextEntry()
            }
        }
        return destDir
    }
}
