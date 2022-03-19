package org.sqlunet.framenet.provider;

import android.app.SearchManager;

import org.junit.Test;
import org.sqlunet.framenet.provider.FrameNetDispatcher.Result;

import java.util.Arrays;
import java.util.Objects;

public class QueriesUnitTest
{
	private final int[] codes = {FrameNetDispatcher.LEXUNIT, FrameNetDispatcher.LEXUNITS, FrameNetDispatcher.LEXUNITS_X_BY_LEXUNIT, FrameNetDispatcher.FRAME, FrameNetDispatcher.FRAMES, FrameNetDispatcher.FRAMES_X_BY_FRAME, FrameNetDispatcher.FRAMES_RELATED, FrameNetDispatcher.SENTENCE, FrameNetDispatcher.SENTENCES, FrameNetDispatcher.ANNOSET, FrameNetDispatcher.ANNOSETS, FrameNetDispatcher.SENTENCES_LAYERS_X, FrameNetDispatcher.ANNOSETS_LAYERS_X, FrameNetDispatcher.PATTERNS_LAYERS_X, FrameNetDispatcher.VALENCEUNITS_LAYERS_X, FrameNetDispatcher.PATTERNS_SENTENCES, FrameNetDispatcher.VALENCEUNITS_SENTENCES, FrameNetDispatcher.GOVERNORS_ANNOSETS, FrameNetDispatcher.WORDS_LEXUNITS_FRAMES, FrameNetDispatcher.LEXUNITS_OR_FRAMES, FrameNetDispatcher.FRAMES_FES, FrameNetDispatcher.FRAMES_FES_BY_FE, FrameNetDispatcher.LEXUNITS_SENTENCES, FrameNetDispatcher.LEXUNITS_SENTENCES_BY_SENTENCE, FrameNetDispatcher.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS, FrameNetDispatcher.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE, FrameNetDispatcher.LEXUNITS_GOVERNORS, FrameNetDispatcher.LEXUNITS_REALIZATIONS, FrameNetDispatcher.LEXUNITS_REALIZATIONS_BY_REALIZATION, FrameNetDispatcher.LEXUNITS_GROUPREALIZATIONS, FrameNetDispatcher.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN, FrameNetDispatcher.LOOKUP_FTS_WORDS, FrameNetDispatcher.LOOKUP_FTS_SENTENCES, FrameNetDispatcher.LOOKUP_FTS_SENTENCES_X, FrameNetDispatcher.LOOKUP_FTS_SENTENCES_X_BY_SENTENCE, FrameNetDispatcher.SUGGEST_WORDS, FrameNetDispatcher.SUGGEST_FTS_WORDS,};

	private final String uriLast = "LAST";
	private final String[] projection = {"PROJ1", "PROJ2", "PROJ3"};
	private final String selection = "SEL";
	private final String[] selectionArgs = {"ARG1", "ARG2", "ARG3"};
	private final String sortOrder = "SORT";

	@Test
	public void queriesLegacyAgainstProvider()
	{
		for (int i = 0; i < codes.length; i++)
		{
			int code = codes[i];
			queriesLegacyAgainstProvider(code, uriLast, projection, selection, selectionArgs, sortOrder);
		}
	}

	private void queriesLegacyAgainstProvider(final int code, final String uriLast, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder)
	{
		Result r1 = queryLegacy(code, uriLast, projection, selection, selectionArgs);
		Result r2 = queryProvider(code, uriLast, projection, selection, selectionArgs);
		check(code, r1, r2);
	}

	private void queryXAgainstY(final int code, final String uriLast, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder)
	{
	}

	public static Result queryProvider(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		Result r = queryProviderMain(code, uriLast, projection0, selection0, selectionArgs0);
		if (r == null)
		{
			r = queryProviderSearch(code, projection0, selection0, selectionArgs0);
			if (r == null)
			{
				r = queryProviderSuggest(code, uriLast);
			}
		}
		return r;
	}

	public static Result queryLegacy(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		Result r = queryLegacyMain(code, uriLast, projection0, selection0, selectionArgs0);
		if (r == null)
		{
			r = queryLegacySearch(code, projection0, selection0, selectionArgs0);
			if (r == null)
			{
				r = queryLegacySuggest(code, uriLast);
			}
		}
		return r;
	}

	public static Result queryProviderMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		return FrameNetDispatcher.queryMain(code, uriLast, projection0, selection0, selectionArgs0);
	}

	public static Result queryProviderSearch(final int code, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		return FrameNetDispatcher.querySearch(code, projection0, selection0, selectionArgs0);
	}

	public static Result queryProviderSuggest(final int code, final String uriLast)
	{
		return FrameNetDispatcher.querySuggest(code, uriLast);
	}

	public static Result queryLegacyMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String selection = selection0;
		String groupBy = null;

		switch (code)
		{
			// T A B L E
			// table uri : last element is table

			case FrameNetDispatcher.LEXUNITS:
				table = FrameNetContract.LexUnits.TABLE;
				break;

			case FrameNetDispatcher.FRAMES:
				table = FrameNetContract.Frames.TABLE;
				break;

			case FrameNetDispatcher.ANNOSETS:
				table = FrameNetContract.AnnoSets.TABLE;
				break;

			case FrameNetDispatcher.SENTENCES:
				table = FrameNetContract.Sentences.TABLE;
				break;

			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case FrameNetDispatcher.LEXUNIT:
				table = FrameNetContract.LexUnits.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += FrameNetContract.LexUnits.LUID + " = " + uriLast;
				break;

			case FrameNetDispatcher.FRAME:
				table = FrameNetContract.Frames.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += FrameNetContract.Frames.FRAMEID + " = " + uriLast;
				break;

			case FrameNetDispatcher.SENTENCE:
				table = FrameNetContract.Sentences.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += FrameNetContract.Sentences.SENTENCEID + " = " + uriLast;
				break;

			case FrameNetDispatcher.ANNOSET:
				table = FrameNetContract.AnnoSets.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += FrameNetContract.AnnoSets.ANNOSETID + " = " + uriLast;
				break;

			// J O I N S

			case FrameNetDispatcher.LEXUNITS_OR_FRAMES:
				table = "(" + //
						"SELECT fnwordid + 10000 AS " + FrameNetContract.LexUnits_or_Frames.ID + ", luid AS " + FrameNetContract.LexUnits_or_Frames.FNID + ", fnwordid AS " + FrameNetContract.LexUnits_or_Frames.FNWORDID + ", wordid AS " + FrameNetContract.LexUnits_or_Frames.WORDID + ", word AS " + FrameNetContract.LexUnits_or_Frames.WORD + ", lexunit AS " + FrameNetContract.LexUnits_or_Frames.NAME + ", frame AS " + FrameNetContract.LexUnits_or_Frames.FRAMENAME + ", frameid AS " + FrameNetContract.LexUnits_or_Frames.FRAMEID + ", 0 AS " + FrameNetContract.LexUnits_or_Frames.ISFRAME + " " + //
						"FROM fnwords " + //
						"INNER JOIN fnlexemes USING (fnwordid) " + //
						"INNER JOIN fnlexunits AS " + FrameNetContract.LU + " USING (luid) " + //
						"INNER JOIN fnframes AS " + FrameNetContract.FRAME + " USING (frameid) " + //
						"UNION " + //
						"SELECT frameid AS " + FrameNetContract.LexUnits_or_Frames.ID + ", frameid AS " + FrameNetContract.LexUnits_or_Frames.FNID + ", 0 AS " + FrameNetContract.LexUnits_or_Frames.FNWORDID + ", 0 AS " + FrameNetContract.LexUnits_or_Frames.WORDID + ", frame AS " + FrameNetContract.LexUnits_or_Frames.WORD + ", frame AS " + FrameNetContract.LexUnits_or_Frames.NAME + ", frame AS " + FrameNetContract.LexUnits_or_Frames.FRAMENAME + ", frameid AS " + FrameNetContract.LexUnits_or_Frames.FRAMEID + ", 1 AS " + FrameNetContract.LexUnits_or_Frames.ISFRAME + " " + //
						"FROM fnframes " + //
						")";
				break;

			case FrameNetDispatcher.FRAMES_X_BY_FRAME:
				groupBy = "frameid";
				table = "fnframes " + //
						"LEFT JOIN fnframes_semtypes USING (frameid) " + //
						"LEFT JOIN fnsemtypes USING (semtypeid)";
				break;

			case FrameNetDispatcher.FRAMES_RELATED:
				table = "fnframes_related AS " + FrameNetContract.RELATED + ' ' + //
						"LEFT JOIN fnframes AS " + FrameNetContract.SRC + " USING (frameid) " + //
						"LEFT JOIN fnframes AS " + FrameNetContract.DEST + " ON (frame2id = " + FrameNetContract.DEST + ".frameid) " + //  //  //
						"LEFT JOIN fnframerelations USING (relationid)";
				break;

			case FrameNetDispatcher.LEXUNITS_X_BY_LEXUNIT:
				groupBy = "luid";
				table = "fnlexunits AS " + FrameNetContract.LU + ' ' + //
						"LEFT JOIN fnframes AS " + FrameNetContract.FRAME + " USING (frameid) " + //
						"LEFT JOIN fnposes AS " + FrameNetContract.POS + " ON (" + FrameNetContract.LU + ".posid = " + FrameNetContract.POS + ".posid) " + //
						"LEFT JOIN fnfetypes AS " + FrameNetContract.FETYPE + " ON (incorporatedfetypeid = " + FrameNetContract.FETYPE + ".fetypeid) " + //
						"LEFT JOIN fnfes AS " + FrameNetContract.FE + " ON (incorporatedfeid = " + FrameNetContract.FE + ".feid)";
				break;

			case FrameNetDispatcher.SENTENCES_LAYERS_X:
				table = "(SELECT annosetid,sentenceid,layerid,layertype,rank," + //
						"GROUP_CONCAT(start||':'||" + //
						"end||':'||" + //
						"labeltype||':'||" + //
						"CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END||':'||" + //
						"CASE WHEN bgcolor IS NULL THEN '' ELSE bgcolor END||':'||" + //
						"CASE WHEN fgcolor IS NULL THEN '' ELSE fgcolor END,'|') AS " + FrameNetContract.Sentences_Layers_X.LAYERANNOTATIONS + ' ' + //
						"FROM fnsentences " + //
						"LEFT JOIN fnannosets USING (sentenceid) " + //
						"LEFT JOIN fnlayers USING (annosetid) " + //
						"LEFT JOIN fnlayertypes USING (layertypeid) " + //
						"LEFT JOIN fnlabels USING (layerid) " + //
						"LEFT JOIN fnlabeltypes USING (labeltypeid) " + //
						"LEFT JOIN fnlabelitypes USING (labelitypeid) " + //
						"WHERE sentenceid = ? AND labeltypeid IS NOT NULL " + //
						"GROUP BY layerid " + //
						"ORDER BY rank,layerid,start,end)";
				break;

			case FrameNetDispatcher.ANNOSETS_LAYERS_X:
				table = "(SELECT sentenceid,text,layerid,layertype,rank," + //
						"GROUP_CONCAT(start||':'||" + //
						"end||':'||" + //
						"labeltype||':'||" + //
						"CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END||':'||" + //
						"CASE WHEN bgcolor IS NULL THEN '' ELSE bgcolor END||':'||" + //
						"CASE WHEN fgcolor IS NULL THEN '' ELSE fgcolor END,'|') AS " + FrameNetContract.AnnoSets_Layers_X.LAYERANNOTATIONS + ' ' + //
						"FROM fnannosets " + //
						"LEFT JOIN fnsentences USING (sentenceid) " + //
						"LEFT JOIN fnlayers USING (annosetid) " + //
						"LEFT JOIN fnlayertypes USING (layertypeid) " + //
						"LEFT JOIN fnlabels USING (layerid) " + //
						"LEFT JOIN fnlabeltypes USING (labeltypeid) " + //
						"LEFT JOIN fnlabelitypes USING (labelitypeid) " + //
						"WHERE annosetid = ? AND labeltypeid IS NOT NULL " + //
						"GROUP BY layerid " + //
						"ORDER BY rank,layerid,start,end)";
				break;

			case FrameNetDispatcher.PATTERNS_LAYERS_X:
				table = "(SELECT annosetid,sentenceid,text,layerid,layertype,rank," + //
						"GROUP_CONCAT(start||':'||" + //
						"end||':'||" + //
						"labeltype||':'||" + //
						"CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END||':'||" + //
						"CASE WHEN bgcolor IS NULL THEN '' ELSE bgcolor END||':'||" + //
						"CASE WHEN fgcolor IS NULL THEN '' ELSE fgcolor END,'|') AS " + FrameNetContract.Patterns_Layers_X.LAYERANNOTATIONS + ' ' + //
						"FROM fnpatterns_annosets " + //
						"LEFT JOIN fnannosets USING (annosetid) " + //
						"LEFT JOIN fnsentences USING (sentenceid) " + //
						"LEFT JOIN fnlayers USING (annosetid) " + //
						"LEFT JOIN fnlayertypes USING (layertypeid) " + //
						"LEFT JOIN fnlabels USING (layerid) " + //
						"LEFT JOIN fnlabeltypes USING (labeltypeid) " + //
						"LEFT JOIN fnlabelitypes USING (labelitypeid) " + //
						"WHERE patternid = ? AND labeltypeid IS NOT NULL " + //
						"GROUP BY layerid " + //
						"ORDER BY rank,layerid,start,end)";
				break;

			case FrameNetDispatcher.VALENCEUNITS_LAYERS_X:
				table = "(SELECT annosetid,sentenceid,text,layerid,layertype,rank," + //
						"GROUP_CONCAT(start||':'||" + //
						"end||':'||" + //
						"labeltype||':'||" + //
						"CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END||':'||" + //
						"CASE WHEN bgcolor IS NULL THEN '' ELSE bgcolor END||':'||" + //
						"CASE WHEN fgcolor IS NULL THEN '' ELSE fgcolor END,'|') AS " + FrameNetContract.ValenceUnits_Layers_X.LAYERANNOTATIONS + ' ' + //
						"FROM fnvalenceunits_annosets " + //
						"LEFT JOIN fnannosets USING (annosetid) " + //
						"LEFT JOIN fnsentences USING (sentenceid) " + //
						"LEFT JOIN fnlayers USING (annosetid) " + //
						"LEFT JOIN fnlayertypes USING (layertypeid) " + //
						"LEFT JOIN fnlabels USING (layerid) " + //
						"LEFT JOIN fnlabeltypes USING (labeltypeid) " + //
						"LEFT JOIN fnlabelitypes USING (labelitypeid) " + //
						"WHERE vuid = ? AND labeltypeid IS NOT NULL " + //
						"GROUP BY layerid " + //
						"ORDER BY rank,layerid,start,end)";
				break;

			case FrameNetDispatcher.WORDS_LEXUNITS_FRAMES:
				table = "words " + //
						"INNER JOIN fnwords USING (wordid) " + //
						"INNER JOIN fnlexemes USING (fnwordid) " + //
						"INNER JOIN fnlexunits AS " + FrameNetContract.LU + " USING (luid) " + //
						"LEFT JOIN fnframes USING (frameid) " + //
						"LEFT JOIN fnposes AS " + FrameNetContract.POS + " ON (" + FrameNetContract.LU + ".posid = " + FrameNetContract.POS + ".posid) " + //
						"LEFT JOIN fnfes AS " + FrameNetContract.FE + " ON (incorporatedfeid = feid) " + //
						"LEFT JOIN fnfetypes AS " + FrameNetContract.FETYPE + " ON (incorporatedfetypeid = " + FrameNetContract.FE + ".fetypeid)";
				break;

			case FrameNetDispatcher.FRAMES_FES_BY_FE:
				groupBy = "feid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case FrameNetDispatcher.FRAMES_FES:
				table = "fnframes " + //
						"INNER JOIN fnfes USING (frameid) " + //
						"LEFT JOIN fnfetypes USING (fetypeid) " + //
						"LEFT JOIN fncoretypes USING (coretypeid) " + //
						"LEFT JOIN fnfes_semtypes USING (feid) " + //
						"LEFT JOIN fnsemtypes USING (semtypeid)";
				break;

			case FrameNetDispatcher.LEXUNITS_SENTENCES_BY_SENTENCE:
				groupBy = FrameNetContract.SENTENCE + ".sentenceid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case FrameNetDispatcher.LEXUNITS_SENTENCES:
				table = "fnlexunits AS " + FrameNetContract.LU + ' ' + //
						"LEFT JOIN fnsubcorpuses USING (luid) " + //
						"LEFT JOIN fnsubcorpuses_sentences USING (subcorpusid) " + //
						"INNER JOIN fnsentences AS " + FrameNetContract.SENTENCE + " USING (sentenceid)";
				break;

			case FrameNetDispatcher.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE:
				groupBy = FrameNetContract.SENTENCE + ".sentenceid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case FrameNetDispatcher.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS:
				table = "fnlexunits AS " + FrameNetContract.LU + ' ' + //
						"LEFT JOIN fnsubcorpuses USING (luid) " + //
						"LEFT JOIN fnsubcorpuses_sentences USING (subcorpusid) " + //
						"INNER JOIN fnsentences AS " + FrameNetContract.SENTENCE + " USING (sentenceid) " + //
						"LEFT JOIN fnannosets USING (sentenceid) " + //
						"LEFT JOIN fnlayers USING (annosetid) " + //
						"LEFT JOIN fnlayertypes USING (layertypeid) " + //
						"LEFT JOIN fnlabels USING (layerid) " + //
						"LEFT JOIN fnlabeltypes USING (labeltypeid) " + //
						"LEFT JOIN fnlabelitypes USING (labelitypeid)";
				break;

			case FrameNetDispatcher.LEXUNITS_GOVERNORS:
				table = "fnlexunits " + //
						"INNER JOIN fnlexunits_governors USING (luid) " + //
						"INNER JOIN fngovernors USING (governorid) " + //
						"LEFT JOIN fnwords USING (fnwordid)";
				break;

			case FrameNetDispatcher.GOVERNORS_ANNOSETS:
				table = "fngovernors_annosets " + //
						"LEFT JOIN fnannosets USING (annosetid) " + //
						"LEFT JOIN fnsentences USING (sentenceid)";
				break;

			case FrameNetDispatcher.LEXUNITS_REALIZATIONS_BY_REALIZATION:
				groupBy = "ferid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case FrameNetDispatcher.LEXUNITS_REALIZATIONS:
				table = "fnlexunits " + //
						"INNER JOIN fnferealizations USING (luid) " + //
						"LEFT JOIN fnvalenceunits USING (ferid) " + //
						"LEFT JOIN fnfetypes USING (fetypeid) " + //
						"LEFT JOIN fngftypes USING (gfid) " + //
						"LEFT JOIN fnpttypes USING (ptid)";
				break;

			case FrameNetDispatcher.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN:
				groupBy = "patternid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case FrameNetDispatcher.LEXUNITS_GROUPREALIZATIONS:
				table = "fnlexunits " + //
						"INNER JOIN fnfegrouprealizations USING (luid) " + //
						"LEFT JOIN fnpatterns USING (fegrid) " + //
						"LEFT JOIN fnpatterns_valenceunits USING (patternid) " + //
						"LEFT JOIN fnvalenceunits USING (vuid) " + //
						"LEFT JOIN fnfetypes USING (fetypeid) " + //
						"LEFT JOIN fngftypes USING (gfid) " + //
						"LEFT JOIN fnpttypes USING (ptid)";
				break;

			case FrameNetDispatcher.PATTERNS_SENTENCES:
				table = "fnpatterns_annosets " + //
						"LEFT JOIN fnannosets AS " + FrameNetContract.ANNOSET + " USING (annosetid) " + //
						"LEFT JOIN fnsentences AS " + FrameNetContract.SENTENCE + " USING (sentenceid)";
				break;

			case FrameNetDispatcher.VALENCEUNITS_SENTENCES:
				table = "fnvalenceunits_annosets " + //
						"LEFT JOIN fnannosets AS " + FrameNetContract.ANNOSET + " USING (annosetid) " + //
						"LEFT JOIN fnsentences AS " + FrameNetContract.SENTENCE + " USING (sentenceid)";
				break;

			default:
				return null;
		}
		return new Result(table, projection0, selection, selectionArgs0, groupBy);
	}

	public static Result queryLegacySearch(final int code, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String groupBy = null;

		switch (code)
		{
			case FrameNetDispatcher.LOOKUP_FTS_WORDS:
				table = "fnwords_word_fts4";
				break;

			case FrameNetDispatcher.LOOKUP_FTS_SENTENCES:
				table = "fnsentences_text_fts4";
				break;

			case FrameNetDispatcher.LOOKUP_FTS_SENTENCES_X_BY_SENTENCE:
				groupBy = "sentenceid";
				//addProjection(projection, "GROUP_CONCAT(DISTINCT  frame || '@' || frameid)", "GROUP_CONCAT(DISTINCT  lexunit || '@' || luid)");
				//$FALL-THROUGH$
				//noinspection fallthrough

			case FrameNetDispatcher.LOOKUP_FTS_SENTENCES_X:
				table = "fnsentences_text_fts4 " + //
						"LEFT JOIN fnframes USING (frameid) " + //
						"LEFT JOIN fnlexunits USING (frameid,luid)";
				break;

			default:
				return null;
		}
		return new Result(table, projection0, selection0, selectionArgs0, groupBy);
	}

	public static Result queryLegacySuggest(final int code, final String uriLast)
	{
		String table;
		String[] projection;
		String selection;
		String[] selectionArgs;

		switch (code)
		{
			case FrameNetDispatcher.SUGGEST_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				table = "fnwords";
				projection = new String[]{"fnwordid AS _id", //
						"word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"word AS " + SearchManager.SUGGEST_COLUMN_QUERY};
				selection = "word LIKE ? || '%'";
				selectionArgs = new String[]{uriLast};
				break;
			}

			case FrameNetDispatcher.SUGGEST_FTS_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				table = "fnwords_word_fts4";
				projection = new String[]{"fnwordid AS _id", //
						"word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"word AS " + SearchManager.SUGGEST_COLUMN_QUERY};
				selection = "word MATCH ?";
				selectionArgs = new String[]{uriLast + '*'};
				break;
			}

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs, null);
	}

	private void check(final int code, final Result r1, final Result r2)
	{
		assert equals(r1.table, r2.table) : "Code=" + code + " " + r1.table + " != " + r2.table;
		assert Arrays.equals(r1.projection, r2.projection) : "Code=" + code + " " + Arrays.toString(r1.projection) + " != " + Arrays.toString(r2.projection);
		assert equals(r1.selection, r2.selection) : "Code=" + code + " " + r1.selection + " != " + r2.selection;
		assert Arrays.equals(r1.selectionArgs, r2.selectionArgs) : "Code=" + code + " " + Arrays.toString(r1.selectionArgs) + " != " + Arrays.toString(r2.selectionArgs);
		assert equals(r1.groupBy, r2.groupBy) : "Code=" + code + " " + r1.groupBy + " != " + r2.groupBy;
	}

	private static boolean equals(Object a, Object b)
	{
		return Objects.equals(a, b);
	}
}
