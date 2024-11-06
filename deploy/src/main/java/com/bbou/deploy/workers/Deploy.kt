/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.deploy.workers

import android.net.Uri
import android.os.Build
import android.util.Log
import com.bbou.concurrency.Cancelable
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.regex.Pattern
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

/**
 * Deploy
 */
object Deploy {

    private const val TAG = "Deploy"

    /**
     * Zip file extension
     */
    const val ZIP_EXTENSION = ".zip"

    /**
     * MD5 file extension
     */
    const val MD5_EXTENSION = ".md5"

    /**
     * Buffer size
     */
    private const val CHUNK_SIZE = 1024

    /**
     * Fast check data storage
     *
     * @param toDir dir (throws RuntimeException if operation cannot succeed)
     */
    private fun fastCheck(toDir: File) {
        if (!toDir.exists()) {
            throw RuntimeException("Does not exist " + toDir.absolutePath)
        }
        if (!toDir.isDirectory) {
            throw RuntimeException("Is not a directory " + toDir.absolutePath)
        }
        val dirContent = toDir.list()
        if (dirContent == null || dirContent.isEmpty()) {
            throw RuntimeException("Is empty " + toDir.absolutePath)
        }
    }

    /**
     * Check data storage
     *
     * @param toDir dir (throws RuntimeException if operation cannot succeed)
     */
    @Synchronized
    fun check(toDir: File) {
        fastCheck(toDir)
        md5(toDir)
    }

    /**
     * Deploy to data storage (throws RuntimeException if operation cannot succeed)
     *
     * @param toDir  dir
     * @param lang   default language, used if asset has to be expanded
     * @param getter input stream getter, used if asset has to be expanded
     * @return dir
     */
    @Synchronized
    fun deploy(toDir: File, lang: String, getter: InputStreamGetter): File {
        if (!toDir.exists()) {
            toDir.mkdirs()
        }
        if (toDir.isDirectory) {
            val dirContent = toDir.list()
            if (dirContent == null || dirContent.isEmpty()) {
                // expand asset
                expandZipFile(lang + ZIP_EXTENSION, toDir, false, getter)
            }

            // check
            md5(toDir)
            return toDir
        }
        throw RuntimeException("Inconsistent dir $toDir")
    }

    /**
     * Redeploy to data storage
     *
     * @param toDir  dir
     * @param lang   default language, used if asset has to be expanded
     * @param getter input stream getter, used if asset has to be expanded
     */
    @Suppress("unused")
    @Synchronized
    fun redeploy(toDir: File, lang: String, getter: InputStreamGetter) {
        emptyDirectory(toDir)
        var dirContent: Array<out File>? = toDir.listFiles() ?: throw RuntimeException("Null directory")
        if (dirContent != null) {
            if (dirContent.isNotEmpty()) {
                throw RuntimeException("Incomplete removal of previous data")
            }
        }
        deploy(toDir, lang, getter)
        dirContent = toDir.listFiles()
        if (dirContent == null) {
            throw RuntimeException("Null directory")
        }
        if (dirContent.isEmpty()) {
            throw RuntimeException("Failed deployment of data for $lang")
        }
        val tag = File(toDir, lang)
        if (!tag.exists()) {
            throw RuntimeException("Incomplete data (lang tag missing)$lang")
        }
    }

    // C O P Y

    /**
     * Copy file
     *
     * @param fromFilename source file name
     * @param fromDir  source dir
     * @param toDir    dest dir
     * @param getter   input stream getter
     * @return uri of copied file
     */
    @Suppress("unused")
    @Synchronized
    fun copyFile(fromFilename: String, fromDir: File, toDir: File, getter: InputStreamGetter): Uri? {
        toDir.mkdirs()
        val fromFile = File(fromDir, fromFilename)
        val toFile = File(toDir, fromFilename)
        return if (copy(fromFile.absolutePath, toFile.absolutePath, getter)) {
            Uri.fromFile(toFile)
        } else null
    }

    /**
     * Copy file to path
     *
     * @param fromPath source path
     * @param toPath   destination path
     * @return true if successful
     */
    @Suppress("unused")
    @Synchronized
    fun copyFile(fromPath: String, toPath: String): Boolean {
        try {
            File(toPath).createNewFile()
        } catch (e: IOException) {
            return false
        }
        try {
            val is1 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Files.newInputStream(Paths.get(fromPath)) else FileInputStream(fromPath)
            val is2 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Files.newOutputStream(Paths.get(toPath)) else FileOutputStream(toPath)
            is1.use { `is` ->
                is2.use {
                    copyStreams(`is`, it)
                    return true
                }
            }
        } catch (ignored: Exception) {
        }
        return false
    }

    /**
     * Copy file to path
     *
     * @param fromPath source path
     * @param toPath   destination path
     * @param getter   input stream getter
     * @return true if successful
     */
    private fun copy(fromPath: String, toPath: String, getter: InputStreamGetter): Boolean {
        try {
            File(toPath).createNewFile()
        } catch (e: IOException) {
            return false
        }
        try {
            getter.getInputStream(fromPath).use { `is` ->
                val os = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Files.newOutputStream(Paths.get(toPath)) else FileOutputStream(toPath)
                os.use {
                    copyStreams(`is`, it)
                    return true
                }
            }
        } catch (ignored: Exception) {
        }
        return false
    }

    /**
     * Copy instream to outstream
     *
     * @param is instream
     * @param os outstream
     * @throws IOException io exception
     */
    @Throws(IOException::class)
    private fun copyStreams(`is`: InputStream, os: OutputStream) {
        val buffer = ByteArray(CHUNK_SIZE)
        var readCount: Int
        while (`is`.read(buffer).also { readCount = it } != -1) {
            os.write(buffer, 0, readCount)
        }
    }

    /**
     * Copy from file in task
     *
     * @param fromPath    source file
     * @param toPath      destination file
     * @param task        async task context that flags interruption of this operation
     * @param publisher   publisher
     * @param publishRate publish rate
     * @return true if successful
     */
    @Suppress("unused")
    @Synchronized
    fun copyFile(fromPath: String, toPath: String, task: Cancelable, publisher: Publisher, publishRate: Int): Boolean {
        Log.d(TAG, "Copying from $fromPath to $toPath")
        val sourceFile = File(fromPath)
        val length = sourceFile.length()
        try {
            FileInputStream(fromPath).use { `is` ->
                FileOutputStream(toPath).use {
                    copyStreams(`is`, it, length, task, publisher, publishRate)
                    return true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "While copying", e)
        }
        return false
    }

    /**
     * Copy instream to outstream
     *
     * @param is          in stream
     * @param os          out stream
     * @param length      in stream size
     * @param task        async task context that flags interruption of this operation
     * @param publisher   progress publisher
     * @param publishRate publish rate
     * @throws IOException io exception
     */
    @Throws(IOException::class)
    private fun copyStreams(`is`: InputStream, os: OutputStream, length: Long, task: Cancelable, publisher: Publisher, publishRate: Int) {
        val buffer = ByteArray(CHUNK_SIZE)
        var byteCount: Long = 0
        var chunkCount = 0
        var readCount: Int
        while (`is`.read(buffer).also { readCount = it } != -1) {
            // write
            os.write(buffer, 0, readCount)

            // count
            byteCount += readCount.toLong()
            chunkCount++

            // publish
            if (chunkCount % publishRate == 0) {
                publisher.publish(byteCount, length)
            }

            // interrupt/cancel hook
            if (Thread.interrupted() || task.isCancelled() || Thread.interrupted()) {
                break
            }
        }
        publisher.publish(byteCount, length)
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
    private fun expandZipFile(fromPath: String, toDir: File, @Suppress("SameParameterValue") flatten: Boolean, getter: InputStreamGetter): Uri? {
        toDir.mkdirs()
        return if (expandZip(fromPath, toDir.absolutePath, flatten, getter)) {
            Uri.fromFile(toDir)
        } else null
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
    private fun expandZip(fromPath: String, toPath: String, flatten: Boolean, getter: InputStreamGetter): Boolean {
        val toDir = File(toPath)
        toDir.mkdirs()
        try {
            getter.getInputStream(fromPath).use {
                expandZipStream(it, null, toDir, flatten)
                return true
            }
        } catch (ignored: Exception) {
            return false
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
    @Throws(IOException::class)
    private fun expandZipStream(`is`: InputStream, @Suppress("SameParameterValue") pathPrefixFilter: String?, toDir: File, flatten: Boolean): File {

        // prefix
        var entryFilter = pathPrefixFilter
        if (!entryFilter.isNullOrEmpty() && entryFilter[0] == File.separatorChar) {
            entryFilter = entryFilter.substring(1)
        }

        // create output directory if not exists
        toDir.mkdir()
        ZipInputStream(`is`).use { zis ->
            // get the zipped file list entry
            val buffer = ByteArray(CHUNK_SIZE)
            generateSequence { zis.nextEntry }
                .filterNot { it.isDirectory }
                .filterNot { it.name.endsWith("MANIFEST.MF") }
                .filter { entryFilter.isNullOrEmpty() || it.name.startsWith(entryFilter) }
                .forEach { it ->

                    // flatten zip hierarchy
                    val outFile = if (flatten) File(toDir.toString() + File.separator + File(it.name).name) else File(toDir.toString() + File.separator + it.name)

                    // create all non exists folders else you will hit FileNotFoundException for compressed folder
                    val parent = outFile.parent
                    if (parent != null) {
                        val dir = File(parent)
                        val created = dir.mkdirs()
                        Log.d(TAG, "Created : " + dir + " result=" + created + " exists=" + dir.exists())
                    }
                    Log.d(TAG, "Created : $outFile")
                    FileOutputStream(outFile).use { os ->
                        var len: Int
                        while (zis.read(buffer).also { len = it } > 0) {
                            os.write(buffer, 0, len)
                        }
                    }
                }
        }
        return toDir
    }

    /**
     * Unzip entries from archive
     *
     * @param fromPath    source archive
     * @param toPath      destination dir
     * @param task        async task context that flags interruption of this operation
     * @param publisher   publisher
     * @param publishRate publish rate
     * @return true if successful
     */
    @Suppress("unused")
    @Synchronized
    fun unzipFromArchive(fromPath: String, toPath: String, task: Cancelable, publisher: Publisher, publishRate: Int): Boolean {
        Log.d(TAG, "Expanding from $fromPath to $toPath")
        try {
            ZipFile(fromPath).use { zipFile ->
                val zipEntries = zipFile.entries()
                while (zipEntries.hasMoreElements()) {
                    val zipEntry = zipEntries.nextElement()
                    // Log.d(TAG, "Expand zip entry  " + zipEntry.getName())
                    if (zipEntry.isDirectory) {
                        continue
                    }

                    // out
                    val outFile = File(toPath + '/' + zipEntry.name)
                    // Log.d(TAG, outFile + " exist=" + outFile.exists())

                    // create all non exists folders else you will hit FileNotFoundException for compressed folder
                    val parent = outFile.parent
                    if (parent != null) {
                        val dir = File(parent)
                        val created = dir.mkdirs()
                        Log.d(TAG, "Created : " + dir + " result=" + created + " exists=" + dir.exists())
                    }

                    // input
                    try {
                        zipFile.getInputStream(zipEntry).use { `is` ->
                            FileOutputStream(outFile).use { os ->
                                val length = zipEntry.size

                                // copy
                                val buffer = ByteArray(CHUNK_SIZE)
                                var byteCount: Long = 0
                                var chunkCount = 0
                                var readCount: Int
                                while (`is`.read(buffer).also { readCount = it } != -1) {
                                    // write
                                    os.write(buffer, 0, readCount)

                                    // count
                                    byteCount += readCount.toLong()
                                    chunkCount++

                                    // publish
                                    if (chunkCount % publishRate == 0) {
                                        publisher.publish(byteCount, length)
                                    }

                                    // interrupt/cancel hook
                                    if (Thread.interrupted() || task.isCancelled()) {
                                        break
                                    }
                                }
                                publisher.publish(byteCount, length)
                            }
                        }
                    } catch (e1: IOException) {
                        Log.e(TAG, "While executing from archive", e1)
                    }
                    Log.d(TAG, "Created : " + outFile + " exists=" + outFile.exists())
                }
                return true
            }
        } catch (e1: IOException) {
            Log.e(TAG, "While executing from archive", e1)
        }
        return false
    }

    /**
     * Unzip entry from archive
     *
     * @param fromPath    source archive
     * @param fromEntry   source entry
     * @param toFilePath  destination file
     * @param task        async task context that flags interruption of this operation
     * @param publisher   publisher
     * @param publishRate publish rate
     * @return true if successful
     */
    @Suppress("unused")
    @Synchronized
    fun unzipEntryFromArchive(fromPath: String, fromEntry: String, toFilePath: String, task: Cancelable, publisher: Publisher, publishRate: Int): Boolean {
        Log.d(TAG, "Expanding from $fromPath (entry $fromEntry) to $toFilePath")
        try {
            ZipFile(fromPath).use { zipFile ->
                val zipEntry = zipFile.getEntry(fromEntry) ?: throw IOException("Zip entry not found $fromEntry")
                try {
                    zipFile.getInputStream(zipEntry).use { `is` ->
                        FileOutputStream(toFilePath).use { os ->
                            val length = zipEntry.size
                            val buffer = ByteArray(CHUNK_SIZE)
                            var byteCount: Long = 0
                            var chunkCount = 0
                            var readCount: Int
                            while (`is`.read(buffer).also { readCount = it } != -1) {
                                // write
                                os.write(buffer, 0, readCount)

                                // count
                                byteCount += readCount.toLong()
                                chunkCount++

                                // publish
                                if (chunkCount % publishRate == 0) {
                                    publisher.publish(byteCount, length)
                                }

                                // interrupt/cancel hook
                                if (Thread.interrupted() || task.isCancelled()) {
                                    break
                                }
                            }
                            publisher.publish(byteCount, length)
                            return true
                        }
                    }
                } catch (e1: IOException) {
                    Log.e(TAG, "While executing from archive", e1)
                }
            }
        } catch (e: IOException) {
            return false
        }
        return false
    }

    // M D 5

    private val md5LinePattern = Pattern.compile("([a-f0-9]+)\\s*(.*)")

    /**
     * MD5 from file
     *
     * @param fromPath    source file
     * @param task        async task context that flags interruption of this operation
     * @param publisher   publisher
     * @param publishRate publish rate
     * @return digest if successful
     */
    @Suppress("unused")
    @Synchronized
    fun md5FromFile(fromPath: String, task: Cancelable, publisher: Publisher, publishRate: Int): String? {
        Log.d(TAG, "MD5summing $fromPath")
        try {
            val md = MessageDigest.getInstance("MD5")
            val sourceFile = File(fromPath)
            val length = sourceFile.length()
            FileInputStream(fromPath).use { fis ->
                DigestInputStream(fis, md).use { dis ->
                    val buffer = ByteArray(CHUNK_SIZE)
                    var byteCount: Long = 0
                    var chunkCount = 0
                    var readCount: Int

                    // read decorated stream (dis) to EOF as normal
                    while (dis.read(buffer).also { readCount = it } != -1) {
                        // count
                        byteCount += readCount.toLong()
                        chunkCount++

                        // publish
                        if (chunkCount % publishRate == 0) {
                            publisher.publish(byteCount, length)
                        }

                        // interrupt/cancel hook
                        if (Thread.interrupted() || task.isCancelled()) {
                            break
                        }
                    }
                    val digest = md.digest()
                    return digestToString(*digest)
                }
            }
        } catch (e: NoSuchAlgorithmException) {
            return null
        } catch (e: IOException) {
            return null
        }
    }

    /**
     * Scan directory recursively (throws RuntimeException if anything goes wrong)
     *
     * @param dir dir
     */
    private fun md5(dir: File) {
        val map: MutableMap<String, String> = HashMap()
        try {
            BufferedReader(FileReader(File(dir, "md5sum.txt"))).use { reader ->
                reader.lineSequence().forEach { it ->
                    val matcher = it.let { md5LinePattern.matcher(it) }
                    if (matcher.find()) {
                        val digest = matcher.group(1)
                        matcher.group(2)?.let {
                            var name = it
                            if (name.startsWith("./")) {
                                name = name.substring(2)
                            }
                            val path = File(dir, name).absolutePath
                            if (digest != null) {
                                map[path] = digest.toString()
                            }
                        }
                    }
                }
            }
        } catch (e: IOException) {
            throw RuntimeException("Can't read 'md5sum.txt'.")
        }
        md5Scan(dir, map)
    }

    /**
     * Scan directory recursively (throws RuntimeException if anything goes wrong).
     *
     * @param file file
     */
    private fun md5Scan(file: File, map: Map<String, String>) {
        if (file.isDirectory) {
            val childFiles = file.listFiles()
            if (childFiles != null && childFiles.isNotEmpty()) {
                // Directory has other files. Need to delete them first
                for (childFile in childFiles) {
                    // Recursively compute
                    md5Scan(childFile, map)
                }
            }
        } else if (file.length() > 0 && file.name != "md5sum.txt") {
            val path = file.absolutePath
            val digest = computeDigest(path)
            // Log.d(TAG, path + " " + digest)
            if (!map.containsKey(path)) {
                //Log.e(TAG, "Missing digest for " + path)
                //throw new RuntimeException("Missing digest for " + path)
                // not deemed critical by distributor
                Log.d(TAG, "No digest for $path skipped")
                return
            }
            val refDigest = map[path]
            if (refDigest == null) {
                Log.e(TAG, "Null digest for $path")
                throw RuntimeException("Null digest for $path")
            }
            if (refDigest != digest) {
                Log.e(TAG, "Altered $path")
                throw RuntimeException("Altered $path")
            }
            Log.d(TAG, "MD5 of $path ok")
        }
    }

    fun computeDigest(path: String): String? {
        // Log.d(TAG, "MD5 " + path)
        val md: MessageDigest = try {
            MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            return null
        }
        try {
            FileInputStream(path).use { fis ->
                DigestInputStream(fis, md).use { dis ->
                    val buffer = ByteArray(CHUNK_SIZE)
                    while (dis.read(buffer) != -1) {
                        // consume flow: but do not use it because we are only interested in digest
                    }
                    val digest = md.digest()
                    return digestToString(*digest)
                }
            }
        } catch (e: IOException) {
            return null
        }
    }

    /**
     * Digest to string
     *
     * @param byteArray digest
     * @return string
     */
    fun digestToString(vararg byteArray: Byte): String {
        val sb = StringBuilder()
        for (b in byteArray) {
            sb.append(((b.toInt() and 0xff) + 0x100).toString(16).substring(1))
        }
        return sb.toString()
    }

    // D E L E T E

    /**
     * Empty directory recursively.
     *
     * @param dir file.
     */
    @Synchronized
    fun emptyDirectory(dir: File) {
        if (dir.isDirectory) {
            val childFiles = dir.listFiles()
            if (childFiles != null && childFiles.isNotEmpty()) {
                // Directory has other files. Need to delete them first
                for (childFile in childFiles) {
                    // Recursively delete the files
                    zap(childFile)
                }
            }
        }
        val dirContent = dir.listFiles() ?: throw RuntimeException("Null directory")
        if (dirContent.isNotEmpty()) {
            throw RuntimeException("Cannot empty $dir")
        }
    }

    /**
     * Delete this file or dir recursively.
     *
     * @param file file.
     */
    private fun zap(file: File) {
        if (file.isDirectory) {
            val childFiles = file.listFiles()
            if (childFiles != null && childFiles.isNotEmpty()) {
                // Directory has other files. Need to delete them first
                for (childFile in childFiles) {
                    // Recursively delete the files
                    zap(childFile)
                }
            }
            // Directory is empty.
        }
        //else
        //{
        // Regular file.
        //}
        file.delete()
    }

    // I N T E R F A C E S

    /**
     * Input stream getter
     */
    fun interface InputStreamGetter {

        /**
         * Get input stream from string identifier
         *
         * @param id or path
         * @return input stream
         */
        @Throws(IOException::class)
        fun getInputStream(id: String?): InputStream
    }

    /**
     * Publisher interface
     */
    interface Publisher {

        /**
         * Publish progress
         *
         * @param current current progress
         * @param total total
         */
        fun publish(current: Long, total: Long)
    }
}
