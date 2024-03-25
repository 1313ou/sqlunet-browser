/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.settings

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import java.io.File

/**
 * Storage
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object Storage {
    private const val TAG = "Storage"

    /**
     * SqlUNet DB basename
     */
    private const val DBNAME = "sqlunet"

    /**
     * SqlUNet DB filename
     */
    const val DBFILE = "$DBNAME.db"

    /**
     * SqlUNet DB zipped filename
     */
    const val DBFILEZIP = "$DBFILE.zip"

    /*
	 * SqlUNet DB sql filename
	 */
    //const val DBSQL = "$DBNAME.sql"

    /*
	 * SqlUNet DB zipped sql filename
	 */
    //const val DBSQLZIP = DBSQL + ".zip"

    /**
     * SqlUNet sub directory when external public
     */
    const val SQLUNETDIR = "sqlunet" + '/'

    /**
     * SqlUNet storage preference name
     */
    const val PREF_SQLUNET_STORAGE = "pref_storage"

    /**
     * SqlUNet cache preference name
     */
    const val PREF_SQLUNET_CACHE = "pref_cache"

    // D A T A B A S E

    /**
     * Get database storage
     *
     * @param context context
     * @return database storage directory
     */
    @JvmStatic
    @SuppressLint("CommitPrefEdits", "ApplySharedPref")
    fun getSqlUNetStorage(context: Context): File {
        // test if set in preference
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val prefValue = sharedPref.getString(PREF_SQLUNET_STORAGE, null)
        if (!prefValue.isNullOrEmpty()) 
        {
            // pref defined
            if (StorageUtils.DirType.AUTO.toString() != prefValue) {
                val prefStorage = File(prefValue)
                if (build(prefStorage)) {
                    // Log.d(TAG, "Using defined pref " + prefStorage.getAbsolutePath())
                    return prefStorage
                }
                //  pref defined as invalid value
            }
            //  pref defined as auto
        }
        //  pref not defined
        //  pref not defined || defined as auto || defined as invalid value

        // auto (as of marshmallow which allows for adopted storage)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && StorageUtils.DirType.AUTO.toString() == prefValue) 
        {
            val autoStorage = context.filesDir
            Log.d(TAG, StorageUtils.DirType.AUTO.toDisplay() + ' ' + autoStorage.absolutePath)
            return autoStorage // context.getDatabasePath(DBFILE).getParentFile()
        }

        // discover pref not defined || defined as auto but not marshmallow || defined as invalid value
        val discoveredStorage = discover(context)

        // record as discovered
        sharedPref.edit().putString(PREF_SQLUNET_STORAGE, discoveredStorage.absolutePath).commit()
        Log.d(TAG, "Saving " + discoveredStorage.absolutePath)
        return discoveredStorage
    }

    /**
     * Discover SqlUNet storage
     *
     * @param context context
     * @return SqlUNet storage
     */
    private fun discover(context: Context): File {
        val dirs = StorageUtils.getSortedStorageDirectories(context)
        for (dir in dirs) {
            if (dir.status == 0) {
                Log.d(TAG, "Select $dir")
                return dir.dir.file
            }
        }
        Log.e(TAG, "Error while looking for storage directories. External storage is " + StorageReports.reportExternalStorage(context))
        throw RuntimeException("Cannot find suitable storage directory " + StorageReports.reportStorageDirectories(context) + ' ' + StorageReports.reportExternalStorage(context))
    }

    /**
     * Build the dir and tests
     *
     * @param dir directory
     * @return true if it qualifies
     */
    private fun build(dir: File?): Boolean {
        return if (dir == null) {
            false
        } else dir.mkdirs() || dir.isDirectory()

        // make sure that path can be created and it is a directory
    }
    // C A C H E (cache is used to download SQL zip file)
    /**
     * Get data cache
     *
     * @param context context
     * @return data cache
     */
    @JvmStatic
    @SuppressLint("CommitPrefEdits", "ApplySharedPref")
    fun getCacheDir(context: Context): String {
        // test if set in preference
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        var prefValue = sharedPref.getString(PREF_SQLUNET_CACHE, null)
        if (prefValue.isNullOrEmpty()) {
            var cache: File? = null

            // consider external cache
            for (externalCache in ContextCompat.getExternalCacheDirs(context)) {
                if (externalCache != null) {
                    cache = externalCache
                    break
                }
            }
            if (cache == null) {
                // fall back on internal cache
                cache = context.cacheDir
            }
            prefValue = cache!!.absolutePath

            // record as discovered
            sharedPref.edit().putString(PREF_SQLUNET_CACHE, prefValue).commit()
        }
        return prefValue!!
    }
}
