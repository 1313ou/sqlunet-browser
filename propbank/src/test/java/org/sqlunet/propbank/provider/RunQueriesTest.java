/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.propbank.provider;

import org.junit.Test;
import org.sqlunet.browser.Module;
import org.sqlunet.propbank.loaders.Queries;
import org.sqlunet.provider.SQLiteQueryBuilder;
import org.sqlunet.test.SqlProcessor;

import java.sql.SQLException;

public class RunQueriesTest
{
	@Test
	public void runQueries() throws SQLException
	{
		String db = System.getProperty("db");
		System.out.println(db);
		final SqlProcessor processor = new SqlProcessor(db);
		process(processor, Queries.prepareRoleSet(0));
		process(processor, Queries.prepareRoleSets(0));
		process(processor, Queries.prepareRoles(0));
		process(processor, Queries.prepareExamples(0));
	}

	private void process(final SqlProcessor processor, final Module.ContentProviderSql providerSql) throws SQLException
	{
		System.out.println("URI: " + providerSql.providerUri);
		final int code = uriToCode(providerSql.providerUri);
		final String sql = toSql(code, providerSql);
		try
		{
			processor.process(sql);
		}
		catch(Exception e)
		{
			System.err.println(providerSql);
			throw e;
		}
	}

	private static String toSql(final int code, final Module.ContentProviderSql providerSql)
	{
		PropBankControl.Result r = PropBankControl.queryMain(code, null, providerSql.projection, providerSql.selection, providerSql.selectionArgs);
		if (r == null)
		{
			throw new IllegalArgumentException("Illegal query code: " + code);
		}
		return SQLiteQueryBuilder.buildQueryString(false, r.table, r.projection, r.selection, r.groupBy, null, null, null);
	}

	private int uriToCode(final String providerUri)
	{
		switch (providerUri)
		{
			case PropBankContract.PbRoleSets.URI1:
				return PropBankControl.PBROLESET;
			case PropBankContract.PbRoleSets.URI:
				return PropBankControl.PBROLESETS;
			case PropBankContract.PbRoleSets_X.URI:
				return PropBankControl.PBROLESETS_X;
			case PropBankContract.PbRoleSets_X.URI_BY_ROLESET:
				return PropBankControl.PBROLESETS_X_BY_ROLESET;
			case PropBankContract.Words_PbRoleSets.URI:
				return PropBankControl.WORDS_PBROLESETS;
			case PropBankContract.PbRoleSets_PbRoles.URI:
				return PropBankControl.PBROLESETS_PBROLES;
			case PropBankContract.PbRoleSets_PbExamples.URI:
				return PropBankControl.PBROLESETS_PBEXAMPLES;
			case PropBankContract.PbRoleSets_PbExamples.URI_BY_EXAMPLE:
				return PropBankControl.PBROLESETS_PBEXAMPLES_BY_EXAMPLE;
			case PropBankContract.Lookup_PbExamples.URI:
				return PropBankControl.LOOKUP_FTS_EXAMPLES;
			case PropBankContract.Lookup_PbExamples_X.URI:
				return PropBankControl.LOOKUP_FTS_EXAMPLES_X;
			case PropBankContract.Lookup_PbExamples_X.URI_BY_EXAMPLE:
				return PropBankControl.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE;
			case PropBankContract.Suggest_PbWords.SEARCH_WORD_PATH:
				return PropBankControl.SUGGEST_WORDS;
			case PropBankContract.Suggest_FTS_PbWords.SEARCH_WORD_PATH:
				return PropBankControl.SUGGEST_FTS_WORDS;
			default:
				throw new IllegalArgumentException("Illegal uri: " + providerUri);
		}
	}
}
