/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.provider

import org.junit.Test
import org.sqlunet.verbnet.provider.VerbNetControl.queryMain
import org.sqlunet.verbnet.provider.VerbNetControl.querySearch
import org.sqlunet.verbnet.provider.VerbNetControl.querySuggest

class QueriesUnitTest {

    private val codes = intArrayOf(
            VerbNetControl.VNCLASS1,
            VerbNetControl.VNCLASSES,
            VerbNetControl.VNCLASSES_X_BY_VNCLASS,
            VerbNetControl.WORDS_VNCLASSES,
            VerbNetControl.VNCLASSES_VNMEMBERS_X_BY_WORD,
            VerbNetControl.VNCLASSES_VNROLES_X_BY_VNROLE,
            VerbNetControl.VNCLASSES_VNFRAMES_X_BY_VNFRAME,
            VerbNetControl.LOOKUP_FTS_EXAMPLES,
            VerbNetControl.LOOKUP_FTS_EXAMPLES_X,
            VerbNetControl.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE,
            VerbNetControl.SUGGEST_WORDS,
            VerbNetControl.SUGGEST_FTS_WORDS
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

    private fun check(code: Int, r1: VerbNetControl.Result?, r2: VerbNetControl.Result?) {
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

        fun queryProvider(code: Int, uriLast: String, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): VerbNetControl.Result? {
            var r = queryProviderMain(code, uriLast, projection0, selection0, selectionArgs0)
            if (r == null) {
                r = queryProviderSearch(code, projection0, selection0, selectionArgs0)
                if (r == null) {
                    r = queryProviderSuggest(code, uriLast)
                }
            }
            return r
        }

        private fun queryProviderMain(code: Int, uriLast: String, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): VerbNetControl.Result? {
            return queryMain(code, uriLast, projection0, selection0, selectionArgs0)
        }

        private fun queryProviderSearch(code: Int, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): VerbNetControl.Result? {
            return querySearch(code, projection0, selection0, selectionArgs0)
        }

        private fun queryProviderSuggest(code: Int, uriLast: String): VerbNetControl.Result? {
            return querySuggest(code, uriLast)
        }

        private fun equals(a: Any?, b: Any?): Boolean {
            return a == b
        }
    }
}
