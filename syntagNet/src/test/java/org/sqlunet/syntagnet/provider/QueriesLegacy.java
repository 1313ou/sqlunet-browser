package org.sqlunet.syntagnet.provider;

import org.sqlunet.syntagnet.provider.SyntagNetDispatcher.Result;

public class QueriesLegacy
{
	public static Result queryLegacy(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		return queryLegacyMain(code, uriLast, projection0, selection0, selectionArgs0);
	}

	public static Result queryLegacyMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String[] projection = projection0;
		String selection = selection0;
		String[] selectionArgs = selectionArgs0;
		String groupBy = null;

		switch (code)
		{
			case SyntagNetDispatcher.COLLOCATIONS:
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

			case SyntagNetDispatcher.COLLOCATIONS_X:
				table = "sn_syntagms " + //
						"JOIN words AS " + SyntagNetContract.W1 + " ON (word1id = " + SyntagNetContract.W1 + ".wordid) " + //
						"JOIN words AS " + SyntagNetContract.W2 + " ON (word2id = " + SyntagNetContract.W2 + ".wordid) " + //
						"JOIN synsets AS " + SyntagNetContract.S1 + " ON (synset1id = " + SyntagNetContract.S1 + ".synsetid) " + //
						"JOIN synsets AS " + SyntagNetContract.S2 + " ON (synset2id = " + SyntagNetContract.S2 + ".synsetid)";
				break;

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs, groupBy);
	}
}
