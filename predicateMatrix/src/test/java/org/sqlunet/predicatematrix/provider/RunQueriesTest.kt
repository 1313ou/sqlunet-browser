/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.predicatematrix.provider

import org.junit.Test
import org.sqlunet.browser.Module.ContentProviderSql
import org.sqlunet.predicatematrix.loaders.Queries.preparePmFromRoleId
import org.sqlunet.predicatematrix.loaders.Queries.preparePmFromWord
import org.sqlunet.predicatematrix.loaders.Queries.preparePmFromWordGrouped
import org.sqlunet.predicatematrix.provider.PredicateMatrixControl.queryMain
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
        process(processor, preparePmFromRoleId(0, "s"))
        process(processor, preparePmFromWord("w", "s"))
        process(processor, preparePmFromWordGrouped("w", "s"))
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
            PredicateMatrixContract.Pm.URI -> PredicateMatrixControl.PM
            PredicateMatrixContract.Pm_X.URI -> PredicateMatrixControl.PM_X
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
