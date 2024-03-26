/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.provider

import org.junit.Test
import org.sqlunet.browser.Module.ContentProviderSql
import org.sqlunet.loaders.Queries.prepareFnXSelect
import org.sqlunet.loaders.Queries.preparePbSelectVn
import org.sqlunet.loaders.Queries.preparePbXSelect
import org.sqlunet.loaders.Queries.prepareVnXSelect
import org.sqlunet.loaders.Queries.prepareVnXSelectVn
import org.sqlunet.loaders.Queries.prepareWordSelect
import org.sqlunet.loaders.Queries.prepareWordXSelect
import org.sqlunet.loaders.Queries.prepareWordXSelectVn
import org.sqlunet.provider.SQLiteQueryBuilder.Companion.buildQueryString
import org.sqlunet.provider.XNetControl.queryMain
import org.sqlunet.test.SqlProcessor
import java.sql.SQLException

class RunQueriesTest {

    @Test
    @Throws(SQLException::class)
    fun runQueries() {
        val db = System.getProperty("db")
        println(db)
        val processor = SqlProcessor(db)
        process(processor, prepareWordXSelect("w"))
        process(processor, prepareWordSelect("w"))
        process(processor, prepareVnXSelect(0))
        process(processor, preparePbXSelect(0))
        process(processor, prepareFnXSelect(0))
        process(processor, prepareVnXSelectVn(0))
        process(processor, preparePbSelectVn(0))
        process(processor, prepareWordXSelectVn("w"))
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
            XNetContract.PredicateMatrix.URI -> XNetControl.PREDICATEMATRIX
            XNetContract.PredicateMatrix_VerbNet.URI -> XNetControl.PREDICATEMATRIX_VERBNET
            XNetContract.PredicateMatrix_PropBank.URI -> XNetControl.PREDICATEMATRIX_PROPBANK
            XNetContract.PredicateMatrix_FrameNet.URI -> XNetControl.PREDICATEMATRIX_FRAMENET
            XNetContract.Words_FnWords_PbWords_VnWords.URI -> XNetControl.WORDS_FNWORDS_PBWORDS_VNWORDS
            XNetContract.Words_PbWords_VnWords.URI -> XNetControl.WORDS_PBWORDS_VNWORDS
            XNetContract.Words_VnWords_VnClasses.URI -> XNetControl.WORDS_VNWORDS_VNCLASSES
            XNetContract.Words_VnWords_VnClasses_U.URI -> XNetControl.WORDS_VNWORDS_VNCLASSES_U
            XNetContract.Words_VnWords_VnClasses_1.URI -> XNetControl.WORDS_VNWORDS_VNCLASSES_1
            XNetContract.Words_VnWords_VnClasses_2.URI -> XNetControl.WORDS_VNWORDS_VNCLASSES_2
            XNetContract.Words_VnWords_VnClasses_1U2.URI -> XNetControl.WORDS_VNWORDS_VNCLASSES_1U2
            XNetContract.Words_PbWords_PbRoleSets.URI -> XNetControl.WORDS_PBWORDS_PBROLESETS
            XNetContract.Words_PbWords_PbRoleSets_U.URI -> XNetControl.WORDS_PBWORDS_PBROLESETS_U
            XNetContract.Words_PbWords_PbRoleSets_1.URI -> XNetControl.WORDS_PBWORDS_PBROLESETS_1
            XNetContract.Words_PbWords_PbRoleSets_2.URI -> XNetControl.WORDS_PBWORDS_PBROLESETS_2
            XNetContract.Words_PbWords_PbRoleSets_1U2.URI -> XNetControl.WORDS_PBWORDS_PBROLESETS_1U2
            XNetContract.Words_FnWords_FnFrames_U.URI -> XNetControl.WORDS_FNWORDS_FNFRAMES_U
            XNetContract.Words_FnWords_FnFrames_1U2.URI -> XNetControl.WORDS_FNWORDS_FNFRAMES_1U2
            XNetContract.Words_FnWords_FnFrames_1.URI -> XNetControl.WORDS_FNWORDS_FNFRAMES_1
            XNetContract.Words_FnWords_FnFrames_2.URI -> XNetControl.WORDS_FNWORDS_FNFRAMES_2
            XNetContract.Sources.URI -> XNetControl.SOURCES
            else -> throw IllegalArgumentException("Illegal uri: $providerUri")
        }
    }

    companion object {

        private fun toSql(code: Int, providerSql: ContentProviderSql): String {
            val r = queryMain(code, providerSql.projection, providerSql.selection, providerSql.selectionArgs) ?: throw IllegalArgumentException("Illegal query code: $code")
            return buildQueryString(false, r.table, r.projection, r.selection, r.groupBy, null, r.orderBy, null)
        }
    }
}
