/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.provider

import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteCantOpenDatabaseException
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log
import org.sqlunet.propbank.provider.PropBankContract.Lookup_PbExamples
import org.sqlunet.propbank.provider.PropBankContract.Lookup_PbExamples_X
import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets
import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets_PbExamples
import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets_PbRoles
import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets_X
import org.sqlunet.propbank.provider.PropBankContract.PbWords
import org.sqlunet.propbank.provider.PropBankContract.Suggest_FTS_PbWords
import org.sqlunet.propbank.provider.PropBankContract.Suggest_PbWords
import org.sqlunet.propbank.provider.PropBankContract.Words_PbRoleSets
import org.sqlunet.propbank.provider.PropBankControl.queryMain
import org.sqlunet.propbank.provider.PropBankControl.querySearch
import org.sqlunet.propbank.provider.PropBankControl.querySuggest
import org.sqlunet.provider.BaseProvider
import org.sqlunet.sql.SqlFormatter.format

/**
 * PropBank provider
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class PropBankProvider : BaseProvider() {

    // M I M E

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            PropBankControl.PBROLESET -> VENDOR + ".android.cursor.item/" + VENDOR + '.' + AUTHORITY + '.' + PbRoleSets.URI
            PropBankControl.PBROLESETS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + PbRoleSets.URI
            PropBankControl.PBROLESETS_X -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + PbRoleSets_X.URI
            PropBankControl.PBROLESETS_X_BY_ROLESET -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + PbRoleSets_X.URI_BY_ROLESET
            PropBankControl.WORDS_PBROLESETS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Words_PbRoleSets.URI
            PropBankControl.PBROLESETS_PBROLES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + PbRoleSets_PbRoles.URI
            PropBankControl.PBROLESETS_PBEXAMPLES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + PbRoleSets_PbExamples.URI
            PropBankControl.PBROLESETS_PBEXAMPLES_BY_EXAMPLE -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + PbRoleSets_PbExamples.URI_BY_EXAMPLE
            PropBankControl.LOOKUP_FTS_EXAMPLES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Lookup_PbExamples.URI
            PropBankControl.LOOKUP_FTS_EXAMPLES_X -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Lookup_PbExamples_X.URI
            PropBankControl.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Lookup_PbExamples_X.URI_BY_EXAMPLE
            PropBankControl.SUGGEST_WORDS, PropBankControl.SUGGEST_FTS_WORDS -> VENDOR + ".android.cursor.item/" + VENDOR + '.' + AUTHORITY + '.' + PbWords.URI
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
        var result: PropBankControl.Result?
        // MAIN
        result = queryMain(code, projection0, selection0, selectionArgs0)
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

        private const val TAG = "PropBankProvider"

        // C O N T E N T   P R O V I D E R   A U T H O R I T Y

        private val AUTHORITY = makeAuthority("propbank_authority")

        // U R I M A T C H E R

        // uri matcher
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            matchURIs()
        }

        private fun matchURIs() {
            uriMatcher.addURI(AUTHORITY, PbRoleSets.URI, PropBankControl.PBROLESET)
            uriMatcher.addURI(AUTHORITY, PbRoleSets.URI, PropBankControl.PBROLESETS)
            uriMatcher.addURI(AUTHORITY, PbRoleSets_X.URI, PropBankControl.PBROLESETS_X)
            uriMatcher.addURI(AUTHORITY, PbRoleSets_X.URI_BY_ROLESET, PropBankControl.PBROLESETS_X_BY_ROLESET)
            uriMatcher.addURI(AUTHORITY, Words_PbRoleSets.URI, PropBankControl.WORDS_PBROLESETS)
            uriMatcher.addURI(AUTHORITY, PbRoleSets_PbRoles.URI, PropBankControl.PBROLESETS_PBROLES)
            uriMatcher.addURI(AUTHORITY, PbRoleSets_PbExamples.URI, PropBankControl.PBROLESETS_PBEXAMPLES)
            uriMatcher.addURI(AUTHORITY, PbRoleSets_PbExamples.URI_BY_EXAMPLE, PropBankControl.PBROLESETS_PBEXAMPLES_BY_EXAMPLE)
            uriMatcher.addURI(AUTHORITY, Lookup_PbExamples.URI + "/", PropBankControl.LOOKUP_FTS_EXAMPLES)
            uriMatcher.addURI(AUTHORITY, Lookup_PbExamples_X.URI + "/", PropBankControl.LOOKUP_FTS_EXAMPLES_X)
            uriMatcher.addURI(AUTHORITY, Lookup_PbExamples_X.URI_BY_EXAMPLE + "/", PropBankControl.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE)
            uriMatcher.addURI(AUTHORITY, Suggest_PbWords.URI + "/*", PropBankControl.SUGGEST_WORDS)
            uriMatcher.addURI(AUTHORITY, Suggest_PbWords.URI + "/", PropBankControl.SUGGEST_WORDS)
            uriMatcher.addURI(AUTHORITY, Suggest_FTS_PbWords.URI + "/*", PropBankControl.SUGGEST_FTS_WORDS)
            uriMatcher.addURI(AUTHORITY, Suggest_FTS_PbWords.URI + "/", PropBankControl.SUGGEST_FTS_WORDS)
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
