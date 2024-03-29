/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.provider

import org.junit.Test
import org.sqlunet.browser.Module.ContentProviderSql
import org.sqlunet.provider.SQLiteQueryBuilder.Companion.buildQueryString
import org.sqlunet.syntagnet.loaders.Queries.prepareCollocation
import org.sqlunet.syntagnet.loaders.Queries.prepareCollocations
import org.sqlunet.syntagnet.loaders.Queries.prepareSnSelect
import org.sqlunet.syntagnet.provider.SyntagNetControl.queryMain
import org.sqlunet.test.SqlProcessor
import java.sql.SQLException

class RunQueriesTest {

    @Test
    @Throws(SQLException::class)
    fun runQueries() {
        val db = System.getProperty("db")
        println(db)
        val processor = SqlProcessor(db)
        process(processor, prepareCollocation(0))
        process(processor, prepareCollocations(0L, 0L, 0L, 0L))
        process(processor, prepareCollocations(0))
        process(processor, prepareCollocations("w"))
        process(processor, prepareSnSelect(0))
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
            SyntagNetContract.SnCollocations.URI -> SyntagNetControl.COLLOCATIONS
            SyntagNetContract.SnCollocations_X.URI -> SyntagNetControl.COLLOCATIONS_X
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
