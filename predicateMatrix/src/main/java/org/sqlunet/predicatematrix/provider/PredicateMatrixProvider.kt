/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.predicatematrix.provider

import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteCantOpenDatabaseException
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log
import org.sqlunet.predicatematrix.provider.PredicateMatrixControl.queryMain
import org.sqlunet.provider.BaseProvider
import org.sqlunet.settings.LogUtils
import org.sqlunet.sql.SqlFormatter.format

/**
 * PredicateMatrix provider
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class PredicateMatrixProvider : BaseProvider() {

    // M I M E

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            PredicateMatrixControl.PM -> VENDOR + ".android.cursor.item/" + VENDOR + '.' + AUTHORITY + '.' + PredicateMatrixContract.Pm.URI
            PredicateMatrixControl.PM_X -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + PredicateMatrixContract.Pm_X.URI
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

        // choose the table to query and a sort order based on the code returned for the incoming URI
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
                Log.d(TAG + "ARGS", argsToString(result.selectionArgs ?: selectionArgs0))
            }

            // do query
            try {
                val cursor = db!!.rawQuery(sql, result.selectionArgs ?: selectionArgs0)
                Log.d(TAG + "COUNT", cursor.count.toString() + " items")
                return cursor
            } catch (e: SQLiteException) {
                Log.d(TAG + "SQL", sql)
                Log.e(TAG, "PropBank provider query failed", e)
                LogUtils.writeLog("${e}\n$sql\n", true, context!!, null)
            }
        }
        return null
    }

    companion object {

        private const val TAG = "PMProvider"

        // C O N T E N T   P R O V I D E R   A U T H O R I T Y

        private val AUTHORITY = makeAuthority("predicatematrix_authority")

        // U R I M A T C H E R

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            matchURIs()
        }

        private fun matchURIs() {
            uriMatcher.addURI(AUTHORITY, PredicateMatrixContract.Pm.URI, PredicateMatrixControl.PM)
            uriMatcher.addURI(AUTHORITY, PredicateMatrixContract.Pm_X.URI, PredicateMatrixControl.PM_X)
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
    }
}
