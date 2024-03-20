/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.bnc.provider;

import androidx.annotation.Nullable;

/**
 * BNC query control
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class BNCControl
{
	// table codes
	static final int BNC = 11;

	// join tables
	static final int WORDS_BNC = 100;

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
	public static Result queryMain(final int code, @SuppressWarnings("unused") final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String selection = selection0;

		switch (code)
		{
			case BNC:
				table = Q.BNCS.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += Q.BNCS.SELECTION;
				break;

			// J O I N S

			case WORDS_BNC:
				table = Q.WORDS_BNCS.TABLE;
				break;

			default:
				return null;
		}
		return new Result(table, projection0, selection, selectionArgs0, null);
	}
}
