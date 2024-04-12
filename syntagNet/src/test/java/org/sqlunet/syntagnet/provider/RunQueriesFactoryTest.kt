/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.provider

import org.junit.Test
import org.sqlunet.syntagnet.provider.Expected.expected
import org.sqlunet.syntagnet.provider.SyntagNetControl.queryMain

class RunQueriesFactoryTest {

    private val codes = intArrayOf(SyntagNetControl.COLLOCATIONS, SyntagNetControl.COLLOCATION1, SyntagNetControl.COLLOCATIONS_X)
    private val uriLast = "LAST"
    private val projection = arrayOf("PROJ1", "PROJ2", "PROJ3")
    private val selection = "SEL"
    private val selectionArgs = arrayOf("ARG1", "ARG2", "ARG3")
    private val sortOrder = "SORT"

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

    private fun check(code: Int, r1: SyntagNetControl.Result?, r2: SyntagNetControl.Result?) {
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

        fun queryProvider(code: Int, uriLast: String, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): SyntagNetControl.Result? {
            return queryProviderMain(code, uriLast, projection0, selection0, selectionArgs0)
        }

        private fun queryProviderMain(code: Int, uriLast: String, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): SyntagNetControl.Result? {
            return queryMain(code, uriLast, projection0, selection0, selectionArgs0)
        }

        private fun equals(a: Any?, b: Any?): Boolean {
            return a == b
        }
    }
}
