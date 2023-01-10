/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.provider;

import org.junit.Test;
import org.sqlunet.browser.Module;
import org.sqlunet.loaders.Queries;
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
		process(processor, Queries.prepareWordXSelect("w"));
		process(processor, Queries.prepareWordSelect("w"));
		process(processor, Queries.prepareVnXSelect(0));
		process(processor, Queries.preparePbXSelect(0));
		process(processor, Queries.prepareFnXSelect(0));
		process(processor, Queries.prepareVnXSelectVn(0));
		process(processor, Queries.preparePbSelectVn(0));
		process(processor, Queries.prepareWordXSelectVn("w"));
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
		XNetControl.Result r = XNetControl.queryMain(code, null, providerSql.projection, providerSql.selection, providerSql.selectionArgs);
		if (r == null)
		{
			throw new IllegalArgumentException("Illegal query code: " + code);
		}
		return SQLiteQueryBuilder.buildQueryString(false, r.table, r.projection, r.selection, r.groupBy, null, r.orderBy, null);
	}

	private int uriToCode(final String providerUri)
	{
		switch (providerUri)
		{
			case XNetContract.PredicateMatrix.URI:
				return XNetControl.PREDICATEMATRIX;
			case XNetContract.PredicateMatrix_VerbNet.URI:
				return XNetControl.PREDICATEMATRIX_VERBNET;
			case XNetContract.PredicateMatrix_PropBank.URI:
				return XNetControl.PREDICATEMATRIX_PROPBANK;
			case XNetContract.PredicateMatrix_FrameNet.URI:
				return XNetControl.PREDICATEMATRIX_FRAMENET;
			case XNetContract.Words_FnWords_PbWords_VnWords.URI:
				return XNetControl.WORDS_FNWORDS_PBWORDS_VNWORDS;
			case XNetContract.Words_PbWords_VnWords.URI:
				return XNetControl.WORDS_PBWORDS_VNWORDS;
			case XNetContract.Words_VnWords_VnClasses.URI:
				return XNetControl.WORDS_VNWORDS_VNCLASSES;
			case XNetContract.Words_VnWords_VnClasses_U.URI:
				return XNetControl.WORDS_VNWORDS_VNCLASSES_U;
			case XNetContract.Words_VnWords_VnClasses_1.URI:
				return XNetControl.WORDS_VNWORDS_VNCLASSES_1;
			case XNetContract.Words_VnWords_VnClasses_2.URI:
				return XNetControl.WORDS_VNWORDS_VNCLASSES_2;
			case XNetContract.Words_VnWords_VnClasses_1U2.URI:
				return XNetControl.WORDS_VNWORDS_VNCLASSES_1U2;
			case XNetContract.Words_PbWords_PbRoleSets.URI:
				return XNetControl.WORDS_PBWORDS_PBROLESETS;
			case XNetContract.Words_PbWords_PbRoleSets_U.URI:
				return XNetControl.WORDS_PBWORDS_PBROLESETS_U;
			case XNetContract.Words_PbWords_PbRoleSets_1.URI:
				return XNetControl.WORDS_PBWORDS_PBROLESETS_1;
			case XNetContract.Words_PbWords_PbRoleSets_2.URI:
				return XNetControl.WORDS_PBWORDS_PBROLESETS_2;
			case XNetContract.Words_PbWords_PbRoleSets_1U2.URI:
				return XNetControl.WORDS_PBWORDS_PBROLESETS_1U2;
			case XNetContract.Words_FnWords_FnFrames_U.URI:
				return XNetControl.WORDS_FNWORDS_FNFRAMES_U;
			case XNetContract.Words_FnWords_FnFrames_1U2.URI:
				return XNetControl.WORDS_FNWORDS_FNFRAMES_1U2;
			case XNetContract.Words_FnWords_FnFrames_1.URI:
				return XNetControl.WORDS_FNWORDS_FNFRAMES_1;
			case XNetContract.Words_FnWords_FnFrames_2.URI:
				return XNetControl.WORDS_FNWORDS_FNFRAMES_2;
			case XNetContract.Sources.URI:
				return XNetControl.SOURCES;
			default:
				throw new IllegalArgumentException("Illegal uri: " + providerUri);
		}
	}
}
