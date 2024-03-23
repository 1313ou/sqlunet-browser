/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.predicatematrix.loaders

import org.sqlunet.browser.Module.ContentProviderSql
import org.sqlunet.predicatematrix.provider.PredicateMatrixContract

object Queries {

    @JvmStatic
    fun preparePmFromWord(word: String, sortBy: String?): ContentProviderSql {
        val providerSql = preparePm()
        providerSql.selection = PredicateMatrixContract.PredicateMatrix.WORD + "= ?"
        providerSql.selectionArgs = arrayOf(word)
        providerSql.sortBy = sortBy
        return providerSql
    }

    @JvmStatic
    fun preparePmFromWordGrouped(word: String, sortBy: String?): ContentProviderSql {
        val providerSql = preparePm()
        providerSql.selection = PredicateMatrixContract.PredicateMatrix.WORD + "= ?"
        providerSql.selectionArgs = arrayOf(word)
        providerSql.sortBy = sortBy
        return providerSql
    }

    @JvmStatic
    fun preparePmFromRoleId(pmRoleId: Long, sortBy: String?): ContentProviderSql {
        val providerSql = preparePm()
        providerSql.selection = PredicateMatrixContract.PredicateMatrix.PMROLEID + "= ?"
        providerSql.selectionArgs = arrayOf(pmRoleId.toString())
        providerSql.sortBy = sortBy
        return providerSql
    }

    private fun preparePm(): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = PredicateMatrixContract.Pm_X.URI
        providerSql.projection = arrayOf( //
            PredicateMatrixContract.PredicateMatrix.PMID,  //
            PredicateMatrixContract.PredicateMatrix.PMROLEID,  //
            PredicateMatrixContract.PredicateMatrix.PMPREDICATEID,  //
            PredicateMatrixContract.PredicateMatrix.PMPREDICATE,  //
            PredicateMatrixContract.PredicateMatrix.PMROLE,  //
            PredicateMatrixContract.AS_PMROLES + '.' + PredicateMatrixContract.PredicateMatrix.PMPOS,  //
            PredicateMatrixContract.PredicateMatrix.WORD,  //
            PredicateMatrixContract.PredicateMatrix.SYNSETID,  //
            PredicateMatrixContract.Pm_X.DEFINITION,  //
            PredicateMatrixContract.PredicateMatrix.VNCLASSID,  //
            PredicateMatrixContract.Pm_X.VNCLASS,  //
            PredicateMatrixContract.AS_VNROLETYPES + '.' + PredicateMatrixContract.Pm_X.VNROLETYPEID,  //
            PredicateMatrixContract.Pm_X.VNROLETYPE,  //
            PredicateMatrixContract.PredicateMatrix.PBROLESETID,  //
            PredicateMatrixContract.Pm_X.PBROLESETNAME,  //
            PredicateMatrixContract.Pm_X.PBROLESETDESCR,  //
            PredicateMatrixContract.Pm_X.PBROLESETHEAD,  //
            PredicateMatrixContract.Pm_X.PBROLEID,  //
            PredicateMatrixContract.Pm_X.PBROLEDESCR,  //
            PredicateMatrixContract.AS_PBARGS + '.' + PredicateMatrixContract.Pm_X.PBROLEARGTYPE,  //
            PredicateMatrixContract.Pm_X.PBROLEARGTYPE,  //
            PredicateMatrixContract.PredicateMatrix.FNFRAMEID,  //
            PredicateMatrixContract.Pm_X.FNFRAME,  //
            PredicateMatrixContract.Pm_X.FNFRAMEDEFINITION,  //
            PredicateMatrixContract.AS_FNFETYPES + '.' + PredicateMatrixContract.Pm_X.FNFETYPEID,  //
            PredicateMatrixContract.Pm_X.FNFETYPE,  //
            PredicateMatrixContract.Pm_X.FNFEABBREV,  //
            PredicateMatrixContract.Pm_X.FNFEDEFINITION
        )
        return providerSql
    }
}
