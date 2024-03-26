/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.provider

import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteCantOpenDatabaseException
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log
import org.sqlunet.provider.BaseProvider
import org.sqlunet.sql.SqlFormatter.format
import org.sqlunet.syntagnet.provider.SyntagNetContract.SnCollocations
import org.sqlunet.syntagnet.provider.SyntagNetContract.SnCollocations_X
import org.sqlunet.syntagnet.provider.SyntagNetControl.queryMain

/**
 * SyntagNet provider
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SyntagNetProvider : BaseProvider() {

    // M I M E

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            SyntagNetControl.COLLOCATIONS -> VENDOR + ".android.cursor.item/" + VENDOR + '.' + AUTHORITY + '.' + SnCollocations.URI
            SyntagNetControl.COLLOCATIONS_X -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + SnCollocations_X.URI
            else -> throw UnsupportedOperationException("Illegal MIME type")
        }
    }

    // Q U E R Y

    override fun query(uri: Uri, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?, sortOrder0: String?): Cursor? {
        if (db == null) {
            try {
                openReadOnly()
            } catch (e: SQLiteCantOpenDatabaseException) {
                return null
            }
        }
        val code = uriMatcher.match(uri)
        Log.d(TAG + "URI", String.format("%s (code %s)\n", uri, code))
        if (code == UriMatcher.NO_MATCH) {
            throw RuntimeException("Malformed URI $uri")
        }
        val result = queryMain(code, projection0, selection0, selectionArgs0)
        if (result != null) {
            val sql = SQLiteQueryBuilder.buildQueryString(false, result.table, result.projection, result.selection, result.groupBy, null, sortOrder0, null)
            logSql(sql, *selectionArgs0 ?: arrayOf())
            if (logSql) {
                Log.d(TAG + "SQL", format(sql).toString())
                Log.d(TAG + "ARG", argsToString(result.selectionArgs ?: selectionArgs0))
            }

            // do query
            try {
                val cursor = db!!.rawQuery(sql, result.selectionArgs ?: selectionArgs0)
                Log.d(TAG + "COUNT", cursor.count.toString() + " items")
                return cursor
            } catch (e: SQLiteException) {
                Log.d(TAG + "SQL", sql)
                Log.e(TAG, "WordNet provider query failed", e)
            }
        }
        return null
    }

    companion object {
        private const val TAG = "SyntagNetProvider"

        // C O N T E N T   P R O V I D E R   A U T H O R I T Y

        private val AUTHORITY = makeAuthority("syntagnet_authority")

        // U R I M A T C H E R

        // uri matcher
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            matchURIs()
        }

        private fun matchURIs() {
            uriMatcher.addURI(AUTHORITY, SnCollocations.URI, SyntagNetControl.COLLOCATIONS)
            uriMatcher.addURI(AUTHORITY, SnCollocations_X.URI, SyntagNetControl.COLLOCATIONS_X)
        }

        @JvmStatic
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
    }
}
