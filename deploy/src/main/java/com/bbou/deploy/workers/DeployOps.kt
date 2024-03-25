/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.deploy.workers

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import com.bbou.concurrency.Task
import com.bbou.deploy.workers.Deploy.digestToString
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

/**
 * Deploy ops
 * Functions have a publisher parameter that acts as a sink for operation progress
 */
object DeployOps {
    private const val TAG = "DeployOps"

    /**
     * Buffer size
     */
    private const val CHUNK_SIZE = 1024

    // C O P Y

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
    @JvmStatic
    @Synchronized
    fun copyFromFile(srcFile: String, destFile: String, task: Task<String, Pair<Number, Number>, Boolean>, publisher: Publisher, publishRate: Int): Boolean {
        Log.d(TAG, "Copying from $srcFile to $destFile")
        val sourceFile = File(srcFile)
        val length = sourceFile.length()
        try {
            FileInputStream(srcFile).use { `is` ->
                FileOutputStream(destFile).use { os ->
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

                        // cancel hook
                        if (task.isCancelled()) {
                            break
                        }
                    }
                    publisher.publish(byteCount, length)
                    return true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "While copying", e)
        }
        return false
    }

    /**
     * Copy from uri
     *
     * @param srcUri      source uri
     * @param resolver    content resolver
     * @param destFile    destination file
     * @param task        async task
     * @param publisher   publisher
     * @param publishRate publish rate
     * @return true if successful
     */
    @JvmStatic
    @Synchronized
    fun copyFromUri(srcUri: Uri, resolver: ContentResolver, destFile: String, task: Task<Uri, Pair<Number, Number>, Boolean>, publisher: Publisher, publishRate: Int): Boolean {
        Log.d(TAG, "Copying from $srcUri to $destFile")
        try {
            resolver.openInputStream(srcUri).use { `is` ->
                FileOutputStream(destFile).use { os ->
                    val buffer = ByteArray(CHUNK_SIZE)
                    var byteCount: Long = 0
                    var chunkCount = 0
                    var readCount: Int
                    while (`is`!!.read(buffer).also { readCount = it } != -1) {
                        // write
                        os.write(buffer, 0, readCount)

                        // count
                        byteCount += readCount.toLong()
                        chunkCount++

                        // publish
                        if (chunkCount % publishRate == 0) {
                            publisher.publish(byteCount, -1)
                        }

                        // cancel hook
                        if (task.isCancelled()) {
                            break
                        }
                    }
                    publisher.publish(byteCount, -1)
                    return true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "While copying", e)
        }
        return false
    }

    /**
     * Copy from uri
     *
     * @param srcUrl      source url
     * @param destFile    destination file
     * @param task        async task
     * @param publisher   publisher
     * @param publishRate publish rate
     * @return true if successful
     */
    @JvmStatic
    @Synchronized
    fun copyFromUrl(srcUrl: URL, destFile: String, task: Task<URL, Pair<Number, Number>, Boolean>, publisher: Publisher, publishRate: Int): Boolean {
        Log.d(TAG, "Copying from $srcUrl to $destFile")
        try {
            srcUrl.openStream().use { `is` ->
                FileOutputStream(destFile).use { os ->
                    val buffer = ByteArray(CHUNK_SIZE)
                    var byteCount: Long = 0
                    var chunkCount = 0
                    var readCount: Int
                    while (`is`!!.read(buffer).also { readCount = it } != -1) {
                        // write
                        os.write(buffer, 0, readCount)

                        // count
                        byteCount += readCount.toLong()
                        chunkCount++

                        // publish
                        if (chunkCount % publishRate == 0) {
                            publisher.publish(byteCount, -1)
                        }

                        // cancel hook
                        if (task.isCancelled()) {
                            break
                        }
                    }
                    publisher.publish(byteCount, -1)
                    return true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "While copying", e)
        }
        return false
    }

    // U N Z I P

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
    @JvmStatic
    @Synchronized
    fun unzipFromArchiveFile(srcArchive: String, destDir: String, task: Task<String, Pair<Number, Number>, Boolean>, publisher: Publisher, publishRate: Int): Boolean {
        Log.d(TAG, "Expanding from $srcArchive to $destDir")
        try {
            ZipFile(srcArchive).use { zipFile ->
                val zipEntries = zipFile.entries()
                while (zipEntries.hasMoreElements()) {
                    val zipEntry = zipEntries.nextElement()
                    // Log.d(TAG, "Expanding zip entry  " + zipEntry.getName())
                    if (zipEntry.isDirectory) {
                        continue
                    }

                    // out
                    val outFile = File(destDir + '/' + zipEntry.name)
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

                                    // cancel hook
                                    if (task.isCancelled()) {
                                        break
                                    }
                                }
                                os.flush()
                                publisher.publish(byteCount, length)
                            }
                        }
                    } catch (e1: IOException) {
                        Log.e(TAG, "While executing from archive", e1)
                    }
                    if (outFile.exists()) {
                        Log.d(TAG, "Created : " + outFile + " exists=" + outFile.exists())
                        val stamp = zipEntry.time
                        outFile.setLastModified(stamp)
                    }
                }
                return true
            }
        } catch (e1: IOException) {
            Log.e(TAG, "While executing from archive", e1)
        }
        return false
    }

    /**
     * Unzip entries from archive uri
     *
     * @param srcUri      source archive uri
     * @param resolver    content resolver
     * @param destDir     destination dir
     * @param task        async task
     * @param publisher   publisher
     * @param publishRate publish rate
     * @return true if successful
     */
    @JvmStatic
    @Synchronized
    fun unzipFromArchiveUri(srcUri: Uri, resolver: ContentResolver, destDir: String, task: Task<Uri, Pair<Number, Number>, Boolean>, publisher: Publisher, publishRate: Int): Boolean {
        Log.d(TAG, "Expanding from $srcUri to $destDir")
        try {
            resolver.openInputStream(srcUri).use { `is` ->
                ZipInputStream(`is`).use { zis ->
                    generateSequence { zis.nextEntry }
                        .filterNot { it.isDirectory }
                        .forEach { it ->

                            // out
                            val outFile = File(destDir + '/' + it.name)

                            // create all non exists folders else you will hit FileNotFoundException for compressed folder
                            val parent = outFile.parent
                            if (parent != null) {
                                val dir = File(parent)
                                val created = dir.mkdirs()
                                Log.d(TAG, "Created : " + dir + " result=" + created + " exists=" + dir.exists())
                            }

                            // input
                            try {
                                FileOutputStream(outFile).use { os ->
                                    val length = it.size

                                    // copy
                                    val buffer = ByteArray(CHUNK_SIZE)
                                    var byteCount: Long = 0
                                    var chunkCount = 0
                                    var readCount: Int
                                    while (zis.read(buffer).also { readCount = it } != -1) {
                                        // write
                                        os.write(buffer, 0, readCount)

                                        // count
                                        byteCount += readCount.toLong()
                                        chunkCount++

                                        // publish
                                        if (chunkCount % publishRate == 0) {
                                            publisher.publish(byteCount, length)
                                        }

                                        // cancel hook
                                        if (task.isCancelled()) {
                                            break
                                        }
                                    }
                                    os.flush()
                                    publisher.publish(byteCount, length)
                                }
                            } catch (e1: IOException) {
                                Log.e(TAG, "While executing from archive", e1)
                            }
                            if (outFile.exists()) {
                                Log.d(TAG, "Created : " + outFile + " exists=" + outFile.exists())
                                val stamp = it.time
                                outFile.setLastModified(stamp)
                            }
                        }
                    return true
                }
            }
        } catch (e1: IOException) {
            Log.e(TAG, "While executing from archive", e1)
        }
        return false
    }

    /**
     * Unzip entries from archive url
     *
     * @param srcUrl      source archive url
     * @param destDir     destination dir
     * @param task        async task
     * @param publisher   publisher
     * @param publishRate publish rate
     * @return true if successful
     */
    @JvmStatic
    @Synchronized
    fun unzipFromArchiveUrl(srcUrl: URL, destDir: String, task: Task<URL, Pair<Number, Number>, Boolean>, publisher: Publisher, publishRate: Int): Boolean {
        Log.d(TAG, "Expanding from $srcUrl to $destDir")
        try {
            srcUrl.openStream().use { `is` ->
                ZipInputStream(`is`).use { zis ->
                    generateSequence { zis.nextEntry }
                        .filterNot { it.isDirectory }
                        .forEach { it ->

                            // out
                            val outFile = File(destDir + '/' + it.name)

                            // create all non exists folders else you will hit FileNotFoundException for compressed folder
                            val parent = outFile.parent
                            if (parent != null) {
                                val dir = File(parent)
                                val created = dir.mkdirs()
                                Log.d(TAG, "Created : " + dir + " result=" + created + " exists=" + dir.exists())
                            }

                            // input
                            try {
                                FileOutputStream(outFile).use { os ->
                                    val length = it.size

                                    // copy
                                    val buffer = ByteArray(CHUNK_SIZE)
                                    var byteCount: Long = 0
                                    var chunkCount = 0
                                    var readCount: Int
                                    while (zis.read(buffer).also { readCount = it } != -1) {
                                        // write
                                        os.write(buffer, 0, readCount)

                                        // count
                                        byteCount += readCount.toLong()
                                        chunkCount++

                                        // publish
                                        if (chunkCount % publishRate == 0) {
                                            publisher.publish(byteCount, length)
                                        }

                                        // cancel hook
                                        if (task.isCancelled()) {
                                            break
                                        }
                                    }
                                    os.flush()
                                    publisher.publish(byteCount, length)
                                }
                            } catch (e1: IOException) {
                                Log.e(TAG, "While executing from archive", e1)
                            }
                            if (outFile.exists()) {
                                Log.d(TAG, "Created : " + outFile + " exists=" + outFile.exists())
                                val stamp = it.time
                                outFile.setLastModified(stamp)
                            }
                        }
                    return true
                }
            }
        } catch (e1: IOException) {
            Log.e(TAG, "While executing from archive", e1)
        }
        return false
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
    @JvmStatic
    @Synchronized
    fun unzipEntryFromArchiveFile(srcArchive: String, srcEntry: String, destFile: String, task: Task<String, Pair<Number, Number>, Boolean>, publisher: Publisher, publishRate: Int): Boolean {
        Log.d(TAG, "Expanding from $srcArchive (entry $srcEntry) to $destFile")
        try {
            ZipFile(srcArchive).use { zipFile ->
                // entry
                val zipEntry = zipFile.getEntry(srcEntry) ?: throw IOException("Zip entry not found $srcEntry")

                // out
                val outFile = File(destFile)
                // Log.d(TAG, outFile + " exist=" + outFile.exists())

                // create all non exists folders else you will hit FileNotFoundException for compressed folder
                val parent = outFile.parent
                if (parent != null) {
                    val dir = File(parent)
                    val created = dir.mkdirs()
                    Log.d(TAG, "Created : " + dir + " result=" + created + " exists=" + dir.exists())
                }

                // unzip
                try {
                    zipFile.getInputStream(zipEntry).use { `is` ->
                        FileOutputStream(outFile).use { os ->
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

                                // cancel hook
                                if (task.isCancelled()) {
                                    break
                                }
                            }
                            os.flush()
                            publisher.publish(byteCount, length)
                        }
                    }
                } catch (e1: IOException) {
                    Log.e(TAG, "While executing from archive", e1)
                }

                // inherit time stamp and return
                if (outFile.exists()) {
                    Log.d(TAG, "Created : " + outFile + " exist=" + outFile.exists())
                    val stamp = zipEntry.time
                    outFile.setLastModified(stamp)
                    return true
                }
            }
        } catch (e: IOException) {
            return false
        }
        return false
    }

    /**
     * Unzip entry from archive uri
     *
     * @param srcUri      source archive uri
     * @param srcEntry    source entry
     * @param resolver    content resolver
     * @param destFile    destination file
     * @param task        async task
     * @param publisher   publisher
     * @param publishRate publish rate
     * @return true if successful
     */
    @JvmStatic
    @Synchronized
    fun unzipEntryFromArchiveUri(srcUri: Uri, srcEntry: String, resolver: ContentResolver, destFile: String, task: Task<Uri, Pair<Number, Number>, Boolean>, publisher: Publisher, publishRate: Int): Boolean {
        Log.d(TAG, "Expanding from $srcUri (entry $srcEntry) to $destFile")
        try {
            resolver.openInputStream(srcUri).use { `is` ->
                ZipInputStream(`is`).use { zis ->
                    generateSequence { zis.nextEntry }
                        .filterNot { it.isDirectory }
                        .filter { it.name == srcEntry }
                        .forEach { it ->

                            // out
                            val outFile = File(destFile)
                            // Log.d(TAG, outFile + " exist=" + outFile.exists())

                            // create all non exists folders else you will hit FileNotFoundException for compressed folder
                            val parent = outFile.parent
                            if (parent != null) {
                                val dir = File(parent)
                                val created = dir.mkdirs()
                                Log.d(TAG, "Created : " + dir + " result=" + created + " exists=" + dir.exists())
                            }

                            // unzip
                            try {
                                FileOutputStream(destFile).use { os ->
                                    val length = it.size

                                    // copy
                                    val buffer = ByteArray(CHUNK_SIZE)
                                    var byteCount: Long = 0
                                    var chunkCount = 0
                                    var readCount: Int
                                    while (zis.read(buffer).also { readCount = it } != -1) {
                                        // write
                                        os.write(buffer, 0, readCount)

                                        // count
                                        byteCount += readCount.toLong()
                                        chunkCount++

                                        // publish
                                        if (chunkCount % publishRate == 0) {
                                            publisher.publish(byteCount, length)
                                        }

                                        // cancel hook
                                        if (task.isCancelled()) {
                                            break
                                        }
                                    }
                                    os.flush()
                                    publisher.publish(byteCount, length)
                                }
                            } catch (e1: IOException) {
                                Log.e(TAG, "While executing from archive", e1)
                            }

                            // inherit time stamp and return
                            if (outFile.exists()) {
                                Log.d(TAG, "Created : " + outFile + " exists=" + outFile.exists())
                                val stamp = it.time
                                outFile.setLastModified(stamp)
                                return true
                            }
                        }
                }
            }
        } catch (e: IOException) {
            return false
        }
        return false
    }

    /**
     * Unzip entry from archive uri
     *
     * @param srcUrl      source archive url
     * @param srcEntry    source entry
     * @param destFile    destination file
     * @param task        async task
     * @param publisher   publisher
     * @param publishRate publish rate
     * @return true if successful
     */
    @JvmStatic
    @Synchronized
    fun unzipEntryFromArchiveUrl(srcUrl: URL, srcEntry: String, destFile: String, task: Task<URL, Pair<Number, Number>, Boolean>, publisher: Publisher, publishRate: Int): Boolean {
        Log.d(TAG, "Expanding from $srcUrl (entry $srcEntry) to $destFile")
        try {
            srcUrl.openStream().use { `is` ->
                ZipInputStream(`is`).use { zis ->
                    generateSequence { zis.nextEntry }
                        .filterNot { it.isDirectory }
                        .filter { it.name == srcEntry }
                        .forEach { it ->

                            // out
                            val outFile = File(destFile)
                            // Log.d(TAG, outFile + " exist=" + outFile.exists())

                            // create all non exists folders else you will hit FileNotFoundException for compressed folder
                            val parent = outFile.parent
                            if (parent != null) {
                                val dir = File(parent)
                                val created = dir.mkdirs()
                                Log.d(TAG, "Created : " + dir + " result=" + created + " exists=" + dir.exists())
                            }

                            // unzip
                            try {
                                FileOutputStream(destFile).use { os ->
                                    val length = it.size

                                    // copy
                                    val buffer = ByteArray(CHUNK_SIZE)
                                    var byteCount: Long = 0
                                    var chunkCount = 0
                                    var readCount: Int
                                    while (zis.read(buffer).also { readCount = it } != -1) {
                                        // write
                                        os.write(buffer, 0, readCount)

                                        // count
                                        byteCount += readCount.toLong()
                                        chunkCount++

                                        // publish
                                        if (chunkCount % publishRate == 0) {
                                            publisher.publish(byteCount, length)
                                        }

                                        // cancel hook
                                        if (task.isCancelled()) {
                                            break
                                        }
                                    }
                                    os.flush()
                                    publisher.publish(byteCount, length)
                                }
                            } catch (e1: IOException) {
                                Log.e(TAG, "While executing from archive", e1)
                            }

                            // inherit time stamp and return
                            if (outFile.exists()) {
                                Log.d(TAG, "Created : " + outFile + " exists=" + outFile.exists())
                                val stamp = it.time
                                outFile.setLastModified(stamp)
                                return true
                            }
                        }
                }
            }
        } catch (e: IOException) {
            return false
        }
        return false
    }

    // M D 5

    /**
     * MD5 from file
     *
     * @param srcFile     source file
     * @param task        async task
     * @param publisher   publisher
     * @param publishRate publish rate
     * @return digest if successful
     */
    @JvmStatic
    @Synchronized
    fun md5FromFile(srcFile: String, task: Task<String, Pair<Number, Number>, String?>, publisher: Publisher, publishRate: Int): String? {
        Log.d(TAG, "Md5 $srcFile")
        try {
            val md = MessageDigest.getInstance("MD5")
            val sourceFile = File(srcFile)
            val length = sourceFile.length()
            FileInputStream(srcFile).use { fis ->
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

                        // cancel hook
                        if (task.isCancelled()) {
                            break
                        }
                    }
                    val digest = md.digest()
                    return digestToString(*digest)
                }
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, srcFile, e)
            return null
        } catch (e: IOException) {
            Log.e(TAG, srcFile, e)
            return null
        }
    }

    /**
     * MD5 from uri
     *
     * @param uri         uri
     * @param resolver    content resolver
     * @param task        async task
     * @param publisher   publisher
     * @param publishRate publish rate
     * @return digest if successful
     */
    @JvmStatic
    @Synchronized
    fun md5FromUri(uri: Uri, resolver: ContentResolver, task: Task<Uri, Pair<Number, Number>, String?>, publisher: Publisher, publishRate: Int): String? {
        Log.d(TAG, "Md5 uri $uri")
        try {
            val md = MessageDigest.getInstance("MD5")
            resolver.openInputStream(uri).use { `is` ->
                DigestInputStream(`is`, md).use { dis ->
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
                            publisher.publish(byteCount, -1)
                        }

                        // cancel hook
                        if (task.isCancelled()) {
                            break
                        }
                    }
                    val digest = md.digest()
                    return digestToString(*digest)
                }
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "input stream", e)
            return null
        } catch (e: IOException) {
            Log.e(TAG, "input stream", e)
            return null
        }
    }

    /**
     * MD5 from url
     *
     * @param url         url
     * @param task        async task
     * @param publisher   publisher
     * @param publishRate publish rate
     * @return digest if successful
     */
    @JvmStatic
    @Synchronized
    fun md5FromUrl(url: URL, task: Task<URL, Pair<Number, Number>, String?>, publisher: Publisher, publishRate: Int): String? {
        Log.d(TAG, "Md5 uri $url")
        try {
            val md = MessageDigest.getInstance("MD5")
            url.openStream().use { `is` ->
                DigestInputStream(`is`, md).use { dis ->
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
                            publisher.publish(byteCount, -1)
                        }

                        // cancel hook
                        if (task.isCancelled()) {
                            break
                        }
                    }
                    val digest = md.digest()
                    return digestToString(*digest)
                }
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "input stream", e)
            return null
        } catch (e: IOException) {
            Log.e(TAG, "input stream", e)
            return null
        }
    }

    // I N T E R F A C E S

    /**
     * Progress publish interface
     */
    fun interface Publisher {

        /**
         * Publish progress
         *
         * @param current progress
         * @param total total
         */
        fun publish(current: Long, total: Long)
    }
}
