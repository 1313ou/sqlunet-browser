/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.provider

import org.junit.Test
import org.sqlunet.browser.Module.ContentProviderSql
import org.sqlunet.propbank.loaders.Queries.prepareExamples
import org.sqlunet.propbank.loaders.Queries.prepareRoleSet
import org.sqlunet.propbank.loaders.Queries.prepareRoleSets
import org.sqlunet.propbank.loaders.Queries.prepareRoles
import org.sqlunet.propbank.provider.PropBankControl.queryMain
import org.sqlunet.provider.SQLiteQueryBuilder.Companion.buildQueryString
import org.sqlunet.test.SqlProcessor
import java.sql.SQLException

class RunQueriesTest {

    @Test
    @Throws(SQLException::class)
    fun runQueries() {
        val db = System.getProperty("db")
        println(db)
        val processor = SqlProcessor(db)
        process(processor, prepareRoleSet(0))
        process(processor, prepareRoleSets(0))
        process(processor, prepareRoles(0))
        process(processor, prepareExamples(0))
    }

    @Throws(SQLException::class)
    private fun process(processor: SqlProcessor, providerSql: ContentProviderSql) {
        println("URI: " + providerSql.providerUri)
        val code = uriToCode(providerSql.providerUri)
        val sql = toSql(code, providerSql)
        try {
            processor.process(sql)
        }
        catch (e: Exception) {
            System.err.println(providerSql)
            throw e
        }
    }

    private fun uriToCode(providerUri: String): Int {
        return when (providerUri) {
            PropBankContract.PbRoleSets.URI1 -> PropBankControl.PBROLESET
            PropBankContract.PbRoleSets.URI -> PropBankControl.PBROLESETS
            PropBankContract.PbRoleSets_X.URI -> PropBankControl.PBROLESETS_X
            PropBankContract.PbRoleSets_X.URI_BY_ROLESET -> PropBankControl.PBROLESETS_X_BY_ROLESET
            PropBankContract.Words_PbRoleSets.URI -> PropBankControl.WORDS_PBROLESETS
            PropBankContract.PbRoleSets_PbRoles.URI -> PropBankControl.PBROLESETS_PBROLES
            PropBankContract.PbRoleSets_PbExamples.URI -> PropBankControl.PBROLESETS_PBEXAMPLES
            PropBankContract.PbRoleSets_PbExamples.URI_BY_EXAMPLE -> PropBankControl.PBROLESETS_PBEXAMPLES_BY_EXAMPLE
            PropBankContract.Lookup_PbExamples.URI -> PropBankControl.LOOKUP_FTS_EXAMPLES
            PropBankContract.Lookup_PbExamples_X.URI -> PropBankControl.LOOKUP_FTS_EXAMPLES_X
            PropBankContract.Lookup_PbExamples_X.URI_BY_EXAMPLE -> PropBankControl.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE
            PropBankContract.Suggest_PbWords.SEARCH_WORD_PATH -> PropBankControl.SUGGEST_WORDS
            PropBankContract.Suggest_FTS_PbWords.SEARCH_WORD_PATH -> PropBankControl.SUGGEST_FTS_WORDS
            else -> throw IllegalArgumentException("Illegal uri: $providerUri")
        }
    }

    companion object {

        private fun toSql(code: Int, providerSql: ContentProviderSql): String {
            val r = queryMain(code, providerSql.projection, providerSql.selection, providerSql.selectionArgs) ?: throw IllegalArgumentException("Illegal query code: $code")
            return buildQueryString(false, r.table, r.projection, r.selection, r.groupBy, null, null, null)
        }
    }
}
