package org.sqlunet.propbank.provider;

import org.junit.Test;
import org.sqlunet.browser.Module;
import org.sqlunet.propbank.loaders.Queries;

public class RunQueries
{
	@Test
	public void runQueries()
	{
		Module.ContentProviderSql sql;

		sql = Queries.prepareRoleSet(0);
		sql = Queries.prepareRoleSets(0);
		sql = Queries.prepareRoles(0);
		sql = Queries.prepareExamples(0);
	}
}
