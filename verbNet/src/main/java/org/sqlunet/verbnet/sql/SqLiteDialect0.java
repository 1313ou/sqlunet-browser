/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.verbnet.sql;

/**
 * VerbNet SQL dialect
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class SqLiteDialect0
{
	// CLASS
	// query for verbnet class
	static final String VerbNetClassQuery = //
			"SELECT classid, class, GROUP_CONCAT(grouping, '|') AS groupings " + //
					"FROM vn_classes " + //
					"LEFT JOIN vn_members_groupings USING (classid) " + //
					"LEFT JOIN vn_groupings USING (groupingid) " + //
					"WHERE classid = ? ";
	// query for verbnet class from word and pos
	static final String VerbNetClassQueryFromWordAndPos = //
			"SELECT wordid, (synsetid IS NULL) AS nullsynset, synsetid, definition, domainid, classid, class, classtag " + //
					"FROM words AS w " + //
					"INNER JOIN vn_words USING (wordid) " + //
					"LEFT JOIN vn_members_senses USING (vnwordid) " + //
					"LEFT JOIN synsets USING (synsetid) " + //
					"LEFT JOIN vn_classes USING (classid) " + //
					"WHERE posid = 'v' AND w.word = ? " + //
					"GROUP BY wordid, synsetid " + //
					"ORDER BY domainid,synsetid,nullsynset ASC;";
	// query for verbnet class from sense
	static final String VerbNetClassQueryFromSense = //
			"SELECT classid, class, (synsetid IS NULL) AS nullsynset, definition, sensenum, sensekey, quality, GROUP_CONCAT(grouping, '|') AS groupings " + //
					"FROM words " + //
					"INNER JOIN vn_words USING (wordid) " + //
					"INNER JOIN vn_members_senses USING (vnwordid) " + //
					"LEFT JOIN vn_classes USING (classid) " + //
					"LEFT JOIN vn_members_groupings USING (classid, vnwordid) " + //
					"LEFT JOIN vn_groupings USING (groupingid) " + //
					"LEFT JOIN synsets USING (synsetid) " + //
					"WHERE wordid = ? AND (synsetid = ? OR synsetid IS NULL) " + //
					"GROUP BY classid;";

	// ROLES
	// query for verbnet roles
	static final String VerbNetThematicRolesQueryFromClassId = //
			"SELECT roleid, roletypeid, roletype, restrs, classid " + //
					"FROM vn_roles " + //
					"INNER JOIN vn_roletypes USING (roletypeid) " + //
					"LEFT JOIN vn_restrs USING (restrsid) " + //
					"WHERE classid = ?;";
	static final String VerbNetThematicRolesQueryFromClassIdAndSense = //
			"SELECT roleid, roletypeid, roletype, restrs,classid, (synsetid IS NULL) AS nullsynset, wordid, synsetid, quality " + //
					"FROM words " + //
					"INNER JOIN vn_words USING (wordid) " + //
					"LEFT JOIN vn_members_senses USING (vnwordid) " + //
					"INNER JOIN vn_roles USING (classid) " + //
					"INNER JOIN vn_roletypes USING (roletypeid) " + //
					"LEFT JOIN vn_restrs USING (restrsid) " + //
					"WHERE classid = ? AND wordid = ? AND (synsetid = ? OR synsetid IS NULL) " + //
					"ORDER BY nullsynset ASC,roletypeid ASC;";

	// FRAMES
	// query for verbnet frames
	static final String VerbNetFramesQueryFromClassId = //
			"SELECT frameid, number, xtag, framename, framesubname, syntax, semantics, GROUP_CONCAT(example , '|') AS sampleset, classid " + //
					"FROM vn_classes_frames  " + //
					"INNER JOIN vn_frames USING (frameid) " + //
					"LEFT JOIN vn_framenames USING (framenameid)  " + //
					"LEFT JOIN vn_framesubnames USING (framesubnameid) " + //
					"LEFT JOIN vn_syntaxes USING (syntaxid)  " + //
					"LEFT JOIN vn_semantics USING (semanticsid) " + //
					"LEFT JOIN vn_frames_examples USING (frameid)  " + //
					"LEFT JOIN vn_examples USING (exampleid)  " + //
					"WHERE classid = ?;";
	static final String VerbNetFramesQueryFromClassIdAndSense = //
			"SELECT frameid, number, xtag, framename, framesubname, syntax, semantics, GROUP_CONCAT(example , '|') AS sampleset,classid,(synsetid IS NULL) AS nullsynset,synsetid,wordid,quality " + //
					"FROM words " + //
					"INNER JOIN vn_words USING (wordid) " + //
					"LEFT JOIN vn_members_senses USING (vnwordid) " + //
					"INNER JOIN vn_classes_frames USING (classid) " + //
					"INNER JOIN vn_frames USING (frameid) " + //
					"LEFT JOIN vn_framenames USING (framenameid) " + //
					"LEFT JOIN vn_framesubnames USING (framesubnameid) " + //
					"LEFT JOIN vn_syntaxes USING (syntaxid) " + //
					"LEFT JOIN vn_semantics USING (semanticsid) " + //
					"LEFT JOIN vn_frames_examples USING (frameid) " + //
					"LEFT JOIN vn_examples USING (exampleid) " + //
					"WHERE classid = ? AND wordid = ? AND (synsetid = ? OR synsetid IS NULL) " + //
					"GROUP BY frameid " + //
					"ORDER BY nullsynset ASC,frameid ASC;";
}
