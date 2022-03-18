/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.propbank.provider;

import android.app.SearchManager;

import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets_X;

/**
 * PropBank provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PropBankDispatcher
{
	// table codes
	static final int PBROLESET = 10;
	static final int PBROLESETS = 11;

	// join codes
	static final int PBROLESETS_X = 100;
	static final int PBROLESETS_X_BY_ROLESET = 101;
	static final int WORDS_PBROLESETS = 110;
	static final int PBROLESETS_PBROLES = 120;
	static final int PBROLESETS_PBEXAMPLES = 130;
	static final int PBROLESETS_PBEXAMPLES_BY_EXAMPLE = 131;

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
			// T A B L E S

			case PBROLESETS:
				table = Q.PBROLESETS.TABLE;
				break;

			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case PBROLESET:
				table = Q.PBROLESET.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += Q.PBROLESET.SELECTION;
				break;

			// J O I N S

			case PBROLESETS_X_BY_ROLESET:
				table = Q.PBROLESETS_X.TABLE;
				groupBy = PbRoleSets_X.ROLESETID;
				break;

			case PBROLESETS_X:
				table = Q.PBROLESETS_X.TABLE;
				break;

			case WORDS_PBROLESETS:
				table = Q.WORDS_PBROLESETS.TABLE;
				break;

			case PBROLESETS_PBROLES:
				table = Q.PBROLESETS_PBROLES.TABLE;
				break;

			case PBROLESETS_PBEXAMPLES_BY_EXAMPLE:
				table = Q.PBROLESETS_PBEXAMPLES_BY_EXAMPLE.TABLE;
				groupBy = Q.PBROLESETS_PBEXAMPLES_BY_EXAMPLE.GROUPBY;
				break;

			case PBROLESETS_PBEXAMPLES:
				table = Q.PBROLESETS_PBEXAMPLES.TABLE;
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
				groupBy = Q.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE.GROUPBY;
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
