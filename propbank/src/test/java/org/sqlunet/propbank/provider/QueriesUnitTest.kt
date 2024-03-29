/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.provider

import org.junit.Test
import org.sqlunet.propbank.provider.PropBankControl.queryMain
import org.sqlunet.propbank.provider.PropBankControl.querySearch
import org.sqlunet.propbank.provider.PropBankControl.querySuggest

class QueriesUnitTest {

    private val codes = intArrayOf(
        PropBankControl.PBROLESET,
        PropBankControl.PBROLESETS,
        PropBankControl.PBROLESETS_X,
        PropBankControl.PBROLESETS_X_BY_ROLESET,
        PropBankControl.WORDS_PBROLESETS,
        PropBankControl.PBROLESETS_PBROLES,
        PropBankControl.PBROLESETS_PBEXAMPLES,
        PropBankControl.PBROLESETS_PBEXAMPLES_BY_EXAMPLE,
        PropBankControl.LOOKUP_FTS_EXAMPLES,
        PropBankControl.LOOKUP_FTS_EXAMPLES_X,
        PropBankControl.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE,
        PropBankControl.SUGGEST_WORDS,
        PropBankControl.SUGGEST_FTS_WORDS
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

    private fun check(code: Int, r1: PropBankControl.Result?, r2: PropBankControl.Result?) {
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

        fun queryProvider(code: Int, uriLast: String, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): PropBankControl.Result? {
            var r = queryProviderMain(code, projection0, selection0, selectionArgs0)
            if (r == null) {
                r = queryProviderSearch(code, projection0, selection0, selectionArgs0)
                if (r == null) {
                    r = queryProviderSuggest(code, uriLast)
                }
            }
            return r
        }

        private fun queryProviderMain(code: Int, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): PropBankControl.Result? {
            return queryMain(code, projection0, selection0, selectionArgs0)
        }

        private fun queryProviderSearch(code: Int, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): PropBankControl.Result? {
            return querySearch(code, projection0, selection0, selectionArgs0)
        }

        private fun queryProviderSuggest(code: Int, uriLast: String): PropBankControl.Result? {
            return querySuggest(code, uriLast)
        }

        private fun equals(a: Any?, b: Any?): Boolean {
            return a == b
        }
    }
}
