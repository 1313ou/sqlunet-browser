/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.provider

import org.junit.Test
import org.sqlunet.browser.Module.ContentProviderSql
import org.sqlunet.provider.SQLiteQueryBuilder.Companion.buildQueryString
import org.sqlunet.test.SqlProcessor
import org.sqlunet.wordnet.loaders.Queries.prepareAdjPosition
import org.sqlunet.wordnet.loaders.Queries.prepareLexRelations
import org.sqlunet.wordnet.loaders.Queries.prepareMembers
import org.sqlunet.wordnet.loaders.Queries.prepareMembers2
import org.sqlunet.wordnet.loaders.Queries.prepareMorphs
import org.sqlunet.wordnet.loaders.Queries.prepareRelations
import org.sqlunet.wordnet.loaders.Queries.prepareSamples
import org.sqlunet.wordnet.loaders.Queries.prepareSelectSn
import org.sqlunet.wordnet.loaders.Queries.prepareSemRelations
import org.sqlunet.wordnet.loaders.Queries.prepareSense
import org.sqlunet.wordnet.loaders.Queries.prepareSenses
import org.sqlunet.wordnet.loaders.Queries.prepareSynset
import org.sqlunet.wordnet.loaders.Queries.prepareVFrames
import org.sqlunet.wordnet.loaders.Queries.prepareVTemplates
import org.sqlunet.wordnet.loaders.Queries.prepareWnXSelect
import org.sqlunet.wordnet.loaders.Queries.prepareWord
import org.sqlunet.wordnet.loaders.Queries.prepareWordSelect
import org.sqlunet.wordnet.provider.WordNetControl.queryAnyRelations
import org.sqlunet.wordnet.provider.WordNetControl.queryMain
import org.sqlunet.wordnet.provider.WordNetControl.querySearch
import java.sql.SQLException

class RunQueriesTest {

    @Test
    @Throws(SQLException::class)
    fun runQueries() {
        val db = System.getProperty("db")
        println(db)
        val processor = SqlProcessor(db)
        process(processor, prepareWord(0))
        process(processor, prepareSenses("w"))
        process(processor, prepareSenses(0))
        process(processor, prepareSense(0))
        process(processor, prepareSense("sk"))
        process(processor, prepareSense(0, 0))
        process(processor, prepareSynset(0))
        process(processor, prepareMembers(0))
        process(processor, prepareMembers2(0, true))
        process(processor, prepareMembers2(0, false))
        process(processor, prepareSamples(0))
        process(processor, prepareRelations(0, 0))
        process(processor, prepareSemRelations(0))
        process(processor, prepareSemRelations(0, 0))
        process(processor, prepareLexRelations(0, 0))
        process(processor, prepareLexRelations(0))
        process(processor, prepareVFrames(0))
        process(processor, prepareVFrames(0, 0))
        process(processor, prepareVTemplates(0))
        process(processor, prepareVTemplates(0, 0))
        process(processor, prepareAdjPosition(0))
        process(processor, prepareAdjPosition(0, 0))
        process(processor, prepareMorphs(0))
        process(processor, prepareWnXSelect(0))
        process(processor, prepareWordSelect("w"))
        process(processor, prepareSelectSn("w"))
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

    companion object {

        private fun toSql(code: Int, providerSql: ContentProviderSql): String {
            var r = queryMain(code, "dummy", providerSql.projection, providerSql.selection, providerSql.selectionArgs)
            if (r == null) {
                // RELATIONS
                r = queryAnyRelations(code, providerSql.projection, providerSql.selection, providerSql.selectionArgs)
                if (r == null) {
                    // TEXTSEARCH
                    r = querySearch(code, providerSql.projection, providerSql.selection, providerSql.selectionArgs)
                }
            }
            requireNotNull(r) { "Illegal query code: $code" }
            return buildQueryString(false, r.table, r.projection, r.selection, r.groupBy, null, null, null)
        }

        private fun uriToCode(providerUri: String): Int {
            return when (providerUri) {
                WordNetContract.Words.URI -> WordNetControl.WORDS
                WordNetContract.Words.URI1 -> WordNetControl.WORD
                WordNetContract.Senses.URI -> WordNetControl.SENSES
                WordNetContract.Senses.URI1 -> WordNetControl.SENSE
                WordNetContract.Synsets.URI -> WordNetControl.SYNSETS
                WordNetContract.Synsets.URI1 -> WordNetControl.SYNSET
                WordNetContract.SemRelations.URI -> WordNetControl.SEMRELATIONS
                WordNetContract.LexRelations.URI -> WordNetControl.LEXRELATIONS
                WordNetContract.Relations.URI -> WordNetControl.RELATIONS
                WordNetContract.Poses.URI -> WordNetControl.POSES
                WordNetContract.Domains.URI -> WordNetControl.DOMAINS
                WordNetContract.AdjPositions.URI -> WordNetControl.ADJPOSITIONS
                WordNetContract.Samples.URI -> WordNetControl.SAMPLES
                WordNetContract.Dict.URI -> WordNetControl.DICT
                WordNetContract.Words_Senses_Synsets.URI -> WordNetControl.WORDS_SENSES_SYNSETS
                WordNetContract.Words_Senses_CasedWords_Synsets.URI -> WordNetControl.WORDS_SENSES_CASEDWORDS_SYNSETS
                WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.URI -> WordNetControl.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS
                WordNetContract.Senses_Words.URI -> WordNetControl.SENSES_WORDS
                WordNetContract.Senses_Words.URI_BY_SYNSET -> WordNetControl.SENSES_WORDS_BY_SYNSET
                WordNetContract.Senses_Synsets_Poses_Domains.URI -> WordNetControl.SENSES_SYNSETS_POSES_DOMAINS
                WordNetContract.Synsets_Poses_Domains.URI -> WordNetControl.SYNSETS_POSES_DOMAINS
                WordNetContract.AnyRelations_Senses_Words_X.URI_BY_SYNSET -> WordNetControl.ANYRELATIONS_SENSES_WORDS_X_BY_SYNSET
                WordNetContract.SemRelations_Synsets.URI -> WordNetControl.SEMRELATIONS_SYNSETS
                WordNetContract.SemRelations_Synsets_X.URI -> WordNetControl.SEMRELATIONS_SYNSETS_X
                WordNetContract.SemRelations_Synsets_Words_X.URI_BY_SYNSET -> WordNetControl.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET
                WordNetContract.LexRelations_Senses.URI -> WordNetControl.LEXRELATIONS_SENSES
                WordNetContract.LexRelations_Senses_X.URI -> WordNetControl.LEXRELATIONS_SENSES_X
                WordNetContract.LexRelations_Senses_Words_X.URI_BY_SYNSET -> WordNetControl.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET
                WordNetContract.Senses_VerbFrames.URI -> WordNetControl.SENSES_VFRAMES
                WordNetContract.Senses_VerbTemplates.URI -> WordNetControl.SENSES_VTEMPLATES
                WordNetContract.Senses_AdjPositions.URI -> WordNetControl.SENSES_ADJPOSITIONS
                WordNetContract.Lexes_Morphs.URI -> WordNetControl.LEXES_MORPHS
                WordNetContract.Words_Lexes_Morphs.URI -> WordNetControl.WORDS_LEXES_MORPHS
                WordNetContract.Words_Lexes_Morphs.URI_BY_WORD -> WordNetControl.WORDS_LEXES_MORPHS_BY_WORD
                WordNetContract.Lookup_Words.URI -> WordNetControl.LOOKUP_FTS_WORDS
                WordNetContract.Lookup_Definitions.URI -> WordNetControl.LOOKUP_FTS_DEFINITIONS
                WordNetContract.Lookup_Samples.URI -> WordNetControl.LOOKUP_FTS_SAMPLES
                WordNetContract.Suggest_Words.SEARCH_WORD_PATH -> WordNetControl.SUGGEST_WORDS
                WordNetContract.Suggest_FTS_Words.SEARCH_WORD_PATH -> WordNetControl.SUGGEST_FTS_WORDS
                WordNetContract.Suggest_FTS_Definitions.SEARCH_DEFINITION_PATH -> WordNetControl.SUGGEST_FTS_DEFINITIONS
                WordNetContract.Suggest_FTS_Samples.SEARCH_SAMPLE_PATH -> WordNetControl.SUGGEST_FTS_SAMPLES
                else -> throw IllegalArgumentException("Illegal uri: $providerUri")
            }
        }
    }
}
