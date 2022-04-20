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
			// T A B L E

			case PredicateMatrixDispatcher.PM:
				table = PredicateMatrixContract.Pm.TABLE;
				break;

			// J O I N S

			case PredicateMatrixDispatcher.PM_X:
				table = "pm_pms " + //
						"LEFT JOIN pm_roles AS " + PredicateMatrixContract.PMROLE + " USING (pmroleid) " + //
						"LEFT JOIN pm_predicates AS " + PredicateMatrixContract.PMPREDICATE + " USING (predid) " + //
						"LEFT JOIN synsets USING (synsetid) " + //

						"LEFT JOIN vn_classes AS " + PredicateMatrixContract.VNCLASS + " ON vnclassid = " + PredicateMatrixContract.VNCLASS + ".vnclassid " + //
						"LEFT JOIN vn_roles AS " + PredicateMatrixContract.VNROLE + " ON vnroleid = " + PredicateMatrixContract.VNROLE + ".vnroleid " + //
						"LEFT JOIN vn_roletypes AS " + PredicateMatrixContract.VNROLETYPE + " ON vnroletypeid = " + PredicateMatrixContract.VNROLETYPE + ".vnroletypeid " + //

						"LEFT JOIN pb_rolesets AS " + PredicateMatrixContract.PBROLESET + " ON pbrolesetid = " + PredicateMatrixContract.PBROLESET + ".pbrolesetid " + //
						"LEFT JOIN pb_roles AS " + PredicateMatrixContract.PBROLE + " ON pbroleid = " + PredicateMatrixContract.PBROLE + ".pbroleid " + //
						"LEFT JOIN pb_argns AS " + PredicateMatrixContract.PBARG + " ON " + PredicateMatrixContract.PBROLE + ".nargid = " + PredicateMatrixContract.PBARG + ".nargid " + //

						"LEFT JOIN fn_frames AS " + PredicateMatrixContract.FNFRAME + " ON fnframeid = " + PredicateMatrixContract.FNFRAME + ".fnframeid " + //
						"LEFT JOIN fn_fes AS " + PredicateMatrixContract.FNFE + " ON fnfeid = " + PredicateMatrixContract.FNFE + ".fnfeid " + //
						"LEFT JOIN fn_fetypes AS " + PredicateMatrixContract.FNFETYPE + " ON " + PredicateMatrixContract.FNFE + ".fnfetypeid = " + PredicateMatrixContract.FNFETYPE + ".fnfetypeid " + //
						"LEFT JOIN fn_lexunits AS " + PredicateMatrixContract.FNLU + " ON fnluid = " + PredicateMatrixContract.FNLU + ".fnluid";
				break;

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs, groupBy);
	}
}
