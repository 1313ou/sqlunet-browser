/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.framenet.provider;

import android.app.SearchManager;

import androidx.annotation.Nullable;

/**
 * FrameNet query control
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FrameNetControl
{
	// table codes
	static final int LEXUNIT = 10;
	static final int LEXUNITS = 11;
	static final int LEXUNITS_X_BY_LEXUNIT = 12;
	static final int FRAME = 20;
	static final int FRAMES = 21;
	static final int FRAMES_X_BY_FRAME = 23;
	static final int FRAMES_RELATED = 25;
	static final int SENTENCE = 30;
	static final int SENTENCES = 31;
	static final int ANNOSET = 40;
	static final int ANNOSETS = 41;
	static final int SENTENCES_LAYERS_X = 50;
	static final int ANNOSETS_LAYERS_X = 51;
	static final int PATTERNS_LAYERS_X = 52;
	static final int VALENCEUNITS_LAYERS_X = 53;
	static final int PATTERNS_SENTENCES = 61;
	static final int VALENCEUNITS_SENTENCES = 62;
	static final int GOVERNORS_ANNOSETS = 70;

	// join codes
	static final int WORDS_LEXUNITS_FRAMES = 100;
	static final int WORDS_LEXUNITS_FRAMES_FN = 101;
	static final int LEXUNITS_OR_FRAMES = 110;
	static final int LEXUNITS_OR_FRAMES_FN = 111;
	static final int FRAMES_FES = 200;
	static final int FRAMES_FES_BY_FE = 201;
	static final int LEXUNITS_SENTENCES = 300;
	static final int LEXUNITS_SENTENCES_BY_SENTENCE = 301;
	static final int LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS = 310;
	static final int LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE = 311;
	static final int LEXUNITS_GOVERNORS = 410;
	static final int LEXUNITS_GOVERNORS_FN = 411;
	static final int LEXUNITS_REALIZATIONS = 420;
	static final int LEXUNITS_REALIZATIONS_BY_REALIZATION = 421;
	static final int LEXUNITS_GROUPREALIZATIONS = 430;
	static final int LEXUNITS_GROUPREALIZATIONS_BY_PATTERN = 431;

	// search text codes
	static final int LOOKUP_FTS_WORDS = 510;
	static final int LOOKUP_FTS_SENTENCES = 511;
	static final int LOOKUP_FTS_SENTENCES_X = 512;
	static final int LOOKUP_FTS_SENTENCES_X_BY_SENTENCE = 513;

	// suggest
	static final int SUGGEST_WORDS = 601;
	static final int SUGGEST_FTS_WORDS = 602;

	static public class Result
	{
		final String table;
		final String[] projection;
		final String selection;
		final String[] selectionArgs;
		final String groupBy;

		public Result(final String table, final String[] projection, final String selection, final String[] selectionArgs, final String groupBy)
		{
			this.table = table;
			this.projection = projection;
			this.selection = selection;
			this.selectionArgs = selectionArgs;
			this.groupBy = groupBy;
		}
	}

	@Nullable
	public static Result queryMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String selection = selection0;
		String groupBy = null;

		switch (code)
		{
			// T A B L E
			// table uri : last element is table

			case FrameNetControl.LEXUNITS:
				table = Q.LEXUNITS.TABLE;
				break;

			case FrameNetControl.FRAMES:
				table = Q.FRAMES.TABLE;
				break;

			case FrameNetControl.ANNOSETS:
				table = Q.ANNOSETS.TABLE;
				break;

			case FrameNetControl.SENTENCES:
				table = Q.SENTENCES.TABLE;
				break;

			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case FrameNetControl.LEXUNIT:
				table = Q.LEXUNIT1.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += V.LUID + " = " + uriLast;
				break;

			case FrameNetControl.FRAME:
				table = Q.FRAME1.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += V.FRAMEID + " = " + uriLast;
				break;

			case FrameNetControl.SENTENCE:
				table = Q.SENTENCE1.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += V.SENTENCEID + " = " + uriLast;
				break;

			case FrameNetControl.ANNOSET:
				table = Q.ANNOSET1.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += V.ANNOSETID + " = " + uriLast;
				break;

			// J O I N S

			case FrameNetControl.LEXUNITS_OR_FRAMES:
				table = Q.LEXUNITS_OR_FRAMES.TABLE;
				break;

			case FrameNetControl.LEXUNITS_OR_FRAMES_FN:
				table = Q.LEXUNITS_OR_FRAMES_FN.TABLE;
				break;

			case FrameNetControl.FRAMES_X_BY_FRAME:
				table = Q.FRAMES_X_BY_FRAME.TABLE;
				groupBy = V.FRAMEID;
				break;

			case FrameNetControl.FRAMES_RELATED:
				table = Q.FRAMES_RELATED.TABLE;
				break;

			case FrameNetControl.LEXUNITS_X_BY_LEXUNIT:
				table = Q.LEXUNITS_X_BY_LEXUNIT.TABLE;
				groupBy = V.LUID;
				break;

			case FrameNetControl.SENTENCES_LAYERS_X:
				table = Q.SENTENCES_LAYERS_X.TABLE;
				break;

			case FrameNetControl.ANNOSETS_LAYERS_X:
				table = Q.ANNOSETS_LAYERS_X.TABLE;
				break;

			case FrameNetControl.PATTERNS_LAYERS_X:
				table = Q.PATTERNS_LAYERS_X.TABLE;
				break;

			case FrameNetControl.VALENCEUNITS_LAYERS_X:
				table = Q.VALENCEUNITS_LAYERS_X.TABLE;
				break;

			case FrameNetControl.WORDS_LEXUNITS_FRAMES:
				table = Q.WORDS_LEXUNITS_FRAMES.TABLE;
				break;

			case FrameNetControl.WORDS_LEXUNITS_FRAMES_FN:
				table = Q.WORDS_LEXUNITS_FRAMES_FN.TABLE;
				break;

			case FrameNetControl.FRAMES_FES_BY_FE:
				table = Q.FRAMES_FES_BY_FE.TABLE;
				groupBy = V.FEID;
				break;

			case FrameNetControl.FRAMES_FES:
				table = Q.FRAMES_FES.TABLE;
				break;

			case FrameNetControl.LEXUNITS_SENTENCES_BY_SENTENCE:
				table = Q.LEXUNITS_SENTENCES_BY_SENTENCE.TABLE;
				groupBy = V.AS_SENTENCES + '.' + V.SENTENCEID;
				break;

			case FrameNetControl.LEXUNITS_SENTENCES:
				table = Q.LEXUNITS_SENTENCES.TABLE;
				break;

			case FrameNetControl.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE:
				table = Q.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE.TABLE;
				groupBy = V.AS_SENTENCES + '.' + V.SENTENCEID;
				break;

			case FrameNetControl.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS:
				table = Q.LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS.TABLE;
				break;

			case FrameNetControl.LEXUNITS_GOVERNORS:
				table = Q.LEXUNITS_GOVERNORS.TABLE;
				break;

			case FrameNetControl.LEXUNITS_GOVERNORS_FN:
				table = Q.LEXUNITS_GOVERNORS_FN.TABLE;
				break;

			case FrameNetControl.GOVERNORS_ANNOSETS:
				table = Q.GOVERNORS_ANNOSETS.TABLE;
				break;

			case FrameNetControl.LEXUNITS_REALIZATIONS_BY_REALIZATION:
				table = Q.LEXUNITS_REALIZATIONS_BY_REALIZATION.TABLE;
				groupBy = V.FERID;
				break;

			case FrameNetControl.LEXUNITS_REALIZATIONS:
				table = Q.LEXUNITS_REALIZATIONS.TABLE;
				break;

			case FrameNetControl.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN:
				table = Q.LEXUNITS_GROUPREALIZATIONS_BY_PATTERN.TABLE;
				groupBy = V.PATTERNID;
				break;

			case FrameNetControl.LEXUNITS_GROUPREALIZATIONS:
				table = Q.LEXUNITS_GROUPREALIZATIONS.TABLE;
				break;

			case FrameNetControl.PATTERNS_SENTENCES:
				table = Q.PATTERNS_SENTENCES.TABLE;
				break;

			case FrameNetControl.VALENCEUNITS_SENTENCES:
				table = Q.VALENCEUNITS_SENTENCES.TABLE;
				break;

			default:
				return null;
		}
		return new Result(table, projection0, selection, selectionArgs0, groupBy);
	}

	@Nullable
	public static Result querySearch(final int code, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String groupBy = null;

		switch (code)
		{
			case FrameNetControl.LOOKUP_FTS_WORDS:
				table = Q.LOOKUP_FTS_WORDS.TABLE;
				break;

			case FrameNetControl.LOOKUP_FTS_SENTENCES:
				table = Q.LOOKUP_FTS_SENTENCES.TABLE;
				break;

			case FrameNetControl.LOOKUP_FTS_SENTENCES_X_BY_SENTENCE:
				table = Q.LOOKUP_FTS_SENTENCES_X_BY_SENTENCE.TABLE;
				groupBy = V.SENTENCEID;
				break;

			case FrameNetControl.LOOKUP_FTS_SENTENCES_X:
				table = Q.LOOKUP_FTS_SENTENCES_X.TABLE;
				break;

			default:
				return null;
		}
		return new Result(table, projection0, selection0, selectionArgs0, groupBy);
	}

	@Nullable
	public static Result querySuggest(final int code, final String uriLast)
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
				table = Q.SUGGEST_WORDS.TABLE;
				projection = Q.SUGGEST_WORDS.PROJECTION;
				projection[1] = projection[1].replaceAll("#\\{suggest_text_1\\}", SearchManager.SUGGEST_COLUMN_TEXT_1);
				projection[2] = projection[2].replaceAll("#\\{suggest_query\\}", SearchManager.SUGGEST_COLUMN_QUERY);
				selection = Q.SUGGEST_WORDS.SELECTION;
				selectionArgs = new String[]{uriLast};
				break;
			}

			case FrameNetControl.SUGGEST_FTS_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				table = Q.SUGGEST_FTS_WORDS.TABLE;
				projection = Q.SUGGEST_FTS_WORDS.PROJECTION;
				projection[1] = projection[1].replaceAll("#\\{suggest_text_1\\}", SearchManager.SUGGEST_COLUMN_TEXT_1);
				projection[2] = projection[2].replaceAll("#\\{suggest_query\\}", SearchManager.SUGGEST_COLUMN_QUERY);
				selection = Q.SUGGEST_FTS_WORDS.SELECTION;
				selectionArgs = new String[]{uriLast + '*'};
				break;
			}

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs, null);
	}
}
