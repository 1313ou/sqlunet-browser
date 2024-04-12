/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.provider

import android.app.SearchManager

/**
 * FrameNet query control
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object FrameNetControl {

    // table codes
    const val LEXUNIT1 = 10
    const val LEXUNITS = 11
    const val LEXUNITS_X_BY_LEXUNIT = 12
    const val FRAME1 = 20
    const val FRAMES = 21
    const val FRAMES_X_BY_FRAME = 23
    const val FRAMES_RELATED = 25
    const val SENTENCE1 = 30
    const val SENTENCES = 31
    const val ANNOSET1 = 40
    const val ANNOSETS = 41
    const val SENTENCES_LAYERS_X = 50
    const val ANNOSETS_LAYERS_X = 51
    const val PATTERNS_LAYERS_X = 52
    const val VALENCEUNITS_LAYERS_X = 53
    const val PATTERNS_SENTENCES = 61
    const val VALENCEUNITS_SENTENCES = 62
    const val GOVERNORS_ANNOSETS = 70

    // join codes
    const val WORDS_LEXUNITS_FRAMES = 100
    const val WORDS_LEXUNITS_FRAMES_FN = 101
    const val LEXUNITS_OR_FRAMES = 110
    const val LEXUNITS_OR_FRAMES_FN = 111
    const val FRAMES_FES = 200
    const val FRAMES_FES_BY_FE = 201
    const val LEXUNITS_SENTENCES = 300
    const val LEXUNITS_SENTENCES_BY_SENTENCE = 301
    const val LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS = 310
    const val LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE = 311
    const val LEXUNITS_GOVERNORS = 410
    const val LEXUNITS_GOVERNORS_FN = 411
    const val LEXUNITS_REALIZATIONS = 420
    const val LEXUNITS_REALIZATIONS_BY_REALIZATION = 421
    const val LEXUNITS_GROUPREALIZATIONS = 430
    const val LEXUNITS_GROUPREALIZATIONS_BY_PATTERN = 431

    // search text codes
    const val LOOKUP_FTS_WORDS = 510
    const val LOOKUP_FTS_SENTENCES = 511
    const val LOOKUP_FTS_SENTENCES_X = 512
    const val LOOKUP_FTS_SENTENCES_X_BY_SENTENCE = 513

    // suggest
    const val SUGGEST_WORDS = 601
    const val SUGGEST_FTS_WORDS = 602

    @JvmStatic
    fun queryMain(code: Int, uriLast: String, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): Result? {
        val table: String
        var selection = selection0
        var groupBy: String? = null
        when (code) {
            LEXUNITS -> table = Q.LEXUNITS.TABLE
            FRAMES -> table = Q.FRAMES.TABLE
            ANNOSETS -> table = Q.ANNOSETS.TABLE
            SENTENCES -> table = Q.SENTENCES.TABLE
            LEXUNIT1 -> {
                table = Q.LEXUNIT1.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += Q.LEXUNIT1.SELECTION.replace("#\\{uri_last\\}".toRegex(), uriLast) // V.LUID + " = " + uriLast
            }

            FRAME1 -> {
                table = Q.FRAME1.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += Q.FRAME1.SELECTION.replace("#\\{uri_last\\}".toRegex(), uriLast) // V.FRAMEID + " = " + uriLast
            }

            SENTENCE1 -> {
                table = Q.SENTENCE1.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += Q.SENTENCE1.SELECTION.replace("#\\{uri_last\\}".toRegex(), uriLast) // V.SENTENCEID + " = " + uriLast
            }

            ANNOSET1 -> {
                table = Q.ANNOSET1.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += Q.ANNOSET1.SELECTION.replace("#\\{uri_last\\}".toRegex(), uriLast) // V.ANNOSETID + " = " + uriLast
            }

            LEXUNITS_OR_FRAMES -> table = Q.LEXUNITS_OR_FRAMES.TABLE
            LEXUNITS_OR_FRAMES_FN -> table = Q.LEXUNITS_OR_FRAMES_FN.TABLE
            FRAMES_X_BY_FRAME -> {
                table = Q.FRAMES_X_BY_FRAME.TABLE
                groupBy = V.FRAMEID
            }

            FRAMES_RELATED -> table = Q.FRAMES_RELATED.TABLE
            LEXUNITS_X_BY_LEXUNIT -> {
                table = Q.LEXUNITS_X_BY_LEXUNIT.TABLE
                groupBy = V.LUID
            }

            SENTENCES_LAYERS_X -> table = Q.SENTENCES_LAYERS_X.TABLE
            ANNOSETS_LAYERS_X -> table = Q.ANNOSETS_LAYERS_X.TABLE
            PATTERNS_LAYERS_X -> table = Q.PATTERNS_LAYERS_X.TABLE
            VALENCEUNITS_LAYERS_X -> table = Q.VALENCEUNITS_LAYERS_X.TABLE
            WORDS_LEXUNITS_FRAMES -> table = Q.WORDS_LEXUNITS_FRAMES.TABLE
            WORDS_LEXUNITS_FRAMES_FN -> table = Q.WORDS_LEXUNITS_FRAMES_FN.TABLE
            FRAMES_FES_BY_FE -> {
                table = Q.FRAMES_FES_BY_FE.TABLE
                groupBy = V.FEID
            }

            FRAMES_FES -> table = Q.FRAMES_FES.TABLE
            LEXUNITS_SENTENCES_BY_SENTENCE -> {
                table = Q.LEXUNITS_SENTENCES_BY_SENTENCE.TABLE
                groupBy = V.AS_SENTENCES + '.' + V.SENTENCEID
            }

            LEXUNITS_SENTENCES -> table = Q.LEXUNITS_SENTENCES.TABLE
            LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE -> {
                table = Q.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE.TABLE
                groupBy = V.AS_SENTENCES + '.' + V.SENTENCEID
            }

            LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS -> table = Q.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS.TABLE
            LEXUNITS_GOVERNORS -> table = Q.LEXUNITS_GOVERNORS.TABLE
            LEXUNITS_GOVERNORS_FN -> table = Q.LEXUNITS_GOVERNORS_FN.TABLE
            GOVERNORS_ANNOSETS -> table = Q.GOVERNORS_ANNOSETS.TABLE
            LEXUNITS_REALIZATIONS_BY_REALIZATION -> {
                table = Q.LEXUNITS_REALIZATIONS_BY_REALIZATION.TABLE
                groupBy = V.FERID
            }

            LEXUNITS_REALIZATIONS -> table = Q.LEXUNITS_REALIZATIONS.TABLE
            LEXUNITS_GROUPREALIZATIONS_BY_PATTERN -> {
                table = Q.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN.TABLE
                groupBy = V.PATTERNID
            }

            LEXUNITS_GROUPREALIZATIONS -> table = Q.LEXUNITS_GROUPREALIZATIONS.TABLE
            PATTERNS_SENTENCES -> table = Q.PATTERNS_SENTENCES.TABLE
            VALENCEUNITS_SENTENCES -> table = Q.VALENCEUNITS_SENTENCES.TABLE
            else -> return null
        }
        return Result(table, projection0, selection, selectionArgs0, groupBy)
    }

    @JvmStatic
    fun querySearch(code: Int, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): Result? {
        val table: String
        var groupBy: String? = null
        when (code) {
            LOOKUP_FTS_WORDS -> table = Q.LOOKUP_FTS_WORDS.TABLE
            LOOKUP_FTS_SENTENCES -> table = Q.LOOKUP_FTS_SENTENCES.TABLE
            LOOKUP_FTS_SENTENCES_X_BY_SENTENCE -> {
                table = Q.LOOKUP_FTS_SENTENCES_X_BY_SENTENCE.TABLE
                groupBy = V.SENTENCEID
            }

            LOOKUP_FTS_SENTENCES_X -> table = Q.LOOKUP_FTS_SENTENCES_X.TABLE
            else -> return null
        }
        return Result(table, projection0, selection0, selectionArgs0, groupBy)
    }

    @JvmStatic
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
                selectionArgs = arrayOf(uriLast)
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
                selectionArgs = arrayOf("$uriLast*")
            }

            else -> return null
        }
        return Result(table, projection, selection, selectionArgs, null)
    }

    data class Result(@JvmField val table: String, @JvmField val projection: Array<String>?, @JvmField val selection: String?, @JvmField val selectionArgs: Array<String>?, @JvmField val groupBy: String?) {

        override fun toString(): String {
            return "table='$table'\nprojection=${projection.contentToString()}\nselection='$selection'\nselectionArgs=${selectionArgs.contentToString()}\ngroupBy=$groupBy"
        }
    }
}
