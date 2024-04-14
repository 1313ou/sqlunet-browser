/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.provider

import android.app.SearchManager
import org.sqlunet.provider.BaseProvider.Companion.appendProjection

/**
 * Queries factory, which will execute on the development machine (host).
 */
object Expected {

    fun expected(code: Int, uriLast: String, projection0: Array<String>, selection0: String?, selectionArgs0: Array<String>?, @Suppress("unused") sortOrder0: String?, subqueryFactory: WordNetControl.Factory): WordNetControl.Result? {
        val table: String
        var projection = projection0
        var selection = selection0
        var selectionArgs = selectionArgs0
        var groupBy: String? = null

        when (code) {
            WordNetControl.WORDS -> table = Q.WORDS.TABLE
            WordNetControl.SENSES -> table = Q.SENSES.TABLE
            WordNetControl.SYNSETS -> table = Q.SYNSETS.TABLE
            WordNetControl.SEMRELATIONS -> table = Q.SEMRELATIONS.TABLE
            WordNetControl.LEXRELATIONS -> table = Q.LEXRELATIONS.TABLE
            WordNetControl.RELATIONS -> table = Q.RELATIONS.TABLE
            WordNetControl.POSES -> table = Q.POSES.TABLE
            WordNetControl.DOMAINS -> table = Q.DOMAINS.TABLE
            WordNetControl.ADJPOSITIONS -> table = Q.ADJPOSITIONS.TABLE
            WordNetControl.SAMPLES -> table = Q.SAMPLES.TABLE
            WordNetControl.WORD -> {
                table = Q.WORD1.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += V.WORDID + " = " + uriLast
            }

            WordNetControl.SENSE -> {
                table = Q.SENSE1.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += V.SENSEID + " = " + uriLast
            }

            WordNetControl.SYNSET -> {
                table = Q.SYNSET1.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += V.SYNSETID + " = " + uriLast
            }

            WordNetControl.DICT -> table = Q.DICT.TABLE
            WordNetControl.WORDS_SENSES_SYNSETS -> table = Q.WORDS_SENSES_SYNSETS.TABLE
            WordNetControl.WORDS_SENSES_CASEDWORDS_SYNSETS -> table = Q.WORDS_SENSES_CASEDWORDS_SYNSETS.TABLE
            WordNetControl.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS -> table = Q.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS.TABLE
            WordNetControl.SENSES_WORDS -> table = Q.SENSES_WORDS.TABLE
            WordNetControl.SENSES_WORDS_BY_SYNSET -> {
                table = Q.SENSES_WORDS_BY_SYNSET.TABLE
                projection = appendProjection(projection, Q.SENSES_WORDS_BY_SYNSET.PROJECTION[0].replace("#\\{members\\}".toRegex(), WordNetContract.MEMBERS))
                groupBy = Q.SENSES_WORDS_BY_SYNSET.GROUPBY
            }

            WordNetControl.SENSES_SYNSETS_POSES_DOMAINS -> table = Q.SENSES_SYNSETS_POSES_DOMAINS.TABLE
            WordNetControl.SYNSETS_POSES_DOMAINS -> table = Q.SYNSETS_POSES_DOMAINS.TABLE
            WordNetControl.ANYRELATIONS_SENSES_WORDS_X_BY_SYNSET -> {
                val subQuery = subqueryFactory.make(selection0)
                table = Q.ANYRELATIONS_SENSES_WORDS_X_BY_SYNSET.TABLE.replaceFirst("#\\{query\\}".toRegex(), subQuery!!)
                selection = null
                groupBy = Q.ANYRELATIONS_SENSES_WORDS_X_BY_SYNSET.GROUPBY
            }

            WordNetControl.SEMRELATIONS_SYNSETS -> table = Q.SEMRELATIONS_SYNSETS.TABLE
            WordNetControl.SEMRELATIONS_SYNSETS_X -> table = Q.SEMRELATIONS_SYNSETS_X.TABLE
            WordNetControl.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET -> {
                table = Q.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.TABLE
                projection = appendProjection(projection, Q.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.PROJECTION[0].replace("#\\{members2\\}".toRegex(), WordNetContract.MEMBERS2))
                groupBy = Q.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.GROUPBY
            }

            WordNetControl.LEXRELATIONS_SENSES -> table = Q.LEXRELATIONS_SENSES.TABLE
            WordNetControl.LEXRELATIONS_SENSES_X -> table = Q.LEXRELATIONS_SENSES_X.TABLE
            WordNetControl.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET -> {
                table = Q.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.TABLE
                projection = appendProjection(projection, Q.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.PROJECTION[0].replace("#\\{members2\\}".toRegex(), WordNetContract.MEMBERS2))
                groupBy = Q.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.GROUPBY
            }

            WordNetControl.SENSES_VFRAMES -> table = Q.SENSES_VFRAMES.TABLE
            WordNetControl.SENSES_VTEMPLATES -> table = Q.SENSES_VTEMPLATES.TABLE
            WordNetControl.SENSES_ADJPOSITIONS -> table = Q.SENSES_ADJPOSITIONS.TABLE
            WordNetControl.LEXES_MORPHS -> table = Q.LEXES_MORPHS.TABLE
            WordNetControl.WORDS_LEXES_MORPHS -> table = Q.WORDS_LEXES_MORPHS.TABLE
            WordNetControl.WORDS_LEXES_MORPHS_BY_WORD -> {
                table = Q.WORDS_LEXES_MORPHS.TABLE
                groupBy = Q.WORDS_LEXES_MORPHS_BY_WORD.GROUPBY
            }

            WordNetControl.LOOKUP_FTS_WORDS -> table = Q.LOOKUP_FTS_WORDS.TABLE
            WordNetControl.LOOKUP_FTS_DEFINITIONS -> table = Q.LOOKUP_FTS_DEFINITIONS.TABLE
            WordNetControl.LOOKUP_FTS_SAMPLES -> table = Q.LOOKUP_FTS_SAMPLES.TABLE
            WordNetControl.SUGGEST_WORDS -> {
                projection = Q.SUGGEST_WORDS.PROJECTION
                projection[1] = projection[1].replace("#\\{suggest_text_1\\}".toRegex(), SearchManager.SUGGEST_COLUMN_TEXT_1)
                projection[2] = projection[2].replace("#\\{suggest_query\\}".toRegex(), SearchManager.SUGGEST_COLUMN_QUERY)
                selection = Q.SUGGEST_WORDS.SELECTION
                selectionArgs = arrayOf(Q.SUGGEST_WORDS.ARGS[0].replace("#\\{uri_last\\}".toRegex(), uriLast))
                table = Q.SUGGEST_WORDS.TABLE
            }

            WordNetControl.SUGGEST_FTS_WORDS -> {
                projection = Q.SUGGEST_FTS_WORDS.PROJECTION
                projection[1] = projection[1].replace("#\\{suggest_text_1\\}".toRegex(), SearchManager.SUGGEST_COLUMN_TEXT_1)
                projection[2] = projection[2].replace("#\\{suggest_query\\}".toRegex(), SearchManager.SUGGEST_COLUMN_QUERY)
                selection = Q.SUGGEST_FTS_WORDS.SELECTION
                selectionArgs = arrayOf(Q.SUGGEST_FTS_WORDS.ARGS[0].replace("#\\{uri_last\\}".toRegex(), uriLast))
                table = Q.SUGGEST_FTS_WORDS.TABLE
            }

            WordNetControl.SUGGEST_FTS_DEFINITIONS -> {
                projection = Q.SUGGEST_FTS_DEFINITIONS.PROJECTION
                projection[1] = projection[1].replace("#\\{suggest_text_1\\}".toRegex(), SearchManager.SUGGEST_COLUMN_TEXT_1)
                projection[2] = projection[2].replace("#\\{suggest_query\\}".toRegex(), SearchManager.SUGGEST_COLUMN_QUERY)
                selection = Q.SUGGEST_FTS_DEFINITIONS.SELECTION
                selectionArgs = arrayOf(Q.SUGGEST_FTS_DEFINITIONS.ARGS[0].replace("#\\{uri_last\\}".toRegex(), uriLast))
                table = Q.SUGGEST_FTS_DEFINITIONS.TABLE
            }

            WordNetControl.SUGGEST_FTS_SAMPLES -> {
                projection = Q.SUGGEST_FTS_SAMPLES.PROJECTION
                projection[1] = projection[1].replace("#\\{suggest_text_1\\}".toRegex(), SearchManager.SUGGEST_COLUMN_TEXT_1)
                projection[2] = projection[2].replace("#\\{suggest_query\\}".toRegex(), SearchManager.SUGGEST_COLUMN_QUERY)
                selection = Q.SUGGEST_FTS_SAMPLES.SELECTION
                selectionArgs = arrayOf(Q.SUGGEST_FTS_SAMPLES.ARGS[0].replace("#\\{uri_last\\}".toRegex(), uriLast))
                table = Q.SUGGEST_FTS_SAMPLES.TABLE
            }

            else -> return null
        }
        return WordNetControl.Result(table, projection, selection, selectionArgs, groupBy)
    }
}