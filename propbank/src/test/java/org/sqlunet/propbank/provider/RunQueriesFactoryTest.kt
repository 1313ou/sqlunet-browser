/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.provider

import org.junit.Test
import org.sqlunet.propbank.provider.Expected.expected
import org.sqlunet.propbank.provider.PropBankControl.queryMain
import org.sqlunet.propbank.provider.PropBankControl.querySearch
import org.sqlunet.propbank.provider.PropBankControl.querySuggest

class RunQueriesFactoryTest {

    private val codes = intArrayOf(
        PropBankControl.PBROLESET1,
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

    @Test
    fun queries() {
        for (code in codes) {
            query(code, uriLast, projection, selection, selectionArgs)
        }
    }

    @Test
    fun queriesCompare() {
        for (code in codes) {
            queryCompare(code, uriLast, projection, selection, selectionArgs)
        }
    }

    private fun queryCompare(code: Int, uriLast: String, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?) {
        val expected = expected(code, uriLast, projection!!, selection, selectionArgs)
        val r = queryProvider(code, uriLast, projection, selection, selectionArgs)
        check(code, expected, r)
    }

    private fun query(code: Int, uriLast: String, projection: Array<String>, selection: String, selectionArgs: Array<String>) {
        val r = queryProvider(code, uriLast, projection, selection, selectionArgs)
        println("\n$code\n$r")
    }

    private fun check(code: Int, r1: PropBankControl.Result?, r2: PropBankControl.Result?) {
        assert(r1 != null) { println(code) }
        assert(r2 != null) { println(code) }
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
            var r = queryProviderMain(code, uriLast, projection0, selection0, selectionArgs0)
            if (r == null) {
                r = queryProviderSearch(code, projection0, selection0, selectionArgs0)
                if (r == null) {
                    r = queryProviderSuggest(code, uriLast)
                }
            }
            return r
        }

        private fun queryProviderMain(code: Int, uriLast: String, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): PropBankControl.Result? {
            return queryMain(code, uriLast, projection0, selection0, selectionArgs0)
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
