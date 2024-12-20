/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.predicatematrix.provider

/**
 * PredicateMatrix provider contract
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object PredicateMatrixContract {

    // A L I A S E S

    const val AS_PMROLES = V.AS_PMROLES
    const val AS_PMPREDICATES = V.AS_PMPREDICATES
    const val AS_VNCLASSES = V.AS_VNCLASSES
    const val AS_VNROLES = V.AS_VNROLES
    const val AS_VNROLETYPES = V.AS_VNROLETYPES
    const val AS_PBROLESETS = V.AS_PBROLESETS
    const val AS_PBROLES = V.AS_PBROLES
    const val AS_PBARGS = V.AS_PBARGS
    const val AS_FNFRAMES = V.AS_FNFRAMES
    const val AS_FNFES = V.AS_FNFES
    const val AS_FNFETYPES = V.AS_FNFETYPES
    // const val AS_FNLUS = V.AS_FNLUS

    object PredicateMatrix {

        const val PMID = V.PMID
        const val PMROLEID = V.PMROLEID
        const val PMPREDICATEID = V.PREDICATEID
        const val PMPREDICATE = V.PREDICATE
        const val PMROLE = V.ROLE
        const val PMPOS = V.POS
        const val PMWSOURCE = V.WSOURCE
        const val WORD = V.WORD
        const val WORDID = V.WORDID
        const val SYNSETID = V.SYNSETID
        const val VNWORDID = V.VNWORDID
        const val VNCLASSID = V.VN_CLASSID
        const val VNROLEID = V.VN_ROLEID
        const val PBWORDID = V.PBWORDID
        const val PBROLESETID = V.PB_ROLESETID
        const val PBROLEID = V.PB_ROLEID
        const val FNWORDID = V.FNWORDID
        const val FNFRAMEID = V.FN_FRAMEID
        const val FNFEID = V.FN_FEID
        // const val FNLUID = V.FN_LUID
    }

    object Pm { // : PredicateMatrix
        const val TABLE = "pm_pms"
        const val URI = TABLE
    }

    object Pm_X { // : PredicateMatrix
        const val URI = "pm_x"
        const val DEFINITION = V.DEFINITION
        const val VNCLASS = V.CLASS
        const val VNROLETYPEID = V.VNROLETYPEID
        const val VNROLETYPE = V.VNROLETYPE
        const val PBROLESETNAME = V.PBROLESETNAME
        const val PBROLESETDESCR = V.PBROLESETDESCR
        const val PBROLESETHEAD = V.PBROLESETHEAD
        const val PBROLEDESCR = V.PBROLEDESCR
        const val PBROLEARGTYPE = V.PBARGTYPE
        const val FNFETYPEID = V.FNFETYPEID
        const val FNFRAME = V.FNFRAME
        const val FNFRAMEDEFINITION = V.FNFRAMEDEFINITION
        const val FNFETYPE = V.FNFETYPE
        const val FNFEABBREV = V.FNFEABBREV
        const val FNFEDEFINITION = V.FNFEDEFINITION
        const val PBROLEID = PredicateMatrix.PBROLEID
        // const val FNLEXUNIT = V.FNLEXUNIT
        // const val FNLUDEFINITION = V.FNLUDEFINITION
        // const val FNLUDICT = V.FNLUDICT
    }
}
