/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.content.Context
import android.os.Build
import android.util.Log
import org.sqlunet.browser.common.R
import org.sqlunet.browser.config.DownloadIntentFactory.makeIntent
import org.sqlunet.settings.StorageSettings

/**
 * Manage database tasks
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object SetupDatabaseTasks {

    private const val TAG = "SetupDatabaseTasks"

    /**
     * Create database
     *
     * @param context      context
     * @param databasePath path
     * @return true if successful
     */
    @JvmStatic
    fun createDatabase(context: Context, databasePath: String?): Boolean {
        try {
            val db = context.openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null)
            db.close()
            return true
        } catch (e: Exception) {
            Log.e(TAG, "While creating database", e)
        }
        return false
    }

    /**
     * Delete database
     *
     * @param context      context
     * @param databasePath path
     * @return true if successful
     */
    @JvmStatic
    fun deleteDatabase(context: Context, databasePath: String?): Boolean {
        // make sure you close all connections before deleting
        val authorities = context.resources.getStringArray(R.array.provider_authorities)
        for (authority in authorities) {
            val client = context.contentResolver.acquireContentProviderClient(authority!!)!!
            val provider = client.localContentProvider!!
            provider.shutdown()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                client.close()
            } else {
                @Suppress("DEPRECATION")
                client.release()
            }
        }

        // delete
        val result = context.deleteDatabase(databasePath)
        Log.d(TAG, "Dropping database: $result")
        return result
    }

    /**
     * Update data
     *
     * @param context context
     */
    @JvmStatic
    fun update(context: Context) {
        val success = deleteDatabase(context, StorageSettings.getDatabasePath(context))
        if (success) {
            val intent = makeIntent(context)
            context.startActivity(intent)
        }
    }

    /**
     * Drop tables
     *
     * @param context      context
     * @param databasePath path
     * @param tables       tables to drop
     */
    fun dropAll(context: Context, databasePath: String?, tables: Collection<String>?) {
        if (!tables.isNullOrEmpty()) {
            val db = context.openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null)
            for (table in tables) {
                db.execSQL("DROP TABLE IF EXISTS $table")
                Log.d(TAG, "$table: dropped")
            }
            db.close()
        }
    }

    /**
     * Flush tables
     *
     * @param context      context
     * @param databasePath path
     * @param tables       tables to flush
     */
    fun flushAll(context: Context, databasePath: String?, tables: Collection<String>?) {
        if (!tables.isNullOrEmpty()) {
            val db = context.openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null)
            for (table in tables) {
                val deletedRows = db.delete(table, null, null)
                Log.d(TAG, "$table: deleted $deletedRows rows")
            }
            db.close()
        }
    }
}
