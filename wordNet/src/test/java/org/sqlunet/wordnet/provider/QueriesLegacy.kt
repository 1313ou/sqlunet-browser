/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.provider

import android.app.SearchManager
import org.sqlunet.provider.BaseProvider.Companion.appendProjection

/**
 * Queries factory, which will execute on the development machine (host).
 */
object QueriesLegacy {

    fun queryLegacy(code: Int, uriLast: String, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?, @Suppress("unused") sortOrder0: String?, subqueryFactory: WordNetControl.Factory): WordNetControl.Result? {
        val table: String
        var projection = projection0
        var selection = selection0
        var selectionArgs = selectionArgs0
        var groupBy: String? = null
        when (code) {
            WordNetControl.WORDS -> table = WordNetContract.Words.TABLE
            WordNetControl.SENSES -> table = WordNetContract.Senses.TABLE
            WordNetControl.SYNSETS -> table = WordNetContract.Synsets.TABLE
            WordNetControl.SEMRELATIONS -> table = WordNetContract.SemRelations.TABLE
            WordNetControl.LEXRELATIONS -> table = WordNetContract.LexRelations.TABLE
            WordNetControl.RELATIONS -> table = WordNetContract.Relations.TABLE
            WordNetControl.POSES -> table = WordNetContract.Poses.TABLE
            WordNetControl.DOMAINS -> table = WordNetContract.Domains.TABLE
            WordNetControl.ADJPOSITIONS -> table = WordNetContract.AdjPositions.TABLE
            WordNetControl.SAMPLES -> table = WordNetContract.Samples.TABLE
            WordNetControl.WORD -> {
                table = WordNetContract.Words.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += WordNetContract.Words.WORDID + " = " + uriLast
            }

            WordNetControl.SENSE -> {
                table = WordNetContract.Senses.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += WordNetContract.Senses.SENSEID + " = " + uriLast
            }

            WordNetControl.SYNSET -> {
                table = WordNetContract.Synsets.TABLE
                if (selection != null) {
                    selection += " AND "
                } else {
                    selection = ""
                }
                selection += WordNetContract.Synsets.SYNSETID + " = " + uriLast
            }

            WordNetControl.DICT -> table = WordNetContract.Dict.TABLE
            WordNetControl.WORDS_SENSES_SYNSETS ->
                table = "words AS " + WordNetContract.AS_WORDS + " " +
                        "LEFT JOIN senses AS " + WordNetContract.AS_SENSES + " USING (wordid) " +
                        "LEFT JOIN synsets AS " + WordNetContract.AS_SYNSETS + " USING (synsetid)"

            WordNetControl.WORDS_SENSES_CASEDWORDS_SYNSETS ->
                table = "words AS " + WordNetContract.AS_WORDS + " " +
                        "LEFT JOIN senses AS " + WordNetContract.AS_SENSES + " USING (wordid) " +
                        "LEFT JOIN casedwords AS " + WordNetContract.AS_CASEDS + " USING (wordid,casedwordid) " +
                        "LEFT JOIN synsets AS " + WordNetContract.AS_SYNSETS + " USING (synsetid)"

            WordNetControl.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS ->
                table = "lexes AS " + WordNetContract.AS_LEXES + " " +
                        "INNER JOIN words AS " + WordNetContract.AS_WORDS + " USING (wordid) " +
                        "LEFT JOIN senses AS " + WordNetContract.AS_SENSES + " USING (wordid) " +
                        "LEFT JOIN casedwords AS " + WordNetContract.AS_CASEDS + " USING (wordid,casedwordid) " +
                        "LEFT JOIN synsets AS " + WordNetContract.AS_SYNSETS + " USING (synsetid) " +
                        "LEFT JOIN poses AS " + WordNetContract.AS_POSES + " USING (posid) " +
                        "LEFT JOIN domains AS " + WordNetContract.AS_DOMAINS + " USING (domainid)"

            WordNetControl.SENSES_WORDS ->
                table = "senses AS " + WordNetContract.AS_SENSES + " " +
                        "LEFT JOIN words AS " + WordNetContract.AS_WORDS + " USING (wordid)"

            WordNetControl.SENSES_WORDS_BY_SYNSET -> {
                table = "senses AS " + WordNetContract.AS_SENSES + " " +
                        "LEFT JOIN words AS " + WordNetContract.AS_WORDS + " USING (wordid)"
                projection = appendProjection(projection, "GROUP_CONCAT(DISTINCT " + WordNetContract.AS_WORDS + ".word) AS " + WordNetContract.Senses_Words.MEMBERS)
                groupBy = "synsetid"
            }

            WordNetControl.SENSES_SYNSETS_POSES_DOMAINS ->
                table = "senses AS " + WordNetContract.AS_SENSES + " " +
                        "INNER JOIN synsets AS " + WordNetContract.AS_SYNSETS + " USING (synsetid) " +
                        "LEFT JOIN poses AS " + WordNetContract.AS_POSES + " USING (posid) " +
                        "LEFT JOIN domains AS " + WordNetContract.AS_DOMAINS + " USING (domainid)"

            WordNetControl.SYNSETS_POSES_DOMAINS ->
                table = "synsets AS " + WordNetContract.AS_SYNSETS + " " +
                        "LEFT JOIN poses AS " + WordNetContract.AS_POSES + " USING (posid) " +
                        "LEFT JOIN domains AS " + WordNetContract.AS_DOMAINS + " USING (domainid)"

            WordNetControl.ANYRELATIONS_SENSES_WORDS_X_BY_SYNSET -> {
                val subQuery = subqueryFactory.make(selection0)
                table = "( " + subQuery + " ) AS " + WordNetContract.AS_RELATIONS + ' ' +
                        "INNER JOIN relations USING (relationid) " +
                        "INNER JOIN synsets AS " + WordNetContract.AS_SYNSETS2 + " ON " + WordNetContract.AS_RELATIONS + ".synset2id = " + WordNetContract.AS_SYNSETS2 + ".synsetid " +
                        "LEFT JOIN senses ON " + WordNetContract.AS_SYNSETS2 + ".synsetid = senses.synsetid " +
                        "LEFT JOIN words AS " + WordNetContract.AS_WORDS + " USING (wordid) " +
                        "LEFT JOIN words AS " + WordNetContract.AS_WORDS2 + " ON " + WordNetContract.AS_RELATIONS + ".word2id = " + WordNetContract.AS_WORDS2 + ".wordid"
                selection = null
                groupBy = V.SYNSET2ID + "," + WordNetContract.RELATIONTYPE + ",relation,relationid," + V.WORD2ID + ',' + V.WORD2
            }

            WordNetControl.SEMRELATIONS_SYNSETS ->
                table = "semrelations AS " + WordNetContract.AS_RELATIONS + ' ' +
                        "INNER JOIN synsets AS " + WordNetContract.AS_SYNSETS2 + " ON " + WordNetContract.AS_RELATIONS + ".synset2id = " + WordNetContract.AS_SYNSETS2 + ".synsetid"

            WordNetControl.SEMRELATIONS_SYNSETS_X ->
                table = "semrelations AS " + WordNetContract.AS_RELATIONS + ' ' +
                        "INNER JOIN relations USING (relationid) " +
                        "INNER JOIN synsets AS " + WordNetContract.AS_SYNSETS2 + " ON " + WordNetContract.AS_RELATIONS + ".synset2id = " + WordNetContract.AS_SYNSETS2 + ".synsetid "

            WordNetControl.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET -> {
                table = "semrelations AS " + WordNetContract.AS_RELATIONS + ' ' +
                        "INNER JOIN relations USING (relationid) " +
                        "INNER JOIN synsets AS " + WordNetContract.AS_SYNSETS2 + " ON " + WordNetContract.AS_RELATIONS + ".synset2id = " + WordNetContract.AS_SYNSETS2 + ".synsetid " +
                        "LEFT JOIN senses ON " + WordNetContract.AS_SYNSETS2 + ".synsetid = senses.synsetid " +
                        "LEFT JOIN words AS " + WordNetContract.AS_WORDS2 + " USING (wordid)"
                projection = appendProjection(projection, "GROUP_CONCAT(DISTINCT " + WordNetContract.AS_WORDS2 + ".word) AS " + WordNetContract.SemRelations_Synsets_Words_X.MEMBERS2)
                groupBy = WordNetContract.AS_SYNSETS2 + ".synsetid"
            }

            WordNetControl.LEXRELATIONS_SENSES ->
                table = "lexrelations AS " + WordNetContract.AS_RELATIONS + ' ' +
                        "INNER JOIN synsets AS " + WordNetContract.AS_SYNSETS2 + " ON " + WordNetContract.AS_RELATIONS + ".synset2id = " + WordNetContract.AS_SYNSETS2 + ".synsetid " +
                        "INNER JOIN words AS " + WordNetContract.AS_WORDS + " ON " + WordNetContract.AS_RELATIONS + ".word2id = " + WordNetContract.AS_WORDS + ".wordid"

            WordNetControl.LEXRELATIONS_SENSES_X ->
                table = "lexrelations AS " + WordNetContract.AS_RELATIONS + ' ' +
                        "INNER JOIN relations USING (relationid) " +
                        "INNER JOIN synsets AS " + WordNetContract.AS_SYNSETS2 + " ON " + WordNetContract.AS_RELATIONS + ".synset2id = " + WordNetContract.AS_SYNSETS2 + ".synsetid " +
                        "INNER JOIN words AS " + WordNetContract.AS_WORDS + " ON " + WordNetContract.AS_RELATIONS + ".word2id = " + WordNetContract.AS_WORDS + ".wordid "

            WordNetControl.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET -> {
                table = "lexrelations AS " + WordNetContract.AS_RELATIONS + ' ' +
                        "INNER JOIN relations USING (relationid) " +
                        "INNER JOIN synsets AS " + WordNetContract.AS_SYNSETS2 + " ON " + WordNetContract.AS_RELATIONS + ".synset2id = " + WordNetContract.AS_SYNSETS2 + ".synsetid " +
                        "INNER JOIN words AS " + WordNetContract.AS_WORDS + " ON " + WordNetContract.AS_RELATIONS + ".word2id = " + WordNetContract.AS_WORDS + ".wordid " +
                        "LEFT JOIN senses AS " + WordNetContract.AS_SENSES + " ON " + WordNetContract.AS_SYNSETS2 + ".synsetid = " + WordNetContract.AS_SENSES + ".synsetid " +
                        "LEFT JOIN words AS " + WordNetContract.AS_WORDS2 + " USING (wordid)"
                projection = appendProjection(projection, "GROUP_CONCAT(DISTINCT " + WordNetContract.AS_WORDS2 + ".word) AS " + WordNetContract.LexRelations_Senses_Words_X.MEMBERS2)
                groupBy = WordNetContract.AS_SYNSETS2 + ".synsetid"
            }

            WordNetControl.SENSES_VFRAMES -> table = "senses_vframes " +
                    "LEFT JOIN vframes USING (frameid)"

            WordNetControl.SENSES_VTEMPLATES -> table = "senses_vtemplates " +
                    "LEFT JOIN vtemplates USING (templateid)"

            WordNetControl.SENSES_ADJPOSITIONS -> table = "senses_adjpositions " +
                    "LEFT JOIN adjpositions USING (positionid)"

            WordNetControl.LEXES_MORPHS -> table = "lexes_morphs " +
                    "LEFT JOIN morphs USING (morphid)"

            WordNetControl.WORDS_LEXES_MORPHS ->
                table = "words " +
                        "LEFT JOIN lexes_morphs USING (wordid) " + "LEFT JOIN morphs USING (morphid)"

            WordNetControl.WORDS_LEXES_MORPHS_BY_WORD -> {
                table = "words " +
                        "LEFT JOIN lexes_morphs USING (wordid) " + "LEFT JOIN morphs USING (morphid)"
                groupBy = "wordid"
            }

            WordNetControl.LOOKUP_FTS_WORDS -> table = "words_word_fts4"
            WordNetControl.LOOKUP_FTS_DEFINITIONS -> table = "synsets_definition_fts4"
            WordNetControl.LOOKUP_FTS_SAMPLES -> table = "samples_sample_fts4"
            WordNetControl.SUGGEST_WORDS -> {
                projection = arrayOf(
                    "wordid AS _id",
                    "word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1,
                    "word AS " + SearchManager.SUGGEST_COLUMN_QUERY
                )
                selection = "word LIKE ? || '%'"
                selectionArgs = arrayOf(uriLast)
                table = "words"
            }

            WordNetControl.SUGGEST_FTS_WORDS -> {
                projection = arrayOf(
                    "wordid AS _id",
                    "word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1,
                    "word AS " + SearchManager.SUGGEST_COLUMN_QUERY
                )
                selection = "word MATCH ?"
                selectionArgs = arrayOf("$uriLast*")
                table = "words_word_fts4"
            }

            WordNetControl.SUGGEST_FTS_DEFINITIONS -> {
                projection = arrayOf(
                    "synsetid AS _id",
                    "definition AS " + SearchManager.SUGGEST_COLUMN_TEXT_1,
                    "definition AS " + SearchManager.SUGGEST_COLUMN_QUERY
                )
                selection = "definition MATCH ?"
                selectionArgs = arrayOf("$uriLast*")
                table = "synsets_definition_fts4"
            }

            WordNetControl.SUGGEST_FTS_SAMPLES -> {
                projection = arrayOf(
                    "synsetid AS _id",
                    "sample AS " + SearchManager.SUGGEST_COLUMN_TEXT_1,
                    "sample AS " + SearchManager.SUGGEST_COLUMN_QUERY
                )
                selection = "sample MATCH ?"
                selectionArgs = arrayOf("$uriLast*")
                table = "samples_sample_fts4"
            }

            else -> return null
        }
        return WordNetControl.Result(table, projection, selection, selectionArgs, groupBy)
    }
}