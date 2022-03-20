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

		switch (code)
		{
			case COLLOCATIONS:
				table = Q.COLLOCATIONS.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += Q.COLLOCATIONS.SELECTION;
				break;

			// J O I N S

			case COLLOCATIONS_X:
				table =Q.COLLOCATIONS_X.TABLE;
				break;

			default:
				return null;
		}
		return new Result(table, projection0, selection, selectionArgs0, null);
	}
}
