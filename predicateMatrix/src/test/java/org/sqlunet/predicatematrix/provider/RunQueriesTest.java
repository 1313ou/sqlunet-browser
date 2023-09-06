/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.predicatematrix.provider;

import org.junit.Test;
import org.sqlunet.browser.Module;
import org.sqlunet.predicatematrix.loaders.Queries;
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
		process(processor, Queries.preparePmFromRoleId(0,"s"));
		process(processor, Queries.preparePmFromWord("w", "s"));
		process(processor, Queries.preparePmFromWordGrouped("w", "s"));
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
		catch (Exception e)
		{
			System.err.println(providerSql);
			throw e;
		}
	}

	private static String toSql(final int code, final Module.ContentProviderSql providerSql)
	{
		PredicateMatrixControl.Result r = PredicateMatrixControl.queryMain(code, providerSql.projection, providerSql.selection, providerSql.selectionArgs);
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
			case PredicateMatrixContract.Pm.URI:
				return PredicateMatrixControl.PM;
			case PredicateMatrixContract.Pm_X.URI:
				return PredicateMatrixControl.PM_X;
			default:
				throw new IllegalArgumentException("Illegal uri: " + providerUri);
		}
	}
}
