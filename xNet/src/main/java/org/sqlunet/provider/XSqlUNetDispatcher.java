package org.sqlunet.provider;

public class XSqlUNetDispatcher
{
	// table codes
	static protected final int PREDICATEMATRIX = 200;
	static protected final int PREDICATEMATRIX_VERBNET = 210;
	static protected final int PREDICATEMATRIX_PROPBANK = 220;
	static protected final int PREDICATEMATRIX_FRAMENET = 230;
	// join codes
	static protected final int WORDS_FNWORDS_PBWORDS_VNWORDS = 100;
	static protected final int WORDS_PBWORDS_VNWORDS = 101;
	static protected final int WORDS_VNWORDS_VNCLASSES = 310;
	static protected final int WORDS_VNWORDS_VNCLASSES_U = 311;
	static protected final int WORDS_PBWORDS_PBROLESETS = 320;
	static protected final int WORDS_PBWORDS_PBROLESETS_U = 321;
	static protected final int WORDS_FNWORDS_FNFRAMES_U = 331;
	static protected final int SOURCES = 400;

	public static Result queryMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		return null;
	}

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
}
