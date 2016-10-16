package org.sqlunet.verbnet.sql;

class SqLiteDialect
{
	// get verbnet entry
	static final String VerbNetQueryFromWord = //
			"SELECT wordid, (synsetid IS NULL) AS nullsynset, synsetid, definition, lexdomainid, classid, class, classtag " + // //$NON-NLS-1$
					"FROM words AS w " + // //$NON-NLS-1$
					"INNER JOIN vnwords USING (wordid) " + // //$NON-NLS-1$
					"LEFT JOIN vnclassmembersenses USING (vnwordid) " + // //$NON-NLS-1$
					"LEFT JOIN synsets USING (synsetid) " + // //$NON-NLS-1$
					"LEFT JOIN vnclasses USING (classid) " + // //$NON-NLS-1$
					"WHERE pos = 'v' AND w.lemma = ? " + // //$NON-NLS-1$
					"GROUP BY wordid, synsetid " + // //$NON-NLS-1$
					"ORDER BY lexdomainid,synsetid,nullsynset ASC;"; //$NON-NLS-1$

	// query for verbnet class membership
	static final String VerbNetClassQuery = //
			"SELECT classid, class, GROUP_CONCAT(grouping, '|') AS groupings " + // //$NON-NLS-1$
					"FROM vnclasses " + // //$NON-NLS-1$
					"LEFT JOIN vngroupingmaps USING (classid) " + // //$NON-NLS-1$
					"LEFT JOIN vngroupings USING (groupingid) " + // //$NON-NLS-1$
					"WHERE classid = ? "; //$NON-NLS-1$

	static final String VerbNetClassMembershipQuery = //
			"SELECT classid, class, (synsetid IS NULL) AS nullsynset, sensenum, sensekey, quality, GROUP_CONCAT(grouping, '|') AS groupings " + // //$NON-NLS-1$
					"FROM words " + // //$NON-NLS-1$
					"INNER JOIN vnwords USING (wordid) " + // //$NON-NLS-1$
					"INNER JOIN vnclassmembersenses USING (vnwordid) " + // //$NON-NLS-1$
					"LEFT JOIN vnclasses USING (classid) " + // //$NON-NLS-1$
					"LEFT JOIN vngroupingmaps USING (classid, vnwordid) " + // //$NON-NLS-1$
					"LEFT JOIN vngroupings USING (groupingid) " + // //$NON-NLS-1$
					"WHERE wordid = ? AND (synsetid = ? OR synsetid IS NULL) " + // //$NON-NLS-1$
					"GROUP BY classid;"; //$NON-NLS-1$

	// query for verbnet roles
	static final String VerbNetThematicRolesQuery = //
			"SELECT roleid, roletypeid, roletype, restrs, classid " + // //$NON-NLS-1$
					"FROM vnrolemaps " + // //$NON-NLS-1$
					"INNER JOIN vnroles USING (roleid) " + // //$NON-NLS-1$
					"INNER JOIN vnroletypes USING (roletypeid) " + // //$NON-NLS-1$
					"LEFT JOIN vnrestrs USING (restrsid) " + // //$NON-NLS-1$
					"WHERE classid = ?;"; //$NON-NLS-1$

	static final String VerbNetThematicRolesFromClassAndSenseQuery = //
			"SELECT roleid, roletypeid, roletype, restrs,classid,(synsetid IS NULL) AS nullsynset,wordid,synsetid,quality " + // //$NON-NLS-1$
					"FROM words " + // //$NON-NLS-1$
					"INNER JOIN vnwords USING (wordid) " + // //$NON-NLS-1$
					"LEFT JOIN vnclassmembersenses USING (vnwordid) " + // //$NON-NLS-1$
					"INNER JOIN vnrolemaps USING (classid) " + // //$NON-NLS-1$
					"INNER JOIN vnroles USING (roleid) " + // //$NON-NLS-1$
					"INNER JOIN vnroletypes USING (roletypeid) " + // //$NON-NLS-1$
					"LEFT JOIN vnrestrs USING (restrsid) " + // //$NON-NLS-1$
					"WHERE classid = ? AND wordid = ? AND (synsetid = ? OR synsetid IS NULL) " + // //$NON-NLS-1$
					"ORDER BY nullsynset ASC,roletypeid ASC;"; //$NON-NLS-1$

	// query for verbnet frames
	static final String VerbNetFramesQuery = //
			"SELECT frameid, number, xtag, framename, framesubname, syntax, semantics, GROUP_CONCAT(example , '|') AS exampleset, classid " + // //$NON-NLS-1$
					"FROM vnframemaps  " + // //$NON-NLS-1$
					"INNER JOIN vnframes USING (frameid) " + // //$NON-NLS-1$
					"LEFT JOIN vnframenames USING (nameid)  " + // //$NON-NLS-1$
					"LEFT JOIN vnframesubnames USING (subnameid) " + // //$NON-NLS-1$
					"LEFT JOIN vnsyntaxes USING (syntaxid)  " + // //$NON-NLS-1$
					"LEFT JOIN vnsemantics USING (semanticsid) " + // //$NON-NLS-1$
					"LEFT JOIN vnexamplemaps USING (frameid)  " + // //$NON-NLS-1$
					"LEFT JOIN vnexamples USING (exampleid)  " + // //$NON-NLS-1$
					"WHERE classid = ?;"; //$NON-NLS-1$

	static final String VerbNetFramesFromClassAndSenseQuery = //
			"SELECT frameid, number, xtag, framename, framesubname, syntax, semantics, GROUP_CONCAT(example , '|') AS exampleset,classid,(synsetid IS NULL) AS nullsynset,synsetid,wordid,quality " + // //$NON-NLS-1$
					"FROM words " + // //$NON-NLS-1$
					"INNER JOIN vnwords USING (wordid) " + // //$NON-NLS-1$
					"LEFT JOIN vnclassmembersenses USING (vnwordid) " + // //$NON-NLS-1$
					"INNER JOIN vnframemaps USING (classid) " + // //$NON-NLS-1$
					"INNER JOIN vnframes USING (frameid) " + // //$NON-NLS-1$
					"LEFT JOIN vnframenames USING (nameid) " + // //$NON-NLS-1$
					"LEFT JOIN vnframesubnames USING (subnameid) " + // //$NON-NLS-1$
					"LEFT JOIN vnsyntaxes USING (syntaxid) " + // //$NON-NLS-1$
					"LEFT JOIN vnsemantics USING (semanticsid) " + // //$NON-NLS-1$
					"LEFT JOIN vnexamplemaps USING (frameid) " + // //$NON-NLS-1$
					"LEFT JOIN vnexamples USING (exampleid) " + // //$NON-NLS-1$
					"WHERE classid = ? AND wordid = ? AND (synsetid = ? OR synsetid IS NULL) " + // //$NON-NLS-1$
					"GROUP BY frameid " + // //$NON-NLS-1$
					"ORDER BY nullsynset ASC,frameid ASC;"; //$NON-NLS-1$
}
