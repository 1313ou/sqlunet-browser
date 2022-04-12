package org.sqlunet.predicatematrix.provider;

import org.sqlunet.predicatematrix.provider.PredicateMatrixDispatcher.Result;

public class QueriesLegacy
{
	public static Result queryLegacy(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		Result r = queryLegacyMain(code, uriLast, projection0, selection0, selectionArgs0);
		return r;
	}

	public static Result queryLegacyMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table = null;
		String[] projection = projection0;
		String selection = selection0;
		String[] selectionArgs = selectionArgs0;
		String groupBy = null;

		switch (code)
		{
			case 0:
				break;
			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs, groupBy);
	}
}
