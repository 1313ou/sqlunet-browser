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
		catch(Exception e)
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
			case "words":
				return 10;
			case "word":
				return 11;
			case "senses":
				return 20;
			case "sense":
				return 21;
			case "synsets":
				return 30;
			case "synset":
				return 31;
			case "semrelations":
				return 40;
			case "lexrelations":
				return 50;
			case "relations":
				return 60;
			case "poses":
				return 70;
			case "domains":
				return 80;
			case "adjpositions":
				return 90;
			case "samples":
				return 100;
			case "dict":
				return 200;
			case "words_senses_synsets":
				return 310;
			case "words_senses_casedwords_synsets":
				return 311;
			case "words_senses_casedwords_synsets_poses_domains":
				return 312;
			case "senses_words":
				return 320;
			case "senses_words_by_synset":
				return 321;
			case "senses_synsets_poses_domains":
				return 330;
			case "synsets_poses_domains":
				return 340;
			case "anyrelations_senses_words_x_by_synset":
			case "anyrelations_senses_relations_senses_words_by_synset":
				return 400;
			case "semrelations_synsets":
				return 410;
			case "semrelations_synsets_x":
				return 411;
			case "semrelations_synsets_words_x_by_synset":
			case "semrelations_synsets_relations_senses_words_by_synset":
				return 412;
			case "lexrelations_senses":
				return 420;
			case "lexrelations_senses_x":
				return 421;
			case "lexrelations_senses_words_x_by_synset":
			case "lexrelations_synsets_words_relations_senses_words_by_synset":
				return 422;
			case "senses_vframes":
				return 510;
			case "senses_vtemplates":
				return 515;
			case "senses_adjpositions":
				return 520;
			case "lexes_morphs":
				return 530;
			case "words_lexes_morphs":
				return 541;
			case "words_lexes_morphs_by_word":
				return 542;
			case "lookup_fts_words":
				return 810;
			case "lookup_fts_definitions":
				return 820;
			case "lookup_fts_samples":
				return 830;
			case "suggest_words":
				return 900;
			case "suggest_fts_words":
				return 910;
			case "suggest_fts_definitions":
				return 920;
			case "suggest_fts_samples":
				return 930;
			default:
				throw new IllegalArgumentException("Illegal uri: " + providerUri);
		}
	}
}
