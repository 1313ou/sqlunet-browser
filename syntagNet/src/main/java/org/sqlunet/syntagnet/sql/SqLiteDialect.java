/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.syntagnet.sql;

/**
 * SQL dialect for SyntagNet
 */
class SqLiteDialect
{
	// collocations
	// query for collocation from collocation id
	private static final String SyntagNetBaseCollocationQuery = "SELECT	syntagmid, " + //
			"word1id, w1.lemma AS lemma1, synset1id, s1.pos AS pos1, s1.definition AS definition1, " + //
			"word2id, w2.lemma AS lemma2, synset2id, s2.pos AS pos2, s2.definition AS definition2 " + //
			"FROM syntagms " + //
			"JOIN words AS w1 ON (word1id = w1.wordid) " + //
			"JOIN words AS w2 ON (word2id = w2.wordid) " + //
			"JOIN synsets AS s1 ON (synset1id = s1.synsetid) " + //
			"JOIN synsets AS s2 ON (synset2id = s2.synsetid) ";

	private static final String SyntagNetBaseCollocationOrder = "ORDER BY w1.lemma, w2.lemma";

	// query for collocation from collocation id
	static final String SyntagNetCollocationQuery = SyntagNetBaseCollocationQuery + //
			"WHERE syntagmid = ? " + //
			SyntagNetBaseCollocationOrder + ";";

	// query for collocation from word
	static final String SyntagNetCollocationQueryFromWord = SyntagNetBaseCollocationQuery +//
			"WHERE w1.lemma = ? OR w2.lemma = ? " + //
			SyntagNetBaseCollocationOrder + ";";

	// query for collocation from word id
	static final String SyntagNetCollocationQueryFromWordId = SyntagNetBaseCollocationQuery +//
			"WHERE w1.wordid = ? OR w2.wordid = ? " + //
			SyntagNetBaseCollocationOrder + ";";

	// query for collocation from word ids
	static final String SyntagNetCollocationQueryFromWordIds = SyntagNetBaseCollocationQuery +//
			"WHERE w1.wordid = ? OR w2.word2id = ? " + //
			SyntagNetBaseCollocationOrder + ";";

	// query for collocation from word id and synset id
	static final String SyntagNetCollocationQueryFromWordIdAndSynsetId = SyntagNetBaseCollocationQuery +//
			"WHERE (w1.wordid = ? AND s1.synsetid = ?) OR (w2.wordid = ? AND s2.synsetid = ?) " + //
			SyntagNetBaseCollocationOrder + ";";

	// query for collocation from word ids and synset ids
	static final String SyntagNetCollocationQueryFromWordIdsAndSynsetIds = SyntagNetBaseCollocationQuery +//
			"WHERE (w1.wordid = ? AND s1.synsetid = ?) AND (w2.wordid = ? AND s2.synsetid = ?) " + //
			SyntagNetBaseCollocationOrder + ";";
}
