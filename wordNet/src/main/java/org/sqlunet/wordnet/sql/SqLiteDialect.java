package org.sqlunet.wordnet.sql;

class SqLiteDialect
{
	// query for lexical domain enumeration
	static final String LexDomainEnumQuery = "SELECT lexdomainid, lexdomainname, pos " + "FROM lexdomains " + "ORDER BY lexdomainid;"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	// query for link enumeration
	static final String LinkEnumQuery = "SELECT linkid, link, recurses " + "FROM linktypes " + "ORDER BY linkid;"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	// query for word id
	static final String WordQuery = "SELECT wordid, lemma " + "FROM words " + "WHERE lemma = ?;"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	// query for words in synsets
	static final String SynsetWordsQuery = "SELECT lemma, wordid " + "FROM senses " + "INNER JOIN words USING (wordid) " + "WHERE synsetid = ?;"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	// query for synsets from word
	static final String SynsetsQuery = "SELECT synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS sampleset " + "FROM senses " + "INNER JOIN synsets USING (synsetid) " + "LEFT JOIN samples USING (synsetid) " + "WHERE wordid = ? " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			+ "GROUP BY synsetid " + "ORDER BY lexdomainid ASC, sensenum DESC;"; //$NON-NLS-1$ //$NON-NLS-2$

	// query for synset from synseid
	static final String SynsetQuery = "SELECT synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS sampleset " + "FROM synsets " + "LEFT JOIN samples USING (synsetid) " + "WHERE synsetid = ? " + "GROUP BY synsetid;"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

	// query for synsets of given pos type from word
	static final String PosTypedSynsetsQuery = "SELECT synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS sampleset " + "FROM senses " + "INNER JOIN synsets USING (synsetid) " + "LEFT JOIN samples USING (synsetid) " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			+ "WHERE wordid = ? AND pos = ? " + "GROUP BY synsetid " + "ORDER BY lexdomainid ASC, sensenum ASC;"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	// query for synsets of given lexdomain from word
	static final String LexDomainTypedSynsetsQuery = "SELECT synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS sampleset " + "FROM senses " + "INNER JOIN synsets USING (synsetid) " + "LEFT JOIN samples USING (synsetid) " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			+ "WHERE wordid = ? AND lexdomainid = ? " + "GROUP BY synsetid " + "ORDER BY lexdomainid ASC, sensenum ASC;"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	// query for links from synsets
	static final String LinksQuery = "SELECT synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS sampleset, linkid, synset1id " + "FROM semlinks " + "INNER JOIN synsets ON synset2id = synsetid " + "LEFT JOIN linktypes USING (linkid) " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			+ "LEFT JOIN samples USING (synsetid) " + "WHERE synset1id = ? " + "GROUP BY synsetid " + "UNION " + "SELECT synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS sampleset, linkid, synset1id " + "FROM lexlinks " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			+ "INNER JOIN synsets ON synset2id = synsetid " + "LEFT JOIN linktypes USING (linkid) " + "LEFT JOIN samples USING (synsetid) " + "WHERE synset1id = ? AND CASE ? WHEN -1 THEN word1id = ? ELSE 1 END " + "GROUP BY synsetid " + "ORDER BY 5, 1;"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

	// query for links from synsets and type
	static final String TypedLinksQuery = "SELECT synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS sampleset, linkid, synset1id " + "FROM semlinks " + "INNER JOIN synsets ON synset2id = synsetid " + "LEFT JOIN linktypes USING (linkid) " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			+ "LEFT JOIN samples USING (synsetid) " + "WHERE synset1id = ? AND linkid = ? " + "GROUP BY synsetid " + "UNION " + "SELECT synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS sampleset, linkid, synset1id " + "FROM lexlinks " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			+ "INNER JOIN synsets ON synset2id = synsetid " + "LEFT JOIN linktypes USING (linkid) " + "LEFT JOIN samples USING (synsetid) " + "WHERE synset1id = ? AND linkid = ? AND CASE ? WHEN -1 THEN word1id = ? ELSE 1 END " + "GROUP BY synsetid " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			+ "ORDER BY 5, 1;"; //$NON-NLS-1$

	// query for link types for word
	static final String LinkTypesQuery = "SELECT lexdomainid, linkid " + "FROM words " + "LEFT JOIN senses USING (wordid) " + "LEFT JOIN synsets USING (synsetid) " + "LEFT JOIN semlinks ON synsetid = synset1id " + "WHERE lemma = ? " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			+ "GROUP BY lexdomainid, linkid " + "UNION " + "SELECT lexdomainid, linkid " + "FROM words " + "LEFT JOIN senses USING (wordid) " + "LEFT JOIN synsets USING (synsetid) " + "LEFT JOIN lexlinks ON synsetid = synset1id AND wordid = word1id " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
			+ "WHERE lemma = ? " + "GROUP BY lexdomainid, linkid " + "ORDER BY 1, 2;"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
}
