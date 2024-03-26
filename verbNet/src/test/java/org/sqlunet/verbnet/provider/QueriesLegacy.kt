/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.verbnet.provider;

import android.app.SearchManager;

import org.sqlunet.verbnet.provider.VerbNetControl.Result;

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
	@SuppressWarnings("UnnecessaryLocalVariable")
	public static Result queryLegacyMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String[] projection = projection0;
		String selection = selection0;
		String[] selectionArgs = selectionArgs0;
		String groupBy = null;

		switch (code)
		{
			// T A B L E S

			case VerbNetControl.VNCLASSES:
				table = VerbNetContract.VnClasses.TABLE;
				break;

			case VerbNetControl.VNCLASSES_X_BY_VNCLASS:
				groupBy = "classid";
				table = "vn_classes " + //
						"LEFT JOIN vn_members_groupings USING (classid) " + //
						"LEFT JOIN vn_groupings USING (groupingid)";
				break;

			// I T E M S

			case VerbNetControl.VNCLASS1:
				table = VerbNetContract.VnClasses.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += VerbNetContract.VnClasses.CLASSID + " = " + uriLast;
				break;

			// J O I N S

			case VerbNetControl.WORDS_VNCLASSES:
				table = "words " + //
						"INNER JOIN vn_words USING (wordid) " + //
						"INNER JOIN vn_members_senses USING (vnwordid, wordid) " + //
						"LEFT JOIN vn_classes USING (classid)";
				break;

			case VerbNetControl.VNCLASSES_VNMEMBERS_X_BY_WORD:
				groupBy = "vnwordid";
				table = "words " +
						"INNER JOIN vn_words USING (wordid) " +
						"INNER JOIN vn_members_senses USING (vnwordid, wordid) " +
						"LEFT JOIN vn_members_groupings USING (classid, vnwordid) " + //
						"LEFT JOIN vn_groupings USING (groupingid) " + //
						"LEFT JOIN synsets USING (synsetid)";
				break;

			case VerbNetControl.VNCLASSES_VNROLES_X_BY_VNROLE:
				groupBy = "roleid";
				table = "vn_classes " + //
						"INNER JOIN vn_roles USING (classid) " + //
						"INNER JOIN vn_roletypes USING (roletypeid) " + //
						"LEFT JOIN vn_restrs USING (restrsid)";
				break;

			case VerbNetControl.VNCLASSES_VNFRAMES_X_BY_VNFRAME:
				groupBy = "frameid";
				table = "vn_classes_frames " + //
						"INNER JOIN vn_frames USING (frameid) " + //
						"LEFT JOIN vn_framenames USING (framenameid) " + //
						"LEFT JOIN vn_framesubnames USING (framesubnameid) " + //
						"LEFT JOIN vn_syntaxes USING (syntaxid) " + //
						"LEFT JOIN vn_semantics USING (semanticsid) " + //
						"LEFT JOIN vn_frames_examples USING (frameid) " + //
						"LEFT JOIN vn_examples USING (exampleid)";
				break;

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs, groupBy);
	}

	@Nullable
	public static Result queryLegacySearch(final int code, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String groupBy = null;

		switch (code)
		{
			case VerbNetControl.LOOKUP_FTS_EXAMPLES:
				table = "vn_examples_example_fts4";
				break;

			case VerbNetControl.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE:
				groupBy = "exampleid";
				//$FALL-THROUGH$
				//noinspection fallthrough

			case VerbNetControl.LOOKUP_FTS_EXAMPLES_X:
				table = "vn_examples_example_fts4 " + //
						"LEFT JOIN vn_classes USING (classid)";
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
			case VerbNetControl.SUGGEST_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				table = "vn_words INNER JOIN words USING (wordid)";
				projection = new String[]{"vnwordid AS _id", //
						"word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"word AS " + SearchManager.SUGGEST_COLUMN_QUERY}; //
				selection = "word LIKE ? || '%'";
				selectionArgs = new String[]{uriLast};
				break;
			}

			case VerbNetControl.SUGGEST_FTS_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				table = "vn_words_word_fts4";
				projection = new String[]{"vnwordid AS _id", //
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
