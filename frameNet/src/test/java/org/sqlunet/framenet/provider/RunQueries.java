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
			case "lexunit":
				return 10;
			case FrameNetContract.LexUnits.CONTENT_URI_TABLE:
				return 11;
			case FrameNetContract.LexUnits_X.CONTENT_URI_TABLE:
				return 12;
			case "frame":
				return 20;
			case FrameNetContract.Frames.CONTENT_URI_TABLE:
				return 21;
			case FrameNetContract.Frames_X.CONTENT_URI_TABLE_BY_FRAME:
				return 23;
			case FrameNetContract.Frames_Related.CONTENT_URI_TABLE:
				return 25;
			case "sentence":
				return 30;
			case FrameNetContract.Sentences.CONTENT_URI_TABLE:
				return 31;
			case "annoset":
				return 40;
			case FrameNetContract.AnnoSets.CONTENT_URI_TABLE:
				return 41;
			case FrameNetContract.Sentences_Layers_X.CONTENT_URI_TABLE:
				return 50;
			case FrameNetContract.AnnoSets_Layers_X.CONTENT_URI_TABLE:
				return 51;
			case FrameNetContract.Patterns_Layers_X.CONTENT_URI_TABLE:
				return 52;
			case FrameNetContract.ValenceUnits_Layers_X.CONTENT_URI_TABLE:
				return 53;
			case FrameNetContract.Patterns_Sentences.CONTENT_URI_TABLE:
				return 61;
			case FrameNetContract.ValenceUnits_Sentences.CONTENT_URI_TABLE:
				return 62;
			case FrameNetContract.Governors_AnnoSets_Sentences.CONTENT_URI_TABLE:
				return 70;
			case FrameNetContract.Words_LexUnits_Frames.CONTENT_URI_TABLE:
				return 100;
			case FrameNetContract.Words_LexUnits_Frames.CONTENT_URI_TABLE_FN:
				return 101;
			case FrameNetContract.LexUnits_or_Frames.CONTENT_URI_TABLE:
				return 110;
			case FrameNetContract.LexUnits_or_Frames.CONTENT_URI_TABLE_FN:
				return 111;
			case FrameNetContract.Frames_FEs.CONTENT_URI_TABLE:
				return 200;
			case FrameNetContract.Frames_FEs.CONTENT_URI_TABLE_BY_FE:
				return 201;
			case FrameNetContract.LexUnits_Sentences.CONTENT_URI_TABLE:
				return 300;
			case FrameNetContract.LexUnits_Sentences.CONTENT_URI_TABLE_BY_SENTENCE:
				return 301;
			case FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.CONTENT_URI_TABLE:
				return 310;
			case FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.CONTENT_URI_TABLE_BY_SENTENCE:
				return 311;
			case FrameNetContract.LexUnits_Governors.CONTENT_URI_TABLE:
				return 410;
			case  FrameNetContract.LexUnits_Governors.CONTENT_URI_TABLE_FN:
				return 411;
			case FrameNetContract.LexUnits_FERealizations_ValenceUnits.CONTENT_URI_TABLE:
				return 420;
			case FrameNetContract.LexUnits_FERealizations_ValenceUnits.CONTENT_URI_TABLE_BY_REALIZATION:
				return 421;
			case FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.CONTENT_URI_TABLE:
				return 430;
			case FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.CONTENT_URI_TABLE_BY_PATTERN:
				return 431;
			case FrameNetContract.Lookup_FTS_FnWords.CONTENT_URI_TABLE:
				return 510;
			case FrameNetContract.Lookup_FTS_FnSentences.CONTENT_URI_TABLE:
				return 511;
			case FrameNetContract.Lookup_FTS_FnSentences_X.CONTENT_URI_TABLE:
				return 512;
			case FrameNetContract.Lookup_FTS_FnSentences_X.CONTENT_URI_TABLE_BY_SENTENCE:
				return 513;
			case FrameNetContract.Suggest_FnWords.SEARCH_WORD_PATH:
				return 601;
			case FrameNetContract.Suggest_FTS_FnWords.SEARCH_WORD_PATH:
				return 602;
			default:
				throw new IllegalArgumentException("Illegal uri: " + providerUri);
		}
	}
}
