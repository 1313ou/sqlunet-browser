/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.provider

import org.junit.Test
import org.sqlunet.framenet.provider.FrameNetControl.queryMain
import org.sqlunet.framenet.provider.FrameNetControl.querySearch
import org.sqlunet.framenet.provider.FrameNetControl.querySuggest

class QueriesUnitTest {

    private val codes = intArrayOf(
        FrameNetControl.LEXUNIT,
        FrameNetControl.LEXUNITS,
        FrameNetControl.LEXUNITS_X_BY_LEXUNIT,
        FrameNetControl.FRAME,
        FrameNetControl.FRAMES,
        FrameNetControl.FRAMES_X_BY_FRAME,
        FrameNetControl.FRAMES_RELATED,
        FrameNetControl.SENTENCE,
        FrameNetControl.SENTENCES,
        FrameNetControl.ANNOSET,
        FrameNetControl.ANNOSETS,
        FrameNetControl.SENTENCES_LAYERS_X,
        FrameNetControl.ANNOSETS_LAYERS_X,
        FrameNetControl.PATTERNS_LAYERS_X,
        FrameNetControl.VALENCEUNITS_LAYERS_X,
        FrameNetControl.PATTERNS_SENTENCES,
        FrameNetControl.VALENCEUNITS_SENTENCES,
        FrameNetControl.GOVERNORS_ANNOSETS,
        FrameNetControl.WORDS_LEXUNITS_FRAMES,
        FrameNetControl.LEXUNITS_OR_FRAMES,
        FrameNetControl.FRAMES_FES,
        FrameNetControl.FRAMES_FES_BY_FE,
        FrameNetControl.LEXUNITS_SENTENCES,
        FrameNetControl.LEXUNITS_SENTENCES_BY_SENTENCE,
        FrameNetControl.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS,
        FrameNetControl.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE,
        FrameNetControl.LEXUNITS_GOVERNORS,
        FrameNetControl.LEXUNITS_REALIZATIONS,
        FrameNetControl.LEXUNITS_REALIZATIONS_BY_REALIZATION,
        FrameNetControl.LEXUNITS_GROUPREALIZATIONS,
        FrameNetControl.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN,
        FrameNetControl.LOOKUP_FTS_WORDS,
        FrameNetControl.LOOKUP_FTS_SENTENCES,
        FrameNetControl.LOOKUP_FTS_SENTENCES_X,
        FrameNetControl.LOOKUP_FTS_SENTENCES_X_BY_SENTENCE,
        FrameNetControl.SUGGEST_WORDS,
        FrameNetControl.SUGGEST_FTS_WORDS
    )
    private val uriLast = "LAST"
    private val projection = arrayOf("PROJ1", "PROJ2", "PROJ3")
    private val selection = "SEL"
    private val selectionArgs = arrayOf("ARG1", "ARG2", "ARG3")
    private val sortOrder = "SORT"

    @Test
    fun queriesLegacyAgainstProvider() {
        for (code in codes) {
            queriesLegacyAgainstProvider(code, uriLast, projection, selection, selectionArgs, sortOrder)
        }
    }

    private fun queriesLegacyAgainstProvider(code: Int, uriLast: String, projection: Array<String>, selection: String, selectionArgs: Array<String>, @Suppress("unused") sortOrder: String) {
        val r1 = QueriesLegacy.queryLegacy(code, uriLast, projection, selection, selectionArgs)
        val r2 = queryProvider(code, uriLast, projection, selection, selectionArgs)
        check(code, r1, r2)
    }

    private fun check(code: Int, r1: FrameNetControl.Result?, r2: FrameNetControl.Result?) {
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
            !=
            ${r2.selection}
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

        fun queryProvider(code: Int, uriLast: String?, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): FrameNetControl.Result? {
            var r = queryProviderMain(code, uriLast, projection0, selection0, selectionArgs0)
            if (r == null) {
                r = queryProviderSearch(code, projection0, selection0, selectionArgs0)
                if (r == null) {
                    r = queryProviderSuggest(code, uriLast)
                }
            }
            return r
        }

        private fun queryProviderMain(code: Int, uriLast: String?, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): FrameNetControl.Result? {
            return queryMain(code, uriLast!!, projection0, selection0, selectionArgs0)
        }

        private fun queryProviderSearch(code: Int, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): FrameNetControl.Result? {
            return querySearch(code, projection0, selection0, selectionArgs0)
        }

        private fun queryProviderSuggest(code: Int, uriLast: String?): FrameNetControl.Result? {
            return querySuggest(code, uriLast!!)
        }

        private fun equals(a: Any?, b: Any?): Boolean {
            return a == b
        }
    }
}
