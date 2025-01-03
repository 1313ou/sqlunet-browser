/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.provider

import android.app.SearchManager
import org.sqlunet.provider.BaseProvider.Companion.appendProjection

/**
 * WordNet query control
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object WordNetControl {

    // table codes
    const val WORDS = 10
    const val WORD = 11
    const val SENSES = 20
    const val SENSE = 21
    const val SYNSETS = 30
    const val SYNSET = 31
    const val SEMRELATIONS = 40
    const val LEXRELATIONS = 50
    const val RELATIONS = 60
    const val POSES = 70
    const val DOMAINS = 80
    const val ADJPOSITIONS = 90
    const val SAMPLES = 100
    const val SAMPLE = 101
    const val USAGES = 110
    const val USAGE = 111
    const val ILIS = 120
    const val ILI = 121
    const val WIKIDATAS = 130
    const val WIKIDATA = 131

    // view codes
    const val DICT = 200

    // join codes
    const val WORDS_SENSES_SYNSETS = 310
    const val WORDS_SENSES_CASEDWORDS_SYNSETS = 311
    const val WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS = 312
    const val WORDS_SENSES_CASEDWORDS_PRONUNCIATIONS_SYNSETS_POSES_DOMAINS = 313
    const val SENSES_WORDS = 320
    const val SENSES_WORDS_BY_SYNSET = 321
    const val SENSES_SYNSETS_POSES_DOMAINS = 330
    const val SYNSETS_POSES_DOMAINS = 340
    const val ANYRELATIONS_SENSES_WORDS_X_BY_SYNSET = 400
    const val SEMRELATIONS_SYNSETS = 410
    const val SEMRELATIONS_SYNSETS_X = 411
    const val SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET = 412
    const val LEXRELATIONS_SENSES = 420
    const val LEXRELATIONS_SENSES_X = 421
    const val LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET = 422
    const val SENSES_VFRAMES = 510
    const val SENSES_VTEMPLATES = 515
    const val SENSES_ADJPOSITIONS = 520
    const val LEXES_MORPHS = 530
    const val WORDS_LEXES_MORPHS = 541
    const val WORDS_LEXES_MORPHS_BY_WORD = 542

    // search text codes
    const val LOOKUP_FTS_WORDS = 810
    const val LOOKUP_FTS_DEFINITIONS = 820
    const val LOOKUP_FTS_SAMPLES = 830

    // suggest codes
    const val SUGGEST_WORDS = 900
    const val SUGGEST_FTS_WORDS = 910
    const val SUGGEST_FTS_DEFINITIONS = 920
    const val SUGGEST_FTS_SAMPLES = 930

    fun queryMain(code: Int, uriLast: String, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): Result? {
        val table: String
        var projection = projection0
        var selection = selection0
        var groupBy: String? = null
        when (code) {
            WORDS -> table = Q.WORDS.TABLE
            SENSES -> table = Q.SENSES.TABLE
            SYNSETS -> table = Q.SYNSETS.TABLE
            SEMRELATIONS -> table = Q.SEMRELATIONS.TABLE
            LEXRELATIONS -> table = Q.LEXRELATIONS.TABLE
            RELATIONS -> table = Q.RELATIONS.TABLE
            POSES -> table = Q.POSES.TABLE
            DOMAINS -> table = Q.DOMAINS.TABLE
            ADJPOSITIONS -> table = Q.ADJPOSITIONS.TABLE
            SAMPLES -> table = Q.SAMPLES.TABLE
            USAGES -> table = Q.USAGES.TABLE
            ILIS -> table = Q.ILIS.TABLE
            WIKIDATAS -> table = Q.WIKIDATAS.TABLE
            WORD -> {
                table = Q.WORD1.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                //selection += Q.WORDID + " = " + uriLast
                selection += Q.WORD1.SELECTION.replace("#\\{uri_last\\}".toRegex(), uriLast) // V.WORDID + " = " + uriLast
            }

            SENSE -> {
                table = Q.SENSE1.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                //selection += Q.SENSEID + " = " + uriLast
                selection += Q.SENSE1.SELECTION.replace("#\\{uri_last\\}".toRegex(), uriLast) // V.SENSEID + " = " + uriLast
            }

            SYNSET -> {
                table = Q.SYNSET1.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                //selection += Q.SYNSETID + " = " + uriLast
                selection += Q.SYNSET1.SELECTION.replace("#\\{uri_last\\}".toRegex(), uriLast) // V.SYNSETID + " = " + uriLast
            }

            SAMPLE -> {
                table = Q.SAMPLES.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                //selection += Q.SAMPLEID + " = " + uriLast
                selection += Q.SAMPLE1.SELECTION.replace("#\\{uri_last\\}".toRegex(), uriLast) // V.SAMPLEID + " = " + uriLast
            }

            USAGE -> {
                table = Q.USAGES.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                //selection += Q.SYNSETID + " = " + uriLast
                selection += Q.USAGE1.SELECTION.replace("#\\{uri_last\\}".toRegex(), uriLast) // V.SYNSETID + " = " + uriLast
            }

            ILI -> {
                table = Q.ILIS.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                //selection += Q.SYNSETID + " = " + uriLast
                selection += Q.ILI1.SELECTION.replace("#\\{uri_last\\}".toRegex(), uriLast) // V.SYNSETID + " = " + uriLast
            }

            WIKIDATA -> {
                table = Q.WIKIDATAS.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                //selection += Q.SYNSETID + " = " + uriLast
                selection += Q.WIKIDATA1.SELECTION.replace("#\\{uri_last\\}".toRegex(), uriLast) // V.SYNSETID + " = " + uriLast
            }

            DICT -> table = Q.DICT.TABLE
            WORDS_SENSES_SYNSETS -> table = Q.WORDS_SENSES_SYNSETS.TABLE
            WORDS_SENSES_CASEDWORDS_SYNSETS -> table = Q.WORDS_SENSES_CASEDWORDS_SYNSETS.TABLE
            WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS -> table = Q.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS.TABLE
            WORDS_SENSES_CASEDWORDS_PRONUNCIATIONS_SYNSETS_POSES_DOMAINS -> {
                table = Q.WORDS_SENSES_CASEDWORDS_PRONUNCIATIONS_SYNSETS_POSES_DOMAINS.TABLE
                groupBy = Q.WORDS_SENSES_CASEDWORDS_PRONUNCIATIONS_SYNSETS_POSES_DOMAINS.GROUPBY
            }

            SENSES_WORDS -> table = Q.SENSES_WORDS.TABLE
            SENSES_WORDS_BY_SYNSET -> {
                table = Q.SENSES_WORDS_BY_SYNSET.TABLE
                projection = appendProjection(projection, Q.SENSES_WORDS_BY_SYNSET.PROJECTION)
                groupBy = Q.SENSES_WORDS_BY_SYNSET.GROUPBY
            }

            SENSES_SYNSETS_POSES_DOMAINS -> table = Q.SENSES_SYNSETS_POSES_DOMAINS.TABLE
            SYNSETS_POSES_DOMAINS -> table = Q.SYNSETS_POSES_DOMAINS.TABLE
            SEMRELATIONS_SYNSETS -> table = Q.SEMRELATIONS_SYNSETS.TABLE
            SEMRELATIONS_SYNSETS_X -> table = Q.SEMRELATIONS_SYNSETS_X.TABLE
            SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET -> {
                table = Q.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.TABLE
                projection = appendProjection(projection, Q.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.PROJECTION)
                groupBy = Q.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.GROUPBY
            }

            LEXRELATIONS_SENSES -> table = Q.LEXRELATIONS_SENSES.TABLE
            LEXRELATIONS_SENSES_X -> table = Q.LEXRELATIONS_SENSES_X.TABLE
            LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET -> {
                table = Q.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.TABLE
                projection = appendProjection(projection, Q.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.PROJECTION)
                groupBy = Q.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.GROUPBY
            }

            SENSES_VFRAMES -> table = Q.SENSES_VFRAMES.TABLE
            SENSES_VTEMPLATES -> table = Q.SENSES_VTEMPLATES.TABLE
            SENSES_ADJPOSITIONS -> table = Q.SENSES_ADJPOSITIONS.TABLE
            LEXES_MORPHS -> table = Q.LEXES_MORPHS.TABLE
            WORDS_LEXES_MORPHS -> table = Q.WORDS_LEXES_MORPHS.TABLE
            WORDS_LEXES_MORPHS_BY_WORD -> {
                table = Q.WORDS_LEXES_MORPHS.TABLE
                groupBy = Q.WORDS_LEXES_MORPHS_BY_WORD.GROUPBY
            }

            else -> return null
        }
        return Result(table, projection, selection, selectionArgs0, groupBy)
    }

    fun queryAnyRelations(code: Int, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): Result? {
        if (code == ANYRELATIONS_SENSES_WORDS_X_BY_SYNSET) {
            val table = Q.ANYRELATIONS_SENSES_WORDS_X_BY_SYNSET.TABLE
            val groupBy = Q.ANYRELATIONS_SENSES_WORDS_X_BY_SYNSET.GROUPBY
            return Result(table, projection0, null, selectionArgs0, groupBy)
        }
        return null
    }

    fun querySearch(code: Int, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): Result? {
        val table: String = when (code) {
            LOOKUP_FTS_WORDS -> Q.LOOKUP_FTS_WORDS.TABLE
            LOOKUP_FTS_DEFINITIONS -> Q.LOOKUP_FTS_DEFINITIONS.TABLE
            LOOKUP_FTS_SAMPLES -> Q.LOOKUP_FTS_SAMPLES.TABLE
            else -> return null
        }
        return Result(table, projection0, selection0, selectionArgs0, null)
    }

    fun querySuggest(code: Int, uriLast: String): Result? {
        val table: String
        val projection: Array<String>
        val selection: String
        val selectionArgs: Array<String>
        when (code) {
            SUGGEST_WORDS -> {
                if (SearchManager.SUGGEST_URI_PATH_QUERY == uriLast) {
                    return null
                }
                table = Q.SUGGEST_WORDS.TABLE
                projection = Q.SUGGEST_WORDS.PROJECTION
                projection[1] = projection[1].replace("#\\{suggest_text_1\\}".toRegex(), SearchManager.SUGGEST_COLUMN_TEXT_1)
                projection[2] = projection[2].replace("#\\{suggest_query\\}".toRegex(), SearchManager.SUGGEST_COLUMN_QUERY)
                selection = Q.SUGGEST_WORDS.SELECTION
                selectionArgs = arrayOf(Q.SUGGEST_WORDS.ARGS[0].replace("#\\{uri_last\\}".toRegex(), uriLast))
            }

            SUGGEST_FTS_WORDS -> {
                if (SearchManager.SUGGEST_URI_PATH_QUERY == uriLast) {
                    return null
                }
                table = Q.SUGGEST_FTS_WORDS.TABLE
                projection = Q.SUGGEST_FTS_WORDS.PROJECTION
                projection[1] = projection[1].replace("#\\{suggest_text_1\\}".toRegex(), SearchManager.SUGGEST_COLUMN_TEXT_1)
                projection[2] = projection[2].replace("#\\{suggest_query\\}".toRegex(), SearchManager.SUGGEST_COLUMN_QUERY)
                selection = Q.SUGGEST_FTS_WORDS.SELECTION
                selectionArgs = arrayOf(Q.SUGGEST_FTS_WORDS.ARGS[0].replace("#\\{uri_last\\}".toRegex(), uriLast))
            }

            SUGGEST_FTS_DEFINITIONS -> {
                if (SearchManager.SUGGEST_URI_PATH_QUERY == uriLast) {
                    return null
                }
                table = Q.SUGGEST_FTS_DEFINITIONS.TABLE
                projection = Q.SUGGEST_FTS_DEFINITIONS.PROJECTION
                projection[1] = projection[1].replace("#\\{suggest_text_1\\}".toRegex(), SearchManager.SUGGEST_COLUMN_TEXT_1)
                projection[2] = projection[2].replace("#\\{suggest_query\\}".toRegex(), SearchManager.SUGGEST_COLUMN_QUERY)
                selection = Q.SUGGEST_FTS_DEFINITIONS.SELECTION
                selectionArgs = arrayOf(Q.SUGGEST_FTS_DEFINITIONS.ARGS[0].replace("#\\{uri_last\\}".toRegex(), uriLast))
            }

            SUGGEST_FTS_SAMPLES -> {
                if (SearchManager.SUGGEST_URI_PATH_QUERY == uriLast) {
                    return null
                }
                table = Q.SUGGEST_FTS_SAMPLES.TABLE
                projection = Q.SUGGEST_FTS_SAMPLES.PROJECTION
                projection[1] = projection[1].replace("#\\{suggest_text_1\\}".toRegex(), SearchManager.SUGGEST_COLUMN_TEXT_1)
                projection[2] = projection[2].replace("#\\{suggest_query\\}".toRegex(), SearchManager.SUGGEST_COLUMN_QUERY)
                selection = Q.SUGGEST_FTS_SAMPLES.SELECTION
                selectionArgs = arrayOf(Q.SUGGEST_FTS_SAMPLES.ARGS[0].replace("#\\{uri_last\\}".toRegex(), uriLast))
            }

            else -> return null
        }
        return Result(table, projection, selection, selectionArgs, null)
    }

    data class Result(val table: String, val projection: Array<String>?, val selection: String?, val selectionArgs: Array<String>?, val groupBy: String?) {

        override fun toString(): String {
            return "table='$table'\nprojection=${projection.contentToString()}\nselection='$selection'\nselectionArgs=${selectionArgs.contentToString()}\ngroupBy=$groupBy"
        }
    }

    fun interface Factory {

        fun make(selection: String?): String?
    }
}
