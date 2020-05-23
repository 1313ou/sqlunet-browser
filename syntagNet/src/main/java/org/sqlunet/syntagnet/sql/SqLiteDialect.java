/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.propbank.sql;

/**
 * SQL dialect for PropBank
 */
@SuppressWarnings("unused")
class SqLiteDialect
{
	// ROLE SETS
	// query for role set from role set id
	static final String PropBankRoleSetQuery = //
			"SELECT rolesetid, rolesetname, rolesethead, rolesetdescr " + //
					"FROM pbrolesets " + //
					"WHERE rolesetid = ? ;";
	// query for role set from word
	static final String PropBankRoleSetQueryFromWord = //
			"SELECT wordid, rolesetid, rolesetname, rolesethead, rolesetdescr " + //
					"FROM words AS w " + //
					"INNER JOIN pbwords USING (wordid) " + //
					"LEFT JOIN pbrolesets USING (pbwordid) " + //
					"WHERE w.lemma = ? ";
	// query for role set from word id
	static final String PropBankRoleSetQueryFromWordId = //
			"SELECT rolesetid, rolesetname, rolesethead, rolesetdescr " + //
					"FROM words " + //
					"INNER JOIN pbwords USING (wordid) " + //
					"INNER JOIN pbrolesets USING (pbwordid) " + //
					"WHERE wordid = ? ;";

	// ROLES
	// query for roles
	static final String PropBankRolesQueryFromRoleSetId = //
			"SELECT roleid,roledescr,narg,funcname,thetaname " + //
					"FROM pbrolesets " + //
					"INNER JOIN pbroles USING (rolesetid) " + //
					"LEFT JOIN pbfuncs USING (func) " + //
					"LEFT JOIN pbvnthetas USING (theta) " + //
					"WHERE rolesetid = ? " + //
					"ORDER BY narg;";

	// EXAMPLES
	// query for examples rel(n~arg|n~arg|..)
	static final String PropBankExamplesQueryFromRoleSetId = //
			"SELECT exampleid,text,rel,GROUP_CONCAT(narg||'~'||" + //
					"(CASE WHEN funcname IS NULL THEN '*' ELSE funcname END)||'~'||" + //
					"roledescr||'~'||" + //
					"(CASE WHEN thetaname IS NULL THEN '*' ELSE thetaname END)||'~'||" + //
					"arg,'|')," + //
					"aspectname,formname,tensename,voicename,personname " + //
					"FROM pbrolesets " + //
					"INNER JOIN pbexamples AS e USING (rolesetid) " + //
					"LEFT JOIN pbrels AS r USING (exampleid) " + //
					"LEFT JOIN pbargs AS a USING (exampleid) " + //
					"LEFT JOIN pbfuncs AS f ON (a.func = f.func) " + //
					"LEFT JOIN pbaspects USING (aspect) " + //
					"LEFT JOIN pbforms USING (form) " + //
					"LEFT JOIN pbtenses USING (tense) " + //
					"LEFT JOIN pbvoices USING (voice) " + //
					"LEFT JOIN pbpersons USING (person) " + //
					"LEFT JOIN pbroles USING (rolesetid,narg) " + //
					"LEFT JOIN pbvnthetas USING (theta) " + //
					"WHERE rolesetid = ? " + //
					"GROUP BY e.exampleid " + //
					"ORDER BY e.exampleid,narg;";
}
