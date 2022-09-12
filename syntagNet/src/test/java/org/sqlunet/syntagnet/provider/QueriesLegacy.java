/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.syntagnet.provider;

import org.sqlunet.syntagnet.provider.SyntagNetControl.Result;

public class QueriesLegacy
{
	public static Result queryLegacy(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		return queryLegacyMain(code, uriLast, projection0, selection0, selectionArgs0);
	}

	@SuppressWarnings("UnnecessaryLocalVariable")
	public static Result queryLegacyMain(final int code,  @SuppressWarnings("unused") final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String[] projection = projection0;
		String selection = selection0;
		String[] selectionArgs = selectionArgs0;
		String groupBy = null;

		switch (code)
		{
			case SyntagNetControl.COLLOCATIONS:
				table = SyntagNetContract.SnCollocations.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += SyntagNetContract.SnCollocations.COLLOCATIONID + " = ?";
				break;

			// J O I N S

			case SyntagNetControl.COLLOCATIONS_X:
				table = "sn_syntagms " + //
						"JOIN words AS " + SyntagNetContract.AS_WORDS1 + " ON (word1id = " + SyntagNetContract.AS_WORDS1 + ".wordid) " + //
						"JOIN words AS " + SyntagNetContract.AS_WORDS2 + " ON (word2id = " + SyntagNetContract.AS_WORDS2 + ".wordid) " + //
						"JOIN synsets AS " + SyntagNetContract.AS_SYNSETS1 + " ON (synset1id = " + SyntagNetContract.AS_SYNSETS1 + ".synsetid) " + //
						"JOIN synsets AS " + SyntagNetContract.AS_SYNSETS2 + " ON (synset2id = " + SyntagNetContract.AS_SYNSETS2 + ".synsetid)";
				break;

			default:
				return null;
		}
		//noinspection ConstantConditions
		return new Result(table, projection, selection, selectionArgs, groupBy);
	}
}
