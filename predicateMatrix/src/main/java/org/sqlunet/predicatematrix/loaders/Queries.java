package org.sqlunet.predicatematrix.loaders;

import org.sqlunet.browser.Module;
import org.sqlunet.predicatematrix.provider.PredicateMatrixContract;

public class Queries
{
	public static Module.ContentProviderSql preparePmFromWord(final String word, final String sortBy)
	{
		final Module.ContentProviderSql providerSql = preparePm();
		providerSql.selection = PredicateMatrixContract.PredicateMatrix.WORD + "= ?";
		providerSql.selectionArgs = new String[]{word};
		providerSql.sortBy = sortBy;
		return providerSql;
	}

	public static Module.ContentProviderSql preparePmFromWordGrouped(final String word, final String sortBy)
	{
		final Module.ContentProviderSql providerSql = preparePm();
		providerSql.selection = PredicateMatrixContract.PredicateMatrix.WORD + "= ?";
		providerSql.selectionArgs = new String[]{word};
		providerSql.sortBy = sortBy;
		return providerSql;
	}

	public static Module.ContentProviderSql preparePmFromRoleId(final long pmRoleId, final String sortBy)
	{
		final Module.ContentProviderSql providerSql = preparePm();
		providerSql.selection = PredicateMatrixContract.PredicateMatrix.PMROLEID + "= ?";
		providerSql.selectionArgs = new String[]{Long.toString(pmRoleId)};
		providerSql.sortBy = sortBy;
		return providerSql;
	}

	private static Module.ContentProviderSql preparePm()
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = PredicateMatrixContract.Pm_X.URI;
		providerSql.projection = new String[]{ //
				PredicateMatrixContract.PredicateMatrix.PMID, //
				PredicateMatrixContract.PredicateMatrix.PMROLEID, //
				PredicateMatrixContract.PredicateMatrix.PMPREDICATEID, //
				PredicateMatrixContract.PredicateMatrix.PMPREDICATE, //
				PredicateMatrixContract.PredicateMatrix.PMROLE, //
				PredicateMatrixContract.AS_PMROLES + '.' + PredicateMatrixContract.PredicateMatrix.PMPOS, //

				PredicateMatrixContract.PredicateMatrix.WORD, //
				PredicateMatrixContract.PredicateMatrix.SYNSETID, //
				PredicateMatrixContract.Pm_X.DEFINITION, //

				PredicateMatrixContract.PredicateMatrix.VNCLASSID, //
				PredicateMatrixContract.Pm_X.VNCLASS, //
				PredicateMatrixContract.AS_VNROLETYPES + '.' + PredicateMatrixContract.Pm_X.VNROLETYPEID, //
				PredicateMatrixContract.Pm_X.VNROLETYPE, //

				PredicateMatrixContract.PredicateMatrix.PBROLESETID, //
				PredicateMatrixContract.Pm_X.PBROLESETNAME, //
				PredicateMatrixContract.Pm_X.PBROLESETDESCR, //
				PredicateMatrixContract.Pm_X.PBROLESETHEAD, //
				PredicateMatrixContract.Pm_X.PBROLEID, //
				PredicateMatrixContract.Pm_X.PBROLEDESCR, //
				PredicateMatrixContract.AS_PBARGS + '.' + PredicateMatrixContract.Pm_X.PBROLEARGTYPE, //
				PredicateMatrixContract.Pm_X.PBROLEARGTYPE, //

				PredicateMatrixContract.PredicateMatrix.FRAMEID, //
				PredicateMatrixContract.Pm_X.FRAME, //
				PredicateMatrixContract.Pm_X.FRAMEDEFINITION, //
				PredicateMatrixContract.Pm_X.LEXUNIT, //
				PredicateMatrixContract.Pm_X.LUDEFINITION, //
				PredicateMatrixContract.Pm_X.LUDICT, //
				PredicateMatrixContract.AS_FNFETYPES + '.' + PredicateMatrixContract.Pm_X.FETYPEID, //
				PredicateMatrixContract.Pm_X.FETYPE, //
				PredicateMatrixContract.Pm_X.FEABBREV, //
				PredicateMatrixContract.Pm_X.FEDEFINITION, //
		};
		return providerSql;
	}
}
