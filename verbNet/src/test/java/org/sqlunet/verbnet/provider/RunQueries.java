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
			case VerbNetContract.VnClasses.CONTENT_URI_TABLE1:
				return 10;
			case VerbNetContract.VnClasses.CONTENT_URI_TABLE:
				return 11;
			case VerbNetContract.VnClasses_X.CONTENT_URI_TABLE_BY_VN_CLASS:
				return 20;
			case VerbNetContract.Words_VnClasses.CONTENT_URI_TABLE:
				return 100;
			case VerbNetContract.VnClasses_VnMembers_X.CONTENT_URI_TABLE:
				return 110;
			case VerbNetContract.VnClasses_VnRoles_X.CONTENT_URI_TABLE:
				return 120;
			case VerbNetContract.VnClasses_VnFrames_X.CONTENT_URI_TABLE:
				return 130;
			case VerbNetContract.Lookup_VnExamples.CONTENT_URI_TABLE:
				return 501;
			case VerbNetContract.Lookup_VnExamples_X.CONTENT_URI_TABLE:
				return 511;
			case VerbNetContract.Lookup_VnExamples_X.CONTENT_URI_TABLE_BY_EXAMPLE:
				return 512;
			case VerbNetContract.Suggest_VnWords.SEARCH_WORD_PATH:
				return 601;
			case VerbNetContract.Suggest_FTS_VnWords.SEARCH_WORD_PATH:
				return 602;
			default:
				throw new IllegalArgumentException("Illegal uri: " + providerUri);
		}
	}
}
