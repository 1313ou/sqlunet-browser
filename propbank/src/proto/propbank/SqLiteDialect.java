/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.propbank.sql;

/**
 * SQL dialect for PropBank
 */
class SqLiteDialect
{
	// ROLE SETS
	// query for role set from role set id
	static final String PropBankRoleSetQuery = "SELECT ${rolesets.rolesetid}, ${rolesets.rolesetname}, ${rolesets.rolesethead}, ${rolesets.rolesetdescr} " +
			"FROM ${rolesets.table} " +
			"WHERE ${rolesets.rolesetid} = ? ;";
	// query for role set from word
	static final String PropBankRoleSetQueryFromWord = "SELECT ${wnwords.wordid}, ${rolesets.rolesetid}, ${rolesets.rolesetname}, ${rolesets.rolesethead}, ${rolesets.rolesetdescr} " +
			"FROM ${wnwords.table} AS ${as_words} " + "INNER JOIN ${words.table} USING (${wnwords.wordid}) " +
			"LEFT JOIN ${rolesets.table} USING (${words.pbwordid}) " +
			"WHERE ${as_words}.${wnwords.word} = ? ";
	// query for role set from word id
	static final String PropBankRoleSetQueryFromWordId = "SELECT ${rolesets.rolesetid}, ${rolesets.rolesetname}, ${rolesets.rolesethead}, ${rolesets.rolesetdescr} " +
			"FROM ${wnwords.table} " + "INNER JOIN ${words.table} USING (${wnwords.wordid}) " +
			"INNER JOIN ${rolesets.table} USING (${words.pbwordid}) " +
			"WHERE ${wnwords.wordid} = ? ;";

	// ROLES
	// query for roles
	static final String PropBankRolesQueryFromRoleSetId = "SELECT ${roles.roleid},${roles.roledescr},${argtypes.argtypeid},${funcs.func},${vnroles.vnroleid} " +
			"FROM ${rolesets.table} " + "INNER JOIN ${roles.table} USING (${rolesets.rolesetid}) " +
			"LEFT JOIN ${funcs.table} USING (${funcs.funcid}) " +
			"LEFT JOIN ${vnroles.table} USING (${vnroles.vnroleid}) " +
			"WHERE ${rolesets.rolesetid} = ? " +
			"ORDER BY ${argtypes.argtypeid};";

	// EXAMPLES
	// query for examples rel(n~arg|n~arg|..)
	static final String PropBankExamplesQueryFromRoleSetId = "SELECT ${examples.exampleid},${examples.text},${rels.rel}," +
			"GROUP_CONCAT(DISTINCT ${args.argtypeid}||'~'||(CASE WHEN ${funcs.func} IS NULL THEN '*' ELSE ${funcs.func} END)||'~'||${roles.roledescr}||'~'||(CASE WHEN ${vnroles.vnrole} IS NULL THEN '*' ELSE ${vnroles.vnrole} END)||'~'||${args.arg})," +
			"${aspects.aspect},${forms.form},${tenses.tense},${voices.voice},${persons.person} " +
			"FROM ${rolesets.table} " + "INNER JOIN ${examples.table} AS ${as_examples} USING (${rolesets.rolesetid}) " +
			"LEFT JOIN ${rels.table} AS ${as_relations} USING (${examples.exampleid}) " +
			"LEFT JOIN ${args.table} AS ${as_args} USING (${examples.exampleid}) " +
			"LEFT JOIN ${funcs.table} AS ${as_funcs} USING (${funcs.funcid}) " +
			"LEFT JOIN ${roles.table} USING (${rolesets.rolesetid},${args.argtypeid}) " +
			"LEFT JOIN ${vnroles.table} USING (${vnroles.vnroleid}) " +
			"WHERE ${rolesets.rolesetid} = ? " +
			"GROUP BY ${as_examples}.${examples.exampleid} " +
			"ORDER BY ${as_examples}.${examples.exampleid},${args.argtypeid};";
}
