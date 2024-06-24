/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.predicatematrix.provider

import org.junit.Test
import org.sqlunet.predicatematrix.provider.Expected.expected
import org.sqlunet.predicatematrix.provider.PredicateMatrixControl.queryMain

class RunQueriesFactoryTest {

    private val codes = intArrayOf(PredicateMatrixControl.PM, PredicateMatrixControl.PM_X)
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

    private fun check(code: Int, r1: PredicateMatrixControl.Result?, r2: PredicateMatrixControl.Result?) {
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

        fun queryProvider(code: Int, uriLast: String?, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): PredicateMatrixControl.Result? {
            return queryProviderMain(code, uriLast, projection0, selection0, selectionArgs0)
        }

        private fun queryProviderMain(code: Int, @Suppress("UNUSED_PARAMETER") uriLast: String?, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): PredicateMatrixControl.Result? {
            return queryMain(code, projection0, selection0, selectionArgs0)
        }

        private fun equals(a: Any?, b: Any?): Boolean {
            return a == b
        }
    }
}
