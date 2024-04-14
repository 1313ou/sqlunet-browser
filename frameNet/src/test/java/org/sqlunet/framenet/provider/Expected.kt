/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.provider

import android.app.SearchManager

/**
 * Queries factory, which will execute on the development machine (host).
 */
object Expected {

    fun expected(code: Int, uriLast: String, projection0: Array<String>, selection0: String?, selectionArgs0: Array<String>?): FrameNetControl.Result? {
        val table: String
        var projection = projection0
        var selection = selection0
        var selectionArgs = selectionArgs0
        var groupBy: String? = null

        when (code) {
            FrameNetControl.LEXUNITS -> table = Q.LEXUNITS.TABLE
            FrameNetControl.FRAMES -> table = Q.FRAMES.TABLE
            FrameNetControl.FRAMES_RELATED -> table = Q.FRAMES_RELATED.TABLE
            FrameNetControl.SENTENCES -> table = Q.SENTENCES.TABLE
            FrameNetControl.ANNOSETS -> table = Q.ANNOSETS.TABLE
            FrameNetControl.SENTENCES_LAYERS_X -> table = Q.SENTENCES_LAYERS_X.TABLE
            FrameNetControl.ANNOSETS_LAYERS_X -> table = Q.ANNOSETS_LAYERS_X.TABLE
            FrameNetControl.PATTERNS_LAYERS_X -> table = Q.PATTERNS_LAYERS_X.TABLE
            FrameNetControl.VALENCEUNITS_LAYERS_X -> table = Q.VALENCEUNITS_LAYERS_X.TABLE
            FrameNetControl.PATTERNS_SENTENCES -> table = Q.PATTERNS_SENTENCES.TABLE
            FrameNetControl.VALENCEUNITS_SENTENCES -> table = Q.VALENCEUNITS_SENTENCES.TABLE
            FrameNetControl.GOVERNORS_ANNOSETS -> table = Q.GOVERNORS_ANNOSETS.TABLE
            FrameNetControl.WORDS_LEXUNITS_FRAMES -> table = Q.WORDS_LEXUNITS_FRAMES.TABLE
            FrameNetControl.WORDS_LEXUNITS_FRAMES_FN -> table = Q.WORDS_LEXUNITS_FRAMES_FN.TABLE
            FrameNetControl.LEXUNITS_OR_FRAMES -> table = Q.LEXUNITS_OR_FRAMES.TABLE
            FrameNetControl.LEXUNITS_OR_FRAMES_FN -> table = Q.LEXUNITS_OR_FRAMES_FN.TABLE
            FrameNetControl.FRAMES_FES -> table = Q.FRAMES_FES.TABLE
            FrameNetControl.LEXUNITS_SENTENCES -> table = Q.LEXUNITS_SENTENCES.TABLE
            FrameNetControl.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS -> table = Q.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS.TABLE
            FrameNetControl.LEXUNITS_GOVERNORS -> table = Q.LEXUNITS_GOVERNORS.TABLE
            FrameNetControl.LEXUNITS_GOVERNORS_FN -> table = Q.LEXUNITS_GOVERNORS_FN.TABLE
            FrameNetControl.LEXUNITS_REALIZATIONS -> table = Q.LEXUNITS_REALIZATIONS.TABLE
            FrameNetControl.LEXUNITS_GROUPREALIZATIONS -> table = Q.LEXUNITS_GROUPREALIZATIONS.TABLE
            FrameNetControl.LOOKUP_FTS_WORDS -> table = Q.LOOKUP_FTS_WORDS.TABLE
            FrameNetControl.LOOKUP_FTS_SENTENCES -> table = Q.LOOKUP_FTS_SENTENCES.TABLE
            FrameNetControl.LOOKUP_FTS_SENTENCES_X -> table = Q.LOOKUP_FTS_SENTENCES_X.TABLE

            FrameNetControl.LEXUNIT1 -> {
                table = Q.LEXUNIT1.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += V.LUID + " = " + uriLast
            }

            FrameNetControl.LEXUNITS_X_BY_LEXUNIT -> {
                table = Q.LEXUNITS_X_BY_LEXUNIT.TABLE
                groupBy = Q.LEXUNITS_X_BY_LEXUNIT.GROUPBY
            }

            FrameNetControl.FRAME1 -> {
                table = Q.FRAME1.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += V.FRAMEID + " = " + uriLast
            }

            FrameNetControl.FRAMES_X_BY_FRAME -> {
                table = Q.FRAMES_X_BY_FRAME.TABLE
                groupBy = Q.FRAMES_X_BY_FRAME.GROUPBY
            }

            FrameNetControl.SENTENCE1 -> {
                table = Q.SENTENCE1.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += V.SENTENCEID + " = " + uriLast
            }

            FrameNetControl.ANNOSET1 -> {
                table = Q.ANNOSET1.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += V.ANNOSETID + " = " + uriLast
            }

            FrameNetControl.FRAMES_FES_BY_FE -> {
                table = Q.FRAMES_FES_BY_FE.TABLE
                groupBy = Q.FRAMES_FES_BY_FE.GROUPBY
            }

            FrameNetControl.LEXUNITS_SENTENCES_BY_SENTENCE -> {
                table = Q.LEXUNITS_SENTENCES_BY_SENTENCE.TABLE
                groupBy = Q.LEXUNITS_SENTENCES_BY_SENTENCE.GROUPBY
            }

            FrameNetControl.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE -> {
                table = Q.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE.TABLE
                groupBy = Q.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE.GROUPBY
            }

            FrameNetControl.LEXUNITS_REALIZATIONS_BY_REALIZATION -> {
                table = Q.LEXUNITS_REALIZATIONS_BY_REALIZATION.TABLE
                groupBy = Q.LEXUNITS_REALIZATIONS_BY_REALIZATION.GROUPBY
            }

            FrameNetControl.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN -> {
                table = Q.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN.TABLE
                groupBy = Q.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN.GROUPBY
            }

            FrameNetControl.LOOKUP_FTS_SENTENCES_X_BY_SENTENCE -> {
                table = Q.LOOKUP_FTS_SENTENCES_X_BY_SENTENCE.TABLE
                groupBy = Q.LOOKUP_FTS_SENTENCES_X_BY_SENTENCE.GROUPBY
            }

            FrameNetControl.SUGGEST_WORDS -> {
                projection = Q.SUGGEST_WORDS.PROJECTION
                projection[1] = projection[1].replace("#\\{suggest_text_1\\}".toRegex(), SearchManager.SUGGEST_COLUMN_TEXT_1)
                projection[2] = projection[2].replace("#\\{suggest_query\\}".toRegex(), SearchManager.SUGGEST_COLUMN_QUERY)
                selection = Q.SUGGEST_WORDS.SELECTION
                selectionArgs = arrayOf(Q.SUGGEST_WORDS.ARGS[0].replace("#\\{uri_last\\}".toRegex(), uriLast))
                table = Q.SUGGEST_WORDS.TABLE
            }

            FrameNetControl.SUGGEST_FTS_WORDS -> {
                projection = Q.SUGGEST_FTS_WORDS.PROJECTION
                projection[1] = projection[1].replace("#\\{suggest_text_1\\}".toRegex(), SearchManager.SUGGEST_COLUMN_TEXT_1)
                projection[2] = projection[2].replace("#\\{suggest_query\\}".toRegex(), SearchManager.SUGGEST_COLUMN_QUERY)
                selection = Q.SUGGEST_FTS_WORDS.SELECTION
                selectionArgs = arrayOf(Q.SUGGEST_FTS_WORDS.ARGS[0].replace("#\\{uri_last\\}".toRegex(), uriLast))
                table = Q.SUGGEST_FTS_WORDS.TABLE
            }

            else -> return null
        }
        return FrameNetControl.Result(table, projection, selection, selectionArgs, groupBy)
    }
}