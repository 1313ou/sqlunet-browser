/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.content.Context
import android.net.Uri
import android.text.Editable
import android.text.SpannableStringBuilder
import android.util.Log
import org.sqlunet.browser.common.R
import org.sqlunet.provider.ManagerContract.TablesAndIndices
import org.sqlunet.provider.ManagerProvider
import org.sqlunet.settings.StorageSettings
import java.io.File

/**
 * Database _status
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class Status {

    companion object {

        private const val TAG = "Status"

        // status flags
        const val EXISTS = 0x1
        const val EXISTS_TABLES = 0x2
        const val EXISTS_INDEXES = 0x10

        /**
         * Get _status
         *
         * @param context context
         * @return _status
         */
        fun status(context: Context): Int {
            if (existsDatabase(context)) {
                var status = EXISTS
                val res = context.resources
                val requiredTables = res.getStringArray(R.array.required_tables)
                val requiredIndexes = res.getStringArray(R.array.required_indexes)
                val existingTablesAndIndexes: List<String>? = try {
                    tablesAndIndexes(context)
                } catch (e: Exception) {
                    Log.e(TAG, "While getting _status", e)
                    return status
                }
                val existsTables = contains(existingTablesAndIndexes, *requiredTables)
                val existsIdx = contains(existingTablesAndIndexes, *requiredIndexes)
                if (existsTables) {
                    status = status or EXISTS_TABLES
                }
                if (existsIdx) {
                    status = status or EXISTS_INDEXES
                }
                return status
            }
            return 0
        }

        /**
         * Can run _status
         *
         * @param context context
         * @return true if app is ready to run
         */
        fun canRun(context: Context): Boolean {
            val status = status(context)
            return status and (EXISTS or EXISTS_TABLES or EXISTS_INDEXES) == EXISTS or EXISTS_TABLES or EXISTS_INDEXES
        }

        /**
         * Test existence of database
         *
         * @param context context
         * @return true if database exists
         */
        fun existsDatabase(context: Context): Boolean {
            val databasePath = StorageSettings.getDatabasePath(context)
            val db = File(databasePath)
            return db.exists() && db.isFile && db.canWrite()
        }

        /**
         * Get tables and indexes
         *
         * @param context context
         * @return list of tables and indexes
         */
        fun tablesAndIndexes(context: Context): List<String>? {
            val order = ("CASE "
                    + "WHEN " + TablesAndIndices.TYPE + " = 'table' THEN '1' "
                    + "WHEN " + TablesAndIndices.TYPE + " = 'view' THEN '2' "
                    + "WHEN " + TablesAndIndices.TYPE + " = 'index' THEN '3' "
                    + "ELSE " + TablesAndIndices.TYPE + " END ASC,"
                    + TablesAndIndices.NAME + " ASC")
            context.contentResolver.query(
                Uri.parse(ManagerProvider.makeUri(TablesAndIndices.URI)), arrayOf(TablesAndIndices.TYPE, TablesAndIndices.NAME),  // projection
                "name NOT LIKE 'sqlite_%' AND name NOT LIKE 'android_%'",  // selection criteria 
                null,
                order
            ).use {
                var result: MutableList<String>? = null
                if (it != null) {
                    if (it.moveToFirst()) {
                        val nameId = it.getColumnIndex(TablesAndIndices.NAME)
                        result = ArrayList()
                        do {
                            val name = it.getString(nameId)
                            result.add(name)
                        } while (it.moveToNext())
                    }
                }
                return result
            }
        }

        /**
         * Test if targets are contained in tables and indexes
         *
         * @param tablesAndIndexes tables and indexes
         * @param targets          targets
         * @return true if targets are contained in tables and indexes
         */
        fun contains(tablesAndIndexes: Collection<String>?, vararg targets: String): Boolean {
            if (tablesAndIndexes == null) {
                return false
            }
            val result = tablesAndIndexes.containsAll(listOf(*targets))
            if (!result) {
                for (target in targets) {
                    if (!tablesAndIndexes.contains(target)) {
                        Log.e(TAG, "Absent $target")
                    }
                }
            }
            return result
        }

        fun toString(status: Int): CharSequence {
            val sb: Editable = SpannableStringBuilder()
            sb.append(Integer.toHexString(status))
            if (status and EXISTS != 0) {
                sb.append(" file")
            }
            if (status and EXISTS_TABLES != 0) {
                sb.append(" tables")
            }
            if (status and EXISTS_INDEXES != 0) {
                sb.append(" indexes")
            }
            return sb
        }
    }
}
