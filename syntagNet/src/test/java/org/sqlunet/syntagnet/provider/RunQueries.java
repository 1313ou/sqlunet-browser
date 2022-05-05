package org.sqlunet.syntagnet.provider;

import org.junit.Test;
import org.sqlunet.browser.Module;
import org.sqlunet.syntagnet.loaders.Queries;

public class RunQueries
{
	@Test
	public void runQueries()
	{
		Module.ContentProviderSql sql;

		sql = Queries.prepareCollocation(0);
		sql = Queries.prepareCollocations(0L, 0L, 0L, 0L);
		sql = Queries.prepareCollocations(0);
		sql = Queries.prepareCollocations("w");
	}
}
