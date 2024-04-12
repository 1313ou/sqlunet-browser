/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.bnc.provider

import junit.framework.TestCase
import org.junit.Test
import org.sqlunet.bnc.loaders.Queries.prepareBnc
import org.sqlunet.bnc.provider.BNCControl.queryMain
import org.sqlunet.browser.Module.ContentProviderSql
import org.sqlunet.provider.SQLiteQueryBuilder.Companion.buildQueryString
import org.sqlunet.test.SqlProcessor
import java.sql.SQLException

class RunQueriesAsExplainTest {

    @Test
    @Throws(SQLException::class)
    fun runQueries() {
        val db = System.getenv()["db"] // System.getProperty("db")
        if (db.isNullOrEmpty()) {
            TestCase.fail("No database")
            return
        }
        println(db)
        val processor = SqlProcessor(db)
        process(processor, prepareBnc(0, 'n'))
        process(processor, prepareBnc(0, null))
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
            BNCContract.BNCs.URI -> BNCControl.BNC
            BNCContract.Words_BNCs.URI -> BNCControl.WORDS_BNC
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
