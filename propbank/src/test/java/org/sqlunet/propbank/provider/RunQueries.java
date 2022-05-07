package org.sqlunet.propbank.provider;

import org.junit.Test;
import org.sqlunet.browser.Module;
import org.sqlunet.propbank.loaders.Queries;
import org.sqlunet.provider.SQLiteQueryBuilder;
import org.sqlunet.provider.XSqlUNetDispatcher;
import org.sqlunet.test.SqlProcessor;

import java.sql.SQLException;

public class RunQueries
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
		PropBankDispatcher.Result r = PropBankDispatcher.queryMain(code, null, providerSql.projection, providerSql.selection, providerSql.selectionArgs);
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
			case PropBankContract.PbRoleSets.CONTENT_URI_TABLE1:
				return 10;
			case PropBankContract.PbRoleSets.CONTENT_URI_TABLE:
				return 11;
			case PropBankContract.PbRoleSets_X.CONTENT_URI_TABLE:
				return 100;
			case PropBankContract.PbRoleSets_X.CONTENT_URI_TABLE_BY_ROLESET:
				return 101;
			case PropBankContract.Words_PbRoleSets.CONTENT_URI_TABLE:
				return 110;
			case PropBankContract.PbRoleSets_PbRoles.CONTENT_URI_TABLE:
				return 120;
			case PropBankContract.PbRoleSets_PbExamples.CONTENT_URI_TABLE:
				return 130;
			case PropBankContract.PbRoleSets_PbExamples.CONTENT_URI_TABLE_BY_EXAMPLE:
				return 131;
			case PropBankContract.Lookup_PbExamples.CONTENT_URI_TABLE:
				return 501;
			case PropBankContract.Lookup_PbExamples_X.CONTENT_URI_TABLE:
				return 511;
			case PropBankContract.Lookup_PbExamples_X.CONTENT_URI_TABLE_BY_EXAMPLE:
				return 512;
			case PropBankContract.Suggest_PbWords.SEARCH_WORD_PATH:
				return 601;
			case PropBankContract.Suggest_FTS_PbWords.SEARCH_WORD_PATH:
				return 602;
			default:
				throw new IllegalArgumentException("Illegal uri: " + providerUri);
		}
	}
}
