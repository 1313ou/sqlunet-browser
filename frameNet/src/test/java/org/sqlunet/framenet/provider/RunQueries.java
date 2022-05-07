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
			case "lexunits":
				return 11;
			case "lexunits_x_by_lexunit":
			case "fnlexunits_x_by_lexunit":
				return 12;
			case "frame":
				return 20;
			case "frames":
				return 21;
			case "frames_x_by_frame":
			case "fnframes_x_by_frame":
				return 23;
			case "frames_related":
			case "fnframes_related":
				return 25;
			case "sentence":
				return 30;
			case "sentences":
			case "fn_sentences":
				return 31;
			case "annoset":
				return 40;
			case "annosets":
				return 41;
			case "sentences_layers_x":
			case "fnsentences_fnlayers_x":
				return 50;
			case "annosets_layers_x":
			case "fnannosets_fnlayers_x":
				return 51;
			case "patterns_layers_x":
			case "fnpatterns_fnlayers_x":
				return 52;
			case "valenceunits_layers_x":
			case "fnvalenceunits_fnlayers_x":
				return 53;
			case "patterns_sentences":
			case "fnpatterns_annosets":
				return 61;
			case "valenceunits_sentences":
			case "fnvalenceunits_annosets":
				return 62;
			case "governors_annosets":
			case "fngovernors_annosets_sentences":
				return 70;
			case "words_lexunits_frames":
				return 100;
			case "words_lexunits_frames_fn":
			case "words_fnlexunits_fn":
				return 101;
			case "lexunits_or_frames":
				return 110;
			case "lexunits_or_frames_fn":
				return 111;
			case "frames_fes":
				return 200;
			case "frames_fes_by_fe":
			case "fnframes_fnfes/fe":
				return 201;
			case "lexunits_sentences":
				return 300;
			case "lexunits_sentences_by_sentence":
				return 301;
			case "lexunits_sentences_annosets_layers_labels":
				return 310;
			case "lexunits_sentences_annosets_layers_labels_by_sentence":
			case "fnlexunits_fnsentences_fnannosets_fnlayers_fnlabels/sentence":
				return 311;
			case "lexunits_governors":
				return 410;
			case "lexunits_governors_fn":
			case "fnlexunits_fngovernors_fn":
				return 411;
			case "lexunits_realizations":
				return 420;
			case "lexunits_realizations_by_realization":
			case "fnlexunits_fnferealizations_fnvalenceunits/realization":
				return 421;
			case "lexunits_grouprealizations":
				return 430;
			case "lexunits_grouprealizations_by_pattern":
			case "fnlexunits_fnferealizations_fnpatterns_fnvalenceunits/pattern":
				return 431;
			case "lookup_fts_words":
				return 510;
			case "lookup_fts_sentences":
				return 511;
			case "lookup_fts_sentences_x":
				return 512;
			case "lookup_fts_sentences_x_by_sentence":
				return 513;
			case "suggest_words":
				return 601;
			case "suggest_fts_words":
				return 602;
			default:
				throw new IllegalArgumentException("Illegal uri: " + providerUri);
		}
	}
}
