/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.provider

import org.junit.Test
import org.sqlunet.browser.Module.ContentProviderSql
import org.sqlunet.provider.SQLiteQueryBuilder.Companion.buildQueryString
import org.sqlunet.test.SqlProcessor
import org.sqlunet.verbnet.loaders.Queries.prepareVnClass
import org.sqlunet.verbnet.loaders.Queries.prepareVnClasses
import org.sqlunet.verbnet.loaders.Queries.prepareVnFrames
import org.sqlunet.verbnet.loaders.Queries.prepareVnMembers
import org.sqlunet.verbnet.loaders.Queries.prepareVnRoles
import org.sqlunet.verbnet.provider.VerbNetControl.queryMain
import org.sqlunet.verbnet.provider.VerbNetControl.querySearch
import java.sql.SQLException

class RunQueriesTest {

    @Test
    @Throws(SQLException::class)
    fun runQueries() {
        val db = System.getProperty("db")
        println(db)
        val processor = SqlProcessor(db)
        process(processor, prepareVnClass(0))
        process(processor, prepareVnClasses(0, null))
        process(processor, prepareVnClasses(0, 0L))
        process(processor, prepareVnRoles(0))
        process(processor, prepareVnMembers(0))
        process(processor, prepareVnFrames(0))
    }

    @Throws(SQLException::class)
    private fun process(processor: SqlProcessor, providerSql: ContentProviderSql) {
        println("URI: " + providerSql.providerUri)
        val code = uriToCode(providerSql.providerUri)
        val sql = toSql(code, providerSql)
        try {
            processor.process(sql)
        } catch (e: Exception) {
            System.err.println(providerSql)
            throw e
        }
    }

    private fun uriToCode(providerUri: String): Int {
        return when (providerUri) {
            VerbNetContract.VnClasses.URI1 -> VerbNetControl.VNCLASS1
            VerbNetContract.VnClasses.URI -> VerbNetControl.VNCLASSES
            VerbNetContract.VnClasses_X.URI_BY_VNCLASS -> VerbNetControl.VNCLASSES_X_BY_VNCLASS
            VerbNetContract.Words_VnClasses.URI -> VerbNetControl.WORDS_VNCLASSES
            VerbNetContract.VnClasses_VnMembers_X.URI_BY_WORD -> VerbNetControl.VNCLASSES_VNMEMBERS_X_BY_WORD
            VerbNetContract.VnClasses_VnRoles_X.URI_BY_ROLE -> VerbNetControl.VNCLASSES_VNROLES_X_BY_VNROLE
            VerbNetContract.VnClasses_VnFrames_X.URI_BY_FRAME -> VerbNetControl.VNCLASSES_VNFRAMES_X_BY_VNFRAME
            VerbNetContract.Lookup_VnExamples.URI -> VerbNetControl.LOOKUP_FTS_EXAMPLES
            VerbNetContract.Lookup_VnExamples_X.URI -> VerbNetControl.LOOKUP_FTS_EXAMPLES_X
            VerbNetContract.Lookup_VnExamples_X.URI_BY_EXAMPLE -> VerbNetControl.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE
            VerbNetContract.Suggest_VnWords.SEARCH_WORD_PATH -> VerbNetControl.SUGGEST_WORDS
            VerbNetContract.Suggest_FTS_VnWords.SEARCH_WORD_PATH -> VerbNetControl.SUGGEST_FTS_WORDS
            else -> throw IllegalArgumentException("Illegal uri: $providerUri")
        }
    }

    companion object {

        private fun toSql(code: Int, providerSql: ContentProviderSql): String {
            var r = queryMain(code, "", providerSql.projection, providerSql.selection, providerSql.selectionArgs)
            if (r == null) {
                // TEXTSEARCH
                r = querySearch(code, providerSql.projection, providerSql.selection, providerSql.selectionArgs)
            }
            requireNotNull(r) { "Illegal query code: $code" }
            return buildQueryString(false, r.table, r.projection, r.selection, r.groupBy, null, null, null)
        }
    }
}
