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
			case "pbroleset":
				return 10;
			case "pbrolesets":
				return 11;
			case "pbrolesets_x":
				return 100;
			case "pbrolesets_x_by_roleset":
				return 101;
			case "words_pbrolesets":
				return 110;
			case "pbrolesets_pbroles":
				return 120;
			case "pbrolesets_pbexamples":
				return 130;
			case "pbrolesets_pbexamples_by_example":
				return 131;
			case "lookup_fts_examples":
				return 501;
			case "lookup_fts_examples_x":
				return 511;
			case "lookup_fts_examples_x_by_example":
				return 512;
			case "suggest_words":
				return 601;
			case "suggest_fts_words":
				return 602;
			default:
				throw new IllegalArgumentException("Illegal uri: " + providerUri);
		}
	}
}
