/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.framenet.provider;

import org.junit.Test;
import org.sqlunet.browser.Module;
import org.sqlunet.framenet.loaders.Queries;
import org.sqlunet.provider.SQLiteQueryBuilder;
import org.sqlunet.test.SqlProcessor;

import java.sql.SQLException;

public class RunQueriesTest
{
	@Test
	public void runQueries() throws SQLException
	{
		String db = System.getProperty("db");
		System.out.println(db);
		final SqlProcessor processor = new SqlProcessor(db);
		process(processor, Queries.prepareFrame(0));
		process(processor, Queries.prepareRelatedFrames(0));
		process(processor, Queries.prepareFesForFrame(0));
		process(processor, Queries.prepareLexUnit(0));
		process(processor, Queries.prepareLexUnitsForFrame(0));
		process(processor, Queries.prepareLexUnitsForWordAndPos(0, 'n'));
		process(processor, Queries.prepareGovernorsForLexUnit(0));
		process(processor, Queries.prepareRealizationsForLexicalUnit(0));
		process(processor, Queries.prepareGroupRealizationsForLexUnit(0));
		process(processor, Queries.prepareSentence(0));
		process(processor, Queries.prepareSentencesForLexUnit(0));
		process(processor, Queries.prepareSentencesForPattern(0));
		process(processor, Queries.prepareSentencesForValenceUnit(0));
		process(processor, Queries.prepareAnnoSet(0));
		process(processor, Queries.prepareAnnoSetsForGovernor(0));
		process(processor, Queries.prepareAnnoSetsForPattern(0));
		process(processor, Queries.prepareAnnoSetsForValenceUnit(0));
		process(processor, Queries.prepareLayersForSentence(0));
	}

	private void process(final SqlProcessor processor, final Module.ContentProviderSql providerSql) throws SQLException
	{
		System.out.println("URI: " + providerSql.providerUri);
		final int code = uriToCode(providerSql.providerUri);
		final String sql = toSql(code, providerSql);
		try
		{
			processor.process(sql);
		}
		catch (Exception e)
		{
			System.err.println(providerSql);
			throw e;
		}
	}

	private static String toSql(final int code, final Module.ContentProviderSql providerSql)
	{
		FrameNetControl.Result r = FrameNetControl.queryMain(code, null, providerSql.projection, providerSql.selection, providerSql.selectionArgs);
		if (r == null)
		{
			throw new IllegalArgumentException("Illegal query code: " + code);
		}
		return SQLiteQueryBuilder.buildQueryString(false, r.table, r.projection, r.selection, r.groupBy, null, null, null);
	}

	private int uriToCode(final String providerUri)
	{
		switch (providerUri)
		{
			case FrameNetContract.LexUnits.URI1:
				return FrameNetControl.LEXUNIT;
			case FrameNetContract.LexUnits.URI:
				return FrameNetControl.LEXUNITS;
			case FrameNetContract.LexUnits_X.URI_BY_LEXUNIT:
				return FrameNetControl.LEXUNITS_X_BY_LEXUNIT;
			case FrameNetContract.Frames.URI1:
				return FrameNetControl.FRAME;
			case FrameNetContract.Frames.URI:
				return FrameNetControl.FRAMES;
			case FrameNetContract.Frames_X.URI_BY_FRAME:
				return FrameNetControl.FRAMES_X_BY_FRAME;
			case FrameNetContract.Frames_Related.URI:
				return FrameNetControl.FRAMES_RELATED;
			case FrameNetContract.Sentences.URI1:
				return FrameNetControl.SENTENCE;
			case FrameNetContract.Sentences.URI:
				return FrameNetControl.SENTENCES;
			case FrameNetContract.AnnoSets.URI1:
				return FrameNetControl.ANNOSET;
			case FrameNetContract.AnnoSets.URI:
				return FrameNetControl.ANNOSETS;
			case FrameNetContract.Sentences_Layers_X.URI:
				return FrameNetControl.SENTENCES_LAYERS_X;
			case FrameNetContract.AnnoSets_Layers_X.URI:
				return FrameNetControl.ANNOSETS_LAYERS_X;
			case FrameNetContract.Patterns_Layers_X.URI:
				return FrameNetControl.PATTERNS_LAYERS_X;
			case FrameNetContract.ValenceUnits_Layers_X.URI:
				return FrameNetControl.VALENCEUNITS_LAYERS_X;
			case FrameNetContract.Patterns_Sentences.URI:
				return FrameNetControl.PATTERNS_SENTENCES;
			case FrameNetContract.ValenceUnits_Sentences.URI:
				return FrameNetControl.VALENCEUNITS_SENTENCES;
			case FrameNetContract.Governors_AnnoSets_Sentences.URI:
				return FrameNetControl.GOVERNORS_ANNOSETS;
			case FrameNetContract.Words_LexUnits_Frames.URI:
				return FrameNetControl.WORDS_LEXUNITS_FRAMES;
			case FrameNetContract.Words_LexUnits_Frames.URI_FN:
				return FrameNetControl.WORDS_LEXUNITS_FRAMES_FN;
			case FrameNetContract.LexUnits_or_Frames.URI:
				return FrameNetControl.LEXUNITS_OR_FRAMES;
			case FrameNetContract.LexUnits_or_Frames.URI_FN:
				return FrameNetControl.LEXUNITS_OR_FRAMES_FN;
			case FrameNetContract.Frames_FEs.URI:
				return FrameNetControl.FRAMES_FES;
			case FrameNetContract.Frames_FEs.URI_BY_FE:
				return FrameNetControl.FRAMES_FES_BY_FE;
			case FrameNetContract.LexUnits_Sentences.URI:
				return FrameNetControl.LEXUNITS_SENTENCES;
			case FrameNetContract.LexUnits_Sentences.URI_BY_SENTENCE:
				return FrameNetControl.LEXUNITS_SENTENCES_BY_SENTENCE;
			case FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.URI:
				return FrameNetControl.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS;
			case FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.URI_BY_SENTENCE:
				return FrameNetControl.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE;
			case FrameNetContract.LexUnits_Governors.URI:
				return FrameNetControl.LEXUNITS_GOVERNORS;
			case FrameNetContract.LexUnits_Governors.URI_FN:
				return FrameNetControl.LEXUNITS_GOVERNORS_FN;
			case FrameNetContract.LexUnits_FERealizations_ValenceUnits.URI:
				return FrameNetControl.LEXUNITS_REALIZATIONS;
			case FrameNetContract.LexUnits_FERealizations_ValenceUnits.URI_BY_REALIZATION:
				return FrameNetControl.LEXUNITS_REALIZATIONS_BY_REALIZATION;
			case FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.URI:
				return FrameNetControl.LEXUNITS_GROUPREALIZATIONS;
			case FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.URI_BY_PATTERN:
				return FrameNetControl.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN;
			case FrameNetContract.Lookup_FTS_FnWords.URI:
				return FrameNetControl.LOOKUP_FTS_WORDS;
			case FrameNetContract.Lookup_FTS_FnSentences.URI:
				return FrameNetControl.LOOKUP_FTS_SENTENCES;
			case FrameNetContract.Lookup_FTS_FnSentences_X.URI:
				return FrameNetControl.LOOKUP_FTS_SENTENCES_X;
			case FrameNetContract.Lookup_FTS_FnSentences_X.URI_BY_SENTENCE:
				return FrameNetControl.LOOKUP_FTS_SENTENCES_X_BY_SENTENCE;
			case FrameNetContract.Suggest_FnWords.SEARCH_WORD_PATH:
				return FrameNetControl.SUGGEST_WORDS;
			case FrameNetContract.Suggest_FTS_FnWords.SEARCH_WORD_PATH:
				return FrameNetControl.SUGGEST_FTS_WORDS;
			default:
				throw new IllegalArgumentException("Illegal uri: " + providerUri);
		}
	}
}
