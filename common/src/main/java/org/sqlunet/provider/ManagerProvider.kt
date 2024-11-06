/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.provider

import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteCantOpenDatabaseException
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log
import org.sqlunet.provider.ManagerContract.TablesAndIndices
import org.sqlunet.settings.LogUtils

/**
 * (House-keeping) Manager provider
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class ManagerProvider : BaseProvider() {

    // M I M E

    override fun getType(uri: Uri): String {
        if (uriMatcher.match(uri) == TABLES_AND_INDICES) {
            return VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + TablesAndIndices.TABLE
        }
        throw UnsupportedOperationException("Illegal MIME type")
    }

    // Q U E R Y

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {

        if (db == null) {
            try {
                openReadWrite()
            } catch (e: SQLiteCantOpenDatabaseException) {
                return null
            }
        }

        // choose the table to query and a sort order based on the code returned for the incoming URI
        val code = uriMatcher.match(uri)
        // Log.d(TAG + "URI", String.format("%s (code %s)\n", uri, code))
        val table: String = when (code) {
            TABLES_AND_INDICES -> uri.lastPathSegment!!
            UriMatcher.NO_MATCH -> throw RuntimeException("Malformed URI $uri")
            else -> throw RuntimeException("Malformed URI $uri")
        }

        //if (logSql) {
        //  val sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, selection, null, null, sortOrder, null)
        //  logSql(sql, argsToString(selectionArgs))
        //  Log.d(TAG + "SQL", SqlFormatter.format(sql).toString())
        //  Log.d(TAG + "ARGS", argsToString(selectionArgs))
        //}

        // do query
        return try {
            db!!.query(table, projection, selection, selectionArgs, null, null, sortOrder)
        } catch (e: SQLiteException) {
            val sql2 = SQLiteQueryBuilder.buildQueryString(false, table, projection, selection, null, null, sortOrder, null)
            Log.d(TAG + "SQL", sql2)
            Log.e(TAG, "Manager provider query failed", e)
            LogUtils.writeLog("${e}\n$sql2\n", true, context!!, null)
            null
        }
    }

    companion object {

        private const val TAG = "ManagerProvider"

        // C O N T E N T   P R O V I D E R   A U T H O R I T Y

        private val AUTHORITY = makeAuthority("manager_authority")

        // U R I M A T C H E R

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            matchURIs()
        }

        // join codes
        private const val TABLES_AND_INDICES = 100
        private fun matchURIs() {
            uriMatcher.addURI(AUTHORITY, TablesAndIndices.TABLE, TABLES_AND_INDICES)
        }

        fun makeUri(table: String): String {
            return "$SCHEME$AUTHORITY/$table"
        }

        // C L O S E

        /**
         * Close provider
         *
         * @param context context
         */
        fun close(context: Context) {
            val uri = Uri.parse(SCHEME + AUTHORITY)
            closeProvider(context, uri)
        }

        /**
         * Get tables utility
         *
         * @param context context
         * @return collection of tables
         */
        @Suppress("unused")
        fun getTables(context: Context): Collection<String> {
            val tables: MutableCollection<String> = ArrayList()
            val uri = Uri.parse(makeUri(TablesAndIndices.URI))
            val projection = arrayOf(TablesAndIndices.TYPE, TablesAndIndices.NAME)
            val selection = TablesAndIndices.TYPE + " = 'table' AND name NOT IN ('sqlite_sequence', 'android_metadata' )"
            val selectionArgs = arrayOf<String>()
            context.contentResolver.query(uri, projection, selection, selectionArgs, null).use {
                if (it != null) {
                    if (it.moveToFirst()) {
                        val idName = it.getColumnIndex(TablesAndIndices.NAME)
                        do {
                            val table = it.getString(idName)
                            tables.add(table)
                        } while (it.moveToNext())
                    }
                }
            }
            return tables
        }
    }
}
