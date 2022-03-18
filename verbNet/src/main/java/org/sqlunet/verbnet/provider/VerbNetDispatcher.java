/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.verbnet.provider;

import android.app.SearchManager;

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
		String selection = selection0;
		String groupBy = null;

		switch (code)
		{
			case VNCLASSES:
				table = Q.VNCLASSES.TABLE;
				break;

			case VNCLASSES_X_BY_VNCLASS:
				table = Q.VNCLASSES_X_BY_VNCLASS.TABLE;
				groupBy = Q.VNCLASSES_X_BY_VNCLASS.GROUPBY;
				break;

			// I T E M S

			case VNCLASS:
				table = Q.VNCLASS.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += Q.VNCLASS.SELECTION.replaceAll("#\\{uri_last\\}", uriLast);
				break;

			// J O I N S

			case WORDS_VNCLASSES:
				table = Q.WORDS_VNCLASSES.TABLE;
				break;

			case VNCLASSES_VNMEMBERS_X_BY_WORD:
				table = Q.VNCLASSES_VNMEMBERS_X_BY_WORD.TABLE;
				groupBy = Q.VNWORDID;
				break;

			case VNCLASSES_VNROLES_X_BY_VNROLE:
				table = Q.VNCLASSES_VNROLES_X_BY_VNROLE.TABLE;
				groupBy = Q.ROLEID;
				break;

			case VNCLASSES_VNFRAMES_X_BY_VNFRAME:
				table = Q.VNCLASSES_VNFRAMES_X_BY_VNFRAME.TABLE;
				groupBy = Q.FRAMEID;
				break;

			default:
				return null;
		}
		return new Result(table, projection0, selection, selectionArgs0, groupBy);
	}

	public static Result querySearch(final int code, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String groupBy = null;

		switch (code)
		{
			case LOOKUP_FTS_EXAMPLES:
				table = Q.LOOKUP_FTS_EXAMPLES.TABLE;
				break;

			case LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE:
				table = Q.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE.TABLE;
				groupBy = Q.EXAMPLEID;
				break;

			case LOOKUP_FTS_EXAMPLES_X:
				table = Q.LOOKUP_FTS_EXAMPLES_X.TABLE;
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
				table = Q.SUGGEST_WORDS.TABLE;
				projection = Q.SUGGEST_WORDS.PROJECTION;
				projection[1] = projection[1].replaceAll("#\\{suggest_text_1\\}", SearchManager.SUGGEST_COLUMN_TEXT_1);
				projection[2] = projection[2].replaceAll("#\\{suggest_query\\}", SearchManager.SUGGEST_COLUMN_QUERY);
				selection = Q.SUGGEST_WORDS.SELECTION;
				selectionArgs = new String[]{Q.SUGGEST_WORDS.ARGS[0].replaceAll("#\\{uri_last\\}", uriLast)};
				break;
			}

			case SUGGEST_FTS_WORDS:
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
				selectionArgs = new String[]{Q.SUGGEST_FTS_WORDS.ARGS[0].replaceAll("#\\{uri_last\\}", uriLast)};
				break;
			}

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs, null);
	}
}
