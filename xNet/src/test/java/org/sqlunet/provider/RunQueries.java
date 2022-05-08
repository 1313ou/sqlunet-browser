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
			case XNetContract.PredicateMatrix.CONTENT_URI_TABLE:
				return XNetControl.PREDICATEMATRIX;
			case XNetContract.PredicateMatrix_VerbNet.CONTENT_URI_TABLE:
				return XNetControl.PREDICATEMATRIX_VERBNET;
			case XNetContract.PredicateMatrix_PropBank.CONTENT_URI_TABLE:
				return XNetControl.PREDICATEMATRIX_PROPBANK;
			case XNetContract.PredicateMatrix_FrameNet.CONTENT_URI_TABLE:
				return XNetControl.PREDICATEMATRIX_FRAMENET;
			case XNetContract.Words_FnWords_PbWords_VnWords.CONTENT_URI_TABLE:
				return XNetControl.WORDS_FNWORDS_PBWORDS_VNWORDS;
			case XNetContract.Words_PbWords_VnWords.CONTENT_URI_TABLE:
				return XNetControl.WORDS_PBWORDS_VNWORDS;
			case XNetContract.Words_VnWords_VnClasses.CONTENT_URI_TABLE:
				return XNetControl.WORDS_VNWORDS_VNCLASSES;
			case XNetContract.Words_VnWords_VnClasses_U.CONTENT_URI_TABLE:
				return XNetControl.WORDS_VNWORDS_VNCLASSES_U;
			case XNetContract.Words_VnWords_VnClasses_1.CONTENT_URI_TABLE:
				return XNetControl.WORDS_VNWORDS_VNCLASSES_1;
			case XNetContract.Words_VnWords_VnClasses_2.CONTENT_URI_TABLE:
				return XNetControl.WORDS_VNWORDS_VNCLASSES_2;
			case XNetContract.Words_VnWords_VnClasses_1U2.CONTENT_URI_TABLE:
				return XNetControl.WORDS_VNWORDS_VNCLASSES_1U2;
			case XNetContract.Words_PbWords_PbRoleSets.CONTENT_URI_TABLE:
				return XNetControl.WORDS_PBWORDS_PBROLESETS;
			case XNetContract.Words_PbWords_PbRoleSets_U.CONTENT_URI_TABLE:
				return XNetControl.WORDS_PBWORDS_PBROLESETS_U;
			case XNetContract.Words_PbWords_PbRoleSets_1.CONTENT_URI_TABLE:
				return XNetControl.WORDS_PBWORDS_PBROLESETS_1;
			case XNetContract.Words_PbWords_PbRoleSets_2.CONTENT_URI_TABLE:
				return XNetControl.WORDS_PBWORDS_PBROLESETS_2;
			case XNetContract.Words_PbWords_PbRoleSets_1U2.CONTENT_URI_TABLE:
				return XNetControl.WORDS_PBWORDS_PBROLESETS_1U2;
			case XNetContract.Words_FnWords_FnFrames_U.CONTENT_URI_TABLE:
				return XNetControl.WORDS_FNWORDS_FNFRAMES_U;
			case XNetContract.Words_FnWords_FnFrames_1U2.CONTENT_URI_TABLE:
				return XNetControl.WORDS_FNWORDS_FNFRAMES_1U2;
			case XNetContract.Words_FnWords_FnFrames_1.CONTENT_URI_TABLE:
				return XNetControl.WORDS_FNWORDS_FNFRAMES_1;
			case XNetContract.Words_FnWords_FnFrames_2.CONTENT_URI_TABLE:
				return XNetControl.WORDS_FNWORDS_FNFRAMES_2;
			case XNetContract.Sources.CONTENT_URI_TABLE:
				return XNetControl.SOURCES;
			default:
				throw new IllegalArgumentException("Illegal uri: " + providerUri);
		}
	}
}
