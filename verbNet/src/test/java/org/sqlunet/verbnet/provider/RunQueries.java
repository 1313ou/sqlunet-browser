package org.sqlunet.verbnet.provider;

import org.junit.Test;
import org.sqlunet.browser.Module;
import org.sqlunet.verbnet.loaders.Queries;

public class RunQueries
{
	@Test
	public void runQueries()
	{
		Module.ContentProviderSql sql;
		sql = Queries.prepareVnClass(0);
		sql = Queries.prepareVnClasses(0, null);
		sql = Queries.prepareVnClasses(0, 0L);
		sql = Queries.prepareVnRoles(0);
		sql = Queries.prepareVnMembers(0);
		sql = Queries.prepareVnFrames(0);
	}
}
