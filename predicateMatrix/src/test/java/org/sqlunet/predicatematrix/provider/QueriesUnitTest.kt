/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.predicatematrix.provider

import org.junit.Test
import org.sqlunet.predicatematrix.provider.PredicateMatrixControl.queryMain

class QueriesUnitTest {

    private val codes = intArrayOf(PredicateMatrixControl.PM, PredicateMatrixControl.PM_X)
    private val uriLast = "LAST"
    private val projection = arrayOf("PROJ1", "PROJ2", "PROJ3")
    private val selection = "SEL"
    private val selectionArgs = arrayOf("ARG1", "ARG2", "ARG3")

    @Test
    fun queriesLegacyAgainstProvider() {
        for (code in codes) {
            queriesLegacyAgainstProvider(code, projection, selection, selectionArgs)
        }
    }

    private fun queriesLegacyAgainstProvider(code: Int, projection: Array<String>, selection: String, selectionArgs: Array<String>) {
        val r1 = QueriesLegacy.queryLegacy(code, projection, selection, selectionArgs)
        val r2 = queryProvider(code, projection, selection, selectionArgs)
        check(code, r1, r2)
    }

    private fun check(code: Int, r1: PredicateMatrixControl.Result?, r2: PredicateMatrixControl.Result?) {
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

        fun queryProvider(code: Int, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): PredicateMatrixControl.Result? {
            return queryProviderMain(code, projection0, selection0, selectionArgs0)
        }

        private fun queryProviderMain(code: Int, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): PredicateMatrixControl.Result? {
            return queryMain(code, projection0, selection0, selectionArgs0)
        }

        private fun equals(a: Any?, b: Any?): Boolean {
            return a == b
        }
    }
}
