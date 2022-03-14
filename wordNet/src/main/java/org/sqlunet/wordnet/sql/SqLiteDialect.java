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
	// query for lexical domains enumeration
	static final String AllDomainsQuery = "SELECT domainid, domainname, pos " +  //
			"FROM domains " +  //
			"ORDER BY domainid;";
	// query for relations enumeration
	static final String AllRelationsQuery = "SELECT relationid, relation, recurses " +  //
			"FROM relations " +  //
			"ORDER BY relationid;";

	// WORD
	// query for word id
	static final String WordQuery = "SELECT wordid, lemma " +  //
			"FROM words " +  //
			"WHERE wordid = ?;";
	// query for word
	static final String WordQueryFromLemma = "SELECT wordid, lemma " +  //
			"FROM words " +  //
			"WHERE lemma = ?;";

	// SYNSET
	// query for synset from synset id
	static final String SynsetQuery = "SELECT synsetid, definition, domainid, GROUP_CONCAT(sample, '|' ) AS sampleset " +  //
			"FROM synsets " +  //
			"LEFT JOIN samples USING (synsetid) " +  //
			"WHERE synsetid = ? " +  //
			"GROUP BY synsetid;";
	// query for words in synsets
	static final String SynsetWordsQuery = "SELECT lemma, wordid " +  //
			"FROM senses " +  //
			"INNER JOIN words USING (wordid) " + //
			"WHERE synsetid = ?;";
	// query for synsets from word id
	static final String SynsetsQueryFromWordId = "SELECT synsetid, definition, pos, domainid, GROUP_CONCAT(sample, '|' ) AS sampleset " +  //
			"FROM senses " +  //
			"INNER JOIN synsets USING (synsetid) " + //
			"LEFT JOIN samples USING (synsetid) " + //
			"WHERE wordid = ? " + //
			"GROUP BY synsetid " +  //
			"ORDER BY domainid ASC, sensenum DESC;";
	// query for synsets of given pos type from word id
	static final String SynsetsQueryFromWordIdAndPos = "SELECT synsetid, definition, domainid, GROUP_CONCAT(sample, '|' ) AS sampleset " + //
			"FROM senses " + //
			"INNER JOIN synsets USING (synsetid) " + //
			"LEFT JOIN samples USING (synsetid) " + //
			"WHERE wordid = ? AND pos = ? " + //
			"GROUP BY synsetid " + //
			"ORDER BY domainid ASC, sensenum ASC;";
	// query for synsets of given domain from word
	static final String SynsetsQueryFromWordIdAndDomainId = "SELECT synsetid, definition, domainid, GROUP_CONCAT(sample, '|' ) AS sampleset " + //
			"FROM senses " + //
			"INNER JOIN synsets USING (synsetid) " + //
			"LEFT JOIN samples USING (synsetid) " + //
			"WHERE wordid = ? AND domainid = ? " + //
			"GROUP BY synsetid " + //
			"ORDER BY domainid ASC, sensenum ASC;";

	// RELATIONS
	// query for relateds from synsets
	static final String RelatedsQueryFromSynsetId = "SELECT relationid, synsetid, definition, domainid, GROUP_CONCAT(sample, '|' ) AS sampleset, 0 AS word2id, NULL AS word2, synset1id, 0 " +  //
			"FROM semrelations " + //
			"INNER JOIN synsets ON synset2id = synsetid " +  //
			"LEFT JOIN relations USING (relationid) " + //
			"LEFT JOIN samples USING (synsetid) " + //
			"WHERE synset1id = ? " + //
			"GROUP BY synsetid " + //
			"UNION " + //
			"SELECT relationid, synsetid, definition, domainid, GROUP_CONCAT(sample, '|' ) AS sampleset, GROUP_CONCAT(DISTINCT word2id), GROUP_CONCAT(DISTINCT lemma) AS word2, synset1id, word1id " + //
			"FROM senses_senses " + //
			"INNER JOIN synsets ON synset2id = synsetid " + //
			"LEFT JOIN words ON word2id = wordid " + //
			"LEFT JOIN relations USING (relationid) " + //
			"LEFT JOIN samples USING (synsetid) " + //
			"WHERE synset1id = ? AND CASE ? WHEN 0 THEN 1 ELSE word1id = ? END " + //
			"GROUP BY synsetid " + //
			"ORDER BY 1, 2;";
	// query for relateds from synsets and relation type
	static final String RelatedsQueryFromSynsetIdAndRelationId = "SELECT relationid, synsetid, definition, domainid, GROUP_CONCAT(sample, '|' ) AS samples, 0 AS word2id, NULL AS word2, synset1id, 0 " + //
			"FROM synsets_synsets " + //
			"INNER JOIN synsets ON synset2id = synsetid " + //
			"LEFT JOIN relations USING (relationid) " + //
			"LEFT JOIN samples USING (synsetid) " + //
			"WHERE synset1id = ? AND relationid = ? " + //
			"GROUP BY synsetid " + //
			"UNION " + //
			"SELECT relationid, synsetid, definition, domainid, GROUP_CONCAT(sample, '|' ) AS samples, GROUP_CONCAT(DISTINCT word2id), GROUP_CONCAT(DISTINCT lemma) AS word2, synset1id, word1id " + //
			"FROM synsets_synsets " + //
			"INNER JOIN synsets ON synset2id = synsetid " + //
			"LEFT JOIN words ON word2id = wordid " + //
			"LEFT JOIN relations USING (relationid) " + //
			"LEFT JOIN samples USING (synsetid) " + //
			"WHERE synset1id = ? AND relationid = ? AND CASE ? WHEN 0 THEN 1 ELSE word1id = ? END " + //
			"GROUP BY synsetid " + //
			"ORDER BY 1, 2;";
}
