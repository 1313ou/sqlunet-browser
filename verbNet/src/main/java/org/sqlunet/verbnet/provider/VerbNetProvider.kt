/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.provider

import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteCantOpenDatabaseException
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log
import org.sqlunet.provider.BaseProvider
import org.sqlunet.settings.LogUtils
import org.sqlunet.sql.SqlFormatter.format
import org.sqlunet.verbnet.provider.VerbNetContract.Lookup_VnExamples
import org.sqlunet.verbnet.provider.VerbNetContract.Lookup_VnExamples_X
import org.sqlunet.verbnet.provider.VerbNetContract.Suggest_FTS_VnWords
import org.sqlunet.verbnet.provider.VerbNetContract.Suggest_VnWords
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses_VnFrames_X
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses_VnMembers_X
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses_VnRoles_X
import org.sqlunet.verbnet.provider.VerbNetContract.VnWords
import org.sqlunet.verbnet.provider.VerbNetContract.Words_VnClasses
import org.sqlunet.verbnet.provider.VerbNetControl.queryMain
import org.sqlunet.verbnet.provider.VerbNetControl.querySearch
import org.sqlunet.verbnet.provider.VerbNetControl.querySuggest

/**
 * VerbNet provider
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class VerbNetProvider : BaseProvider() {

    // M I M E

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            VerbNetControl.VNCLASS1 -> VENDOR + ".android.cursor.item/" + VENDOR + '.' + AUTHORITY + '.' + VnClasses.URI
            VerbNetControl.VNCLASSES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + VnClasses.URI
            VerbNetControl.WORDS_VNCLASSES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Words_VnClasses.URI
            VerbNetControl.VNCLASSES_VNMEMBERS_X_BY_WORD -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + VnClasses_VnMembers_X.URI_BY_WORD
            VerbNetControl.VNCLASSES_VNROLES_X_BY_VNROLE -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + VnClasses_VnRoles_X.URI_BY_ROLE
            VerbNetControl.VNCLASSES_VNFRAMES_X_BY_VNFRAME -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + VnClasses_VnFrames_X.URI_BY_FRAME
            VerbNetControl.LOOKUP_FTS_EXAMPLES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Lookup_VnExamples.URI
            VerbNetControl.LOOKUP_FTS_EXAMPLES_X -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Lookup_VnExamples_X.URI
            VerbNetControl.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Lookup_VnExamples_X.URI_BY_EXAMPLE
            VerbNetControl.SUGGEST_WORDS, VerbNetControl.SUGGEST_FTS_WORDS -> VENDOR + ".android.cursor.item/" + VENDOR + '.' + AUTHORITY + '.' + VnWords.URI
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

        // MAIN
        var result = queryMain(code, uri.lastPathSegment!!, projection0, selection0, selectionArgs0)
        if (result == null) {
            // TEXTSEARCH
            result = querySearch(code, projection0, selection0, selectionArgs0)
        }
        // MAIN || TEXTSEARCH
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
                LogUtils.writeLog("${e}\n$sql\n", true, context!!, null)
            }
            return null
        }

        // SUGGEST
        result = querySuggest(code, uri.lastPathSegment!!)
        return if (result != null) {
            db!!.query(result.table, result.projection, result.selection, result.selectionArgs, result.groupBy, null, null)
        } else null
    }

    companion object {

        private const val TAG = "VerbNetProvider"

        // C O N T E N T   P R O V I D E R   A U T H O R I T Y

        private val AUTHORITY = makeAuthority("verbnet_authority")

        // U R I M A T C H E R

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            matchURIs()
        }

        private fun matchURIs() {
            uriMatcher.addURI(AUTHORITY, VnClasses.URI, VerbNetControl.VNCLASS1)
            uriMatcher.addURI(AUTHORITY, VnClasses.URI, VerbNetControl.VNCLASSES)
            uriMatcher.addURI(AUTHORITY, Words_VnClasses.URI, VerbNetControl.WORDS_VNCLASSES)
            uriMatcher.addURI(AUTHORITY, VnClasses_VnMembers_X.URI_BY_WORD, VerbNetControl.VNCLASSES_VNMEMBERS_X_BY_WORD)
            uriMatcher.addURI(AUTHORITY, VnClasses_VnRoles_X.URI_BY_ROLE, VerbNetControl.VNCLASSES_VNROLES_X_BY_VNROLE)
            uriMatcher.addURI(AUTHORITY, VnClasses_VnFrames_X.URI_BY_FRAME, VerbNetControl.VNCLASSES_VNFRAMES_X_BY_VNFRAME)
            uriMatcher.addURI(AUTHORITY, Lookup_VnExamples.URI + "/", VerbNetControl.LOOKUP_FTS_EXAMPLES)
            uriMatcher.addURI(AUTHORITY, Lookup_VnExamples_X.URI + "/", VerbNetControl.LOOKUP_FTS_EXAMPLES_X)
            uriMatcher.addURI(AUTHORITY, Lookup_VnExamples_X.URI_BY_EXAMPLE + "/", VerbNetControl.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE)
            uriMatcher.addURI(AUTHORITY, Suggest_VnWords.URI + "/*", VerbNetControl.SUGGEST_WORDS)
            uriMatcher.addURI(AUTHORITY, Suggest_VnWords.URI + "/", VerbNetControl.SUGGEST_WORDS)
            uriMatcher.addURI(AUTHORITY, Suggest_FTS_VnWords.URI + "/*", VerbNetControl.SUGGEST_FTS_WORDS)
            uriMatcher.addURI(AUTHORITY, Suggest_FTS_VnWords.URI + "/", VerbNetControl.SUGGEST_FTS_WORDS)
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
