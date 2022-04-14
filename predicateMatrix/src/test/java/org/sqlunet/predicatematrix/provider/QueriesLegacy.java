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
			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			// J O I N S

			case PredicateMatrixDispatcher.PM:
				table = PredicateMatrixContract.Pm.TABLE;
				break;

			case PredicateMatrixDispatcher.PM_X:
				table = "pm " + //
						"LEFT JOIN pmroles AS " + PredicateMatrixContract.PMROLE + " USING (pmroleid) " + //
						"LEFT JOIN pmpredicates AS " + PredicateMatrixContract.PMPREDICATE + " USING (pmpredid) " + //
						"LEFT JOIN synsets USING (synsetid) " + //
						"LEFT JOIN vnclasses AS " + PredicateMatrixContract.VNCLASS + " ON vnclassid = " + PredicateMatrixContract.VNCLASS + ".classid " + //
						"LEFT JOIN vnroles AS " + PredicateMatrixContract.VNROLE + " ON vnroleid = " + PredicateMatrixContract.VNROLE + ".roleid " + //
						"LEFT JOIN vnroletypes AS " + PredicateMatrixContract.VNROLETYPE + " ON " + PredicateMatrixContract.VNROLE + ".roletypeid = " + PredicateMatrixContract.VNROLETYPE + ".roletypeid " + //
						"LEFT JOIN pbrolesets AS " + PredicateMatrixContract.PBROLESET + " ON pbrolesetid = " + PredicateMatrixContract.PBROLESET + ".rolesetid " + //
						"LEFT JOIN pbroles AS " + PredicateMatrixContract.PBROLE + " ON pbroleid = " + PredicateMatrixContract.PBROLE + ".roleid " + //
						"LEFT JOIN pbargns AS " + PredicateMatrixContract.PBARG + " ON " + PredicateMatrixContract.PBROLE + ".narg = " + PredicateMatrixContract.PBARG + ".narg " + //
						"LEFT JOIN fnframes AS " + PredicateMatrixContract.FNFRAME + " ON fnframeid = " + PredicateMatrixContract.FNFRAME + ".frameid " + //
						"LEFT JOIN fnfes AS " + PredicateMatrixContract.FNFE + " ON fnfeid = " + PredicateMatrixContract.FNFE + ".feid " + //
						"LEFT JOIN fnfetypes AS " + PredicateMatrixContract.FNFETYPE + " ON " + PredicateMatrixContract.FNFE + ".fetypeid = " + PredicateMatrixContract.FNFETYPE + ".fetypeid " + //
						"LEFT JOIN fnlexunits AS " + PredicateMatrixContract.FNLU + " ON fnluid = " + PredicateMatrixContract.FNLU + ".luid";
				break;
			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs, groupBy);
	}
}
