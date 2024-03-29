/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.provider

import org.junit.Test
import org.sqlunet.provider.XNetControl.queryMain

class QueriesUnitTest {

    private val codes = intArrayOf(
        XNetControl.PREDICATEMATRIX,
        XNetControl.PREDICATEMATRIX_VERBNET,
        XNetControl.PREDICATEMATRIX_PROPBANK,
        XNetControl.PREDICATEMATRIX_FRAMENET,
        XNetControl.WORDS_FNWORDS_PBWORDS_VNWORDS,
        XNetControl.WORDS_PBWORDS_VNWORDS,
        XNetControl.WORDS_VNWORDS_VNCLASSES,
        XNetControl.WORDS_VNWORDS_VNCLASSES_U,
        XNetControl.WORDS_PBWORDS_PBROLESETS,
        XNetControl.WORDS_PBWORDS_PBROLESETS_U,
        XNetControl.WORDS_FNWORDS_FNFRAMES_U,
        XNetControl.SOURCES,
        XNetControl.WORDS_VNWORDS_VNCLASSES_1,
        XNetControl.WORDS_VNWORDS_VNCLASSES_2,
        XNetControl.WORDS_VNWORDS_VNCLASSES_1U2,
        XNetControl.WORDS_VNWORDS_VNCLASSES_U,
        XNetControl.WORDS_PBWORDS_PBROLESETS_1,
        XNetControl.WORDS_PBWORDS_PBROLESETS_2,
        XNetControl.WORDS_PBWORDS_PBROLESETS_1U2,
        XNetControl.WORDS_PBWORDS_PBROLESETS_U,
        XNetControl.WORDS_FNWORDS_FNFRAMES_1,
        XNetControl.WORDS_FNWORDS_FNFRAMES_2,
        XNetControl.WORDS_FNWORDS_FNFRAMES_1U2,
        XNetControl.WORDS_FNWORDS_FNFRAMES_U
    )
    private val uriLast = "LAST"
    private val projection = arrayOf("PROJ1", "PROJ2", "PROJ3")
    private val selection = "SEL"
    private val selectionArgs = arrayOf("ARG1", "ARG2", "ARG3")
    private val sortOrder = "SORT"

    @Test
    fun queriesUnion() {
        for (code in intArrayOf(XNetControl.WORDS_VNWORDS_VNCLASSES_U, XNetControl.WORDS_PBWORDS_PBROLESETS_U, XNetControl.WORDS_FNWORDS_FNFRAMES_U)) {
            println("CODE: $code")
            val r = QueriesLegacy.queryLegacy(code, uriLast, projection, selection, selectionArgs)!!
            println("table :" + r.table)
            println("projection :" + r.projection.contentToString())
            println("selection :" + r.selection)
            println("args :" + r.selectionArgs.contentToString())
            println("groupby :" + r.groupBy)
            println()
        }
    }

    @Test
    fun queriesUnionLegacyAgainstProvider() {
        for (code in intArrayOf(XNetControl.WORDS_VNWORDS_VNCLASSES_U, XNetControl.WORDS_PBWORDS_PBROLESETS_U, XNetControl.WORDS_FNWORDS_FNFRAMES_U)) {
            queriesLegacyAgainstProvider(code, uriLast, projection, selection, selectionArgs, sortOrder)
        }
    }

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

    private fun check(code: Int, r1: XNetControl.Result?, r2: XNetControl.Result?) {
        assert(r1 != null)
        assert(r2 != null)
        assert(equals(r1!!.table, r2!!.table)) {
            """
            Code=$code table 
            ${r1.table}
            !=
            ${r2.table}
            """
        }
        assert(r1.projection.contentEquals(r2.projection)) {
            """
            Code=$code projection 
            ${r1.projection.contentToString()}
            !=
            ${r2.projection.contentToString()}
            """
        }
        assert(equals(r1.selection, r2.selection)) {
            """
            Code=$code selection 
            ${r1.selection}
            !=
            ${r2.selection}
            """
        }
        assert(r1.selectionArgs.contentEquals(r2.selectionArgs)) {
            """
            Code=$code args 
            ${r1.selectionArgs.contentToString()}
            !=
            ${r2.selectionArgs.contentToString()}
            """
        }
        assert(equals(r1.groupBy, r2.groupBy)) {
            """
            Code=$code group by 
            ${r1.groupBy}
            !=
            ${r2.groupBy}
            """
        }
    }

    companion object {

        fun queryProvider(code: Int, uriLast: String?, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): XNetControl.Result? {
            return queryProviderMain(code, uriLast, projection0, selection0, selectionArgs0)
        }

        private fun queryProviderMain(code: Int, uriLast: String?, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): XNetControl.Result? {
            return queryMain(code, projection0, selection0, selectionArgs0)
        }

        private fun equals(a: Any?, b: Any?): Boolean {
            return a == b
        }
    }
}
