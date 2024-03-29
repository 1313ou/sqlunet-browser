/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.settings

import android.content.Context
import androidx.preference.PreferenceManager
import org.sqlunet.settings.Storage.getSqlUNetStorage
import org.sqlunet.xnet.R
import java.io.File

/**
 * Storage settings
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object StorageSettings {

    // storage preferences
    const val PREF_STORAGE = Storage.PREF_SQLUNET_STORAGE
    const val PREF_CACHE = Storage.PREF_SQLUNET_CACHE

    // download preferences
    const val PREF_DOWNLOAD_MODE = "pref_download_mode"
    const val PREF_DOWNLOAD_SITE = "pref_download_site"
    const val PREF_DOWNLOAD_DBFILE = "pref_download_dbfile"

    // L O C A L   D A T A B A S E

    /**
     * Get database directory
     *
     * @param context context
     * @return database directory
     */
    @JvmStatic
    fun getDataDir(context: Context): String {
        val dir = getSqlUNetStorage(context)
        if (!dir.exists()) {
            dir.mkdirs()
            check(dir.exists()) { "Can't make directory " + dir.absolutePath }
        }
        return dir.absolutePath
    }

    /**
     * Get database path
     *
     * @param context context
     * @return database path
     */
    @JvmStatic
    fun getDatabasePath(context: Context): String {
        return getDataDir(context) + File.separatorChar + getDatabaseName()
    }

    /**
     * Get database name
     *
     * @return database path
     */
    @JvmStatic
    fun getDatabaseName(): String {
        return Storage.DBFILE
    }

    // C A C H E

    /**
     * Get data cache
     *
     * @param context context
     * @return data cache
     */
    @JvmStatic
    fun getCacheDir(context: Context): String {
        return Storage.getCacheDir(context)
    }

    /**
     * Get download db zipped target
     *
     * @param context context
     * @return cached target
     */
    @JvmStatic
    fun getCachedZippedPath(context: Context): String {
        return getCacheDir(context) + File.separatorChar + getDbDownloadZipName(context)
    }

    // D O W N L O A D

    /**
     * Get download site
     *
     * @param context context
     * @return download site
     */
    private fun getDownloadSite(context: Context): String {
        // test if already in preferences
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        var value = sharedPref.getString(PREF_DOWNLOAD_SITE, null)
        if (value != null) {
            return value
        }

        // set to default value read from resources
        value = context.resources.getString(R.string.default_download_site_url)

        // store value in preferences
        sharedPref.edit().putString(PREF_DOWNLOAD_SITE, value).apply()
        return value
    }

    /**
     * Get download db file
     *
     * @param context context
     * @return download db file
     */
    @JvmStatic
    fun getDbDownloadName(context: Context): String {
        // test if already already in preferences
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        var value = sharedPref.getString(PREF_DOWNLOAD_DBFILE, null)
        if (value != null) {
            return value
        }

        // set to default value read from resources
        value = context.resources.getString(R.string.default_download_datapack_file)

        // store value in preferences
        sharedPref.edit().putString(PREF_DOWNLOAD_DBFILE, value).apply()
        return value
    }

    /**
     * Get download db file
     *
     * @param context context
     * @return download db zip file
     */
    private fun getDbDownloadZipName(context: Context): String {
        return zipped(getDbDownloadName(context))
    }

    /**
     * Get download db source file
     *
     * @param context context
     * @return download db source
     */
    @JvmStatic
    fun getDbDownloadSourcePath(context: Context): String {
        return getDownloadSite(context) + '/' + getDbDownloadName(context)
    }

    /**
     * Get download db zipped source
     *
     * @param context context
     * @return download db zip source
     */
    @JvmStatic
    fun getDbDownloadZippedSourcePath(context: Context): String {
        return zipped(getDbDownloadSourcePath(context))
    }

    /**
     * Get download db source as per download mode
     *
     * @param context context
     * @param zipped  whether download mode needs zipped file or stream
     * @return download db zip source
     */
    @JvmStatic
    fun getDbDownloadSourcePath(context: Context, zipped: Boolean): String {
        return if (zipped) getDbDownloadZippedSourcePath(context) else getDbDownloadSourcePath(context)
    }

    private fun zipped(unzipped: String): String {
        return "$unzipped.zip"
    }
}
