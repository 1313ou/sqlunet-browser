/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.bnc.provider;

import org.junit.Test;
import org.sqlunet.bnc.loaders.Queries;
import org.sqlunet.browser.Module;
import org.sqlunet.provider.SQLiteQueryBuilder;
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
		process(processor, Queries.prepareBnc(0, 'n'));
		process(processor, Queries.prepareBnc(0, null));
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
		BNCControl.Result r = BNCControl.queryMain(code, null, providerSql.projection, providerSql.selection, providerSql.selectionArgs);
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
			case BNCContract.BNCs.URI:
				return  BNCControl.BNC;
			case BNCContract.Words_BNCs.URI:
				return BNCControl.WORDS_BNC;
			default:
				throw new IllegalArgumentException("Illegal uri: " + providerUri);
		}
	}
}
