/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.settings

import android.content.Context
import android.util.Log
import org.sqlunet.dom.DomTransformer
import org.w3c.dom.Document
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * Log utilities
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object LogUtils {

    private const val TAG = "LogUtils"

    const val SQL_LOG = "sqlunet.log"

    const val DOC_LOG = "sqlunet_doc.log"

    /**
     * Write long text to log file
     *
     * @param text     text to write
     * @param append   whether to append to file
     * @param context  context
     * @param fileName file name
     */
    @JvmStatic
    fun writeLog(text: CharSequence, append: Boolean, context: Context, fileName: String?): String? {
        val storage = context.cacheDir
        val logFile = File(storage, fileName ?: SQL_LOG)
        return writeLog(text, append, logFile)
    }

    /**
     * Write long text to log file
     *
     * @param text     text to write
     * @param append   whether to append to file
     * @param logFile  file to log to
     */
    @JvmStatic
    fun writeLog(text: CharSequence, append: Boolean, logFile: File): String? {
        try {
            logFile.createNewFile()
        } catch (e: IOException) {
            Log.e(TAG, "Cannot create file $logFile", e)
            return null
        }
        try {
            val buf = BufferedWriter(FileWriter(logFile, append))
            buf.append(text)
            buf.newLine()
            buf.close()
        } catch (e: IOException) {
            Log.e(TAG, "Cannot write", e)
        }
        val logFilePath = logFile.absolutePath
        Log.d("LOG", logFilePath)
        return logFilePath
    }

    /**
     * Write documents to log file
     *
     * @param append    whether to append to file
     * @param context   context
     * @param fileName0 file name
     * @param docs      documents
     */
    @JvmStatic
    fun writeLog(append: Boolean, context: Context, fileName0: String?, vararg docs: Document): String? {
        val fileName = fileName0 ?: DOC_LOG
        val sb = StringBuilder()
        for (doc in docs) {
            sb.append(DomTransformer.docToXml(doc))
        }
        val data = sb.toString()
        return writeLog(data, append, context, fileName)
    }

    fun clearLogs(context: Context) {
        val storage = context.cacheDir
        for (logFile in listOf(File(storage, SQL_LOG), File(storage, DOC_LOG))) {
            if (logFile.exists()) {
                logFile.delete()
            }
        }
    }
}
