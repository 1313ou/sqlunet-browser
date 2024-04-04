/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.provider

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
import org.sqlunet.wordnet.provider.WordNetContract.AdjPositions
import org.sqlunet.wordnet.provider.WordNetContract.AnyRelations
import org.sqlunet.wordnet.provider.WordNetContract.AnyRelations_Senses_Words_X
import org.sqlunet.wordnet.provider.WordNetContract.Dict
import org.sqlunet.wordnet.provider.WordNetContract.Domains
import org.sqlunet.wordnet.provider.WordNetContract.LexRelations
import org.sqlunet.wordnet.provider.WordNetContract.LexRelations_Senses
import org.sqlunet.wordnet.provider.WordNetContract.LexRelations_Senses_Words_X
import org.sqlunet.wordnet.provider.WordNetContract.LexRelations_Senses_X
import org.sqlunet.wordnet.provider.WordNetContract.Lexes_Morphs
import org.sqlunet.wordnet.provider.WordNetContract.Lookup_Definitions
import org.sqlunet.wordnet.provider.WordNetContract.Lookup_Samples
import org.sqlunet.wordnet.provider.WordNetContract.Lookup_Words
import org.sqlunet.wordnet.provider.WordNetContract.Poses
import org.sqlunet.wordnet.provider.WordNetContract.Relations
import org.sqlunet.wordnet.provider.WordNetContract.Samples
import org.sqlunet.wordnet.provider.WordNetContract.SemRelations
import org.sqlunet.wordnet.provider.WordNetContract.SemRelations_Synsets
import org.sqlunet.wordnet.provider.WordNetContract.SemRelations_Synsets_Words_X
import org.sqlunet.wordnet.provider.WordNetContract.SemRelations_Synsets_X
import org.sqlunet.wordnet.provider.WordNetContract.Senses
import org.sqlunet.wordnet.provider.WordNetContract.Senses_AdjPositions
import org.sqlunet.wordnet.provider.WordNetContract.Senses_Synsets_Poses_Domains
import org.sqlunet.wordnet.provider.WordNetContract.Senses_VerbFrames
import org.sqlunet.wordnet.provider.WordNetContract.Senses_VerbTemplates
import org.sqlunet.wordnet.provider.WordNetContract.Senses_Words
import org.sqlunet.wordnet.provider.WordNetContract.Suggest_FTS_Definitions
import org.sqlunet.wordnet.provider.WordNetContract.Suggest_FTS_Samples
import org.sqlunet.wordnet.provider.WordNetContract.Suggest_FTS_Words
import org.sqlunet.wordnet.provider.WordNetContract.Suggest_Words
import org.sqlunet.wordnet.provider.WordNetContract.Synsets
import org.sqlunet.wordnet.provider.WordNetContract.Synsets_Poses_Domains
import org.sqlunet.wordnet.provider.WordNetContract.Words_Lexes_Morphs
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Synsets
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_Synsets
import org.sqlunet.wordnet.provider.WordNetControl.queryAnyRelations
import org.sqlunet.wordnet.provider.WordNetControl.queryMain
import org.sqlunet.wordnet.provider.WordNetControl.querySearch
import org.sqlunet.wordnet.provider.WordNetControl.querySuggest

/**
 * WordNet provider
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class WordNetProvider : BaseProvider() {

    // M I M E

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            WordNetControl.WORDS, WordNetControl.WORD -> VENDOR + ".android.cursor.item/" + VENDOR + '.' + AUTHORITY + '.' + WordNetContract.Words.URI
            WordNetControl.SENSES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Senses.URI
            WordNetControl.SENSE -> VENDOR + ".android.cursor.item/" + VENDOR + '.' + AUTHORITY + '.' + Senses.URI
            WordNetControl.SYNSETS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Synsets.URI
            WordNetControl.SYNSET -> VENDOR + ".android.cursor.item/" + VENDOR + '.' + AUTHORITY + '.' + Synsets.URI
            WordNetControl.SEMRELATIONS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + SemRelations.URI
            WordNetControl.LEXRELATIONS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + LexRelations.URI
            WordNetControl.RELATIONS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Relations.URI
            WordNetControl.POSES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Poses.URI
            WordNetControl.ADJPOSITIONS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + AdjPositions.URI
            WordNetControl.DOMAINS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Domains.URI
            WordNetControl.SAMPLES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Samples.URI
            WordNetControl.DICT -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Dict.URI
            WordNetControl.WORDS_SENSES_SYNSETS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Words_Senses_Synsets.URI
            WordNetControl.WORDS_SENSES_CASEDWORDS_SYNSETS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Words_Senses_CasedWords_Synsets.URI
            WordNetControl.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Words_Senses_CasedWords_Synsets_Poses_Domains.URI
            WordNetControl.WORDS_SENSES_CASEDWORDS_PRONUNCIATIONS_SYNSETS_POSES_DOMAINS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.URI
            WordNetControl.SENSES_WORDS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Senses_Words.URI
            WordNetControl.SENSES_WORDS_BY_SYNSET -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Senses_Words.URI_BY_SYNSET
            WordNetControl.SENSES_SYNSETS_POSES_DOMAINS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Senses_Synsets_Poses_Domains.URI
            WordNetControl.SYNSETS_POSES_DOMAINS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Synsets_Poses_Domains.URI
            WordNetControl.ANYRELATIONS_SENSES_WORDS_X_BY_SYNSET -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + AnyRelations_Senses_Words_X.URI_BY_SYNSET
            WordNetControl.SEMRELATIONS_SYNSETS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + SemRelations_Synsets.URI
            WordNetControl.SEMRELATIONS_SYNSETS_X -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + SemRelations_Synsets_X.URI
            WordNetControl.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + SemRelations_Synsets_Words_X.URI_BY_SYNSET
            WordNetControl.LEXRELATIONS_SENSES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + LexRelations_Senses.URI
            WordNetControl.LEXRELATIONS_SENSES_X -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + LexRelations_Senses_X.URI
            WordNetControl.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + LexRelations_Senses_Words_X.URI_BY_SYNSET
            WordNetControl.SENSES_VFRAMES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Senses_VerbFrames.URI
            WordNetControl.SENSES_VTEMPLATES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Senses_VerbTemplates.URI
            WordNetControl.SENSES_ADJPOSITIONS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Senses_AdjPositions.URI
            WordNetControl.LEXES_MORPHS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Lexes_Morphs.URI
            WordNetControl.WORDS_LEXES_MORPHS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Words_Lexes_Morphs.URI
            WordNetControl.WORDS_LEXES_MORPHS_BY_WORD -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Words_Lexes_Morphs.URI_BY_WORD
            WordNetControl.LOOKUP_FTS_WORDS -> VENDOR + ".android.cursor.item/" + VENDOR + '.' + AUTHORITY + '.' + WordNetContract.Words.URI
            WordNetControl.LOOKUP_FTS_DEFINITIONS -> VENDOR + ".android.cursor.item/" + VENDOR + '.' + AUTHORITY + '.' + Synsets.URI
            WordNetControl.LOOKUP_FTS_SAMPLES -> VENDOR + ".android.cursor.item/" + VENDOR + '.' + AUTHORITY + '.' + Samples.URI
            WordNetControl.SUGGEST_FTS_WORDS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + WordNetContract.Words.URI
            WordNetControl.SUGGEST_FTS_DEFINITIONS -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Synsets.URI
            WordNetControl.SUGGEST_FTS_SAMPLES -> VENDOR + ".android.cursor.dir/" + VENDOR + '.' + AUTHORITY + '.' + Samples.URI
            else -> throw UnsupportedOperationException("Illegal MIME type")
        }
    }

    // Q U E R Y

    /**
     * Query
     *
     * @param uri            uri
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
        val code = uriMatcher.match(uri)
        Log.d(TAG + "URI", String.format("%s (code %s)\n", uri, code))
        if (code == UriMatcher.NO_MATCH) {
            throw RuntimeException("Malformed URI $uri")
        }

        // MAIN
        var result = queryMain(code, uri.lastPathSegment!!, projection0, selection0, selectionArgs0)
        if (result == null) {
            // RELATIONS
            result = queryAnyRelations(code, projection0, selection0, selectionArgs0)
            if (result == null) {
                // TEXTSEARCH
                result = querySearch(code, projection0, selection0, selectionArgs0)
            }
        }
        // MAIN || RELATIONS || TEXTSEARCH
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

    // S U B Q U E R Y

    /**
     * Any relations subquery
     *
     * @param selection0 input selection
     * @return all relations subquery
     */
    private fun makeAnyRelationsSubQuery(selection0: String): String {
        val semTable = SemRelations.TABLE
        val lexTable = LexRelations.TABLE
        val projection1 = arrayOf(
            SemRelations.RELATIONID,
            SemRelations.SYNSET1ID,
            SemRelations.SYNSET2ID
        )
        val projection2 = arrayOf(
            LexRelations.RELATIONID,
            LexRelations.WORD1ID,
            LexRelations.SYNSET1ID,
            LexRelations.WORD2ID,
            LexRelations.SYNSET2ID
        )
        val unionProjection = arrayOf(
            AnyRelations.RELATIONID,
            AnyRelations.WORD1ID,
            AnyRelations.SYNSET1ID,
            AnyRelations.WORD2ID,
            AnyRelations.SYNSET2ID
        )
        val selections = selection0.split("/\\*\\*/\\|/\\*\\*/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return makeQuery(
            semTable,
            lexTable,
            projection1,
            projection2,
            unionProjection,
            WordNetContract.RELATIONTYPE,
            "sem",
            "lex",
            selections[0],
            selections[1]
        )
    }

    private fun embed(sql: String, projection: Array<String>, selection: String, groupBy: String, sortOrder: String): String {
        val embeddingQueryBuilder = SQLiteQueryBuilder()
        embeddingQueryBuilder.tables = "($sql)"
        return embeddingQueryBuilder.buildQuery(projection, selection, groupBy, null, sortOrder, null)
    }

    companion object {

        private const val TAG = "WordNetProvider"

        // C O N T E N T   P R O V I D E R   A U T H O R I T Y

        private val AUTHORITY = makeAuthority("wordnet_authority")

        // U R I M A T C H E R

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            matchURIs()
        }

        private fun matchURIs() {
            // table
            uriMatcher.addURI(AUTHORITY, WordNetContract.Words.URI, WordNetControl.WORDS)
            uriMatcher.addURI(AUTHORITY, WordNetContract.Words.URI + "/#", WordNetControl.WORD)
            uriMatcher.addURI(AUTHORITY, Senses.URI, WordNetControl.SENSES)
            uriMatcher.addURI(AUTHORITY, Senses.URI + "/#", WordNetControl.SENSE)
            uriMatcher.addURI(AUTHORITY, Synsets.URI, WordNetControl.SYNSETS)
            uriMatcher.addURI(AUTHORITY, Synsets.URI + "/#", WordNetControl.SYNSET)
            uriMatcher.addURI(AUTHORITY, SemRelations.URI, WordNetControl.SEMRELATIONS)
            uriMatcher.addURI(AUTHORITY, LexRelations.URI, WordNetControl.LEXRELATIONS)
            uriMatcher.addURI(AUTHORITY, Relations.URI, WordNetControl.RELATIONS)
            uriMatcher.addURI(AUTHORITY, Poses.URI, WordNetControl.POSES)
            uriMatcher.addURI(AUTHORITY, Domains.URI, WordNetControl.DOMAINS)
            uriMatcher.addURI(AUTHORITY, AdjPositions.URI, WordNetControl.ADJPOSITIONS)
            uriMatcher.addURI(AUTHORITY, Samples.URI, WordNetControl.SAMPLES)

            // view
            uriMatcher.addURI(AUTHORITY, Dict.URI, WordNetControl.DICT)

            // joins
            uriMatcher.addURI(AUTHORITY, Words_Senses_Synsets.URI, WordNetControl.WORDS_SENSES_SYNSETS)
            uriMatcher.addURI(AUTHORITY, Words_Senses_CasedWords_Synsets.URI, WordNetControl.WORDS_SENSES_CASEDWORDS_SYNSETS)
            uriMatcher.addURI(AUTHORITY, Words_Senses_CasedWords_Synsets_Poses_Domains.URI, WordNetControl.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS)
            uriMatcher.addURI(AUTHORITY, WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.URI, WordNetControl.WORDS_SENSES_CASEDWORDS_PRONUNCIATIONS_SYNSETS_POSES_DOMAINS)
            uriMatcher.addURI(AUTHORITY, Senses_Words.URI, WordNetControl.SENSES_WORDS)
            uriMatcher.addURI(AUTHORITY, Senses_Words.URI_BY_SYNSET, WordNetControl.SENSES_WORDS_BY_SYNSET)
            uriMatcher.addURI(AUTHORITY, Senses_Synsets_Poses_Domains.URI, WordNetControl.SENSES_SYNSETS_POSES_DOMAINS)
            uriMatcher.addURI(AUTHORITY, Synsets_Poses_Domains.URI, WordNetControl.SYNSETS_POSES_DOMAINS)
            uriMatcher.addURI(AUTHORITY, AnyRelations_Senses_Words_X.URI_BY_SYNSET, WordNetControl.ANYRELATIONS_SENSES_WORDS_X_BY_SYNSET)
            uriMatcher.addURI(AUTHORITY, SemRelations_Synsets.URI, WordNetControl.SEMRELATIONS_SYNSETS)
            uriMatcher.addURI(AUTHORITY, SemRelations_Synsets_X.URI, WordNetControl.SEMRELATIONS_SYNSETS_X)
            uriMatcher.addURI(AUTHORITY, SemRelations_Synsets_Words_X.URI_BY_SYNSET, WordNetControl.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET)
            uriMatcher.addURI(AUTHORITY, LexRelations_Senses.URI, WordNetControl.LEXRELATIONS_SENSES)
            uriMatcher.addURI(AUTHORITY, LexRelations_Senses_X.URI, WordNetControl.LEXRELATIONS_SENSES_X)
            uriMatcher.addURI(AUTHORITY, LexRelations_Senses_Words_X.URI_BY_SYNSET, WordNetControl.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET)
            uriMatcher.addURI(AUTHORITY, Senses_VerbFrames.URI, WordNetControl.SENSES_VFRAMES)
            uriMatcher.addURI(AUTHORITY, Senses_VerbTemplates.URI, WordNetControl.SENSES_VTEMPLATES)
            uriMatcher.addURI(AUTHORITY, Senses_AdjPositions.URI, WordNetControl.SENSES_ADJPOSITIONS)
            uriMatcher.addURI(AUTHORITY, Lexes_Morphs.URI, WordNetControl.LEXES_MORPHS)
            uriMatcher.addURI(AUTHORITY, Words_Lexes_Morphs.URI, WordNetControl.WORDS_LEXES_MORPHS)
            uriMatcher.addURI(AUTHORITY, Words_Lexes_Morphs.URI_BY_WORD, WordNetControl.WORDS_LEXES_MORPHS_BY_WORD)

            // search text
            uriMatcher.addURI(AUTHORITY, Lookup_Words.URI, WordNetControl.LOOKUP_FTS_WORDS)
            uriMatcher.addURI(AUTHORITY, Lookup_Definitions.URI, WordNetControl.LOOKUP_FTS_DEFINITIONS)
            uriMatcher.addURI(AUTHORITY, Lookup_Samples.URI, WordNetControl.LOOKUP_FTS_SAMPLES)

            // search
            uriMatcher.addURI(AUTHORITY, Suggest_Words.URI + "/*", WordNetControl.SUGGEST_WORDS)
            uriMatcher.addURI(AUTHORITY, Suggest_Words.URI + "/", WordNetControl.SUGGEST_WORDS)
            uriMatcher.addURI(AUTHORITY, Suggest_FTS_Words.URI + "/*", WordNetControl.SUGGEST_FTS_WORDS)
            uriMatcher.addURI(AUTHORITY, Suggest_FTS_Words.URI + "/", WordNetControl.SUGGEST_FTS_WORDS)
            uriMatcher.addURI(AUTHORITY, Suggest_FTS_Definitions.URI + "/*", WordNetControl.SUGGEST_FTS_DEFINITIONS)
            uriMatcher.addURI(AUTHORITY, Suggest_FTS_Definitions.URI + "/", WordNetControl.SUGGEST_FTS_DEFINITIONS)
            uriMatcher.addURI(AUTHORITY, Suggest_FTS_Samples.URI + "/*", WordNetControl.SUGGEST_FTS_SAMPLES)
            uriMatcher.addURI(AUTHORITY, Suggest_FTS_Samples.URI + "/", WordNetControl.SUGGEST_FTS_SAMPLES)
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

        /**
         * Make union query.
         * Requirements on selection : expr1 AND expr2
         * Requirements on selectionArgs : [0] value1, [1] value2
         *
         * @param table1          table1
         * @param table2          table2
         * @param projection1     table1 projection
         * @param projection2     table2 projection
         * @param unionProjection union projection
         * @param discriminator   discriminator field
         * @param value1          value1 for discriminator
         * @param value2          value2 for discriminator
         * @param selection1      selection
         * @param selection2      selection
         * @return union sql
         */
        private fun makeQuery(
            table1: String,
            table2: String,
            projection1: Array<String>,
            projection2: Array<String>,
            unionProjection: Array<String>,
            discriminator: String,
            value1: String,
            value2: String,
            selection1: String,
            selection2: String,
        ): String {
            val actualUnionProjection = appendProjection(unionProjection, WordNetContract.RELATIONTYPE)
            val table1ProjectionList = listOf(*projection1)
            val table2ProjectionList = listOf(*projection2)

            // query 1
            //var actualSelection1 = makeSelectionAndInstantiateArgs(projection1, selection1, selection1Args)
            val actualSelection1 = makeSelection(projection1, selection1)
            val subQueryBuilder1 = SQLiteQueryBuilder()
            subQueryBuilder1.tables = table1
            val subQuery1 = subQueryBuilder1.buildUnionSubQuery(
                discriminator,
                actualUnionProjection,
                HashSet(table1ProjectionList),
                0,
                value1,
                actualSelection1,
                null,
                null
            )

            // query 2
            //var actualSelection2 = makeSelectionAndInstantiateArgs(projection2, selection2, selection2Args)
            val actualSelection2 = makeSelection(projection2, selection2)
            val subQueryBuilder2 = SQLiteQueryBuilder()
            subQueryBuilder2.tables = table2
            val subQuery2 = subQueryBuilder2.buildUnionSubQuery(
                WordNetContract.RELATIONTYPE,
                actualUnionProjection,
                HashSet(table2ProjectionList),
                0,
                value2,
                actualSelection2,
                null,
                null
            )

            // union (equiv to view)
            val uQueryBuilder = SQLiteQueryBuilder()
            uQueryBuilder.isDistinct = true
            return uQueryBuilder.buildUnionQuery(arrayOf(subQuery1, subQuery2), null, null)
            //return embed(uQuery, projection, selection, groupBy, sortOrder)
        }

        private fun makeSelection(@Suppress("UNUSED_PARAMETER") projection: Array<String>, selection: String): String {
            return selection
        }
    }
}
