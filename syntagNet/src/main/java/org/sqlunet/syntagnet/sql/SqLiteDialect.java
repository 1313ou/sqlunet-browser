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
			"word1id, w1.word AS word1, synset1id, y1.posid AS pos1, y1.definition AS definition1, " + //
			"word2id, w2.word AS word2, synset2id, y2.posid AS pos2, y2.definition AS definition2 " + //
			"FROM sn_syntagms " + //
			"JOIN words AS w1 ON (word1id = w1.wordid) " + //
			"JOIN words AS w2 ON (word2id = w2.wordid) " + //
			"JOIN synsets AS y1 ON (synset1id = y1.synsetid) " + //
			"JOIN synsets AS y2 ON (synset2id = y2.synsetid) ";

	private static final String SyntagNetBaseCollocationOrder = "ORDER BY w1.word, w2.word";

	// query for collocation from collocation id
	static final String SyntagNetCollocationQuery = SyntagNetBaseCollocationQuery + //
			"WHERE syntagmid = ? " + //
			SyntagNetBaseCollocationOrder + ";";

	// query for collocation from word
	static final String SyntagNetCollocationQueryFromWord = SyntagNetBaseCollocationQuery +//
			"WHERE w1.word = ? OR w2.word = ? " + //
			SyntagNetBaseCollocationOrder + ";";

	// query for collocation from word id
	static final String SyntagNetCollocationQueryFromWordId = SyntagNetBaseCollocationQuery +//
			"WHERE w1.wordid = ? OR w2.wordid = ? " + //
			SyntagNetBaseCollocationOrder + ";";

	// query for collocation from word ids
	static final String SyntagNetCollocationQueryFromWordIds = SyntagNetBaseCollocationQuery +//
			"WHERE w1.wordid = ? OR w2.wordid = ? " + //
			SyntagNetBaseCollocationOrder + ";";

	// query for collocation from word id and synset id
	static final String SyntagNetCollocationQueryFromWordIdAndSynsetId = SyntagNetBaseCollocationQuery +//
			"WHERE (w1.wordid = ? AND y1.synsetid = ?) OR (w2.wordid = ? AND y2.synsetid = ?) " + //
			SyntagNetBaseCollocationOrder + ";";

	// query for collocation from word ids and synset ids
	static final String SyntagNetCollocationQueryFromWordIdsAndSynsetIds = SyntagNetBaseCollocationQuery +//
			"WHERE (w1.wordid = ? AND y1.synsetid = ?) AND (w2.wordid = ? AND y2.synsetid = ?) " + //
			SyntagNetBaseCollocationOrder + ";";
}
