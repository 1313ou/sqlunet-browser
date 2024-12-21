/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.sql;

/**
 * WordNet SQL dialect
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class SqLiteDialect
{
	// query for domains enumeration
	static final String DomainsQuery = //
			"SELECT ${domains.domainid}, ${domains.domain}, ${domains.posid} " + //
					"FROM ${domains.table} " + //
					"ORDER BY ${domains.domainid};";

	// query for relations enumeration
	static final String RelationsQuery = //
			"SELECT ${relations.relationid}, ${relations.relation}, ${relations.recurses} " + //
					"FROM ${relations.table} " + //
					"ORDER BY ${relations.relationid};";

	// WORD

	// query for word id
	static final String WordQuery = //
			"SELECT ${words.wordid}, ${words.word} " + //
					"FROM ${words.table} " + //
					"WHERE ${words.wordid} = ?;";

	// query for word
	static final String WordQueryFromWord = //
			"SELECT ${words.wordid}, ${words.word} " + //
					"FROM ${words.table} " + //
					"WHERE ${words.word} = ?;";

	// SYNSET

	// query for synset from synset id
	static final String SynsetQuery = //
			"SELECT ${synsets.synsetid}, ${synsets.definition}, ${synsets.domainid}, REPLACE(REPLACE(GROUP_CONCAT(DISTINCT REPLACE(${samples.sample},',','#')),',','|'),'#',',') AS ${sampleset} " + //
					"FROM ${synsets.table} AS y " + //
					"LEFT JOIN ${samples.table} USING (${synsets.synsetid}) " + //
					"WHERE y.${synsets.synsetid} = ? " + //
					"GROUP BY ${synsets.synsetid};";

	// query for words in synsets
	static final String SynsetWordsQuery = //
			"SELECT ${words.word}, ${words.wordid} " + //
					"FROM ${senses.table} AS s " + //
					"INNER JOIN ${words.table} USING (${words.wordid}) " + //
					"WHERE s.${senses.synsetid} = ?;";

	// query for synsets from word id
	static final String SynsetsQueryFromWordId = //
			"SELECT ${synsets.synsetid}, ${synsets.definition}, ${synsets.posid}, ${synsets.domainid}, REPLACE(REPLACE(GROUP_CONCAT(DISTINCT REPLACE(${samples.sample},',','#')),',','|'),'#',',') AS ${sampleset} " + //
					"FROM ${senses.table} AS s " + //
					"INNER JOIN ${synsets.table} USING (${synsets.synsetid}) " + //
					"LEFT JOIN ${samples.table} USING (${synsets.synsetid}) " + //
					"WHERE s.${senses.wordid} = ? " + //
					"GROUP BY ${synsets.synsetid} " + //
					"ORDER BY ${synsets.domainid} ASC, ${senses.sensenum} DESC;";

	// query for synsets of given pos id from word id
	static final String SynsetsQueryFromWordIdAndPos = //
			"SELECT ${synsets.synsetid}, ${synsets.definition}, ${synsets.domainid}, REPLACE(REPLACE(GROUP_CONCAT(DISTINCT REPLACE(${samples.sample},',','#')),',','|'),'#',',') AS ${sampleset} " + //
					"FROM ${senses.table} AS s " + //
					"INNER JOIN ${synsets.table} AS y USING (${synsets.synsetid}) " + //
					"LEFT JOIN ${samples.table} USING (synsets.synsetid) " + //
					"WHERE s.${senses.wordid} = ? AND y.${synsets.posid} = ? " + //
					"GROUP BY ${synsets.synsetid} " + //
					"ORDER BY ${synsets.domainid} ASC, ${senses.sensenum} ASC;";

	// query for synsets of given domain from word
	static final String SynsetsQueryFromWordIdAndDomainId = //
			"SELECT ${synsets.synsetid}, ${synsets.definition}, ${synsets.domainid}, REPLACE(REPLACE(GROUP_CONCAT(DISTINCT REPLACE(${samples.sample},',','#')),',','|'),'#',',') AS ${sampleset} " + //
					"FROM ${senses.table} AS s " + //
					"INNER JOIN ${synsets.table} AS y USING (${synsets.synsetid}) " + //
					"LEFT JOIN ${samples.table} USING (${synsets.synsetid}) " + //
					"WHERE s.${senses.wordid} = ? AND y.${synsets.domainid} = ? " + //
					"GROUP BY ${synsets.synsetid} " + //
					"ORDER BY ${synsets.domainid} ASC, ${senses.sensenum} ASC;";

	// RELATIONS
	// query for relateds from synsets
	static final String RelatedsQueryFromSynsetId = //
			"SELECT ${relations.relationid}, ${synsets.synsetid}, ${synsets.definition}, ${synsets.domainid}, REPLACE(REPLACE(GROUP_CONCAT(DISTINCT REPLACE(${samples.sample},',','#')),',','|'),'#',',') AS ${sampleset}, 0 AS ${lexrelations.word2id}, NULL AS ${word2}, ${semrelations.synset1id}, 0 " + //
					"FROM ${semrelations.table} " + //
					"INNER JOIN ${synsets.table} ON ${semrelations.synset2id} = ${synsets.synsetid} " + //
					"LEFT JOIN ${relations.table} USING (${relations.relationid}) " + //
					"LEFT JOIN ${samples.table} USING (${synsets.synsetid}) " + //
					"WHERE ${semrelations.synset1id} = ? " + //
					"GROUP BY ${synsets.synsetid} " + //
					"UNION " + //
					"SELECT ${relations.relationid}, ${synsets.synsetid}, ${synsets.definition}, ${synsets.domainid}, REPLACE(REPLACE(GROUP_CONCAT(DISTINCT REPLACE(${samples.sample},',','#')),',','|'),'#',',') AS ${sampleset}, GROUP_CONCAT(DISTINCT ${lexrelations.word2id}), GROUP_CONCAT(DISTINCT ${words.word}) AS ${word2}, ${lexrelations.synset1id}, ${lexrelations.word1id} " + //
					"FROM ${lexrelations.table} " + //
					"INNER JOIN ${synsets.table} ON ${lexrelations.synset2id} = ${synsets.synsetid} " + //
					"LEFT JOIN ${words.table} AS w ON ${lexrelations.word2id} = w.${words.wordid} " + //
					"LEFT JOIN ${relations.table} USING (${relations.relationid}) " + //
					"LEFT JOIN ${samples.table} USING (${synsets.synsetid}) " + //
					"WHERE ${lexrelations.synset1id} = ? AND CASE ? WHEN 0 THEN 1 ELSE ${lexrelations.word1id} = ? END " + //
					"GROUP BY ${synsets.synsetid} " + //
					"ORDER BY 1, 2;";

	// query for relateds from synsets and relation type
	static final String RelatedsQueryFromSynsetIdAndRelationId = //
			"SELECT ${relations.relationid}, ${synsets.synsetid}, ${synsets.definition}, ${synsets.domainid}, REPLACE(REPLACE(GROUP_CONCAT(DISTINCT REPLACE(${samples.sample},',','#')),',','|'),'#',',') AS ${sampleset}, 0 AS ${lexrelations.word2id}, NULL AS ${word2}, ${semrelations.synset1id}, 0 " + //
					"FROM ${semrelations.table} AS sr " + //
					"INNER JOIN ${synsets.table} ON ${semrelations.synset2id} = ${synsets.synsetid} " + //
					"LEFT JOIN ${relations.table} USING (${relations.relationid}) " + //
					"LEFT JOIN ${samples.table} USING (${synsets.synsetid}) " + //
					"WHERE ${semrelations.synset1id} = ? AND sr.${relations.relationid} = ? " + //
					"GROUP BY ${synsets.synsetid} " + //
					"UNION " + //
					"SELECT ${relations.relationid}, ${synsets.synsetid}, ${synsets.definition}, ${synsets.domainid}, REPLACE(REPLACE(GROUP_CONCAT(DISTINCT REPLACE(${samples.sample},',','#')),',','|'),'#',',') AS ${sampleset}, GROUP_CONCAT(DISTINCT ${lexrelations.word2id}), GROUP_CONCAT(DISTINCT ${words.word}) AS ${word2}, ${lexrelations.synset1id}, ${lexrelations.word1id} " + //
					"FROM ${lexrelations.table} AS lr " + //
					"INNER JOIN ${synsets.table} ON ${lexrelations.synset2id} = ${synsets.synsetid} " + //
					"LEFT JOIN ${words.table} AS w ON ${lexrelations.word2id} = w.${words.wordid} " + //
					"LEFT JOIN ${relations.table} USING (${relations.relationid}) " + //
					"LEFT JOIN ${samples.table} USING (${synsets.synsetid}) " + //
					"WHERE ${lexrelations.synset1id} = ? AND lr.${relations.relationid} = ? AND CASE ? WHEN 0 THEN 1 ELSE ${lexrelations.word1id} = ? END " + //
					"GROUP BY ${synsets.synsetid} " + //
					"ORDER BY 1, 2;";

}
