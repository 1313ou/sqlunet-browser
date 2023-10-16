/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.syntagnet.provider;

import org.junit.Test;
import org.sqlunet.browser.Module;
import org.sqlunet.provider.SQLiteQueryBuilder;
import org.sqlunet.syntagnet.loaders.Queries;
import org.sqlunet.test.SqlProcessor;

import java.sql.SQLException;

import androidx.annotation.NonNull;

public class RunQueriesTest
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
		process(processor, Queries.prepareSnSelect(0));
	}

	private void process(@NonNull final SqlProcessor processor, @NonNull final Module.ContentProviderSql providerSql) throws SQLException
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

	@NonNull
	private static String toSql(final int code, @NonNull final Module.ContentProviderSql providerSql)
	{
		SyntagNetControl.Result r = SyntagNetControl.queryMain(code, null, providerSql.projection, providerSql.selection, providerSql.selectionArgs);
		if (r == null)
		{
			throw new IllegalArgumentException("Illegal query code: " + code);
		}
		return SQLiteQueryBuilder.buildQueryString(false, r.table, r.projection, r.selection, r.groupBy, null, null, null);
	}

	private int uriToCode(@NonNull final String providerUri)
	{
		switch (providerUri)
		{
			case SyntagNetContract.SnCollocations.URI:
				return SyntagNetControl.COLLOCATIONS;
			case SyntagNetContract.SnCollocations_X.URI:
				return SyntagNetControl.COLLOCATIONS_X;
			default:
				throw new IllegalArgumentException("Illegal uri: " + providerUri);
		}
	}
}
