/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.sn;

/**
 * SQL dialect for SyntagNet
 */
class SqLiteDialect
{
	// collocations
	// query for collocation from collocation id
	private static final String SyntagNetBaseCollocationQuery = "SELECT	${syntagms.syntagmid}, " + //
			"${syntagms.word1id}, ${as_words1}.${wnwords.word} AS ${word1}, ${syntagms.synset1id}, ${as_synsets1}.${wnsynsets.posid} AS ${pos1}, ${as_synsets1}.${wnsynsets.definition} AS ${definition1}, " + //
			"${syntagms.word2id}, ${as_words2}.${wnwords.word} AS ${word2}, ${syntagms.synset2id}, ${as_synsets2}.${wnsynsets.posid} AS ${pos2}, ${as_synsets2}.${wnsynsets.definition} AS ${definition2} " + //
			"FROM ${syntagms.table} " + //
			"JOIN ${wnwords.table} AS ${as_words1} ON (${syntagms.word1id} = ${as_words1}.${wnwords.wordid}) " + //
			"JOIN ${wnwords.table} AS ${as_words2} ON (${syntagms.word2id} = ${as_words2}.${wnwords.wordid}) " + //
			"JOIN ${wnsynsets.table} AS ${as_synsets1} ON (${syntagms.synset1id} = ${as_synsets1}.${wnsynsets.synsetid}) " + //
			"JOIN ${wnsynsets.table} AS ${as_synsets2} ON (${syntagms.synset2id} = ${as_synsets2}.${wnsynsets.synsetid}) ";

	private static final String SyntagNetBaseCollocationOrder = "ORDER BY ${as_words1}.${wnwords.word}, ${as_words2}.${wnwords.word}";

	// query for collocation from collocation id
	static final String SyntagNetCollocationQuery = SyntagNetBaseCollocationQuery + //
			"WHERE ${syntagms.syntagmid} = ? " + //
			SyntagNetBaseCollocationOrder + ";";

	// query for collocation from word
	static final String SyntagNetCollocationQueryFromWord = SyntagNetBaseCollocationQuery +//
			"WHERE ${as_words1}.${wnwords.word} = ? OR ${as_words2}.${wnwords.word} = ? " + //
			SyntagNetBaseCollocationOrder + ";";

	// query for collocation from word id
	static final String SyntagNetCollocationQueryFromWordId = SyntagNetBaseCollocationQuery +//
			"WHERE ${as_words1}.${wnwords.wordid} = ? OR ${as_words2}.${wnwords.wordid} = ? " + //
			SyntagNetBaseCollocationOrder + ";";

	// query for collocation from word ids
	static final String SyntagNetCollocationQueryFromWordIds = SyntagNetBaseCollocationQuery +//
			"WHERE ${as_words1}.${wnwords.wordid} = ? OR ${as_words2}.${wnwords.wordid} = ? " + //
			SyntagNetBaseCollocationOrder + ";";

	// query for collocation from word id and synset id
	static final String SyntagNetCollocationQueryFromWordIdAndSynsetId = SyntagNetBaseCollocationQuery +//
			"WHERE (${as_words1}.${wnwords.wordid} = ? AND ${as_synsets1}.${wnsynsets.synsetid} = ?) OR (${as_words2}.${wnwords.wordid} = ? AND ${as_synsets2}.${wnsynsets.synsetid} = ?) " + //
			SyntagNetBaseCollocationOrder + ";";

	// query for collocation from word ids and synset ids
	static final String SyntagNetCollocationQueryFromWordIdsAndSynsetIds = SyntagNetBaseCollocationQuery +//
			"WHERE (${as_words1}.${wnwords.wordid} = ? AND ${as_synsets1}.${wnsynsets.synsetid} = ?) AND (${as_words2}.${wnwords.wordid} = ? AND ${as_synsets2}.${wnsynsets.synsetid} = ?) " + //
			SyntagNetBaseCollocationOrder + ";";
}
