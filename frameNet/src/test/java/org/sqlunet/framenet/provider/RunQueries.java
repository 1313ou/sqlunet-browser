package org.sqlunet.framenet.provider;

import org.junit.Test;
import org.sqlunet.browser.Module;
import org.sqlunet.framenet.loaders.Queries;
import org.sqlunet.provider.SQLiteQueryBuilder;
import org.sqlunet.test.SqlProcessor;

import java.sql.SQLException;

public class RunQueries
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
		FrameNetDispatcher.Result r = FrameNetDispatcher.queryMain(code, null, providerSql.projection, providerSql.selection, providerSql.selectionArgs);
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
			case FrameNetContract.LexUnits.CONTENT_URI_TABLE1:
				return FrameNetDispatcher.LEXUNIT;
			case FrameNetContract.LexUnits.CONTENT_URI_TABLE:
				return FrameNetDispatcher.LEXUNITS;
			case FrameNetContract.LexUnits_X.CONTENT_URI_TABLE:
				return FrameNetDispatcher.LEXUNITS_X_BY_LEXUNIT;
			case FrameNetContract.Frames.CONTENT_URI_TABLE1:
				return FrameNetDispatcher.FRAME;
			case FrameNetContract.Frames.CONTENT_URI_TABLE:
				return FrameNetDispatcher.FRAMES;
			case FrameNetContract.Frames_X.CONTENT_URI_TABLE_BY_FRAME:
				return FrameNetDispatcher.FRAMES_X_BY_FRAME;
			case FrameNetContract.Frames_Related.CONTENT_URI_TABLE:
				return FrameNetDispatcher.FRAMES_RELATED;
			case FrameNetContract.Sentences.CONTENT_URI_TABLE1:
				return FrameNetDispatcher.SENTENCE;
			case FrameNetContract.Sentences.CONTENT_URI_TABLE:
				return FrameNetDispatcher.SENTENCES;
			case FrameNetContract.AnnoSets.CONTENT_URI_TABLE1:
				return FrameNetDispatcher.ANNOSET;
			case FrameNetContract.AnnoSets.CONTENT_URI_TABLE:
				return FrameNetDispatcher.ANNOSETS;
			case FrameNetContract.Sentences_Layers_X.CONTENT_URI_TABLE:
				return FrameNetDispatcher.SENTENCES_LAYERS_X;
			case FrameNetContract.AnnoSets_Layers_X.CONTENT_URI_TABLE:
				return FrameNetDispatcher.ANNOSETS_LAYERS_X;
			case FrameNetContract.Patterns_Layers_X.CONTENT_URI_TABLE:
				return FrameNetDispatcher.PATTERNS_LAYERS_X;
			case FrameNetContract.ValenceUnits_Layers_X.CONTENT_URI_TABLE:
				return FrameNetDispatcher.VALENCEUNITS_LAYERS_X;
			case FrameNetContract.Patterns_Sentences.CONTENT_URI_TABLE:
				return FrameNetDispatcher.PATTERNS_SENTENCES;
			case FrameNetContract.ValenceUnits_Sentences.CONTENT_URI_TABLE:
				return FrameNetDispatcher.VALENCEUNITS_SENTENCES;
			case FrameNetContract.Governors_AnnoSets_Sentences.CONTENT_URI_TABLE:
				return FrameNetDispatcher.GOVERNORS_ANNOSETS;
			case FrameNetContract.Words_LexUnits_Frames.CONTENT_URI_TABLE:
				return FrameNetDispatcher.WORDS_LEXUNITS_FRAMES;
			case FrameNetContract.Words_LexUnits_Frames.CONTENT_URI_TABLE_FN:
				return FrameNetDispatcher.WORDS_LEXUNITS_FRAMES_FN;
			case FrameNetContract.LexUnits_or_Frames.CONTENT_URI_TABLE:
				return FrameNetDispatcher.LEXUNITS_OR_FRAMES;
			case FrameNetContract.LexUnits_or_Frames.CONTENT_URI_TABLE_FN:
				return FrameNetDispatcher.LEXUNITS_OR_FRAMES_FN;
			case FrameNetContract.Frames_FEs.CONTENT_URI_TABLE:
				return FrameNetDispatcher.FRAMES_FES;
			case FrameNetContract.Frames_FEs.CONTENT_URI_TABLE_BY_FE:
				return FrameNetDispatcher.FRAMES_FES_BY_FE;
			case FrameNetContract.LexUnits_Sentences.CONTENT_URI_TABLE:
				return FrameNetDispatcher.LEXUNITS_SENTENCES;
			case FrameNetContract.LexUnits_Sentences.CONTENT_URI_TABLE_BY_SENTENCE:
				return FrameNetDispatcher.LEXUNITS_SENTENCES_BY_SENTENCE;
			case FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.CONTENT_URI_TABLE:
				return FrameNetDispatcher.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS;
			case FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.CONTENT_URI_TABLE_BY_SENTENCE:
				return FrameNetDispatcher.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE;
			case FrameNetContract.LexUnits_Governors.CONTENT_URI_TABLE:
				return FrameNetDispatcher.LEXUNITS_GOVERNORS;
			case FrameNetContract.LexUnits_Governors.CONTENT_URI_TABLE_FN:
				return FrameNetDispatcher.LEXUNITS_GOVERNORS_FN;
			case FrameNetContract.LexUnits_FERealizations_ValenceUnits.CONTENT_URI_TABLE:
				return FrameNetDispatcher.LEXUNITS_REALIZATIONS;
			case FrameNetContract.LexUnits_FERealizations_ValenceUnits.CONTENT_URI_TABLE_BY_REALIZATION:
				return FrameNetDispatcher.LEXUNITS_REALIZATIONS_BY_REALIZATION;
			case FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.CONTENT_URI_TABLE:
				return FrameNetDispatcher.LEXUNITS_GROUPREALIZATIONS;
			case FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.CONTENT_URI_TABLE_BY_PATTERN:
				return FrameNetDispatcher.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN;
			case FrameNetContract.Lookup_FTS_FnWords.CONTENT_URI_TABLE:
				return FrameNetDispatcher.LOOKUP_FTS_WORDS;
			case FrameNetContract.Lookup_FTS_FnSentences.CONTENT_URI_TABLE:
				return FrameNetDispatcher.LOOKUP_FTS_SENTENCES;
			case FrameNetContract.Lookup_FTS_FnSentences_X.CONTENT_URI_TABLE:
				return FrameNetDispatcher.LOOKUP_FTS_SENTENCES_X;
			case FrameNetContract.Lookup_FTS_FnSentences_X.CONTENT_URI_TABLE_BY_SENTENCE:
				return FrameNetDispatcher.LOOKUP_FTS_SENTENCES_X_BY_SENTENCE;
			case FrameNetContract.Suggest_FnWords.SEARCH_WORD_PATH:
				return FrameNetDispatcher.SUGGEST_WORDS;
			case FrameNetContract.Suggest_FTS_FnWords.SEARCH_WORD_PATH:
				return FrameNetDispatcher.SUGGEST_FTS_WORDS;
			default:
				throw new IllegalArgumentException("Illegal uri: " + providerUri);
		}
	}
}
