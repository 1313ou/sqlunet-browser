package org.sqlunet.wordnet.sql;

class SqLiteDialect
{
	// query for lexical domain enumeration
	static final String LexDomainEnumQuery = "SELECT lexdomainid, lexdomainname, pos " +  //$NON-NLS-1$
			"FROM lexdomains " +  //$NON-NLS-1$
			"ORDER BY lexdomainid;"; //$NON-NLS-1$

	// query for link enumeration
	static final String LinkEnumQuery = "SELECT linkid, link, recurses " +  //$NON-NLS-1$
			"FROM linktypes " +  //$NON-NLS-1$
			"ORDER BY linkid;"; //$NON-NLS-1$

	// query for word id
	static final String WordQuery = "SELECT wordid, lemma " +
			"FROM words " +
			"WHERE lemma = ?;"; //$NON-NLS-1$

	// query for words in synsets
	static final String SynsetWordsQuery = "SELECT lemma, wordid " +
			"FROM senses " +
			"INNER JOIN words USING (wordid) " +
			"WHERE synsetid = ?;"; //$NON-NLS-1$

	// query for synsets from word
	static final String SynsetsQuery = "SELECT synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS sampleset " +
			"FROM senses " +
			"INNER JOIN synsets USING (synsetid) " +
			"LEFT JOIN samples USING (synsetid) " +
			"WHERE wordid = ? " + //$NON-NLS-1$
			"GROUP BY synsetid " +
			"ORDER BY lexdomainid ASC, sensenum DESC;"; //$NON-NLS-1$

	// query for synset from synseid
	static final String SynsetQuery = "SELECT synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS sampleset " +
			"FROM synsets " +
			"LEFT JOIN samples USING (synsetid) " +
			"WHERE synsetid = ? " +
			"GROUP BY synsetid;"; //$NON-NLS-1$

	// query for synsets of given pos type from word
	static final String PosTypedSynsetsQuery = "SELECT synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS sampleset " + //$NON-NLS-1$
			"FROM senses " +
			"INNER JOIN synsets USING (synsetid) " + //$NON-NLS-1$
			"LEFT JOIN samples USING (synsetid) " + //$NON-NLS-1$
			"WHERE wordid = ? AND pos = ? " + //$NON-NLS-1$
			"GROUP BY synsetid " + //$NON-NLS-1$
			"ORDER BY lexdomainid ASC, sensenum ASC;"; //$NON-NLS-1$

	// query for synsets of given lexdomain from word
	static final String LexDomainTypedSynsetsQuery = "SELECT synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS sampleset " + //$NON-NLS-1$
			"FROM senses " + //$NON-NLS-1$
			"INNER JOIN synsets USING (synsetid) " + //$NON-NLS-1$
			"LEFT JOIN samples USING (synsetid) " + //$NON-NLS-1$
			"WHERE wordid = ? AND lexdomainid = ? " + //$NON-NLS-1$
			"GROUP BY synsetid " + //$NON-NLS-1$
			"ORDER BY lexdomainid ASC, sensenum ASC;"; //$NON-NLS-1$

	// query for links from synsets
	static final String LinksQuery = "SELECT synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS sampleset, linkid, synset1id " +  //$NON-NLS-1$
			"FROM semlinks " + //$NON-NLS-1$
			"INNER JOIN synsets ON synset2id = synsetid " +  //$NON-NLS-1$
			"LEFT JOIN linktypes USING (linkid) " + //$NON-NLS-1$
			"LEFT JOIN samples USING (synsetid) " + //$NON-NLS-1$
			"WHERE synset1id = ? " + //$NON-NLS-1$
			"GROUP BY synsetid " + //$NON-NLS-1$
			"UNION " + //$NON-NLS-1$
			"SELECT synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS sampleset, linkid, synset1id " + //$NON-NLS-1$
			"FROM lexlinks " + //$NON-NLS-1$
			"INNER JOIN synsets ON synset2id = synsetid " + //$NON-NLS-1$
			"LEFT JOIN linktypes USING (linkid) " + //$NON-NLS-1$
			"LEFT JOIN samples USING (synsetid) " + //$NON-NLS-1$
			"WHERE synset1id = ? AND CASE ? WHEN -1 THEN word1id = ? ELSE 1 END " + //$NON-NLS-1$
			"GROUP BY synsetid " + //$NON-NLS-1$
			"ORDER BY 5, 1;"; //$NON-NLS-1$

	// query for links from synsets and type
	static final String TypedLinksQuery = "SELECT synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS sampleset, linkid, synset1id " + //$NON-NLS-1$
			"FROM semlinks " + "INNER JOIN synsets ON synset2id = synsetid " + //$NON-NLS-1$
			"LEFT JOIN linktypes USING (linkid) " + //$NON-NLS-1$
			"LEFT JOIN samples USING (synsetid) " + //$NON-NLS-1$
			"WHERE synset1id = ? AND linkid = ? " + //$NON-NLS-1$
			"GROUP BY synsetid " + //$NON-NLS-1$
			"UNION " + //$NON-NLS-1$
			"SELECT synsetid, definition, lexdomainid, GROUP_CONCAT(sample, '|' ) AS sampleset, linkid, synset1id " + //$NON-NLS-1$
			"FROM lexlinks " + //$NON-NLS-1$
			"INNER JOIN synsets ON synset2id = synsetid " + //$NON-NLS-1$
			"LEFT JOIN linktypes USING (linkid) " + //$NON-NLS-1$
			"LEFT JOIN samples USING (synsetid) " + //$NON-NLS-1$
			"WHERE synset1id = ? AND linkid = ? AND CASE ? WHEN -1 THEN word1id = ? ELSE 1 END " + //$NON-NLS-1$
			"GROUP BY synsetid " + //$NON-NLS-1$
			"ORDER BY 5, 1;"; //$NON-NLS-1$

	// query for link types for word
	static final String LinkTypesQuery = "SELECT lexdomainid, linkid " + //$NON-NLS-1$
			"FROM words " + //$NON-NLS-1$
			"LEFT JOIN senses USING (wordid) " + //$NON-NLS-1$
			"LEFT JOIN synsets USING (synsetid) " + //$NON-NLS-1$
			"LEFT JOIN semlinks ON synsetid = synset1id " + //$NON-NLS-1$
			"WHERE lemma = ? " + //$NON-NLS-1$
			"GROUP BY lexdomainid, linkid " + //$NON-NLS-1$
			"UNION " + //$NON-NLS-1$
			"SELECT lexdomainid, linkid " + //$NON-NLS-1$
			"FROM words " + //$NON-NLS-1$
			"LEFT JOIN senses USING (wordid) " + //$NON-NLS-1$
			"LEFT JOIN synsets USING (synsetid) " + //$NON-NLS-1$
			"LEFT JOIN lexlinks ON synsetid = synset1id AND wordid = word1id " + //$NON-NLS-1$
			"WHERE lemma = ? " + //$NON-NLS-1$
			"GROUP BY lexdomainid, linkid " + //$NON-NLS-1$
			"ORDER BY 1, 2;"; //$NON-NLS-1$
}
