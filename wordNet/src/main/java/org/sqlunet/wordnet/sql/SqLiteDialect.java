package org.sqlunet.wordnet.sql;

/**
 * WordNet SQL dialect
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class SqLiteDialect
{
	// query for lexical domain enumeration
	static final String LexDomainEnumQuery = "SELECT lexdomainid, lexdomainname, pos " +  //
			"FROM lexdomains " +  //
			"ORDER BY lexdomainid;";

	// query for link enumeration
	static final String LinkEnumQuery = "SELECT linkid, link, recurses " +  //
			"FROM linktypes " +  //
			"ORDER BY linkid;";

	// query for word id
	static final String WordQuery = "SELECT wordid, lemma " +
			"FROM words " +
			"WHERE lemma = ?;";

	// query for words in synsets
	static final String SynsetWordsQuery = "SELECT lemma, wordid " +
			"FROM senses " +
			"INNER JOIN words USING (wordid) " +
			"WHERE synsetid = ?;";

	// query for synsets from word
	static final String SynsetsQuery = "SELECT synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS sampleset " +
			"FROM senses " +
			"INNER JOIN synsets USING (synsetid) " +
			"LEFT JOIN samples USING (synsetid) " +
			"WHERE wordid = ? " + //
			"GROUP BY synsetid " +
			"ORDER BY lexdomainid ASC, sensenum DESC;";

	// query for synset from synset id
	static final String SynsetQuery = "SELECT synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS sampleset " +
			"FROM synsets " +
			"LEFT JOIN samples USING (synsetid) " +
			"WHERE synsetid = ? " +
			"GROUP BY synsetid;";

	// query for synsets of given pos type from word
	static final String PosTypedSynsetsQuery = "SELECT synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS sampleset " + //
			"FROM senses " +
			"INNER JOIN synsets USING (synsetid) " + //
			"LEFT JOIN samples USING (synsetid) " + //
			"WHERE wordid = ? AND pos = ? " + //
			"GROUP BY synsetid " + //
			"ORDER BY lexdomainid ASC, sensenum ASC;";

	// query for synsets of given lexdomain from word
	static final String LexDomainTypedSynsetsQuery = "SELECT synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS sampleset " + //
			"FROM senses " + //
			"INNER JOIN synsets USING (synsetid) " + //
			"LEFT JOIN samples USING (synsetid) " + //
			"WHERE wordid = ? AND lexdomainid = ? " + //
			"GROUP BY synsetid " + //
			"ORDER BY lexdomainid ASC, sensenum ASC;";

	// query for links from synsets
	static final String LinksQuery = "SELECT linkid, synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS sampleset, 0 AS word2id, NULL AS word2, synset1id, 0 " +  //
			"FROM semlinks " + //
			"INNER JOIN synsets ON synset2id = synsetid " +  //
			"LEFT JOIN linktypes USING (linkid) " + //
			"LEFT JOIN samples USING (synsetid) " + //
			"WHERE synset1id = ? " + //
			"GROUP BY synsetid " + //
			"UNION " + //
			"SELECT linkid, synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS sampleset, GROUP_CONCAT(DISTINCT word2id), GROUP_CONCAT(DISTINCT lemma) AS word2, synset1id, word1id " + //
			"FROM lexlinks " + //
			"INNER JOIN synsets ON synset2id = synsetid " + //
			"LEFT JOIN words ON word2id = wordid " + //
			"LEFT JOIN linktypes USING (linkid) " + //
			"LEFT JOIN samples USING (synsetid) " + //
			"WHERE synset1id = ? AND CASE ? WHEN 0 THEN 1 ELSE word1id = ? END " + //
			"GROUP BY synsetid " + //
			"ORDER BY 1, 2;";

	// query for links from synsets and type
	static final String TypedLinksQuery = "SELECT linkid, synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS samples, 0 AS word2id, NULL AS word2, synset1id, 0 " + //
			"FROM semlinks " + "INNER JOIN synsets ON synset2id = synsetid " + //
			"LEFT JOIN linktypes USING (linkid) " + //
			"LEFT JOIN samples USING (synsetid) " + //
			"WHERE synset1id = ? AND linkid = ? " + //
			"GROUP BY synsetid " + //
			"UNION " + //
			"SELECT linkid, synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS samples, GROUP_CONCAT(DISTINCT word2id), GROUP_CONCAT(DISTINCT lemma) AS word2, synset1id, word1id " + //
			"FROM lexlinks " + //
			"INNER JOIN synsets ON synset2id = synsetid " + //
			"LEFT JOIN words ON word2id = wordid " + //
			"LEFT JOIN linktypes USING (linkid) " + //
			"LEFT JOIN samples USING (synsetid) " + //
			"WHERE synset1id = ? AND linkid = ? AND CASE ? WHEN 0 THEN 1 ELSE word1id = ? END " + //
			"GROUP BY synsetid " + //
			"ORDER BY 1, 2;";

	// query for link types for word
	static final String LinkTypesQuery = "SELECT linkid, lexdomainid " + //
			"FROM words " + //
			"LEFT JOIN senses USING (wordid) " + //
			"LEFT JOIN synsets USING (synsetid) " + //
			"LEFT JOIN semlinks ON synsetid = synset1id " + //
			"WHERE lemma = ? " + //
			"GROUP BY lexdomainid, linkid " + //
			"UNION " + //
			"SELECT lexdomainid, linkid " + //
			"FROM words " + //
			"LEFT JOIN senses USING (wordid) " + //
			"LEFT JOIN synsets USING (synsetid) " + //
			"LEFT JOIN lexlinks ON synsetid = synset1id AND wordid = word1id " + //
			"WHERE lemma = ? " + //
			"GROUP BY lexdomainid, linkid " + //
			"ORDER BY 1, 2;";
}
