package org.sqlunet.syntagnet.provider;

public class SyntagNetDispatcher
{
	// table codes
	static final int COLLOCATIONS = 10;

	// join codes
	static final int COLLOCATIONS_X = 100;

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
				table = "syntagms " + //
						"JOIN words AS " + SyntagNetContract.W1 + " ON (word1id = " + SyntagNetContract.W1 + ".wordid) " + //
						"JOIN words AS " + SyntagNetContract.W2 + " ON (word2id = " + SyntagNetContract.W2 + ".wordid) " + //
						"JOIN synsets AS " + SyntagNetContract.S1 + " ON (synset1id = " + SyntagNetContract.S1 + ".synsetid) " + //
						"JOIN synsets AS " + SyntagNetContract.S2 + " ON (synset2id = " + SyntagNetContract.S2 + ".synsetid)";
				break;

			default:
				return null;
		}
		return new Result(table, projection0, selection, selectionArgs0, groupBy);
	}
}
