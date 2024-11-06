/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.utils

import android.content.Context
import com.bbou.download.preference.Settings
import java.io.File
import java.util.Date

/**
 * File data
 *
 * @property name name
 * @property date date
 * @property size size
 * @property etag etag
 * @property version version
 * @property staticVersion static version
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
data class FileData(val name: String?, val date: Long, val size: Long, val etag: String?, val version: String?, val staticVersion: String?) {

    /**
     * Get size
     *
     * @return size or -1
     */
    fun getSize(): Long? {
        return if (size == -1L) null else size
    }

    /**
     * Get date
     *
     * @return date
     */
    fun getDate(): Date? {
        return if (date == -1L || date == 0L) null else Date(date)
    }

    companion object {

        /**
         * Make file data from file
         *
         * @param file file
         * @return filedata
         */
        fun makeFileDataFrom(file: File?): FileData? {
            return if (file != null && file.exists()) {
                FileData(file.name, file.lastModified(), file.length(), null, null, null)
            } else null
        }

        /**
         * Get current datapack's data from settings
         *
         * @param context context
         * @return current data
         */
        fun getCurrentData(context: Context): FileData {
            val name = Settings.getDatapackName(context)
            val date = Settings.getDatapackDate(context)
            val size = Settings.getDatapackSize(context)
            val etag = Settings.getDatapackSourceEtag(context)
            val version = Settings.getDatapackSourceVersion(context)
            val staticVersion = Settings.getDatapackSourceStaticVersion(context)
            return FileData(name, date, size, etag, version, staticVersion)
        }

        /**
         * Record datapack file data
         *
         * @param context context
         * @param datapackFile datapack file
         */
        fun recordDatapackFile(context: Context, datapackFile: File) {
            Settings.setDatapackName(context, datapackFile.name)
            val fileData = makeFileDataFrom(datapackFile)
            if (fileData != null) {
                if (fileData.date != -1L) {
                    Settings.setDatapackDate(context, fileData.date)
                }
                if (fileData.size != -1L) {
                    Settings.setDatapackSize(context, fileData.size)
                }
            }
        }
    }
}
