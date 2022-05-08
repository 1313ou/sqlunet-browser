package org.sqlunet.provider;

import org.junit.Test;
import org.sqlunet.browser.Module;
import org.sqlunet.loaders.Queries;
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
		process(processor, Queries.prepareWord("w"));
		process(processor, Queries.prepareWordX("w"));
		process(processor, Queries.prepareVn(0));
		process(processor, Queries.preparePb(0));
		process(processor, Queries.prepareFn(0));
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
		XSqlUNetDispatcher.Result r = XSqlUNetDispatcher.queryMain(code, null, providerSql.projection, providerSql.selection, providerSql.selectionArgs);
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
			case XSqlUNetContract.PredicateMatrix.CONTENT_URI_TABLE:
				return XSqlUNetDispatcher.PREDICATEMATRIX;
			case XSqlUNetContract.PredicateMatrix_VerbNet.CONTENT_URI_TABLE:
				return XSqlUNetDispatcher.PREDICATEMATRIX_VERBNET;
			case XSqlUNetContract.PredicateMatrix_PropBank.CONTENT_URI_TABLE:
				return XSqlUNetDispatcher.PREDICATEMATRIX_PROPBANK;
			case XSqlUNetContract.PredicateMatrix_FrameNet.CONTENT_URI_TABLE:
				return XSqlUNetDispatcher.PREDICATEMATRIX_FRAMENET;
			case XSqlUNetContract.Words_FnWords_PbWords_VnWords.CONTENT_URI_TABLE:
				return XSqlUNetDispatcher.WORDS_FNWORDS_PBWORDS_VNWORDS;
			case XSqlUNetContract.Words_PbWords_VnWords.CONTENT_URI_TABLE:
				return XSqlUNetDispatcher.WORDS_PBWORDS_VNWORDS;
			case XSqlUNetContract.Words_VnWords_VnClasses.CONTENT_URI_TABLE:
				return XSqlUNetDispatcher.WORDS_VNWORDS_VNCLASSES;
			case XSqlUNetContract.Words_VnWords_VnClasses_U.CONTENT_URI_TABLE:
				return XSqlUNetDispatcher.WORDS_VNWORDS_VNCLASSES_U;
			case XSqlUNetContract.Words_VnWords_VnClasses_1.CONTENT_URI_TABLE:
				return XSqlUNetDispatcher.WORDS_VNWORDS_VNCLASSES_1;
			case XSqlUNetContract.Words_VnWords_VnClasses_2.CONTENT_URI_TABLE:
				return XSqlUNetDispatcher.WORDS_VNWORDS_VNCLASSES_2;
			case XSqlUNetContract.Words_VnWords_VnClasses_1U2.CONTENT_URI_TABLE:
				return XSqlUNetDispatcher.WORDS_VNWORDS_VNCLASSES_1U2;
			case XSqlUNetContract.Words_PbWords_PbRoleSets.CONTENT_URI_TABLE:
				return XSqlUNetDispatcher.WORDS_PBWORDS_PBROLESETS;
			case XSqlUNetContract.Words_PbWords_PbRoleSets_U.CONTENT_URI_TABLE:
				return XSqlUNetDispatcher.WORDS_PBWORDS_PBROLESETS_U;
			case XSqlUNetContract.Words_PbWords_PbRoleSets_1.CONTENT_URI_TABLE:
				return XSqlUNetDispatcher.WORDS_PBWORDS_PBROLESETS_1;
			case XSqlUNetContract.Words_PbWords_PbRoleSets_2.CONTENT_URI_TABLE:
				return XSqlUNetDispatcher.WORDS_PBWORDS_PBROLESETS_2;
			case XSqlUNetContract.Words_PbWords_PbRoleSets_1U2.CONTENT_URI_TABLE:
				return XSqlUNetDispatcher.WORDS_PBWORDS_PBROLESETS_1U2;
			case XSqlUNetContract.Words_FnWords_FnFrames_U.CONTENT_URI_TABLE:
				return XSqlUNetDispatcher.WORDS_FNWORDS_FNFRAMES_U;
			case XSqlUNetContract.Words_FnWords_FnFrames_1U2.CONTENT_URI_TABLE:
				return XSqlUNetDispatcher.WORDS_FNWORDS_FNFRAMES_1U2;
			case XSqlUNetContract.Words_FnWords_FnFrames_1.CONTENT_URI_TABLE:
				return XSqlUNetDispatcher.WORDS_FNWORDS_FNFRAMES_1;
			case XSqlUNetContract.Words_FnWords_FnFrames_2.CONTENT_URI_TABLE:
				return XSqlUNetDispatcher.WORDS_FNWORDS_FNFRAMES_2;
			case XSqlUNetContract.Sources.CONTENT_URI_TABLE:
				return XSqlUNetDispatcher.SOURCES;
			default:
				throw new IllegalArgumentException("Illegal uri: " + providerUri);
		}
	}
}
