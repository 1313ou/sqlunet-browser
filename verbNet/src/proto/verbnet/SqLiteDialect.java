/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.vn;

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
			"SELECT ${classes.classid}, ${classes.class}, GROUP_CONCAT(${groupings.grouping}, '|') AS ${groupings} " + //
					"FROM ${classes.table} " + //
					"LEFT JOIN ${members_groupings.table} USING (${classes.classid}) " + //
					"LEFT JOIN ${groupings.table} USING (${groupings.groupingid}) " + //
					"WHERE ${classes.classid} = ? ";
	// query for verbnet class from word and pos
	static final String VerbNetClassQueryFromWordAndPos = //
			"SELECT ${wnwords.wordid}, (${wnsynsets.synsetid} IS NULL) AS ${nullsynset}, ${wnsynsets.synsetid}, ${wnsynsets.definition}, ${wnsynsets.domainid}, ${classes.classid}, ${classes.class}, ${classes.classtag} " + //
					"FROM ${wnwords.table} AS ${as_words} " + //
					"INNER JOIN ${words.table} USING (${wnwords.wordid}) " + //
					"LEFT JOIN ${members_senses.table} USING (${words.vnwordid}) " + //
					"LEFT JOIN ${wnsynsets.table} USING (${wnsynsets.synsetid}) " + //
					"LEFT JOIN ${classes.table} USING (${classes.classid}) " + //
					"WHERE ${wnsynsets.posid} = 'v' AND ${as_words}.${wnwords.word} = ? " + //
					"GROUP BY ${wnwords.wordid}, ${wnsynsets.synsetid} " + //
					"ORDER BY ${wnsynsets.domainid},${wnsynsets.synsetid},${nullsynset} ASC;";
	// query for verbnet class from sense
	static final String VerbNetClassQueryFromSense = //
			"SELECT ${classes.classid}, ${classes.class}, (${wnsynsets.synsetid} IS NULL) AS ${nullsynset}, ${wnsynsets.definition}, ${members_senses.sensenum}, ${members_senses.sensekey}, ${members_senses.quality}, GROUP_CONCAT(${groupings.grouping}, '|') AS ${groupings} " + //
					"FROM ${wnwords.table} " + //
					"INNER JOIN ${words.table} USING (${wnwords.wordid}) " + //
					"INNER JOIN ${members_senses.table} USING (${words.vnwordid}) " + //
					"LEFT JOIN ${classes.table} USING (${classes.classid}) " + //
					"LEFT JOIN ${members_groupings.table} USING (${classes.classid}, ${words.vnwordid}) " + //
					"LEFT JOIN ${groupings.table} USING (${groupings.groupingid}) " + //
					"LEFT JOIN ${wnsynsets.table} USING (${wnsynsets.synsetid}) " + //
					"WHERE ${wnwords.wordid} = ? AND (${wnsynsets.synsetid} = ? OR ${wnsynsets.synsetid} IS NULL) " + //
					"GROUP BY ${classes.classid};";

	// ROLES
	// query for verbnet roles
	static final String VerbNetThematicRolesQueryFromClassId = //
			"SELECT ${roles.roleid}, ${roletypes.roletypeid}, ${roletypes.roletype}, ${restrs.restrs}, ${classes.classid} " + //
					"FROM ${roles.table} " + //
					"INNER JOIN ${roletypes.table} USING (${roletypes.roletypeid}) " + //
					"LEFT JOIN ${restrs.table} USING (${restrs.restrsid}) " + //
					"WHERE ${classes.classid} = ?;";
	static final String VerbNetThematicRolesQueryFromClassIdAndSense = //
			"SELECT ${roles.roleid}, ${roletypes.roletypeid}, ${roletypes.roletype}, ${restrs.restrs}, ${classes.classid}, (${wnsynsets.synsetid} IS NULL) AS ${nullsynset}, ${wnwords.wordid}, ${wnsynsets.synsetid}, ${members_senses.quality} " + //
					"FROM ${wnwords.table} " + //
					"INNER JOIN ${words.table} USING (${wnwords.wordid}) " + //
					"LEFT JOIN ${members_senses.table} USING (${words.vnwordid}) " + //
					"INNER JOIN ${roles.table} USING (${classes.classid}) " + //
					"INNER JOIN ${roletypes.table} USING (${roletypes.roletypeid}) " + //
					"LEFT JOIN ${restrs.table} USING (${restrs.restrsid}) " + //
					"WHERE ${classes.classid} = ? AND ${wnwords.wordid} = ? AND (${wnsynsets.synsetid} = ? OR ${wnsynsets.synsetid} IS NULL) " + //
					"ORDER BY ${nullsynset} ASC,${roletypes.roletypeid} ASC;";

	// FRAMES
	// query for verbnet frames
	static final String VerbNetFramesQueryFromClassId = //
			"SELECT ${frames.frameid}, ${frames.number}, ${frames.xtag}, ${framenames.framename}, ${framesubnames.framesubname}, ${syntaxes.syntax}, ${semantics.semantics}, GROUP_CONCAT(${examples.example} , '|') AS ${sampleset}, ${classes.classid} " + //
					"FROM ${classes_frames.table}  " + //
					"INNER JOIN ${frames.table} USING (${frames.frameid}) " + //
					"LEFT JOIN ${framenames.table} USING (${framenames.framenameid})  " + //
					"LEFT JOIN ${framesubnames.table} USING (${framesubnames.framesubnameid}) " + //
					"LEFT JOIN ${syntaxes.table} USING (${syntaxes.syntaxid})  " + //
					"LEFT JOIN ${semantics.table} USING (${semantics.semanticsid}) " + //
					"LEFT JOIN ${frames_examples.table} USING (${frames.frameid})  " + //
					"LEFT JOIN ${examples.table} USING (${examples.exampleid})  " + //
					"WHERE ${classes.classid} = ?;";
	static final String VerbNetFramesQueryFromClassIdAndSense = //
			"SELECT ${frames.frameid}, ${frames.number}, ${frames.xtag}, ${framenames.framename}, ${framesubnames.framesubname}, ${syntaxes.syntax}, ${semantics.semantics}, GROUP_CONCAT(${examples.example} , '|') AS ${sampleset},${classes.classid},(${wnsynsets.synsetid} IS NULL) AS ${nullsynset},${wnsynsets.synsetid},${wnwords.wordid},${members_senses.quality} " + //
					"FROM ${wnwords.table} " + //
					"INNER JOIN ${words.table} USING (${wnwords.wordid}) " + //
					"LEFT JOIN ${members_senses.table} USING (${words.vnwordid}) " + //
					"INNER JOIN ${classes_frames.table} USING (${classes.classid}) " + //
					"INNER JOIN ${frames.table} USING (${frames.frameid}) " + //
					"LEFT JOIN ${framenames.table} USING (${framenames.framenameid}) " + //
					"LEFT JOIN ${framesubnames.table} USING (${framesubnames.framesubnameid}) " + //
					"LEFT JOIN ${syntaxes.table} USING (${syntaxes.syntaxid}) " + //
					"LEFT JOIN ${semantics.table} USING (${semantics.semanticsid}) " + //
					"LEFT JOIN ${frames_examples.table} USING (${frames.frameid}) " + //
					"LEFT JOIN ${examples.table} USING (${examples.exampleid}) " + //
					"WHERE ${classes.classid} = ? AND ${wnwords.wordid} = ? AND (${wnsynsets.synsetid} = ? OR ${wnsynsets.synsetid} IS NULL) " + //
					"GROUP BY ${frames.frameid} " + //
					"ORDER BY ${nullsynset} ASC,${frames.frameid} ASC;";
}
