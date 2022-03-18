/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.verbnet.provider;

import android.app.SearchManager;

import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses;

/**
 * VerbNet query dispatcher
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VerbNetDispatcher
{
	// table codes
	static final int VNCLASS = 10;
	static final int VNCLASSES = 11;
	static final int VNCLASSES_X_BY_VNCLASS = 20;

	// join codes
	static final int WORDS_VNCLASSES = 100;
	static final int VNCLASSES_VNMEMBERS_X_BY_WORD = 110;
	static final int VNCLASSES_VNROLES_X_BY_VNROLE = 120;
	static final int VNCLASSES_VNFRAMES_X_BY_VNFRAME = 130;

	// search text codes
	static final int LOOKUP_FTS_EXAMPLES = 501;
	static final int LOOKUP_FTS_EXAMPLES_X = 511;
	static final int LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE = 512;

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

	public static Result queryMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String[] projection = projection0;
		String selection = selection0;
		String groupBy = null;

		switch (code)
		{
			case VNCLASS:
				table = VnClasses.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += VnClasses.CLASSID + " = " + uriLast;
				break;

			case VNCLASSES:
				table = VnClasses.TABLE;
				break;

			case VNCLASSES_X_BY_VNCLASS:
				groupBy = "classid";
				table = "vnclasses " + //
						"LEFT JOIN vngroupingmaps USING (classid) " + //
						"LEFT JOIN vngroupings USING (groupingid)";
				break;

			// J O I N S

			case WORDS_VNCLASSES:
				table = "words " + //
						"INNER JOIN vnwords USING (wordid) " + //
						"INNER JOIN vnclassmembersenses USING (vnwordid) " + //
						"LEFT JOIN vnclasses USING (classid)";
				break;

			case VNCLASSES_VNMEMBERS_X_BY_WORD:
				groupBy = "vnwordid";
				table = "vnclassmembersenses " + //
						"LEFT JOIN vnwords USING (vnwordid) " + //
						"LEFT JOIN vngroupingmaps USING (classid, vnwordid) " + //
						"LEFT JOIN vngroupings USING (groupingid) " + //
						"LEFT JOIN synsets USING (synsetid)";
				break;

			case VNCLASSES_VNROLES_X_BY_VNROLE:
				groupBy = "roleid";
				table = "vnrolemaps " + //
						"INNER JOIN vnroles USING (roleid) " + //
						"INNER JOIN vnroletypes USING (roletypeid) " + //
						"LEFT JOIN vnrestrs USING (restrsid)";
				break;

			case VNCLASSES_VNFRAMES_X_BY_VNFRAME:
				groupBy = "frameid";
				table = "vnframemaps " + //
						"INNER JOIN vnframes USING (frameid) " + //
						"LEFT JOIN vnframenames USING (nameid) " + //
						"LEFT JOIN vnframesubnames USING (subnameid) " + //
						"LEFT JOIN vnsyntaxes USING (syntaxid) " + //
						"LEFT JOIN vnsemantics USING (semanticsid) " + //
						"LEFT JOIN vnexamplemaps USING (frameid) " + //
						"LEFT JOIN vnexamples USING (exampleid)";
				break;

			// L O O K U P

			case LOOKUP_FTS_EXAMPLES:
				table = "vnexamples_example_fts4";
				break;

			case LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE:
				groupBy = "exampleid";
				//$FALL-THROUGH$
				//noinspection fallthrough

			case LOOKUP_FTS_EXAMPLES_X:
				table = "vnexamples_example_fts4 " + //
						"LEFT JOIN vnclasses USING (classid)";
				break;

			// S U G G E S T

			case SUGGEST_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				table = "vnwords";
				projection = new String[]{"vnwordid AS _id", //
						"word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"word AS " + SearchManager.SUGGEST_COLUMN_QUERY}; //
				selection = "word LIKE ? || '%'";
			}

			case SUGGEST_FTS_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				table = "vnwords_word_fts4";
				projection = new String[]{"vnwordid AS _id", //
						"word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"word AS " + SearchManager.SUGGEST_COLUMN_QUERY};
				selection =	"word MATCH ?";
			}

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs0, groupBy);
	}

	public static Result querySearch(final int code, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String groupBy = null;

		switch (code)
		{
			case LOOKUP_FTS_EXAMPLES:
				table = "vnexamples_example_fts4";
				break;

			case LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE:
				groupBy = "exampleid";
				//$FALL-THROUGH$
				//noinspection fallthrough

			case LOOKUP_FTS_EXAMPLES_X:
				table = "vnexamples_example_fts4 " + //
						"LEFT JOIN vnclasses USING (classid)";
				break;

			default:
				return null;
		}
		return new Result(table, projection0, selection0, selectionArgs0, groupBy);
	}

	public static Result querySuggest(final int code, final String uriLast)
	{
		String table;
		String[] projection;
		String selection;
		String[] selectionArgs;

		switch (code)
		{
			case SUGGEST_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				table = "vnwords";
				projection = new String[]{"vnwordid AS _id", //
						"word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"word AS " + SearchManager.SUGGEST_COLUMN_QUERY}; //
				selection = "word LIKE ? || '%'";
				selectionArgs = new String[]{uriLast};
				break;
			}

			case SUGGEST_FTS_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				table = "vnwords_word_fts4";
				projection = new String[]{"vnwordid AS _id", //
						"word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"word AS " + SearchManager.SUGGEST_COLUMN_QUERY};
				selection =	"word MATCH ?";
				selectionArgs = new String[]{uriLast + '*'};
				break;
			}

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs, null);
	}
}
