/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.verbnet.provider;

import org.junit.Test;
import org.sqlunet.browser.Module;
import org.sqlunet.provider.SQLiteQueryBuilder;
import org.sqlunet.test.SqlProcessor;
import org.sqlunet.verbnet.loaders.Queries;

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
		process(processor, Queries.prepareVnClass(0));
		process(processor, Queries.prepareVnClasses(0, null));
		process(processor, Queries.prepareVnClasses(0, 0L));
		process(processor, Queries.prepareVnRoles(0));
		process(processor, Queries.prepareVnMembers(0));
		process(processor, Queries.prepareVnFrames(0));
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

	private int uriToCode(@NonNull final String providerUri)
	{
		switch (providerUri)
		{
			case VerbNetContract.VnClasses.URI1:
				return VerbNetControl.VNCLASS1;
			case VerbNetContract.VnClasses.URI:
				return VerbNetControl.VNCLASSES;
			case VerbNetContract.VnClasses_X.URI_BY_VNCLASS:
				return VerbNetControl.VNCLASSES_X_BY_VNCLASS;
			case VerbNetContract.Words_VnClasses.URI:
				return VerbNetControl.WORDS_VNCLASSES;
			case VerbNetContract.VnClasses_VnMembers_X.URI_BY_WORD:
				return VerbNetControl.VNCLASSES_VNMEMBERS_X_BY_WORD;
			case VerbNetContract.VnClasses_VnRoles_X.URI_BY_ROLE:
				return VerbNetControl.VNCLASSES_VNROLES_X_BY_VNROLE;
			case VerbNetContract.VnClasses_VnFrames_X.URI_BY_FRAME:
				return VerbNetControl.VNCLASSES_VNFRAMES_X_BY_VNFRAME;
			case VerbNetContract.Lookup_VnExamples.URI:
				return VerbNetControl.LOOKUP_FTS_EXAMPLES;
			case VerbNetContract.Lookup_VnExamples_X.URI:
				return VerbNetControl.LOOKUP_FTS_EXAMPLES_X;
			case VerbNetContract.Lookup_VnExamples_X.URI_BY_EXAMPLE:
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
