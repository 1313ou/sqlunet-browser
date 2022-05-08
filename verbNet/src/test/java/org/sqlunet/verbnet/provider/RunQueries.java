package org.sqlunet.verbnet.provider;

import org.junit.Test;
import org.sqlunet.browser.Module;
import org.sqlunet.provider.SQLiteQueryBuilder;
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
		catch (Exception e)
		{
			System.err.println(providerSql);
			throw e;
		}
	}

	private static String toSql(final int code, final Module.ContentProviderSql providerSql)
	{
		VerbNetControl.Result r = VerbNetControl.queryMain(code, null, providerSql.projection, providerSql.selection, providerSql.selectionArgs);
		if (r == null)
		{
			// TEXTSEARCH
			r = VerbNetControl.querySearch(code, providerSql.projection, providerSql.selection, providerSql.selectionArgs);
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
				return VerbNetControl.VNCLASS1;
			case VerbNetContract.VnClasses.CONTENT_URI_TABLE:
				return VerbNetControl.VNCLASSES;
			case VerbNetContract.VnClasses_X.CONTENT_URI_TABLE_BY_VN_CLASS:
				return VerbNetControl.VNCLASSES_X_BY_VNCLASS;
			case VerbNetContract.Words_VnClasses.CONTENT_URI_TABLE:
				return VerbNetControl.WORDS_VNCLASSES;
			case VerbNetContract.VnClasses_VnMembers_X.CONTENT_URI_TABLE:
				return VerbNetControl.VNCLASSES_VNMEMBERS_X_BY_WORD;
			case VerbNetContract.VnClasses_VnRoles_X.CONTENT_URI_TABLE:
				return VerbNetControl.VNCLASSES_VNROLES_X_BY_VNROLE;
			case VerbNetContract.VnClasses_VnFrames_X.CONTENT_URI_TABLE:
				return VerbNetControl.VNCLASSES_VNFRAMES_X_BY_VNFRAME;
			case VerbNetContract.Lookup_VnExamples.CONTENT_URI_TABLE:
				return VerbNetControl.LOOKUP_FTS_EXAMPLES;
			case VerbNetContract.Lookup_VnExamples_X.CONTENT_URI_TABLE:
				return VerbNetControl.LOOKUP_FTS_EXAMPLES_X;
			case VerbNetContract.Lookup_VnExamples_X.CONTENT_URI_TABLE_BY_EXAMPLE:
				return VerbNetControl.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE;
			case VerbNetContract.Suggest_VnWords.SEARCH_WORD_PATH:
				return VerbNetControl.SUGGEST_WORDS;
			case VerbNetContract.Suggest_FTS_VnWords.SEARCH_WORD_PATH:
				return VerbNetControl.SUGGEST_FTS_WORDS;
			default:
				throw new IllegalArgumentException("Illegal uri: " + providerUri);
		}
	}
}
