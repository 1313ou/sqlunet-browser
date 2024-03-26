/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.bnc.provider;

import org.sqlunet.bnc.provider.BNCControl.Result;

import androidx.annotation.Nullable;

public class QueriesLegacy
{
	@Nullable
	public static Result queryLegacy(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		return queryLegacyMain(code, uriLast, projection0, selection0, selectionArgs0);
	}

	@Nullable
	@SuppressWarnings("UnnecessaryLocalVariable")
	public static Result queryLegacyMain(final int code, @SuppressWarnings("unused") final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String[] projection = projection0;
		String selection = selection0;
		String[] selectionArgs = selectionArgs0;
		String groupBy = null;

		switch (code)
		{
			case BNCControl.BNC:
				table = BNCContract.BNCs.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += BNCContract.BNCs.POSID + " = ?";
				break;

			// J O I N S

			case BNCControl.WORDS_BNC:
				table = "bnc_bncs " + //
						"LEFT JOIN bnc_spwrs USING (wordid, posid) " + //
						"LEFT JOIN bnc_convtasks USING (wordid, posid) " + //
						"LEFT JOIN bnc_imaginfs USING (wordid, posid) ";
				break;

			default:
				return null;
		}
		//noinspection ConstantConditions
		return new Result(table, projection, selection, selectionArgs, groupBy);
	}
}
