/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.propbank.sql;

/**
 * SQL dialect for PropBank
 */
class SqLiteDialect0
{
	// ROLE SETS
	// query for role set from role set id
	static final String PropBankRoleSetQuery = //
			"SELECT rolesetid, rolesetname, rolesethead, rolesetdescr " + //
					"FROM pb_rolesets " + //
					"WHERE rolesetid = ? ;";
	// query for role set from word
	static final String PropBankRoleSetQueryFromWord = //
			"SELECT wordid, rolesetid, rolesetname, rolesethead, rolesetdescr " + //
					"FROM words AS w " + //
					"INNER JOIN pb_words USING (wordid) " + //
					"LEFT JOIN pb_rolesets USING (pbwordid) " + //
					"WHERE w.word = ? ";
	// query for role set from word id
	static final String PropBankRoleSetQueryFromWordId = //
			"SELECT rolesetid, rolesetname, rolesethead, rolesetdescr " + //
					"FROM words " + //
					"INNER JOIN pb_words USING (wordid) " + //
					"INNER JOIN pb_rolesets USING (pbwordid) " + //
					"WHERE wordid = ? ;";

	// ROLES
	// query for roles
	static final String PropBankRolesQueryFromRoleSetId = //
			"SELECT roleid,roledescr,nargid,func,thetaid " + //
					"FROM pb_rolesets " + //
					"INNER JOIN pb_roles USING (rolesetid) " + //
					"LEFT JOIN pb_funcs USING (funcid) " + //
					"LEFT JOIN pb_thetas USING (theta) " + //
					"WHERE rolesetid = ? " + //
					"ORDER BY nargid;";

	// EXAMPLES
	// query for examples rel(n~arg|n~arg|..)
	static final String PropBankExamplesQueryFromRoleSetId = //
			"SELECT exampleid,text,rel,GROUP_CONCAT(nargid||'~'||" + //
					"(CASE WHEN func IS NULL THEN '*' ELSE func END)||'~'||" + //
					"roledescr||'~'||" + //
					"(CASE WHEN theta IS NULL THEN '*' ELSE theta END)||'~'||" + //
					"arg,'|')," + //
					"aspect,form,tense,voice,person " + //
					"FROM pb_rolesets " + //
					"INNER JOIN pb_examples AS e USING (rolesetid) " + //
					"LEFT JOIN pb_rels AS r USING (exampleid) " + //
					"LEFT JOIN pb_args AS ar USING (exampleid) " + //
					"LEFT JOIN pb_funcs AS fu ON (ar.func = fu.func) " + //
					"LEFT JOIN pb_aspects USING (aspect) " + //
					"LEFT JOIN pb_forms USING (form) " + //
					"LEFT JOIN pb_tenses USING (tense) " + //
					"LEFT JOIN pb_voices USING (voice) " + //
					"LEFT JOIN pb_persons USING (person) " + //
					"LEFT JOIN pb_roles USING (rolesetid,nargid) " + //
					"LEFT JOIN pb_thetas USING (theta) " + //
					"WHERE rolesetid = ? " + //
					"GROUP BY e.exampleid " + //
					"ORDER BY e.exampleid,nargid;";
}
