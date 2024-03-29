/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.framenet.provider;

import android.app.SearchManager;

import org.sqlunet.framenet.provider.FrameNetControl.Result;

import androidx.annotation.Nullable;

public class QueriesLegacy
{
	@Nullable
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

	@Nullable
	public static Result queryLegacyMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String selection = selection0;
		String groupBy = null;

		switch (code)
		{
			// T A B L E
			// table uri : last element is table

			case FrameNetControl.LEXUNITS:
				table = FrameNetContract.LexUnits.TABLE;
				break;

			case FrameNetControl.FRAMES:
				table = FrameNetContract.Frames.TABLE;
				break;

			case FrameNetControl.ANNOSETS:
				table = FrameNetContract.AnnoSets.TABLE;
				break;

			case FrameNetControl.SENTENCES:
				table = FrameNetContract.Sentences.TABLE;
				break;

			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case FrameNetControl.LEXUNIT:
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

			case FrameNetControl.FRAME:
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

			case FrameNetControl.SENTENCE:
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

			case FrameNetControl.ANNOSET:
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

			case FrameNetControl.LEXUNITS_OR_FRAMES:
				table = "(" + //
						"SELECT fnwordid + 10000 AS " + FrameNetContract.LexUnits_or_Frames.ID + ", luid AS " + FrameNetContract.LexUnits_or_Frames.FNID + ", fnwordid AS " + FrameNetContract.LexUnits_or_Frames.FNWORDID + ", wordid AS " + FrameNetContract.LexUnits_or_Frames.WORDID + ", word AS " + FrameNetContract.LexUnits_or_Frames.WORD + ", lexunit AS " + FrameNetContract.LexUnits_or_Frames.NAME + ", frame AS " + FrameNetContract.LexUnits_or_Frames.FRAMENAME + ", frameid AS " + FrameNetContract.LexUnits_or_Frames.FRAMEID + ", 0 AS " + FrameNetContract.LexUnits_or_Frames.ISFRAME + " " + //
						"FROM fn_words " + //
						"INNER JOIN words USING (wordid) " + //
						"INNER JOIN fn_lexemes USING (fnwordid) " + //
						"INNER JOIN fn_lexunits AS " + FrameNetContract.AS_LEXUNITS + " USING (luid) " + //
						"INNER JOIN fn_frames AS " + FrameNetContract.AS_FRAMES + " USING (frameid) " + //
						"UNION " + //
						"SELECT frameid AS " + FrameNetContract.LexUnits_or_Frames.ID + ", frameid AS " + FrameNetContract.LexUnits_or_Frames.FNID + ", 0 AS " + FrameNetContract.LexUnits_or_Frames.FNWORDID + ", 0 AS " + FrameNetContract.LexUnits_or_Frames.WORDID + ", frame AS " + FrameNetContract.LexUnits_or_Frames.WORD + ", frame AS " + FrameNetContract.LexUnits_or_Frames.NAME + ", frame AS " + FrameNetContract.LexUnits_or_Frames.FRAMENAME + ", frameid AS " + FrameNetContract.LexUnits_or_Frames.FRAMEID + ", 1 AS " + FrameNetContract.LexUnits_or_Frames.ISFRAME + " " + //
						"FROM fn_frames " + //
						")";
				break;

			case FrameNetControl.FRAMES_X_BY_FRAME:
				groupBy = "frameid";
				table = "fn_frames " + //
						"LEFT JOIN fn_frames_semtypes USING (frameid) " + //
						"LEFT JOIN fn_semtypes USING (semtypeid)";
				break;

			case FrameNetControl.FRAMES_RELATED:
				table = "fn_frames_related AS " + FrameNetContract.AS_RELATED_FRAMES + ' ' + //
						"LEFT JOIN fn_frames AS " + FrameNetContract.AS_SRC_FRAMES + " USING (frameid) " + //
						"LEFT JOIN fn_frames AS " + FrameNetContract.AS_DEST_FRAMES + " ON (frame2id = " + FrameNetContract.AS_DEST_FRAMES + ".frameid) " + //  //  //
						"LEFT JOIN fn_framerelations USING (relationid)";
				break;

			case FrameNetControl.LEXUNITS_X_BY_LEXUNIT:
				groupBy = "luid";
				table = "fn_lexunits AS " + FrameNetContract.AS_LEXUNITS + ' ' + //
						"LEFT JOIN fn_frames AS " + FrameNetContract.AS_FRAMES + " USING (frameid) " + //
						"LEFT JOIN fn_poses AS " + FrameNetContract.AS_POSES + " ON (" + FrameNetContract.AS_LEXUNITS + ".posid = " + FrameNetContract.AS_POSES + ".posid) " + //
						"LEFT JOIN fn_fetypes AS " + FrameNetContract.AS_FETYPES + " ON (incorporatedfetypeid = " + FrameNetContract.AS_FETYPES + ".fetypeid) " + //
						"LEFT JOIN fn_fes AS " + FrameNetContract.AS_FES + " ON (" + FrameNetContract.AS_FRAMES + ".frameid = " + FrameNetContract.AS_FES + ".frameid AND incorporatedfetypeid = " + FrameNetContract.AS_FES + ".fetypeid)";
				break;

			case FrameNetControl.SENTENCES_LAYERS_X:
				table = "(SELECT annosetid,sentenceid,layerid,layertype,rank," + //
						"GROUP_CONCAT(start||':'||" + //
						"end||':'||" + //
						"labeltype||':'||" + //
						"CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END" + //
						//"||':'||CASE WHEN bgcolor IS NULL THEN '' ELSE bgcolor END" + //
						//"||':'||CASE WHEN fgcolor IS NULL THEN '' ELSE fgcolor END" +
						",'|') AS " + FrameNetContract.Sentences_Layers_X.LAYERANNOTATIONS + ' ' + //
						"FROM fn_sentences " + //
						"LEFT JOIN fn_annosets USING (sentenceid) " + //
						"LEFT JOIN fn_layers USING (annosetid) " + //
						"LEFT JOIN fn_layertypes USING (layertypeid) " + //
						"LEFT JOIN fn_labels USING (layerid) " + //
						"LEFT JOIN fn_labeltypes USING (labeltypeid) " + //
						"LEFT JOIN fn_labelitypes USING (labelitypeid) " + //
						"WHERE sentenceid = ? AND labeltypeid IS NOT NULL " + //
						"GROUP BY layerid " + //
						"ORDER BY rank,layerid,start,end)";
				break;

			case FrameNetControl.ANNOSETS_LAYERS_X:
				table = "(SELECT sentenceid,text,layerid,layertype,rank," + //
						"GROUP_CONCAT(start||':'||" + //
						"end||':'||" + //
						"labeltype||':'||" + //
						"CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END" + //
						//"||':'||CASE WHEN bgcolor IS NULL THEN '' ELSE bgcolor END" + //
						//"||':'||CASE WHEN fgcolor IS NULL THEN '' ELSE fgcolor END" +
						",'|') AS " + FrameNetContract.AnnoSets_Layers_X.LAYERANNOTATIONS + ' ' + //
						"FROM fn_annosets " + //
						"LEFT JOIN fn_sentences USING (sentenceid) " + //
						"LEFT JOIN fn_layers USING (annosetid) " + //
						"LEFT JOIN fn_layertypes USING (layertypeid) " + //
						"LEFT JOIN fn_labels USING (layerid) " + //
						"LEFT JOIN fn_labeltypes USING (labeltypeid) " + //
						"LEFT JOIN fn_labelitypes USING (labelitypeid) " + //
						"WHERE annosetid = ? AND labeltypeid IS NOT NULL " + //
						"GROUP BY layerid " + //
						"ORDER BY rank,layerid,start,end)";
				break;

			case FrameNetControl.PATTERNS_LAYERS_X:
				table = "(SELECT annosetid,sentenceid,text,layerid,layertype,rank," + //
						"GROUP_CONCAT(start||':'||" + //
						"end||':'||" + //
						"labeltype||':'||" + //
						"CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END" + //
						//"||':'||CASE WHEN bgcolor IS NULL THEN '' ELSE bgcolor END" + //
						//"||':'||CASE WHEN fgcolor IS NULL THEN '' ELSE fgcolor END" +
						",'|') AS " + FrameNetContract.Patterns_Layers_X.LAYERANNOTATIONS + ' ' + //
						"FROM fn_grouppatterns_annosets " + //
						"LEFT JOIN fn_annosets USING (annosetid) " + //
						"LEFT JOIN fn_sentences USING (sentenceid) " + //
						"LEFT JOIN fn_layers USING (annosetid) " + //
						"LEFT JOIN fn_layertypes USING (layertypeid) " + //
						"LEFT JOIN fn_labels USING (layerid) " + //
						"LEFT JOIN fn_labeltypes USING (labeltypeid) " + //
						"LEFT JOIN fn_labelitypes USING (labelitypeid) " + //
						"WHERE patternid = ? AND labeltypeid IS NOT NULL " + //
						"GROUP BY layerid " + //
						"ORDER BY rank,layerid,start,end)";
				break;

			case FrameNetControl.VALENCEUNITS_LAYERS_X:
				table = "(SELECT annosetid,sentenceid,text,layerid,layertype,rank," + //
						"GROUP_CONCAT(start||':'||" + //
						"end||':'||" + //
						"labeltype||':'||" + //
						"CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END" + //
						//"||':'||CASE WHEN bgcolor IS NULL THEN '' ELSE bgcolor END" + //
						//"||':'||CASE WHEN fgcolor IS NULL THEN '' ELSE fgcolor END" +
						",'|') AS " + FrameNetContract.ValenceUnits_Layers_X.LAYERANNOTATIONS + ' ' + //
						"FROM fn_valenceunits_annosets " + //
						"LEFT JOIN fn_annosets USING (annosetid) " + //
						"LEFT JOIN fn_sentences USING (sentenceid) " + //
						"LEFT JOIN fn_layers USING (annosetid) " + //
						"LEFT JOIN fn_layertypes USING (layertypeid) " + //
						"LEFT JOIN fn_labels USING (layerid) " + //
						"LEFT JOIN fn_labeltypes USING (labeltypeid) " + //
						"LEFT JOIN fn_labelitypes USING (labelitypeid) " + //
						"WHERE vuid = ? AND labeltypeid IS NOT NULL " + //
						"GROUP BY layerid " + //
						"ORDER BY rank,layerid,start,end)";
				break;

			case FrameNetControl.WORDS_LEXUNITS_FRAMES:
				table = "fn_words " + //
						"INNER JOIN words USING (wordid) " + //
						"INNER JOIN fn_lexemes USING (fnwordid) " + //
						"INNER JOIN fn_lexunits AS " + FrameNetContract.AS_LEXUNITS + " USING (luid) " + //
						"LEFT JOIN fn_frames AS "+ FrameNetContract.AS_FRAMES + " USING (frameid) " + //
						"LEFT JOIN fn_poses AS " + FrameNetContract.AS_POSES + " ON (" + FrameNetContract.AS_LEXUNITS + ".posid = " + FrameNetContract.AS_POSES + ".posid) " + //
						"LEFT JOIN fn_fetypes AS " + FrameNetContract.AS_FETYPES + " ON (incorporatedfetypeid = " + FrameNetContract.AS_FETYPES + ".fetypeid) " + //
						"LEFT JOIN fn_fes AS " + FrameNetContract.AS_FES + " ON ("+ FrameNetContract.AS_FRAMES + ".frameid = " + FrameNetContract.AS_FES + ".frameid AND incorporatedfetypeid = " + FrameNetContract.AS_FES + ".fetypeid)";
				break;

			case FrameNetControl.FRAMES_FES_BY_FE:
				groupBy = "feid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case FrameNetControl.FRAMES_FES:
				table = "fn_frames " + //
						"INNER JOIN fn_fes USING (frameid) " + //
						"LEFT JOIN fn_fetypes USING (fetypeid) " + //
						"LEFT JOIN fn_coretypes USING (coretypeid) " + //
						"LEFT JOIN fn_fes_semtypes USING (feid) " + //
						"LEFT JOIN fn_semtypes USING (semtypeid)";
				break;

			case FrameNetControl.LEXUNITS_SENTENCES_BY_SENTENCE:
				groupBy = FrameNetContract.AS_SENTENCES + ".sentenceid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case FrameNetControl.LEXUNITS_SENTENCES:
				table = "fn_lexunits AS " + FrameNetContract.AS_LEXUNITS + ' ' + //
						"LEFT JOIN fn_subcorpuses USING (luid) " + //
						"LEFT JOIN fn_subcorpuses_sentences USING (subcorpusid) " + //
						"INNER JOIN fn_sentences AS " + FrameNetContract.AS_SENTENCES + " USING (sentenceid)";
				break;

			case FrameNetControl.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE:
				groupBy = FrameNetContract.AS_SENTENCES + ".sentenceid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case FrameNetControl.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS:
				table = "fn_lexunits AS " + FrameNetContract.AS_LEXUNITS + ' ' + //
						"LEFT JOIN fn_subcorpuses USING (luid) " + //
						"LEFT JOIN fn_subcorpuses_sentences USING (subcorpusid) " + //
						"INNER JOIN fn_sentences AS " + FrameNetContract.AS_SENTENCES + " USING (sentenceid) " + //
						"LEFT JOIN fn_annosets USING (sentenceid) " + //
						"LEFT JOIN fn_layers USING (annosetid) " + //
						"LEFT JOIN fn_layertypes USING (layertypeid) " + //
						"LEFT JOIN fn_labels USING (layerid) " + //
						"LEFT JOIN fn_labeltypes USING (labeltypeid) " + //
						"LEFT JOIN fn_labelitypes USING (labelitypeid)";
				break;

			case FrameNetControl.LEXUNITS_GOVERNORS:
				table = "fn_lexunits " + //
						"INNER JOIN fn_lexunits_governors USING (luid) " + //
						"INNER JOIN fn_governors USING (governorid) " + //
						"INNER JOIN fn_words USING (fnwordid) " + //
						"LEFT JOIN words USING (wordid)";
				break;

			case FrameNetControl.GOVERNORS_ANNOSETS:
				table = "fn_governors_annosets " + //
						"LEFT JOIN fn_annosets USING (annosetid) " + //
						"LEFT JOIN fn_sentences USING (sentenceid)";
				break;

			case FrameNetControl.LEXUNITS_REALIZATIONS_BY_REALIZATION:
				groupBy = "ferid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case FrameNetControl.LEXUNITS_REALIZATIONS:
				table = "fn_lexunits " + //
						"INNER JOIN fn_ferealizations USING (luid) " + //
						"LEFT JOIN fn_ferealizations_valenceunits USING (ferid) " +
						"LEFT JOIN fn_valenceunits USING (vuid) " +
						"LEFT JOIN fn_fetypes USING (fetypeid) " + //
						"LEFT JOIN fn_gftypes USING (gfid) " + //
						"LEFT JOIN fn_pttypes USING (ptid)";
				break;

			case FrameNetControl.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN:
				groupBy = "patternid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case FrameNetControl.LEXUNITS_GROUPREALIZATIONS:
				table = "fn_lexunits " + //
						"INNER JOIN fn_fegrouprealizations USING (luid) " + //
						"LEFT JOIN fn_grouppatterns USING (fegrid) " + //
						"LEFT JOIN fn_grouppatterns_patterns USING (patternid) " + //
						"LEFT JOIN fn_valenceunits USING (vuid) " + //
						"LEFT JOIN fn_fetypes USING (fetypeid) " + //
						"LEFT JOIN fn_gftypes USING (gfid) " + //
						"LEFT JOIN fn_pttypes USING (ptid)";
				break;

			case FrameNetControl.PATTERNS_SENTENCES:
				table = "fn_grouppatterns_annosets " + //
						"LEFT JOIN fn_annosets AS " + FrameNetContract.AS_ANNOSETS + " USING (annosetid) " + //
						"LEFT JOIN fn_sentences AS " + FrameNetContract.AS_SENTENCES + " USING (sentenceid)";
				break;

			case FrameNetControl.VALENCEUNITS_SENTENCES:
				table = "fn_valenceunits_annosets " + //
						"LEFT JOIN fn_annosets AS " + FrameNetContract.AS_ANNOSETS + " USING (annosetid) " + //
						"LEFT JOIN fn_sentences AS " + FrameNetContract.AS_SENTENCES + " USING (sentenceid)";
				break;

			default:
				return null;
		}
		return new Result(table, projection0, selection, selectionArgs0, groupBy);
	}

	@Nullable
	public static Result queryLegacySearch(final int code, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String groupBy = null;

		switch (code)
		{
			case FrameNetControl.LOOKUP_FTS_WORDS:
				table = "fn_words_word_fts4";
				break;

			case FrameNetControl.LOOKUP_FTS_SENTENCES:
				table = "fn_sentences_text_fts4";
				break;

			case FrameNetControl.LOOKUP_FTS_SENTENCES_X_BY_SENTENCE:
				groupBy = "sentenceid";
				//addProjection(projection, "GROUP_CONCAT(DISTINCT  frame || '@' || frameid)", "GROUP_CONCAT(DISTINCT  lexunit || '@' || luid)");
				//$FALL-THROUGH$
				//noinspection fallthrough

			case FrameNetControl.LOOKUP_FTS_SENTENCES_X:
				table = "fn_sentences_text_fts4 " + //
						"LEFT JOIN fn_frames USING (frameid) " + //
						"LEFT JOIN fn_lexunits USING (frameid,luid)";
				break;

			default:
				return null;
		}
		return new Result(table, projection0, selection0, selectionArgs0, groupBy);
	}

	@Nullable
	public static Result queryLegacySuggest(final int code, final String uriLast)
	{
		String table;
		String[] projection;
		String selection;
		String[] selectionArgs;

		switch (code)
		{
			case FrameNetControl.SUGGEST_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				table = "fn_words";
				projection = new String[]{"fnwordid AS _id", //
						"word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"word AS " + SearchManager.SUGGEST_COLUMN_QUERY};
				selection = "word LIKE ? || '%'";
				selectionArgs = new String[]{uriLast};
				break;
			}

			case FrameNetControl.SUGGEST_FTS_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				table = "fn_words_word_fts4";
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
}
