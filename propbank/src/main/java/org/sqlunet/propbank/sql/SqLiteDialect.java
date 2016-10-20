package org.sqlunet.propbank.sql;

/**
 * SQL dialect
 */
@SuppressWarnings("unused")
class SqLiteDialect
{
	// query for roleSet from roleSetId
	static final String PropBankRoleset = //
			"SELECT roleSetId, rolesetname, rolesethead, rolesetdescr " + //$NON-NLS-1$
					"FROM pbrolesets " + //$NON-NLS-1$
					"WHERE roleSetId = ? ;"; //$NON-NLS-1$

	// query for roleSet from word
	static final String PropBankRolesetFromWord = //
			"SELECT wordid, roleSetId, rolesetname, rolesethead, rolesetdescr " + //$NON-NLS-1$
					"FROM words AS w " + //$NON-NLS-1$
					"INNER JOIN pbwords USING (wordid) " + //$NON-NLS-1$
					"LEFT JOIN pbrolesets USING (pbwordid) " + //$NON-NLS-1$
					"WHERE w.lemma = ? "; //$NON-NLS-1$

	// query for roleSet from word id
	static final String PropBankRolesetFromWordId = //
			"SELECT roleSetId, rolesetname, rolesethead, rolesetdescr " + //$NON-NLS-1$
					"FROM words " + //$NON-NLS-1$
					"INNER JOIN pbwords USING (wordid) " + //$NON-NLS-1$
					"INNER JOIN pbrolesets USING (pbwordid) " + //$NON-NLS-1$
					"WHERE wordid = ? ;"; //$NON-NLS-1$

	// query for roles
	static final String PropBankRolesQuery = //
			"SELECT roleid,roledescr,narg,funcname,thetaname " + //$NON-NLS-1$
					"FROM pbrolesets " + //$NON-NLS-1$
					"INNER JOIN pbroles USING (roleSetId) " + //$NON-NLS-1$
					"LEFT JOIN pbfuncs USING (func) " + //$NON-NLS-1$
					"LEFT JOIN pbvnthetas USING (theta) " + //$NON-NLS-1$
					"WHERE roleSetId = ? " + //$NON-NLS-1$
					"ORDER BY narg;"; //$NON-NLS-1$

	// query for examples rel(n~arg|n~arg|..)
	static final String PropBankExamplesShortQuery = //
			"SELECT exampleid,text,rel,GROUP_CONCAT(narg||'~'||" +
					"(CASE WHEN funcname IS NULL THEN '*' ELSE funcname END)||'~'||" + //$NON-NLS-1$
					"arg,'|')," + //$NON-NLS-1$
					"aspectname,formname,tensename,voicename,personname " + //$NON-NLS-1$
					"FROM pbrolesets " + //$NON-NLS-1$
					"INNER JOIN pbexamples AS e USING (roleSetId) " + //$NON-NLS-1$
					"LEFT JOIN pbrels AS r USING (exampleid) " + //$NON-NLS-1$
					"LEFT JOIN pbargs AS a USING (exampleid) " + //$NON-NLS-1$
					"LEFT JOIN pbfuncs AS f ON (a.func = f.func) " + //$NON-NLS-1$
					"LEFT JOIN pbaspects USING (aspect) " + //$NON-NLS-1$
					"LEFT JOIN pbforms USING (form) " + //$NON-NLS-1$
					"LEFT JOIN pbtenses USING (tense) " + //$NON-NLS-1$
					"LEFT JOIN pbvoices USING (voice) " + //$NON-NLS-1$
					"LEFT JOIN pbpersons USING (person) " + //$NON-NLS-1$
					"WHERE roleSetId = ? " + //$NON-NLS-1$
					"GROUP BY e.exampleid " + //$NON-NLS-1$
					"ORDER BY e.exampleid,narg;"; //$NON-NLS-1$

	// query for examples rel(n~arg|n~arg|..)
	static final String PropBankExamplesQuery = //
			"SELECT exampleid,text,rel,GROUP_CONCAT(narg||'~'||" + //$NON-NLS-1$
					"(CASE WHEN funcname IS NULL THEN '*' ELSE funcname END)||'~'||" + //$NON-NLS-1$
					"roledescr||'~'||" + //$NON-NLS-1$
					"(CASE WHEN thetaname IS NULL THEN '*' ELSE thetaname END)||'~'||" + //$NON-NLS-1$
					"arg,'|')," + //$NON-NLS-1$
					"aspectname,formname,tensename,voicename,personname " + //$NON-NLS-1$
					"FROM pbrolesets " + //$NON-NLS-1$
					"INNER JOIN pbexamples AS e USING (roleSetId) " + //$NON-NLS-1$
					"LEFT JOIN pbrels AS r USING (exampleid) " + //$NON-NLS-1$
					"LEFT JOIN pbargs AS a USING (exampleid) " + //$NON-NLS-1$
					"LEFT JOIN pbfuncs AS f ON (a.func = f.func) " + //$NON-NLS-1$
					"LEFT JOIN pbaspects USING (aspect) " + //$NON-NLS-1$
					"LEFT JOIN pbforms USING (form) " + //$NON-NLS-1$
					"LEFT JOIN pbtenses USING (tense) " + //$NON-NLS-1$
					"LEFT JOIN pbvoices USING (voice) " + //$NON-NLS-1$
					"LEFT JOIN pbpersons USING (person) " + //$NON-NLS-1$
					"LEFT JOIN pbroles USING (roleSetId,narg) " + //$NON-NLS-1$
					"LEFT JOIN pbvnthetas USING (theta) " + //$NON-NLS-1$
					"WHERE roleSetId = ? " + //$NON-NLS-1$
					"GROUP BY e.exampleid " + //$NON-NLS-1$
					"ORDER BY e.exampleid,narg;"; //$NON-NLS-1$
}
