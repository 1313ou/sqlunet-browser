/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.preference

import android.content.Context
import androidx.preference.PreferenceManager
import com.bbou.download.DownloadData
import com.bbou.download.utils.FileData.Companion.makeFileDataFrom
import java.io.File

/**
 * Settings and preferences
 */
object Settings {

    // preference names

    /**
     * Key for
     */
    const val PREFERENCES_DEVICE = "preferences_device"

    /**
     * Key for
     */
    const val PREFERENCES_DATAPACK = "preferences_datapack"

    // download mode preference key

    /**
     * Key for
     */
    const val PREF_DOWNLOAD_MODE = "pref_download_mode"

    // general prefs
    private const val PREF_INITIALIZED = "pref_initialized"

    /**
     * Key for
     */
    const val PREF_REPO = "pref_repo"

    // device prefs

    /**
     * Key for
     */
    const val PREF_CACHE = "pref_cache"

    /**
     * Key for
     */
    const val PREF_DATAPACK_DIR = "pref_datapack_dir"

    // datapack preference keys

    /**
     * Key for datapack name
     */
    const val PREF_DATAPACK_NAME = "pref_datapack_name"

    /**
     * Key for datapack date
     */
    const val PREF_DATAPACK_DATE = "pref_datapack_date"

    /**
     * Key for datapack size
     */
    const val PREF_DATAPACK_SIZE = "pref_datapack_size"

    // datapack source preference keys

    /**
     * Key for datapack source (the container that was downloaded for the datapack)
     */
    const val PREF_DATAPACK_SOURCE = "pref_datapack_source"

    /**
     * Key for datapack source date
     */
    const val PREF_DATAPACK_SOURCE_DATE = "pref_datapack_source_date"

    /**
     * Key for datapack source size
     */
    const val PREF_DATAPACK_SOURCE_SIZE = "pref_datapack_source_size"

    /**
     * Key for datapack source etag
     */
    const val PREF_DATAPACK_SOURCE_ETAG = "pref_datapack_source_etag"

    /**
     * Key for datapack source version
     */
    const val PREF_DATAPACK_SOURCE_VERSION = "pref_datapack_source_version"

    /**
     * Key for datapack source static version
     */
    const val PREF_DATAPACK_SOURCE_STATIC_VERSION = "pref_datapack_source_static_version"

    /**
     * Key for datapack source type
     */
    const val PREF_DATAPACK_SOURCE_TYPE = "pref_datapack_source_type"

    // clear button preference key

    /**
     * Key for clear button
     */
    const val PREF_DATAPACK_CLEAR_BUTTON = "pref_datapack_clear"

    // F R O M

    /**
     * Get initialized flag setting
     *
     * @param context context
     * @return whether vital preferences have been initialized
     */
    @Suppress("unused")
    fun getInitializedPref(context: Context): Boolean {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPref.getBoolean(PREF_INITIALIZED, false)
    }

    /**
     * Set initialized flag, this happens on app install or version upgrade
     *
     * @param context context
     * @param flag initialized flag
     */
    @Suppress("unused")
    fun setInitializedPref(context: Context, flag: Boolean?) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        if (flag == null) {
            editor.remove(PREF_INITIALIZED)
        } else {
            editor.putBoolean(PREF_INITIALIZED, flag)
        }
        editor.apply()
    }

    // F R O M

    /**
     * Get download source
     *
     * @param context context
     * @return download source
     */
    @Suppress("unused")
    fun getDownloadSource(context: Context): String {
        val repo = getRepoPref(context)
        val name = getDatapackName(context)
        return "${repo}/${name}"
    }

    // T O

    /**
     * Get download target
     *
     * @param context context
     * @param name name, by default datapack name
     * @return download target
     */
    fun getDownloadTarget(context: Context, name: String? = getDatapackName(context)): String {
        val dest = getDatapackDir(context)
        return "${dest}/${name}"
    }

    /**
     * Get download cache target
     *
     * @param context context
     * @param name name, by default datapack name
     * @return download cache target
     */
    @Suppress("unused")
    fun getDownloadCacheTarget(context: Context, name: String? = getDatapackName(context)): String {
        val dest = getCachePref(context)
        return "${dest}/${name}"
    }

    // R E P O

    /**
     * Get repo preference
     *
     * @param context context
     * @return repo preference
     */
    fun getRepoPref(context: Context): String? {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPref.getString(PREF_REPO, null)
    }

    /**
     * Set repo preference
     *
     * @param context context
     * @param repo repo
     */
    @Suppress("unused")
    fun setRepoPref(context: Context, repo: String?) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        if (repo == null) {
            editor.remove(PREF_REPO)
        } else {
            editor.putString(PREF_REPO, repo)
        }
        editor.apply()
    }

    // D E V I C E

    /**
     * Get datapack location on device preference
     *
     * @param context context
     * @return datapack preference
     */
    fun getDatapackDir(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREFERENCES_DEVICE, Context.MODE_PRIVATE)
        return sharedPref.getString(PREF_DATAPACK_DIR, null)
    }

    /**
     * Set datapack location on device preference
     *
     * @param context context
     * @param dest datapack location on device
     */
    @Suppress("unused")
    fun setDatapackDir(context: Context, dest: String?) {
        val sharedPref = context.getSharedPreferences(PREFERENCES_DEVICE, Context.MODE_PRIVATE)
        sharedPref.edit().putString(PREF_DATAPACK_DIR, dest).apply()
    }

    /**
     * Get cache location on device
     *
     * @param context context
     * @return cache location on device
     */
    fun getCachePref(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREFERENCES_DEVICE, Context.MODE_PRIVATE)
        return sharedPref.getString(PREF_CACHE, null)
    }

    /**
     * Set cache location on device
     *
     * @param context context
     * @param cache cache location on device
     */
    @Suppress("unused")
    fun setCachePref(context: Context, cache: String?) {
        val sharedPref = context.getSharedPreferences(PREFERENCES_DEVICE, Context.MODE_PRIVATE)
        sharedPref.edit().putString(PREF_CACHE, cache).apply()
    }

    // M O D E L

    /**
     * Get datapack name
     *
     * @param context context
     * @return name
     */
    fun getDatapackName(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE)
        return sharedPref.getString(PREF_DATAPACK_NAME, null)
    }

    /**
     * Set datapack name
     *
     * @param context context
     * @param name    name
     */
    fun setDatapackName(context: Context, name: String?) {
        val sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE)
        sharedPref.edit().putString(PREF_DATAPACK_NAME, name).apply()
    }

    /**
     * Get datapack date
     *
     * @param context context
     * @return timestamp
     */
    fun getDatapackDate(context: Context): Long {
        val sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE)
        return sharedPref.getLong(PREF_DATAPACK_DATE, -1)
    }

    /**
     * Set datapack date
     *
     * @param context   context
     * @param timestamp timestamp
     */
    fun setDatapackDate(context: Context, timestamp: Long) {
        val sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE)
        sharedPref.edit().putLong(PREF_DATAPACK_DATE, timestamp).apply()
    }

    /**
     * Get datapack size
     *
     * @param context context
     * @return size
     */
    fun getDatapackSize(context: Context): Long {
        val sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE)
        return sharedPref.getLong(PREF_DATAPACK_SIZE, -1)
    }

    /**
     * Set datapack size
     *
     * @param context context
     * @param size    size
     */
    fun setDatapackSize(context: Context, size: Long) {
        val sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE)
        sharedPref.edit().putLong(PREF_DATAPACK_SIZE, size).apply()
    }

    /**
     * Get datapack source
     *
     * @param context context
     * @return datapack source (repo)
     */
    fun getDatapackSource(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE)
        return sharedPref.getString(PREF_DATAPACK_SOURCE, null)
    }

    /**
     * Get datapack source date
     *
     * @param context context
     * @return timestamp
     */
    fun getDatapackSourceDate(context: Context): Long {
        val sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE)
        return sharedPref.getLong(PREF_DATAPACK_SOURCE_DATE, -1)
    }

    /**
     * Get datapack source size
     *
     * @param context context
     * @return size
     */
    fun getDatapackSourceSize(context: Context): Long {
        val sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE)
        return sharedPref.getLong(PREF_DATAPACK_SOURCE_SIZE, -1)
    }

    /**
     * Get datapack source etag
     *
     * @param context context
     * @return etag
     */
    fun getDatapackSourceEtag(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE)
        return sharedPref.getString(PREF_DATAPACK_SOURCE_ETAG, null)
    }

    /**
     * Get datapack source version
     *
     * @param context context
     * @return version
     */
    fun getDatapackSourceVersion(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE)
        return sharedPref.getString(PREF_DATAPACK_SOURCE_VERSION, null)
    }

    /**
     * Get datapack source static version
     *
     * @param context context
     * @return static version
     */
    fun getDatapackSourceStaticVersion(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE)
        return sharedPref.getString(PREF_DATAPACK_SOURCE_STATIC_VERSION, null)
    }

    /**
     * Get datapack source type
     *
     * @param context context
     * @return type
     */
    fun getDatapackSourceType(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE)
        return sharedPref.getString(PREF_DATAPACK_SOURCE_TYPE, null)
    }

    /**
     * Record datapack info
     *
     * @param context      context
     * @param datapackFile datapack file
     */
    fun recordDatapackFile(context: Context, datapackFile: File) {
        val sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE)
        val edit = sharedPref.edit()
        if (!datapackFile.exists()) {
            edit.putString(PREF_DATAPACK_NAME, datapackFile.name)
            edit.remove(PREF_DATAPACK_DATE)
            edit.remove(PREF_DATAPACK_SIZE)
            edit.apply()
            return
        }
        if (datapackFile.isDirectory) {
            edit.remove(PREF_DATAPACK_NAME)
            edit.remove(PREF_DATAPACK_DATE)
            edit.remove(PREF_DATAPACK_SIZE)
            edit.apply()
        } else {
            val fileData = makeFileDataFrom(datapackFile)
            if (fileData != null) {
                if (fileData.name != null) {
                    edit.putString(PREF_DATAPACK_NAME, fileData.name)
                } else {
                    edit.remove(PREF_DATAPACK_NAME)
                }
                if (fileData.date != -1L) {
                    edit.putLong(PREF_DATAPACK_DATE, fileData.date)
                } else {
                    edit.remove(PREF_DATAPACK_DATE)
                }
                if (fileData.size != -1L) {
                    edit.putLong(PREF_DATAPACK_SIZE, fileData.size)
                } else {
                    edit.remove(PREF_DATAPACK_SIZE)
                }
                edit.apply()
            } else {
                edit.clear()
            }
        }
    }

    /**
     * Record datapack info
     *
     * @param context     context
     * @param datapackUri datapack uri
     */
    @Suppress("unused")
    fun recordDatapackUri(context: Context, datapackUri: String?) {
        val sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE)
        val edit = sharedPref.edit()
        if (datapackUri != null) {
            edit.putString(PREF_DATAPACK_NAME, datapackUri)
        } else {
            edit.remove(PREF_DATAPACK_NAME)
        }
        edit.remove(PREF_DATAPACK_DATE)
        edit.remove(PREF_DATAPACK_SIZE)
        edit.apply()
    }

    /**
     * Unrecord datapack info
     *
     * @param context context
     */
    fun unrecordDatapack(context: Context) {
        val sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE)
        val edit = sharedPref.edit()
        edit.remove(PREF_DATAPACK_NAME)
            .remove(PREF_DATAPACK_DATE)
            .remove(PREF_DATAPACK_SIZE)
            .apply()
    }

    /**
     * Record datapack source info
     *
     * @param context context
     * @param dl downloadDat
     * @param sourceType source type ("download", "asset", ...)
     */
    fun recordDatapackSource(context: Context, dl: DownloadData, sourceType: String?) {

        val sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE)
        val edit = sharedPref.edit()
        if (dl.fromUrl != null) {
            edit.putString(PREF_DATAPACK_SOURCE, dl.fromUrl)
        } else {
            edit.remove(PREF_DATAPACK_SOURCE)
        }
        if (dl.size != null && dl.size != -1L) {
            edit.putLong(PREF_DATAPACK_SOURCE_SIZE, dl.size)
        } else {
            edit.remove(PREF_DATAPACK_SOURCE_SIZE)
        }
        if (dl.date != null && dl.date != -1L) {
            edit.putLong(PREF_DATAPACK_SOURCE_DATE, dl.date)
        } else {
            edit.remove(PREF_DATAPACK_SOURCE_DATE)
        }
        if (dl.etag != null) {
            edit.putString(PREF_DATAPACK_SOURCE_ETAG, dl.etag)
        } else {
            edit.remove(PREF_DATAPACK_SOURCE_ETAG)
        }
        if (dl.version != null) {
            edit.putString(PREF_DATAPACK_SOURCE_VERSION, dl.version)
        } else {
            edit.remove(PREF_DATAPACK_SOURCE_VERSION)
        }
        if (dl.staticVersion != null) {
            edit.putString(PREF_DATAPACK_SOURCE_STATIC_VERSION, dl.staticVersion)
        } else {
            edit.remove(PREF_DATAPACK_SOURCE_STATIC_VERSION)
        }
        if (sourceType != null) {
            edit.putString(PREF_DATAPACK_SOURCE_TYPE, sourceType)
        } else {
            edit.remove(PREF_DATAPACK_SOURCE_TYPE)
        }
        edit.apply()
    }

    /**
     * Clear datapack source info
     *
     * @param context context
     */
    @Suppress("unused")
    fun unrecordDatapackSource(context: Context) {
        val sharedPref = context.getSharedPreferences(PREFERENCES_DATAPACK, Context.MODE_PRIVATE)
        sharedPref.edit()
            .remove(PREF_DATAPACK_SOURCE)
            .remove(PREF_DATAPACK_SOURCE_DATE)
            .remove(PREF_DATAPACK_SOURCE_SIZE)
            .remove(PREF_DATAPACK_SOURCE_ETAG)
            .remove(PREF_DATAPACK_SOURCE_VERSION)
            .remove(PREF_DATAPACK_SOURCE_STATIC_VERSION)
            .remove(PREF_DATAPACK_SOURCE_TYPE)
            .apply()
    }

    /**
     * Downloaders
     */
    enum class Downloader {

        /**
         * Plain download
         */
        DOWNLOAD,

        /**
         * Download from a zipped archive (data in transfer is compressed). Requires a source sipped archive.
         */
        DOWNLOAD_ZIP;
    }

    /**
     * Download modes
     */
    enum class Mode {

        /**
         * Plain download
         */
        DOWNLOAD,

        /**
         * Download from a zipped archive (data in transfer is compressed) and decompressed on the fly.
         */
        DOWNLOAD_ZIP,

        /**
         * Plain download of a zipped archive (data in transfer is compressed) which is then decompressed. Requires temporary storage space prior to expansion.
         */
        DOWNLOAD_ZIP_THEN_UNZIP;

        /**
         * Mode to downloader type
         *
         * @return downloader type
         */
        fun toDownloader(): Downloader {
            return when (this) {
                DOWNLOAD, DOWNLOAD_ZIP_THEN_UNZIP -> Downloader.DOWNLOAD
                DOWNLOAD_ZIP -> Downloader.DOWNLOAD_ZIP
            }
        }

        companion object {

            private fun getModePrefString(context: Context): String? {
                val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
                return sharedPref.getString(PREF_DOWNLOAD_MODE, null)
            }

            /**
             * Get mode preference
             *
             * @param context context
             * @return mode preference
             */
            fun getModePref(context: Context): Mode? {
                val str = getModePrefString(context) ?: return null
                return valueOf(str)
            }

            /**
             * Set mode preference
             *
             * @param context context
             * @param value mode value
             */
            @Suppress("unused")
            fun setModePref(context: Context, value: Mode) {
                val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
                sharedPref.edit().putString(PREF_DOWNLOAD_MODE, value.toString()).apply()
            }
        }
    }
}
