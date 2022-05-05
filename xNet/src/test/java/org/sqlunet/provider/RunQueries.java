package org.sqlunet.provider;

import org.junit.Test;
import org.sqlunet.browser.Module;
import org.sqlunet.loaders.Queries;

public class RunQueries
{
	@Test
	public void runQueries()
	{
		Module.ContentProviderSql sql;

		sql = Queries.prepareWord("w");
		sql = Queries.prepareWordX("w");
		sql = Queries.prepareVn(0);
		sql = Queries.preparePb(0);
		sql = Queries.prepareFn(0);
	}
}
