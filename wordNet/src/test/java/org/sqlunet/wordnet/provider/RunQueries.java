package org.sqlunet.wordnet.provider;

import org.junit.Test;
import org.sqlunet.browser.Module;
import org.sqlunet.wordnet.loaders.Queries;

public class RunQueries
{
	@Test
	public void runQueries()
	{
		Module.ContentProviderSql sql;

		sql = Queries.prepareWord(0);
		sql = Queries.prepareSenses("w");
		sql = Queries.prepareSenses(0);
		sql = Queries.prepareSense(0);
		sql = Queries.prepareSense("sk");
		sql = Queries.prepareSense(0, 0);
		sql = Queries.prepareSynset(0);
		sql = Queries.prepareMembers(0);
		sql = Queries.prepareMembers2(0, true);
		sql = Queries.prepareMembers2(0, false);
		sql = Queries.prepareSamples(0);
		sql = Queries.prepareRelations(0, 0);
		sql = Queries.prepareSemRelations(0);
		sql = Queries.prepareSemRelations(0, 0);
		sql = Queries.prepareLexRelations(0, 0);
		sql = Queries.prepareLexRelations(0);
		sql = Queries.prepareVFrames(0);
		sql = Queries.prepareVFrames(0, 0);
		sql = Queries.prepareVTemplates(0);
		sql = Queries.prepareVTemplates(0, 0);
		sql = Queries.prepareAdjPosition(0);
		sql = Queries.prepareAdjPosition(0, 0);
		sql = Queries.prepareMorphs(0);
	}
}
