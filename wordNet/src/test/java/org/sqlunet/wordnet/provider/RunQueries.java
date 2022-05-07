package org.sqlunet.wordnet.provider;

import org.junit.Test;
import org.sqlunet.browser.Module;
import org.sqlunet.provider.SQLiteQueryBuilder;
import org.sqlunet.test.SqlProcessor;
import org.sqlunet.wordnet.loaders.Queries;

import java.sql.SQLException;

public class RunQueries
{
	@Test
	public void runQueries() throws SQLException
	{
		String db = System.getProperty("db");
		System.out.println(db);
		final SqlProcessor processor = new SqlProcessor(db);
		process(processor, Queries.prepareWord(0));
		process(processor, Queries.prepareSenses("w"));
		process(processor, Queries.prepareSenses(0));
		process(processor, Queries.prepareSense(0));
		process(processor, Queries.prepareSense("sk"));
		process(processor, Queries.prepareSense(0, 0));
		process(processor, Queries.prepareSynset(0));
		process(processor, Queries.prepareMembers(0));
		process(processor, Queries.prepareMembers2(0, true));
		process(processor, Queries.prepareMembers2(0, false));
		process(processor, Queries.prepareSamples(0));
		process(processor, Queries.prepareRelations(0, 0));
		process(processor, Queries.prepareSemRelations(0));
		process(processor, Queries.prepareSemRelations(0, 0));
		process(processor, Queries.prepareLexRelations(0, 0));
		process(processor, Queries.prepareLexRelations(0));
		process(processor, Queries.prepareVFrames(0));
		process(processor, Queries.prepareVFrames(0, 0));
		process(processor, Queries.prepareVTemplates(0));
		process(processor, Queries.prepareVTemplates(0, 0));
		process(processor, Queries.prepareAdjPosition(0));
		process(processor, Queries.prepareAdjPosition(0, 0));
		process(processor, Queries.prepareMorphs(0));
		process(processor, Queries.prepareWn(0));
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
		WordNetDispatcher.Result r = WordNetDispatcher.queryMain(code, null, providerSql.projection, providerSql.selection, providerSql.selectionArgs);
		if (r == null)
		{
			// RELATIONS
			r = WordNetDispatcher.queryAnyRelations(code, providerSql.projection, providerSql.selection, providerSql.selectionArgs);
			if (r == null)
			{
				// TEXTSEARCH
				r = WordNetDispatcher.querySearch(code, providerSql.projection, providerSql.selection, providerSql.selectionArgs);
			}
		}
		if (r == null)
		{
			throw new IllegalArgumentException("Illegal query code: " + code);
		}
		return SQLiteQueryBuilder.buildQueryString(false, r.table, r.projection, r.selection, r.groupBy, null, null, null);
	}

	private static int uriToCode(final String providerUri)
	{
		switch (providerUri)
		{
			case WordNetContract.Words.CONTENT_URI_TABLE:
				return 10;
			case WordNetContract.Words.CONTENT_URI_TABLE1:
				return 11;
			case WordNetContract.Senses.CONTENT_URI_TABLE:
				return 20;
			case WordNetContract.Senses.CONTENT_URI_TABLE1:
				return 21;
			case WordNetContract.Synsets.CONTENT_URI_TABLE:
				return 30;
			case WordNetContract.Synsets.CONTENT_URI_TABLE1:
				return 31;
			case WordNetContract.SemRelations.CONTENT_URI_TABLE:
				return 40;
			case WordNetContract.LexRelations.CONTENT_URI_TABLE:
				return 50;
			case WordNetContract.Relations.CONTENT_URI_TABLE:
				return 60;
			case WordNetContract.Poses.CONTENT_URI_TABLE:
				return 70;
			case WordNetContract.Domains.CONTENT_URI_TABLE:
				return 80;
			case WordNetContract.AdjPositions.CONTENT_URI_TABLE:
				return 90;
			case WordNetContract.Samples.CONTENT_URI_TABLE:
				return 100;
			case WordNetContract.Dict.CONTENT_URI_TABLE:
				return 200;
			case WordNetContract.Words_Senses_Synsets.CONTENT_URI_TABLE:
				return 310;
			case WordNetContract.Words_Senses_CasedWords_Synsets.CONTENT_URI_TABLE:
				return 311;
			case WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.CONTENT_URI_TABLE:
				return 312;
			case WordNetContract.Senses_Words.CONTENT_URI_TABLE:
				return 320;
			case WordNetContract.Senses_Words.CONTENT_URI_TABLE_BY_SYNSET:
				return 321;
			case WordNetContract.Senses_Synsets_Poses_Domains.CONTENT_URI_TABLE:
				return 330;
			case WordNetContract.Synsets_Poses_Domains.CONTENT_URI_TABLE:
				return 340;
			case WordNetContract.AnyRelations_Senses_Words_X.CONTENT_URI_TABLE_BY_SYNSET:
				return 400;
			case WordNetContract.SemRelations_Synsets.CONTENT_URI_TABLE:
				return 410;
			case WordNetContract.SemRelations_Synsets_X.CONTENT_URI_TABLE:
				return 411;
			case WordNetContract.SemRelations_Synsets_Words_X.CONTENT_URI_TABLE_BY_SYNSET:
				return 412;
			case WordNetContract.LexRelations_Senses.CONTENT_URI_TABLE:
				return 420;
			case WordNetContract.LexRelations_Senses_X.CONTENT_URI_TABLE:
				return 421;
			case WordNetContract.LexRelations_Senses_Words_X.CONTENT_URI_TABLE_BY_SYNSET:
				return 422;
			case WordNetContract.Senses_VerbFrames.CONTENT_URI_TABLE:
				return 510;
			case WordNetContract.Senses_VerbTemplates.CONTENT_URI_TABLE:
				return 515;
			case WordNetContract.Senses_AdjPositions.CONTENT_URI_TABLE:
				return 520;
			case WordNetContract.Lexes_Morphs.CONTENT_URI_TABLE:
				return 530;
			case WordNetContract.Words_Lexes_Morphs.CONTENT_URI_TABLE:
				return 541;
			case WordNetContract.Words_Lexes_Morphs.CONTENT_URI_TABLE_BY_WORD:
				return 542;
			case WordNetContract.Lookup_Words.CONTENT_URI_TABLE:
				return 810;
			case WordNetContract.Lookup_Definitions.CONTENT_URI_TABLE:
				return 820;
			case WordNetContract.Lookup_Samples.CONTENT_URI_TABLE:
				return 830;
			case WordNetContract.Suggest_Words.SEARCH_WORD_PATH:
				return 900;
			case WordNetContract.Suggest_FTS_Words.SEARCH_WORD_PATH:
				return 910;
			case WordNetContract.Suggest_FTS_Definitions.SEARCH_DEFINITION_PATH:
				return 920;
			case WordNetContract.Suggest_FTS_Samples.SEARCH_SAMPLE_PATH:
				return 930;
			default:
				throw new IllegalArgumentException("Illegal uri: " + providerUri);
		}
	}
}
