/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.propbank.loaders;

import org.sqlunet.browser.Module;
import org.sqlunet.propbank.provider.PropBankContract;

public class Queries
{
	public static Module.ContentProviderSql prepareRoleSet(final long roleSetId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = PropBankContract.PbRoleSets_X.URI;
		providerSql.projection = new String[]{ //
				PropBankContract.PbRoleSets_X.ROLESETID, //
				PropBankContract.PbRoleSets_X.ROLESETNAME, //
				PropBankContract.PbRoleSets_X.ROLESETHEAD, //
				PropBankContract.PbRoleSets_X.ROLESETDESC, //
				"GROUP_CONCAT(" + PropBankContract.PbRoleSets_X.WORD + ") AS " + PropBankContract.PbRoleSets_X.ALIASES};
		providerSql.selection = PropBankContract.PbRoleSets_X.ROLESETID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(roleSetId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareRoleSets(final long wordId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = PropBankContract.Words_PbRoleSets.URI;
		providerSql.projection = new String[]{ //
				PropBankContract.Words_PbRoleSets.ROLESETID, //
				PropBankContract.Words_PbRoleSets.ROLESETNAME, //
				PropBankContract.Words_PbRoleSets.ROLESETHEAD, //
				PropBankContract.Words_PbRoleSets.ROLESETDESC, //
		};
		providerSql.selection = PropBankContract.Words_PbRoleSets.WORDID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(wordId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareRoles(final int roleSetId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = PropBankContract.PbRoleSets_PbRoles.URI;
		providerSql.projection = new String[]{ //
				PropBankContract.PbRoleSets_PbRoles.ROLEID, //
				PropBankContract.PbRoleSets_PbRoles.ROLEDESCR, //
				PropBankContract.PbRoleSets_PbRoles.ARGTYPE, //
				PropBankContract.PbRoleSets_PbRoles.FUNC, //
				PropBankContract.PbRoleSets_PbRoles.THETA, //
		};
		providerSql.selection = PropBankContract.PbRoleSets_PbRoles.ROLESETID + "= ?";
		providerSql.selectionArgs = new String[]{Long.toString(roleSetId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareExamples(final int roleSetId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = PropBankContract.PbRoleSets_PbExamples.URI;
		providerSql.projection = new String[]{ //
				PropBankContract.PbRoleSets_PbExamples.TEXT, //
				PropBankContract.PbRoleSets_PbExamples.REL, //
				"GROUP_CONCAT(" + //
						PropBankContract.PbRoleSets_PbExamples.ARGTYPE + //
						"||'~'" + //
						"||(CASE WHEN " + PropBankContract.PbRoleSets_PbExamples.FUNCNAME + " IS NULL THEN '*' ELSE " + PropBankContract.PbRoleSets_PbExamples.FUNCNAME + " END)" + //
						"||'~'" + //
						"||" + PropBankContract.PbRoleSets_PbExamples.ROLEDESCR + //
						"||'~'" + //
						"||(CASE WHEN " + PropBankContract.PbRoleSets_PbExamples.THETA + " IS NULL THEN '*' ELSE " + PropBankContract.PbRoleSets_PbExamples.THETA + " END)" + //
						"||'~'" + //
						"||" + PropBankContract.PbRoleSets_PbExamples.ARG + ",'|') AS " + PropBankContract.PbRoleSets_PbExamples.ARGS, //
				PropBankContract.PbRoleSets_PbExamples.ASPECT, //
				PropBankContract.PbRoleSets_PbExamples.FORM, //
				PropBankContract.PbRoleSets_PbExamples.TENSE, //
				PropBankContract.PbRoleSets_PbExamples.VOICE, //
				PropBankContract.PbRoleSets_PbExamples.PERSON, //
		};
		providerSql.selection = PropBankContract.PbRoleSets_PbExamples.ROLESETID + "= ?";
		providerSql.selectionArgs = new String[]{Long.toString(roleSetId)};
		providerSql.sortBy = PropBankContract.PbRoleSets_PbExamples.EXAMPLEID + ',' + PropBankContract.PbRoleSets_PbExamples.ARGTYPE;
		return providerSql;
	}
}
