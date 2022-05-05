package org.sqlunet.framenet.provider;

import org.junit.Test;
import org.sqlunet.browser.Module;
import org.sqlunet.framenet.loaders.Queries;

public class RunQueries
{
	@Test
	public void runQueries()
	{
		Module.ContentProviderSql sql;
		sql = Queries.prepareFrame(0);
		sql = Queries.prepareRelatedFrames(0);
		sql = Queries.prepareFesForFrame(0);
		sql = Queries.prepareLexUnit(0);
		sql = Queries.prepareLexUnitsForFrame(0);
		sql = Queries.prepareLexUnitsForWordAndPos(0, 'n');
		sql = Queries.prepareGovernorsForLexUnit(0);
		sql = Queries.prepareRealizationsForLexicalUnit(0);
		sql = Queries.prepareGroupRealizationsForLexUnit(0);
		sql = Queries.prepareSentencesForLexUnit(0);
		sql = Queries.prepareSentencesForPattern(0);
		sql = Queries.prepareSentencesForValenceUnit(0);
		sql = Queries.prepareAnnoSet(0);
		sql = Queries.prepareAnnoSetsForGovernor(0);
		sql = Queries.prepareAnnoSetsForPattern(0);
		sql = Queries.prepareAnnoSetsForValenceUnit(0);
		sql = Queries.prepareLayersForSentence(0);
	}
}
