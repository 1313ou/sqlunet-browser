/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.loaders

import org.sqlunet.browser.Module.ContentProviderSql
import org.sqlunet.propbank.provider.PropBankContract

object Queries {

    fun prepareRoleSet(roleSetId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = PropBankContract.PbRoleSets_X.URI_BY_ROLESET
        providerSql.projection = arrayOf(
            PropBankContract.PbRoleSets_X.ROLESETID,
            PropBankContract.PbRoleSets_X.ROLESETNAME,
            PropBankContract.PbRoleSets_X.ROLESETHEAD,
            PropBankContract.PbRoleSets_X.ROLESETDESC,
            "GROUP_CONCAT(DISTINCT " + PropBankContract.PbRoleSets_X.WORD + ") AS " + PropBankContract.PbRoleSets_X.ALIASES
        )
        providerSql.selection = PropBankContract.PbRoleSets_X.ROLESETID + " = ?"
        providerSql.selectionArgs = arrayOf(roleSetId.toString())
        return providerSql
    }

    fun prepareRoleSets(wordId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = PropBankContract.Words_PbRoleSets.URI
        providerSql.projection = arrayOf(
            PropBankContract.Words_PbRoleSets.ROLESETID,
            PropBankContract.Words_PbRoleSets.ROLESETNAME,
            PropBankContract.Words_PbRoleSets.ROLESETHEAD,
            PropBankContract.Words_PbRoleSets.ROLESETDESC
        )
        providerSql.selection = PropBankContract.Words_PbRoleSets.WORDID + " = ?"
        providerSql.selectionArgs = arrayOf(wordId.toString())
        return providerSql
    }

    fun prepareRoles(roleSetId: Int): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = PropBankContract.PbRoleSets_PbRoles.URI
        providerSql.projection = arrayOf(
            PropBankContract.PbRoleSets_PbRoles.ROLEID,
            PropBankContract.PbRoleSets_PbRoles.ROLEDESCR,
            PropBankContract.PbRoleSets_PbRoles.ARGTYPE,
            PropBankContract.PbRoleSets_PbRoles.FUNC,
            PropBankContract.PbRoleSets_PbRoles.VNROLE
        )
        providerSql.selection = PropBankContract.PbRoleSets_PbRoles.ROLESETID + "= ?"
        providerSql.selectionArgs = arrayOf(roleSetId.toString())
        return providerSql
    }

    fun prepareExamples(roleSetId: Int): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = PropBankContract.PbRoleSets_PbExamples.URI_BY_EXAMPLE
        providerSql.projection = arrayOf(
            PropBankContract.PbRoleSets_PbExamples.TEXT,
            PropBankContract.PbRoleSets_PbExamples.REL,
            "REPLACE(REPLACE(GROUP_CONCAT(DISTINCT REPLACE(" +
                    PropBankContract.PbRoleSets_PbExamples.ARGTYPE +
                    "||'~'" +
                    "||(CASE WHEN " + PropBankContract.PbRoleSets_PbExamples.FUNCNAME + " IS NULL THEN '*' ELSE " + PropBankContract.PbRoleSets_PbExamples.FUNCNAME + " END)" +
                    "||'~'" +
                    "||" + PropBankContract.PbRoleSets_PbExamples.ROLEDESCR +
                    "||'~'" +
                    "||(CASE WHEN " + PropBankContract.PbRoleSets_PbExamples.VNROLE + " IS NULL THEN '*' ELSE " + PropBankContract.PbRoleSets_PbExamples.VNROLE + " END)" +
                    "||'~'" +
                    "||" + PropBankContract.PbRoleSets_PbExamples.ARG +
                    ",',','#')),',','|'),'#',',') AS " + PropBankContract.PbRoleSets_PbExamples.ARGS,
            // PropBankContract.PbRoleSets_PbExamples.ASPECT,
            // PropBankContract.PbRoleSets_PbExamples.FORM,
            // PropBankContract.PbRoleSets_PbExamples.TENSE,
            // PropBankContract.PbRoleSets_PbExamples.VOICE,
            // PropBankContract.PbRoleSets_PbExamples.PERSON
        )
        providerSql.selection = PropBankContract.PbRoleSets_PbExamples.ROLESETID + "= ?"
        providerSql.selectionArgs = arrayOf(roleSetId.toString())
        providerSql.sortBy = PropBankContract.PbRoleSets_PbExamples.EXAMPLEID + ',' + PropBankContract.PbRoleSets_PbExamples.ARGTYPE
        return providerSql
    }
}
