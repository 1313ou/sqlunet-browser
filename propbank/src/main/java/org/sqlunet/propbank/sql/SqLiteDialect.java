package org.sqlunet.propbank.sql;

/**
 * SQL dialect for PropBank
 */
@SuppressWarnings("unused")
class SqLiteDialect
{
	// query for role set  from role set Id
	static final String PropBankRoleset = //
			"SELECT rolesetid, rolesetname, rolesethead, rolesetdescr " + //
					"FROM pbrolesets " + //
					"WHERE rolesetid = ? ;";
	// query for role set  from word
	static final String PropBankRolesetFromWord = //
			"SELECT wordid, rolesetid, rolesetname, rolesethead, rolesetdescr " + //
					"FROM words AS w " + //
					"INNER JOIN pbwords USING (wordid) " + //
					"LEFT JOIN pbrolesets USING (pbwordid) " + //
					"WHERE w.lemma = ? ";
	// query for role set  from word id
	static final String PropBankRolesetFromWordId = //
			"SELECT rolesetid, rolesetname, rolesethead, rolesetdescr " + //
					"FROM words " + //
					"INNER JOIN pbwords USING (wordid) " + //
					"INNER JOIN pbrolesets USING (pbwordid) " + //
					"WHERE wordid = ? ;";
	// query for roles
	static final String PropBankRolesQuery = //
			"SELECT roleid,roledescr,narg,funcname,thetaname " + //
					"FROM pbrolesets " + //
					"INNER JOIN pbroles USING (rolesetid) " + //
					"LEFT JOIN pbfuncs USING (func) " + //
					"LEFT JOIN pbvnthetas USING (theta) " + //
					"WHERE rolesetid = ? " + //
					"ORDER BY narg;";
	// query for examples rel(n~arg|n~arg|..)
	static final String PropBankExamplesShortQuery = //
			"SELECT exampleid,text,rel,GROUP_CONCAT(narg||'~'||" +
					"(CASE WHEN funcname IS NULL THEN '*' ELSE funcname END)||'~'||" + //
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
					"WHERE rolesetid = ? " + //
					"GROUP BY e.exampleid " + //
					"ORDER BY e.exampleid,narg;";
	// query for examples rel(n~arg|n~arg|..)
	static final String PropBankExamplesQuery = //
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
