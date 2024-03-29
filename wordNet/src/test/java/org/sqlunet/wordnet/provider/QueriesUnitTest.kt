/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.provider

import org.junit.Test
import org.sqlunet.wordnet.provider.QueriesNew.queryNew
import org.sqlunet.wordnet.provider.WordNetControl.queryAnyRelations
import org.sqlunet.wordnet.provider.WordNetControl.queryMain
import org.sqlunet.wordnet.provider.WordNetControl.querySearch
import org.sqlunet.wordnet.provider.WordNetControl.querySuggest

/**
 * Queries factory, which will execute on the development machine (host).
 */
class QueriesUnitTest {

    private val codes = intArrayOf(
        WordNetControl.WORDS,
        WordNetControl.WORD,
        WordNetControl.SENSES,
        WordNetControl.SENSE,
        WordNetControl.SYNSETS,
        WordNetControl.SYNSET,
        WordNetControl.SEMRELATIONS,
        WordNetControl.LEXRELATIONS,
        WordNetControl.RELATIONS,
        WordNetControl.POSES,
        WordNetControl.DOMAINS,
        WordNetControl.ADJPOSITIONS,
        WordNetControl.SAMPLES,
        WordNetControl.DICT,
        WordNetControl.WORDS_SENSES_SYNSETS,
        WordNetControl.WORDS_SENSES_CASEDWORDS_SYNSETS,
        WordNetControl.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS,
        WordNetControl.SENSES_WORDS,
        WordNetControl.SENSES_WORDS_BY_SYNSET,
        WordNetControl.SENSES_SYNSETS_POSES_DOMAINS,
        WordNetControl.SYNSETS_POSES_DOMAINS,
        WordNetControl.ANYRELATIONS_SENSES_WORDS_X_BY_SYNSET,
        WordNetControl.SEMRELATIONS_SYNSETS,
        WordNetControl.SEMRELATIONS_SYNSETS_X,
        WordNetControl.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET,
        WordNetControl.LEXRELATIONS_SENSES,
        WordNetControl.LEXRELATIONS_SENSES_X,
        WordNetControl.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET,
        WordNetControl.SENSES_VFRAMES,
        WordNetControl.SENSES_VTEMPLATES,
        WordNetControl.SENSES_ADJPOSITIONS,
        WordNetControl.LEXES_MORPHS,
        WordNetControl.WORDS_LEXES_MORPHS,
        WordNetControl.WORDS_LEXES_MORPHS_BY_WORD,
        WordNetControl.LOOKUP_FTS_WORDS,
        WordNetControl.LOOKUP_FTS_DEFINITIONS,
        WordNetControl.LOOKUP_FTS_SAMPLES,
        WordNetControl.SUGGEST_WORDS,
        WordNetControl.SUGGEST_FTS_WORDS,
        WordNetControl.SUGGEST_FTS_DEFINITIONS,
        WordNetControl.SUGGEST_FTS_SAMPLES
    )
    private val uriLast = "LAST"
    private val projection = arrayOf("PROJ1", "PROJ2", "PROJ3")
    private val selection = "SEL"
    private val selectionArgs = arrayOf("ARG1", "ARG2", "ARG3")
    private val sortOrder = "SORT"
    private val factory = WordNetControl.Factory { _: String? -> Q.ANYRELATIONS_QUERY.TABLE }

    @Test
    fun queriesLegacyAgainstNew() {
        for (code in codes) {
            queryLegacyAgainstNew(code, uriLast, projection, selection, selectionArgs, sortOrder)
        }
    }

    @Test
    fun queriesLegacyAgainstProvider() {
        for (code in codes) {
            queryLegacyAgainstProvider(code, uriLast, projection, selection, selectionArgs, sortOrder)
        }
    }

    @Test
    fun queriesNewAgainstProvider() {
        for (code in codes) {
            queryNewAgainstProvider(code, uriLast, projection, selection, selectionArgs, sortOrder)
        }
    }

    private fun queryLegacyAgainstNew(code: Int, uriLast: String, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?) {
        val r1 = QueriesLegacy.queryLegacy(code, uriLast, projection, selection, selectionArgs, sortOrder, factory)
        val r2 = queryNew(code, uriLast, projection!!, selection, selectionArgs, sortOrder, factory)
        check(code, r1, r2)
    }

    private fun queryLegacyAgainstProvider(code: Int, uriLast: String, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?) {
        val r1 = QueriesLegacy.queryLegacy(code, uriLast, projection, selection, selectionArgs, sortOrder, factory)
        val r2 = queryProvider(code, uriLast, projection, selection, selectionArgs, sortOrder)
        check(code, r1, r2)
    }

    private fun queryNewAgainstProvider(code: Int, uriLast: String, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?) {
        val r1 = queryNew(code, uriLast, projection!!, selection, selectionArgs, sortOrder, factory)
        val r2 = queryProvider(code, uriLast, projection, selection, selectionArgs, sortOrder)
        check(code, r1, r2)
    }

    private fun queryProvider(code: Int, uriLast: String, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?, @Suppress("unused") sortOrder0: String?): WordNetControl.Result? {
        var r = queryMain(code, uriLast, projection0, selection0, selectionArgs0)
        if (r == null) {
            r = queryAnyRelations(code, projection0, selection0, selectionArgs0)
            if (r == null) {
                r = querySearch(code, projection0, selection0, selectionArgs0)
                if (r == null) {
                    r = querySuggest(code, uriLast)
                }
            }
        }
        return r
    }

    private fun check(code: Int, r1: WordNetControl.Result?, r2: WordNetControl.Result?) {
        assert(r1 != null)
        assert(r2 != null)
        assert(equals(r1!!.table, r2!!.table)) {
            """
            Code=$code
            ${r1.table}
            !=
            ${r2.table}
            """.trimIndent()
        }
        assert(r1.projection.contentEquals(r2.projection)) {
            """
            Code=$code
            ${r1.projection.contentToString()}
            !=
            ${r2.projection.contentToString()}
            """.trimIndent()
        }
        assert(equals(r1.selection, r2.selection)) {
            """
            Code=$code
            ${r1.selection}
            != ${r2.selection}
            """.trimIndent()
        }
        assert(r1.selectionArgs.contentEquals(r2.selectionArgs)) {
            """
            Code=$code
            ${r1.selectionArgs.contentToString()}
            !=
            ${r2.selectionArgs.contentToString()}
            """.trimIndent()
        }
        assert(equals(r1.groupBy, r2.groupBy)) {
            """
            Code=$code
            ${r1.groupBy}
            !=
            ${r2.groupBy}
            """.trimIndent()
        }
    }

    companion object {

        private fun equals(a: Any?, b: Any?): Boolean {
            return a == b
        }
    }
}