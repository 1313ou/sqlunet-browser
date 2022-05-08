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
				return WordNetDispatcher.WORDS;
			case WordNetContract.Words.CONTENT_URI_TABLE1:
				return WordNetDispatcher.WORD;
			case WordNetContract.Senses.CONTENT_URI_TABLE:
				return WordNetDispatcher.SENSES;
			case WordNetContract.Senses.CONTENT_URI_TABLE1:
				return WordNetDispatcher.SENSE;
			case WordNetContract.Synsets.CONTENT_URI_TABLE:
				return WordNetDispatcher.SYNSETS;
			case WordNetContract.Synsets.CONTENT_URI_TABLE1:
				return WordNetDispatcher.SYNSET;
			case WordNetContract.SemRelations.CONTENT_URI_TABLE:
				return WordNetDispatcher.SEMRELATIONS;
			case WordNetContract.LexRelations.CONTENT_URI_TABLE:
				return WordNetDispatcher.LEXRELATIONS;
			case WordNetContract.Relations.CONTENT_URI_TABLE:
				return WordNetDispatcher.RELATIONS;
			case WordNetContract.Poses.CONTENT_URI_TABLE:
				return WordNetDispatcher.POSES;
			case WordNetContract.Domains.CONTENT_URI_TABLE:
				return WordNetDispatcher.DOMAINS;
			case WordNetContract.AdjPositions.CONTENT_URI_TABLE:
				return WordNetDispatcher.ADJPOSITIONS;
			case WordNetContract.Samples.CONTENT_URI_TABLE:
				return WordNetDispatcher.SAMPLES;
			case WordNetContract.Dict.CONTENT_URI_TABLE:
				return WordNetDispatcher.DICT;
			case WordNetContract.Words_Senses_Synsets.CONTENT_URI_TABLE:
				return WordNetDispatcher.WORDS_SENSES_SYNSETS;
			case WordNetContract.Words_Senses_CasedWords_Synsets.CONTENT_URI_TABLE:
				return WordNetDispatcher.WORDS_SENSES_CASEDWORDS_SYNSETS;
			case WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.CONTENT_URI_TABLE:
				return WordNetDispatcher.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS;
			case WordNetContract.Senses_Words.CONTENT_URI_TABLE:
				return WordNetDispatcher.SENSES_WORDS;
			case WordNetContract.Senses_Words.CONTENT_URI_TABLE_BY_SYNSET:
				return WordNetDispatcher.SENSES_WORDS_BY_SYNSET;
			case WordNetContract.Senses_Synsets_Poses_Domains.CONTENT_URI_TABLE:
				return WordNetDispatcher.SENSES_SYNSETS_POSES_DOMAINS;
			case WordNetContract.Synsets_Poses_Domains.CONTENT_URI_TABLE:
				return WordNetDispatcher.SYNSETS_POSES_DOMAINS;
			case WordNetContract.AnyRelations_Senses_Words_X.CONTENT_URI_TABLE_BY_SYNSET:
				return WordNetDispatcher.ANYRELATIONS_SENSES_WORDS_X_BY_SYNSET;
			case WordNetContract.SemRelations_Synsets.CONTENT_URI_TABLE:
				return WordNetDispatcher.SEMRELATIONS_SYNSETS;
			case WordNetContract.SemRelations_Synsets_X.CONTENT_URI_TABLE:
				return WordNetDispatcher.SEMRELATIONS_SYNSETS_X;
			case WordNetContract.SemRelations_Synsets_Words_X.CONTENT_URI_TABLE_BY_SYNSET:
				return WordNetDispatcher.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET;
			case WordNetContract.LexRelations_Senses.CONTENT_URI_TABLE:
				return WordNetDispatcher.LEXRELATIONS_SENSES;
			case WordNetContract.LexRelations_Senses_X.CONTENT_URI_TABLE:
				return WordNetDispatcher.LEXRELATIONS_SENSES_X;
			case WordNetContract.LexRelations_Senses_Words_X.CONTENT_URI_TABLE_BY_SYNSET:
				return WordNetDispatcher.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET;
			case WordNetContract.Senses_VerbFrames.CONTENT_URI_TABLE:
				return WordNetDispatcher.SENSES_VFRAMES;
			case WordNetContract.Senses_VerbTemplates.CONTENT_URI_TABLE:
				return WordNetDispatcher.SENSES_VTEMPLATES;
			case WordNetContract.Senses_AdjPositions.CONTENT_URI_TABLE:
				return WordNetDispatcher.SENSES_ADJPOSITIONS;
			case WordNetContract.Lexes_Morphs.CONTENT_URI_TABLE:
				return WordNetDispatcher.LEXES_MORPHS;
			case WordNetContract.Words_Lexes_Morphs.CONTENT_URI_TABLE:
				return WordNetDispatcher.WORDS_LEXES_MORPHS;
			case WordNetContract.Words_Lexes_Morphs.CONTENT_URI_TABLE_BY_WORD:
				return WordNetDispatcher.WORDS_LEXES_MORPHS_BY_WORD;
			case WordNetContract.Lookup_Words.CONTENT_URI_TABLE:
				return WordNetDispatcher.LOOKUP_FTS_WORDS;
			case WordNetContract.Lookup_Definitions.CONTENT_URI_TABLE:
				return WordNetDispatcher.LOOKUP_FTS_DEFINITIONS;
			case WordNetContract.Lookup_Samples.CONTENT_URI_TABLE:
				return WordNetDispatcher.LOOKUP_FTS_SAMPLES;
			case WordNetContract.Suggest_Words.SEARCH_WORD_PATH:
				return WordNetDispatcher.SUGGEST_WORDS;
			case WordNetContract.Suggest_FTS_Words.SEARCH_WORD_PATH:
				return WordNetDispatcher.SUGGEST_FTS_WORDS;
			case WordNetContract.Suggest_FTS_Definitions.SEARCH_DEFINITION_PATH:
				return WordNetDispatcher.SUGGEST_FTS_DEFINITIONS;
			case WordNetContract.Suggest_FTS_Samples.SEARCH_SAMPLE_PATH:
				return WordNetDispatcher.SUGGEST_FTS_SAMPLES;
			default:
				throw new IllegalArgumentException("Illegal uri: " + providerUri);
		}
	}
}
