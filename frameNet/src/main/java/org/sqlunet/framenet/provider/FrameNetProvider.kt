/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.provider

import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteCantOpenDatabaseException
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log
import org.sqlunet.framenet.provider.FrameNetContract.AnnoSets
import org.sqlunet.framenet.provider.FrameNetContract.AnnoSets_Layers_X
import org.sqlunet.framenet.provider.FrameNetContract.Frames_FEs
import org.sqlunet.framenet.provider.FrameNetContract.Frames_Related
import org.sqlunet.framenet.provider.FrameNetContract.Frames_X
import org.sqlunet.framenet.provider.FrameNetContract.Governors_AnnoSets_Sentences
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_FERealizations_ValenceUnits
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_Governors
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_Sentences
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_X
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_or_Frames
import org.sqlunet.framenet.provider.FrameNetContract.Lookup_FTS_FnSentences
import org.sqlunet.framenet.provider.FrameNetContract.Lookup_FTS_FnSentences_X
import org.sqlunet.framenet.provider.FrameNetContract.Lookup_FTS_FnWords
import org.sqlunet.framenet.provider.FrameNetContract.Patterns_Layers_X
import org.sqlunet.framenet.provider.FrameNetContract.Patterns_Sentences
import org.sqlunet.framenet.provider.FrameNetContract.Sentences
import org.sqlunet.framenet.provider.FrameNetContract.Sentences_Layers_X
import org.sqlunet.framenet.provider.FrameNetContract.Suggest_FTS_FnWords
import org.sqlunet.framenet.provider.FrameNetContract.Suggest_FnWords
import org.sqlunet.framenet.provider.FrameNetContract.ValenceUnits_Layers_X
import org.sqlunet.framenet.provider.FrameNetContract.ValenceUnits_Sentences
import org.sqlunet.framenet.provider.FrameNetContract.Words_LexUnits_Frames
import org.sqlunet.framenet.provider.FrameNetControl.queryMain
import org.sqlunet.framenet.provider.FrameNetControl.querySearch
import org.sqlunet.framenet.provider.FrameNetControl.querySuggest
import org.sqlunet.provider.BaseProvider
import org.sqlunet.settings.LogUtils
import org.sqlunet.sql.SqlFormatter.format

/**
 * FrameNet provider
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FrameNetProvider : BaseProvider() {

    // M I M E

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            FrameNetControl.LEXUNIT -> VENDOR + ".android.cursor.item/" + VENDOR + '.' + AUTHORITY + '.' + LexUnits.URI
            FrameNetControl.LEXUNITS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + LexUnits.URI
            FrameNetControl.LEXUNITS_X_BY_LEXUNIT -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + LexUnits_X.URI_BY_LEXUNIT
            FrameNetControl.LEXUNITS_OR_FRAMES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + LexUnits_or_Frames.URI
            FrameNetControl.LEXUNITS_OR_FRAMES_FN -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + LexUnits_or_Frames.URI_FN
            FrameNetControl.FRAME -> VENDOR + ".android.cursor.item/" + VENDOR + '.' + AUTHORITY + '.' + FrameNetContract.Frames.URI
            FrameNetControl.FRAMES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + FrameNetContract.Frames.URI
            FrameNetControl.FRAMES_X_BY_FRAME -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Frames_X.URI_BY_FRAME
            FrameNetControl.FRAMES_RELATED -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Frames_Related.URI
            FrameNetControl.SENTENCE -> VENDOR + ".android.cursor.item/" + VENDOR + '.' + AUTHORITY + '.' + Sentences.URI
            FrameNetControl.SENTENCES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Sentences.URI
            FrameNetControl.ANNOSET -> VENDOR + ".android.cursor.item/" + VENDOR + '.' + AUTHORITY + '.' + AnnoSets.URI
            FrameNetControl.ANNOSETS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + AnnoSets.URI
            FrameNetControl.SENTENCES_LAYERS_X -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Sentences_Layers_X.URI
            FrameNetControl.ANNOSETS_LAYERS_X -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + AnnoSets_Layers_X.URI
            FrameNetControl.PATTERNS_LAYERS_X -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Patterns_Layers_X.URI
            FrameNetControl.VALENCEUNITS_LAYERS_X -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + ValenceUnits_Layers_X.URI
            FrameNetControl.WORDS_LEXUNITS_FRAMES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Words_LexUnits_Frames.URI
            FrameNetControl.WORDS_LEXUNITS_FRAMES_FN -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Words_LexUnits_Frames.URI_FN
            FrameNetControl.FRAMES_FES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Frames_FEs.URI
            FrameNetControl.LEXUNITS_SENTENCES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + LexUnits_Sentences.URI
            FrameNetControl.LEXUNITS_GOVERNORS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + LexUnits_Governors.URI
            FrameNetControl.LEXUNITS_GOVERNORS_FN -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + LexUnits_Governors.URI_FN
            FrameNetControl.LEXUNITS_REALIZATIONS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + LexUnits_FERealizations_ValenceUnits.URI
            FrameNetControl.LEXUNITS_REALIZATIONS_BY_REALIZATION -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + LexUnits_FERealizations_ValenceUnits.URI_BY_REALIZATION
            FrameNetControl.LEXUNITS_GROUPREALIZATIONS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + LexUnits_FEGroupRealizations_Patterns_ValenceUnits.URI
            FrameNetControl.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + LexUnits_FEGroupRealizations_Patterns_ValenceUnits.URI_BY_PATTERN
            FrameNetControl.PATTERNS_SENTENCES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Patterns_Sentences.URI
            FrameNetControl.VALENCEUNITS_SENTENCES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + ValenceUnits_Sentences.URI
            FrameNetControl.GOVERNORS_ANNOSETS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Governors_AnnoSets_Sentences.URI
            FrameNetControl.LOOKUP_FTS_WORDS -> VENDOR + ".android.cursor.item/" + VENDOR + '.' + AUTHORITY + '.' + FrameNetContract.Words.URI
            FrameNetControl.LOOKUP_FTS_SENTENCES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Lookup_FTS_FnSentences.URI
            FrameNetControl.LOOKUP_FTS_SENTENCES_X -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Lookup_FTS_FnSentences_X.URI
            FrameNetControl.LOOKUP_FTS_SENTENCES_X_BY_SENTENCE -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Lookup_FTS_FnSentences_X.URI_BY_SENTENCE
            FrameNetControl.SUGGEST_WORDS -> VENDOR + ".android.cursor.item/" + VENDOR + '.' + AUTHORITY + '.' + FrameNetContract.Words.TABLE
            FrameNetControl.SUGGEST_FTS_WORDS -> VENDOR + ".android.cursor.item/" + VENDOR + '.' + AUTHORITY + '.' + FrameNetContract.Words.TABLE
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
        Log.d(TAG + "URI", String.format("%s (code %s)", uri, code))
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
            logSql(sql, *result.selectionArgs ?: arrayOf())
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

        private const val TAG = "FrameNetProvider"

        // C O N T E N T   P R O V I D E R   A U T H O R I T Y

        private val AUTHORITY = makeAuthority("framenet_authority")

        // U R I M A T C H E R

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            matchURIs()
        }

        private fun matchURIs() {
            uriMatcher.addURI(AUTHORITY, LexUnits.URI, FrameNetControl.LEXUNIT)
            uriMatcher.addURI(AUTHORITY, LexUnits.URI, FrameNetControl.LEXUNITS)
            uriMatcher.addURI(AUTHORITY, LexUnits_X.URI_BY_LEXUNIT, FrameNetControl.LEXUNITS_X_BY_LEXUNIT)
            uriMatcher.addURI(AUTHORITY, LexUnits_or_Frames.URI, FrameNetControl.LEXUNITS_OR_FRAMES)
            uriMatcher.addURI(AUTHORITY, LexUnits_or_Frames.URI_FN, FrameNetControl.LEXUNITS_OR_FRAMES_FN)
            uriMatcher.addURI(AUTHORITY, FrameNetContract.Frames.URI, FrameNetControl.FRAME)
            uriMatcher.addURI(AUTHORITY, FrameNetContract.Frames.URI, FrameNetControl.FRAMES)
            uriMatcher.addURI(AUTHORITY, Frames_X.URI_BY_FRAME, FrameNetControl.FRAMES_X_BY_FRAME)
            uriMatcher.addURI(AUTHORITY, Frames_Related.URI, FrameNetControl.FRAMES_RELATED)
            uriMatcher.addURI(AUTHORITY, Sentences.URI, FrameNetControl.SENTENCE)
            uriMatcher.addURI(AUTHORITY, Sentences.URI, FrameNetControl.SENTENCES)
            uriMatcher.addURI(AUTHORITY, AnnoSets.URI, FrameNetControl.ANNOSET)
            uriMatcher.addURI(AUTHORITY, AnnoSets.URI, FrameNetControl.ANNOSETS)
            uriMatcher.addURI(AUTHORITY, Sentences_Layers_X.URI, FrameNetControl.SENTENCES_LAYERS_X)
            uriMatcher.addURI(AUTHORITY, AnnoSets_Layers_X.URI, FrameNetControl.ANNOSETS_LAYERS_X)
            uriMatcher.addURI(AUTHORITY, Patterns_Layers_X.URI, FrameNetControl.PATTERNS_LAYERS_X)
            uriMatcher.addURI(AUTHORITY, ValenceUnits_Layers_X.URI, FrameNetControl.VALENCEUNITS_LAYERS_X)
            uriMatcher.addURI(AUTHORITY, Words_LexUnits_Frames.URI, FrameNetControl.WORDS_LEXUNITS_FRAMES)
            uriMatcher.addURI(AUTHORITY, Words_LexUnits_Frames.URI_FN, FrameNetControl.WORDS_LEXUNITS_FRAMES_FN)
            uriMatcher.addURI(AUTHORITY, Frames_FEs.URI, FrameNetControl.FRAMES_FES)
            uriMatcher.addURI(AUTHORITY, Frames_FEs.URI_BY_FE, FrameNetControl.FRAMES_FES_BY_FE)
            uriMatcher.addURI(AUTHORITY, LexUnits_Sentences.URI, FrameNetControl.LEXUNITS_SENTENCES)
            uriMatcher.addURI(AUTHORITY, LexUnits_Sentences.URI_BY_SENTENCE, FrameNetControl.LEXUNITS_SENTENCES_BY_SENTENCE)
            uriMatcher.addURI(AUTHORITY, LexUnits_Sentences_AnnoSets_Layers_Labels.URI, FrameNetControl.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS)
            uriMatcher.addURI(AUTHORITY, LexUnits_Sentences_AnnoSets_Layers_Labels.URI_BY_SENTENCE, FrameNetControl.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE)
            uriMatcher.addURI(AUTHORITY, LexUnits_Governors.URI, FrameNetControl.LEXUNITS_GOVERNORS)
            uriMatcher.addURI(AUTHORITY, LexUnits_Governors.URI_FN, FrameNetControl.LEXUNITS_GOVERNORS_FN)
            uriMatcher.addURI(AUTHORITY, LexUnits_FERealizations_ValenceUnits.URI, FrameNetControl.LEXUNITS_REALIZATIONS)
            uriMatcher.addURI(AUTHORITY, LexUnits_FERealizations_ValenceUnits.URI_BY_REALIZATION, FrameNetControl.LEXUNITS_REALIZATIONS_BY_REALIZATION)
            uriMatcher.addURI(AUTHORITY, LexUnits_FEGroupRealizations_Patterns_ValenceUnits.URI, FrameNetControl.LEXUNITS_GROUPREALIZATIONS)
            uriMatcher.addURI(AUTHORITY, LexUnits_FEGroupRealizations_Patterns_ValenceUnits.URI_BY_PATTERN, FrameNetControl.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN)
            uriMatcher.addURI(AUTHORITY, Patterns_Sentences.URI, FrameNetControl.PATTERNS_SENTENCES)
            uriMatcher.addURI(AUTHORITY, ValenceUnits_Sentences.URI, FrameNetControl.VALENCEUNITS_SENTENCES)
            uriMatcher.addURI(AUTHORITY, Governors_AnnoSets_Sentences.URI, FrameNetControl.GOVERNORS_ANNOSETS)
            uriMatcher.addURI(AUTHORITY, Lookup_FTS_FnWords.URI + "/*", FrameNetControl.LOOKUP_FTS_WORDS)
            uriMatcher.addURI(AUTHORITY, Lookup_FTS_FnWords.URI + "/", FrameNetControl.LOOKUP_FTS_WORDS)
            uriMatcher.addURI(AUTHORITY, Lookup_FTS_FnSentences.URI + "/", FrameNetControl.LOOKUP_FTS_SENTENCES)
            uriMatcher.addURI(AUTHORITY, Lookup_FTS_FnSentences_X.URI + "/", FrameNetControl.LOOKUP_FTS_SENTENCES_X)
            uriMatcher.addURI(AUTHORITY, Lookup_FTS_FnSentences_X.URI_BY_SENTENCE + "/", FrameNetControl.LOOKUP_FTS_SENTENCES_X_BY_SENTENCE)
            uriMatcher.addURI(AUTHORITY, Suggest_FnWords.URI + "/*", FrameNetControl.SUGGEST_WORDS)
            uriMatcher.addURI(AUTHORITY, Suggest_FnWords.URI + "/", FrameNetControl.SUGGEST_WORDS)
            uriMatcher.addURI(AUTHORITY, Suggest_FTS_FnWords.URI + "/*", FrameNetControl.SUGGEST_FTS_WORDS)
            uriMatcher.addURI(AUTHORITY, Suggest_FTS_FnWords.URI + "/", FrameNetControl.SUGGEST_FTS_WORDS)
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
