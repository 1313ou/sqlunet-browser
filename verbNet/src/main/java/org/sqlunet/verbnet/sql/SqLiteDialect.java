package org.sqlunet.verbnet.sql;

/**
 * VerbNet SQL dialect
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class SqLiteDialect
{
	// CLASS
	// query for verbnet class
	static final String VerbNetClassQuery = //
			"SELECT classid, class, GROUP_CONCAT(grouping, '|') AS groupings " + //
					"FROM vnclasses " + //
					"LEFT JOIN vngroupingmaps USING (classid) " + //
					"LEFT JOIN vngroupings USING (groupingid) " + //
					"WHERE classid = ? ";
	// query for verbnet class from word and pos
	static final String VerbNetClassQueryFromWordAndPos = //
			"SELECT wordid, (synsetid IS NULL) AS nullsynset, synsetid, definition, lexdomainid, classid, class, classtag " + //
					"FROM words AS w " + //
					"INNER JOIN vnwords USING (wordid) " + //
					"LEFT JOIN vnclassmembersenses USING (vnwordid) " + //
					"LEFT JOIN synsets USING (synsetid) " + //
					"LEFT JOIN vnclasses USING (classid) " + //
					"WHERE pos = 'v' AND w.lemma = ? " + //
					"GROUP BY wordid, synsetid " + //
					"ORDER BY lexdomainid,synsetid,nullsynset ASC;";
	// query for verbnet class from sense
	static final String VerbNetClassQueryFromSense = //
			"SELECT classid, class, (synsetid IS NULL) AS nullsynset, sensenum, sensekey, quality, GROUP_CONCAT(grouping, '|') AS groupings " + //
					"FROM words " + //
					"INNER JOIN vnwords USING (wordid) " + //
					"INNER JOIN vnclassmembersenses USING (vnwordid) " + //
					"LEFT JOIN vnclasses USING (classid) " + //
					"LEFT JOIN vngroupingmaps USING (classid, vnwordid) " + //
					"LEFT JOIN vngroupings USING (groupingid) " + //
					"WHERE wordid = ? AND (synsetid = ? OR synsetid IS NULL) " + //
					"GROUP BY classid;";

	// ROLES
	// query for verbnet roles
	static final String VerbNetThematicRolesQueryFromClassId = //
			"SELECT roleid, roletypeid, roletype, restrs, classid " + //
					"FROM vnrolemaps " + //
					"INNER JOIN vnroles USING (roleid) " + //
					"INNER JOIN vnroletypes USING (roletypeid) " + //
					"LEFT JOIN vnrestrs USING (restrsid) " + //
					"WHERE classid = ?;";
	static final String VerbNetThematicRolesQueryFromClassIdAndSense = //
			"SELECT roleid, roletypeid, roletype, restrs,classid, (synsetid IS NULL) AS nullsynset, wordid, synsetid, quality " + //
					"FROM words " + //
					"INNER JOIN vnwords USING (wordid) " + //
					"LEFT JOIN vnclassmembersenses USING (vnwordid) " + //
					"INNER JOIN vnrolemaps USING (classid) " + //
					"INNER JOIN vnroles USING (roleid) " + //
					"INNER JOIN vnroletypes USING (roletypeid) " + //
					"LEFT JOIN vnrestrs USING (restrsid) " + //
					"WHERE classid = ? AND wordid = ? AND (synsetid = ? OR synsetid IS NULL) " + //
					"ORDER BY nullsynset ASC,roletypeid ASC;";

	// FRAMES
	// query for verbnet frames
	static final String VerbNetFramesQueryFromClassId = //
			"SELECT frameid, number, xtag, framename, framesubname, syntax, semantics, GROUP_CONCAT(example , '|') AS exampleset, classid " + //
					"FROM vnframemaps  " + //
					"INNER JOIN vnframes USING (frameid) " + //
					"LEFT JOIN vnframenames USING (nameid)  " + //
					"LEFT JOIN vnframesubnames USING (subnameid) " + //
					"LEFT JOIN vnsyntaxes USING (syntaxid)  " + //
					"LEFT JOIN vnsemantics USING (semanticsid) " + //
					"LEFT JOIN vnexamplemaps USING (frameid)  " + //
					"LEFT JOIN vnexamples USING (exampleid)  " + //
					"WHERE classid = ?;";
	static final String VerbNetFramesQueryFromClassIdAndSense = //
			"SELECT frameid, number, xtag, framename, framesubname, syntax, semantics, GROUP_CONCAT(example , '|') AS exampleset,classid,(synsetid IS NULL) AS nullsynset,synsetid,wordid,quality " + //
					"FROM words " + //
					"INNER JOIN vnwords USING (wordid) " + //
					"LEFT JOIN vnclassmembersenses USING (vnwordid) " + //
					"INNER JOIN vnframemaps USING (classid) " + //
					"INNER JOIN vnframes USING (frameid) " + //
					"LEFT JOIN vnframenames USING (nameid) " + //
					"LEFT JOIN vnframesubnames USING (subnameid) " + //
					"LEFT JOIN vnsyntaxes USING (syntaxid) " + //
					"LEFT JOIN vnsemantics USING (semanticsid) " + //
					"LEFT JOIN vnexamplemaps USING (frameid) " + //
					"LEFT JOIN vnexamples USING (exampleid) " + //
					"WHERE classid = ? AND wordid = ? AND (synsetid = ? OR synsetid IS NULL) " + //
					"GROUP BY frameid " + //
					"ORDER BY nullsynset ASC,frameid ASC;";
}
