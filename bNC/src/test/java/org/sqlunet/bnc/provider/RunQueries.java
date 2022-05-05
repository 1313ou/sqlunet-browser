package org.sqlunet.bnc.provider;

import org.junit.Test;
import org.sqlunet.bnc.loaders.Queries;
import org.sqlunet.browser.Module;

public class RunQueries
{
	@Test
	public void runQueries()
	{
		Module.ContentProviderSql sql;
		sql = Queries.prepareBnc(0, 'n');
		sql = Queries.prepareBnc(0, null);
	}
}
