package org.sqlunet.syntagnet.provider;

import org.junit.Test;
import org.sqlunet.browser.Module;
import org.sqlunet.provider.SQLiteQueryBuilder;
import org.sqlunet.provider.XSqlUNetDispatcher;
import org.sqlunet.syntagnet.loaders.Queries;
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
		process(processor, Queries.prepareCollocation(0));
		process(processor, Queries.prepareCollocations(0L, 0L, 0L, 0L));
		process(processor, Queries.prepareCollocations(0));
		process(processor, Queries.prepareCollocations("w"));
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
		SyntagNetDispatcher.Result r = SyntagNetDispatcher.queryMain(code, null, providerSql.projection, providerSql.selection, providerSql.selectionArgs);
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
			case "collocations":
				return 10;
			case "collocations_x":
				return 100;
			default:
				throw new IllegalArgumentException("Illegal uri: " + providerUri);
		}
	}
}
