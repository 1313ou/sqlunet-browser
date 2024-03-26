/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.provider

import org.junit.Test
import org.sqlunet.browser.Module.ContentProviderSql
import org.sqlunet.framenet.loaders.Queries.prepareAnnoSet
import org.sqlunet.framenet.loaders.Queries.prepareAnnoSetsForGovernor
import org.sqlunet.framenet.loaders.Queries.prepareAnnoSetsForPattern
import org.sqlunet.framenet.loaders.Queries.prepareAnnoSetsForValenceUnit
import org.sqlunet.framenet.loaders.Queries.prepareFesForFrame
import org.sqlunet.framenet.loaders.Queries.prepareFrame
import org.sqlunet.framenet.loaders.Queries.prepareGovernorsForLexUnit
import org.sqlunet.framenet.loaders.Queries.prepareGroupRealizationsForLexUnit
import org.sqlunet.framenet.loaders.Queries.prepareLayersForSentence
import org.sqlunet.framenet.loaders.Queries.prepareLexUnit
import org.sqlunet.framenet.loaders.Queries.prepareLexUnitsForFrame
import org.sqlunet.framenet.loaders.Queries.prepareLexUnitsForWordAndPos
import org.sqlunet.framenet.loaders.Queries.prepareRealizationsForLexicalUnit
import org.sqlunet.framenet.loaders.Queries.prepareRelatedFrames
import org.sqlunet.framenet.loaders.Queries.prepareSentence
import org.sqlunet.framenet.loaders.Queries.prepareSentencesForLexUnit
import org.sqlunet.framenet.loaders.Queries.prepareSentencesForPattern
import org.sqlunet.framenet.loaders.Queries.prepareSentencesForValenceUnit
import org.sqlunet.framenet.provider.FrameNetControl.queryMain
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
        process(processor, prepareFrame(0))
        process(processor, prepareRelatedFrames(0))
        process(processor, prepareFesForFrame(0))
        process(processor, prepareLexUnit(0))
        process(processor, prepareLexUnitsForFrame(0))
        process(processor, prepareLexUnitsForWordAndPos(0, 'n'))
        process(processor, prepareGovernorsForLexUnit(0))
        process(processor, prepareRealizationsForLexicalUnit(0))
        process(processor, prepareGroupRealizationsForLexUnit(0))
        process(processor, prepareSentence(0))
        process(processor, prepareSentencesForLexUnit(0))
        process(processor, prepareSentencesForPattern(0))
        process(processor, prepareSentencesForValenceUnit(0))
        process(processor, prepareAnnoSet(0))
        process(processor, prepareAnnoSetsForGovernor(0))
        process(processor, prepareAnnoSetsForPattern(0))
        process(processor, prepareAnnoSetsForValenceUnit(0))
        process(processor, prepareLayersForSentence(0))
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
            FrameNetContract.LexUnits.URI1 -> FrameNetControl.LEXUNIT
            FrameNetContract.LexUnits.URI -> FrameNetControl.LEXUNITS
            FrameNetContract.LexUnits_X.URI_BY_LEXUNIT -> FrameNetControl.LEXUNITS_X_BY_LEXUNIT
            FrameNetContract.Frames.URI1 -> FrameNetControl.FRAME
            FrameNetContract.Frames.URI -> FrameNetControl.FRAMES
            FrameNetContract.Frames_X.URI_BY_FRAME -> FrameNetControl.FRAMES_X_BY_FRAME
            FrameNetContract.Frames_Related.URI -> FrameNetControl.FRAMES_RELATED
            FrameNetContract.Sentences.URI1 -> FrameNetControl.SENTENCE
            FrameNetContract.Sentences.URI -> FrameNetControl.SENTENCES
            FrameNetContract.AnnoSets.URI1 -> FrameNetControl.ANNOSET
            FrameNetContract.AnnoSets.URI -> FrameNetControl.ANNOSETS
            FrameNetContract.Sentences_Layers_X.URI -> FrameNetControl.SENTENCES_LAYERS_X
            FrameNetContract.AnnoSets_Layers_X.URI -> FrameNetControl.ANNOSETS_LAYERS_X
            FrameNetContract.Patterns_Layers_X.URI -> FrameNetControl.PATTERNS_LAYERS_X
            FrameNetContract.ValenceUnits_Layers_X.URI -> FrameNetControl.VALENCEUNITS_LAYERS_X
            FrameNetContract.Patterns_Sentences.URI -> FrameNetControl.PATTERNS_SENTENCES
            FrameNetContract.ValenceUnits_Sentences.URI -> FrameNetControl.VALENCEUNITS_SENTENCES
            FrameNetContract.Governors_AnnoSets_Sentences.URI -> FrameNetControl.GOVERNORS_ANNOSETS
            FrameNetContract.Words_LexUnits_Frames.URI -> FrameNetControl.WORDS_LEXUNITS_FRAMES
            FrameNetContract.Words_LexUnits_Frames.URI_FN -> FrameNetControl.WORDS_LEXUNITS_FRAMES_FN
            FrameNetContract.LexUnits_or_Frames.URI -> FrameNetControl.LEXUNITS_OR_FRAMES
            FrameNetContract.LexUnits_or_Frames.URI_FN -> FrameNetControl.LEXUNITS_OR_FRAMES_FN
            FrameNetContract.Frames_FEs.URI -> FrameNetControl.FRAMES_FES
            FrameNetContract.Frames_FEs.URI_BY_FE -> FrameNetControl.FRAMES_FES_BY_FE
            FrameNetContract.LexUnits_Sentences.URI -> FrameNetControl.LEXUNITS_SENTENCES
            FrameNetContract.LexUnits_Sentences.URI_BY_SENTENCE -> FrameNetControl.LEXUNITS_SENTENCES_BY_SENTENCE
            FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.URI -> FrameNetControl.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS
            FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.URI_BY_SENTENCE -> FrameNetControl.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE
            FrameNetContract.LexUnits_Governors.URI -> FrameNetControl.LEXUNITS_GOVERNORS
            FrameNetContract.LexUnits_Governors.URI_FN -> FrameNetControl.LEXUNITS_GOVERNORS_FN
            FrameNetContract.LexUnits_FERealizations_ValenceUnits.URI -> FrameNetControl.LEXUNITS_REALIZATIONS
            FrameNetContract.LexUnits_FERealizations_ValenceUnits.URI_BY_REALIZATION -> FrameNetControl.LEXUNITS_REALIZATIONS_BY_REALIZATION
            FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.URI -> FrameNetControl.LEXUNITS_GROUPREALIZATIONS
            FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.URI_BY_PATTERN -> FrameNetControl.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN
            FrameNetContract.Lookup_FTS_FnWords.URI -> FrameNetControl.LOOKUP_FTS_WORDS
            FrameNetContract.Lookup_FTS_FnSentences.URI -> FrameNetControl.LOOKUP_FTS_SENTENCES
            FrameNetContract.Lookup_FTS_FnSentences_X.URI -> FrameNetControl.LOOKUP_FTS_SENTENCES_X
            FrameNetContract.Lookup_FTS_FnSentences_X.URI_BY_SENTENCE -> FrameNetControl.LOOKUP_FTS_SENTENCES_X_BY_SENTENCE
            FrameNetContract.Suggest_FnWords.SEARCH_WORD_PATH -> FrameNetControl.SUGGEST_WORDS
            FrameNetContract.Suggest_FTS_FnWords.SEARCH_WORD_PATH -> FrameNetControl.SUGGEST_FTS_WORDS
            else -> throw IllegalArgumentException("Illegal uri: $providerUri")
        }
    }

    companion object {

        private fun toSql(code: Int, providerSql: ContentProviderSql): String {
            val r = queryMain(code, null, providerSql.projection, providerSql.selection, providerSql.selectionArgs) ?: throw IllegalArgumentException("Illegal query code: $code")
            return buildQueryString(false, r.table, r.projection, r.selection, r.groupBy, null, null, null)
        }
    }
}
