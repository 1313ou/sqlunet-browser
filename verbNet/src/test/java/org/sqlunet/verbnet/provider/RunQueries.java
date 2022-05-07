package org.sqlunet.verbnet.provider;

import org.junit.Test;
import org.sqlunet.browser.Module;
import org.sqlunet.provider.SQLiteQueryBuilder;
import org.sqlunet.provider.XSqlUNetDispatcher;
import org.sqlunet.test.SqlProcessor;
import org.sqlunet.verbnet.loaders.Queries;

import java.sql.SQLException;

public class RunQueries
{
	@Test
	public void runQueries() throws SQLException
	{
		String db = System.getProperty("db");
		System.out.println(db);
		final SqlProcessor processor = new SqlProcessor(db);
		process(processor, Queries.prepareVnClass(0));
		process(processor, Queries.prepareVnClasses(0, null));
		process(processor, Queries.prepareVnClasses(0, 0L));
		process(processor, Queries.prepareVnRoles(0));
		process(processor, Queries.prepareVnMembers(0));
		process(processor, Queries.prepareVnFrames(0));
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
		VerbNetDispatcher.Result r = VerbNetDispatcher.queryMain(code, null, providerSql.projection, providerSql.selection, providerSql.selectionArgs);
		if (r == null)
		{
			// TEXTSEARCH
			r = VerbNetDispatcher.querySearch(code, providerSql.projection, providerSql.selection, providerSql.selectionArgs);
		}
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
			case "vnclass1":
				return 10;
			case "vnclasses":
			case "vn_classes":
				return 11;
			case "vnclasses_x_by_vnclass":
				return 20;
			case "words_vnclasses":
				return 100;
			case "vnclasses_vnmembers_x_by_word":
				return 110;
			case "vnclasses_vnroles_x_by_vnrole":
				return 120;
			case "vnclasses_vnframes_x_by_vnframe":
				return 130;
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
