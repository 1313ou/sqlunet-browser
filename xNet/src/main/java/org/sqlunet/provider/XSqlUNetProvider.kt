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
import org.sqlunet.provider.XNetContract.PredicateMatrix_FrameNet
import org.sqlunet.provider.XNetContract.PredicateMatrix_PropBank
import org.sqlunet.provider.XNetContract.PredicateMatrix_VerbNet
import org.sqlunet.provider.XNetContract.Words_FnWords_FnFrames_U
import org.sqlunet.provider.XNetContract.Words_FnWords_PbWords_VnWords
import org.sqlunet.provider.XNetContract.Words_PbWords_PbRoleSets
import org.sqlunet.provider.XNetContract.Words_PbWords_PbRoleSets_U
import org.sqlunet.provider.XNetContract.Words_PbWords_VnWords
import org.sqlunet.provider.XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords
import org.sqlunet.provider.XNetContract.Words_VnWords_VnClasses
import org.sqlunet.provider.XNetContract.Words_VnWords_VnClasses_U
import org.sqlunet.provider.XNetControl.queryMain
import org.sqlunet.sql.SqlFormatter.format

/**
 * Extended cross WordNet-FrameNet-PropBank-VerbNet provider
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class XSqlUNetProvider : BaseProvider() {

    // M I M E

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            XNetControl.WORDS_FNWORDS_PBWORDS_VNWORDS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Words_FnWords_PbWords_VnWords.URI
            XNetControl.WORDS_PRONUNCIATIONS_FNWORDS_PBWORDS_VNWORDS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Words_Pronunciations_FnWords_PbWords_VnWords.URI
            XNetControl.WORDS_PBWORDS_VNWORDS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Words_PbWords_VnWords.URI
            XNetControl.PREDICATEMATRIX -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + XNetContract.PredicateMatrix.URI
            XNetControl.PREDICATEMATRIX_VERBNET -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + PredicateMatrix_VerbNet.URI
            XNetControl.PREDICATEMATRIX_PROPBANK -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + PredicateMatrix_PropBank.URI
            XNetControl.PREDICATEMATRIX_FRAMENET -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + PredicateMatrix_FrameNet.URI
            XNetControl.WORDS_VNWORDS_VNCLASSES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Words_VnWords_VnClasses.URI
            XNetControl.WORDS_VNWORDS_VNCLASSES_U -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Words_VnWords_VnClasses_U.URI
            XNetControl.WORDS_PBWORDS_PBROLESETS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Words_PbWords_PbRoleSets.URI
            XNetControl.WORDS_PBWORDS_PBROLESETS_U -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Words_PbWords_PbRoleSets_U.URI
            XNetControl.WORDS_FNWORDS_FNFRAMES_U -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Words_FnWords_FnFrames_U.URI
            XNetControl.SOURCES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + XNetContract.Sources.URI
            XNetControl.META -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + XNetContract.Meta.URI
            else -> throw UnsupportedOperationException("Illegal MIME type")
        }
    }

    // Q U E R Y

    /**
     * Query
     *
     * @param uri           uri
     * @param projection0    projection
     * @param selection0     selection
     * @param selectionArgs0 selection arguments
     * @param sortOrder0     sort order
     * @return cursor
     */
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
        val result = queryMain(code, uri.lastPathSegment, projection0, selection0, selectionArgs0)
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
        private const val TAG = "XSqlUNetProvider"

        // C O N T E N T   P R O V I D E R   A U T H O R I T Y

        private val AUTHORITY = makeAuthority("xsqlunet_authority")

        // U R I M A T C H E R

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            matchURIs()
        }

        private fun matchURIs() {
            uriMatcher.addURI(AUTHORITY, Words_FnWords_PbWords_VnWords.URI, XNetControl.WORDS_FNWORDS_PBWORDS_VNWORDS)
            uriMatcher.addURI(AUTHORITY, Words_Pronunciations_FnWords_PbWords_VnWords.URI, XNetControl.WORDS_PRONUNCIATIONS_FNWORDS_PBWORDS_VNWORDS)
            uriMatcher.addURI(AUTHORITY, Words_PbWords_VnWords.URI, XNetControl.WORDS_PBWORDS_VNWORDS)
            uriMatcher.addURI(AUTHORITY, XNetContract.PredicateMatrix.URI, XNetControl.PREDICATEMATRIX)
            uriMatcher.addURI(AUTHORITY, PredicateMatrix_VerbNet.URI, XNetControl.PREDICATEMATRIX_VERBNET)
            uriMatcher.addURI(AUTHORITY, PredicateMatrix_PropBank.URI, XNetControl.PREDICATEMATRIX_PROPBANK)
            uriMatcher.addURI(AUTHORITY, PredicateMatrix_FrameNet.URI, XNetControl.PREDICATEMATRIX_FRAMENET)
            uriMatcher.addURI(AUTHORITY, Words_VnWords_VnClasses.URI, XNetControl.WORDS_VNWORDS_VNCLASSES)
            uriMatcher.addURI(AUTHORITY, Words_VnWords_VnClasses_U.URI, XNetControl.WORDS_VNWORDS_VNCLASSES_U)
            uriMatcher.addURI(AUTHORITY, Words_PbWords_PbRoleSets.URI, XNetControl.WORDS_PBWORDS_PBROLESETS)
            uriMatcher.addURI(AUTHORITY, Words_PbWords_PbRoleSets_U.URI, XNetControl.WORDS_PBWORDS_PBROLESETS_U)
            uriMatcher.addURI(AUTHORITY, Words_FnWords_FnFrames_U.URI, XNetControl.WORDS_FNWORDS_FNFRAMES_U)
            uriMatcher.addURI(AUTHORITY, XNetContract.Sources.URI, XNetControl.SOURCES)
            uriMatcher.addURI(AUTHORITY, XNetContract.Meta.URI, XNetControl.META)
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
