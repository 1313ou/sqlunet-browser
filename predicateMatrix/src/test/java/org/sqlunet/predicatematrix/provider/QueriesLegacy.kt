/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.predicatematrix.provider

object QueriesLegacy {

    fun queryLegacy(code: Int, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): PredicateMatrixControl.Result? {
        return queryLegacyMain(code, projection0, selection0, selectionArgs0)
    }

    private fun queryLegacyMain(code: Int, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): PredicateMatrixControl.Result? {
        val groupBy: String? = null
        val table: String = when (code) {
            PredicateMatrixControl.PM -> PredicateMatrixContract.Pm.TABLE
            PredicateMatrixControl.PM_X ->
                "pm_pms " +
                        "LEFT JOIN pm_roles AS " + PredicateMatrixContract.AS_PMROLES + " USING (pmroleid) " +
                        "LEFT JOIN pm_predicates AS " + PredicateMatrixContract.AS_PMPREDICATES + " USING (predicateid) " +
                        "LEFT JOIN synsets USING (synsetid) " +
                        "LEFT JOIN vn_classes AS " + PredicateMatrixContract.AS_VNCLASSES + " ON vnclassid = " + PredicateMatrixContract.AS_VNCLASSES + ".classid " +
                        "LEFT JOIN vn_roles AS " + PredicateMatrixContract.AS_VNROLES + " ON vnroleid = " + PredicateMatrixContract.AS_VNROLES + ".roleid " +
                        "LEFT JOIN vn_roletypes AS " + PredicateMatrixContract.AS_VNROLETYPES + " ON " + PredicateMatrixContract.AS_VNROLES + ".roletypeid = " + PredicateMatrixContract.AS_VNROLETYPES + ".roletypeid " +
                        "LEFT JOIN pb_rolesets AS " + PredicateMatrixContract.AS_PBROLESETS + " ON pbrolesetid = " + PredicateMatrixContract.AS_PBROLESETS + ".rolesetid " +
                        "LEFT JOIN pb_roles AS " + PredicateMatrixContract.AS_PBROLES + " ON pbroleid = " + PredicateMatrixContract.AS_PBROLES + ".roleid " +
                        "LEFT JOIN pb_argtypes AS " + PredicateMatrixContract.AS_PBARGS + " ON " + PredicateMatrixContract.AS_PBROLES + ".argtypeid = " + PredicateMatrixContract.AS_PBARGS + ".argtypeid " +
                        "LEFT JOIN fn_frames AS " + PredicateMatrixContract.AS_FNFRAMES + " ON fnframeid = " + PredicateMatrixContract.AS_FNFRAMES + ".frameid " +
                        "LEFT JOIN fn_fes AS " + PredicateMatrixContract.AS_FNFES + " ON fnfeid = " + PredicateMatrixContract.AS_FNFES + ".feid " +
                        "LEFT JOIN fn_fetypes AS " + PredicateMatrixContract.AS_FNFETYPES + " ON " + PredicateMatrixContract.AS_FNFES + ".fetypeid = " + PredicateMatrixContract.AS_FNFETYPES + ".fetypeid"
                        // + "LEFT JOIN fn_lexunits AS " + PredicateMatrixContract.AS_FNLUS + " ON fnluid = " + PredicateMatrixContract.AS_FNLUS + ".luid"
            else -> return null
        }
        return PredicateMatrixControl.Result(table, projection0, selection0, selectionArgs0, groupBy)
    }
}
