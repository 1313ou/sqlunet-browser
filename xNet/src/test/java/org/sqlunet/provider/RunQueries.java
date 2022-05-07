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
		catch(Exception e)
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
			case "predicatematrix":
				return 200;
			case "predicatematrix_verbnet":
				return 210;
			case "predicatematrix_propbank":
				return 220;
			case "predicatematrix_framenet":
				return 230;
			case "words_fnwords_pbwords_vnwords":
				return 100;
			case "words_pbwords_vnwords":
				return 101;
			case "words_vnwords_vnclasses":
				return 310;
			case "words_vnwords_vnclasses_u":
				return 311;
			case "words_vnwords_vnclasses_1":
				return 312;
			case "words_vnwords_vnclasses_2":
				return 313;
			case "words_vnwords_vnclasses_1u2":
				return 314;
			case "words_pbwords_pbrolesets":
				return 320;
			case "words_pbwords_pbrolesets_u":
				return 321;
			case "words_pbwords_pbrolesets_1":
				return 322;
			case "words_pbwords_pbrolesets_2":
				return 323;
			case "words_pbwords_pbrolesets_1u2":
				return 324;
			case "words_fnwords_fnframes_u":
				return 331;
			case "words_fnwords_fnframes_1":
				return 332;
			case "words_fnwords_fnframes_2":
				return 333;
			case "words_fnwords_fnframes_1u2":
				return 334;
			case "sources":
				return 400;
			default:
				throw new IllegalArgumentException("Illegal uri: " + providerUri);
		}
	}
}
