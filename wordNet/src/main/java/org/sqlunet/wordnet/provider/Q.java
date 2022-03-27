package org.sqlunet.wordnet.provider;

public class Q
{
	// T A B L E S

	static public class LEXES
	{
		static public final String TABLE = "lexes";
	}

	static public class WORDS
	{
		static public final String TABLE = "words";
	}

	static public class CASEDWORDS
	{
		static public final String TABLE = "casedwords";
	}

	static public class PRONUNCIATIONS
	{
		static public final String TABLE = "pronunciations";
	}

	static public class SENSES
	{
		static public final String TABLE = "senses";
	}

	static public class SYNSETS
	{
		static public final String TABLE = "synsets";
	}

	static public class POSES
	{
		static public final String TABLE = "poses";
	}

	static public class DOMAINS
	{
		static public final String TABLE = "domains";
	}

	static public class RELATIONS
	{
		static public final String TABLE = "relations";
	}

	static public class SEMRELATIONS
	{
		static public final String TABLE = "semrelations";
	}

	static public class LEXRELATIONS
	{
		static public final String TABLE = "lexrelations";
	}

	static public class ADJPOSITIONS
	{
		static public final String TABLE = "adjpositions";
	}

	static public class MORPHS
	{
		static public final String TABLE = "morphs";
	}

	static public class SAMPLES
	{
		static public final String TABLE = "samples";
	}

	static public class VFRAMES
	{
		static public final String TABLE = "vframes";
	}

	static public class VTEMPLATES
	{
		static public final String TABLE = "vtemplates";
	}

	static public class LEXES_MORPHS
	{
		static public final String TABLE = "lexes_morphs " +
				"LEFT JOIN morphs USING (morphid)";
	}

	static public class SENSES_VFRAMES
	{
		static public final String TABLE = "senses_vframes " +
				"LEFT JOIN vframes USING (frameid)";
	}

	static public class SENSES_VTEMPLATES
	{
		static public final String TABLE = "senses_vtemplates " +
				"LEFT JOIN vtemplates USING (templateid)";
	}

	static public class SENSES_ADJPOSITIONS
	{
		static public final String TABLE = "senses_adjpositions " +
				"LEFT JOIN adjpositions USING (positionid)";
	}

	// V I E W

	static public class DICT
	{
		static public final String TABLE = "dict";
	}

	// E L E M E N T

	static public class WORD1
	{
		static public final String TABLE = "words";
		static public final String SELECTION = "wordid = #{uri_last}";
	}

	static public class SENSE1
	{
		static public final String TABLE = "senses";
		static public final String SELECTION = "senseid = #{uri_last}";
	}

	static public class SYNSET1
	{
		static public final String TABLE = "synsets";
		static public final String SELECTION = "synsetid = #{uri_last}";
	}

	// W O R D S ...

	static public class WORDS_LEXES_MORPHS
	{
		static public final String TABLE = "words " +
				"LEFT JOIN lexes_morphs USING (wordid) " +
				"LEFT JOIN morphs USING (morphid)";
	}

	static public class WORDS_LEXES_MORPHS_BY_WORD
	{
		static public final String TABLE = "words " +
				"LEFT JOIN lexes_morphs USING (wordid) " +
				"LEFT JOIN morphs USING (morphid)";
		static public final String GROUPBY = "wordid";
	}

	static public class WORDS_SENSES_SYNSETS
	{
		static public final String TABLE = "words AS w " +
				"LEFT JOIN senses AS s USING (wordid) " +
				"LEFT JOIN synsets AS y USING (synsetid)";
	}

	static public class WORDS_SENSES_CASEDWORDS_SYNSETS
	{
		static public final String TABLE = "words AS w " +
				"LEFT JOIN senses AS s USING (wordid) " +
				"LEFT JOIN casedwords AS c USING (wordid,casedwordid) " +
				"LEFT JOIN synsets AS y USING (synsetid)";
	}

	static public class WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS
	{
		static public final String TABLE = "words AS w " +
				"LEFT JOIN senses AS s USING (wordid) " +
				"LEFT JOIN casedwords AS c USING (wordid,casedwordid) " +
				"LEFT JOIN synsets AS y USING (synsetid) " +
				"LEFT JOIN poses AS p USING (posid) " +
				"LEFT JOIN domains AS d USING (domainid)";
	}

	// S E N S E S ...

	static public class SENSES_WORDS
	{
		static public final String TABLE = "senses AS s " +
				"LEFT JOIN words AS w USING (wordid)";
	}

	static public class SENSES_WORDS_BY_SYNSET
	{
		static public final String TABLE = "senses AS s " +
				"LEFT JOIN words AS w USING (wordid)";
	static public final String[] PROJECTION = {"GROUP_CONCAT(words.word, ', ' ) AS members"};
		static public final String GROUPBY = "synsetid";
	}

	static public class SENSES_SYNSETS_POSES_DOMAINS
	{
		static public final String TABLE = "senses AS s " +
				"INNER JOIN synsets AS y USING (synsetid) " +
				"LEFT JOIN poses AS p USING (posid) " +
				"LEFT JOIN domains AS d USING (domainid)";
	}

	// S Y N S E T S ...

	static public class SYNSETS_POSES_DOMAINS
	{
		static public final String TABLE = "synsets AS y " +
				"LEFT JOIN poses AS p USING (posid) " +
				"LEFT JOIN domains AS d USING (domainid)";
	}

	// R E L A T I O N S ...

	static public class ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET
	{
		static public final String TABLE = "( #{query} ) AS r " +
				"INNER JOIN relations USING (relationid) " +
				"INNER JOIN synsets AS y2 ON r.synset2id = y2.synsetid " +
				"LEFT JOIN senses ON y2.synsetid = senses.synsetid " +
				"LEFT JOIN words AS w USING (wordid) " +
				"LEFT JOIN words AS w2 ON r.word2id = w2.wordid";
		static public final String GROUPBY = "synset2id,relationtype,relation,relationid,word2id,word2";
	}

	static public class SEMRELATIONS_SYNSETS
	{
		static public final String TABLE = "semrelations AS r " +
				"INNER JOIN synsets AS y2 ON r.synset2id = y2.synsetid";
	}

	static public class SEMRELATIONS_SYNSETS_X
	{
		static public final String TABLE = "semrelations AS r " +
				"INNER JOIN relations USING (relationid) " +
				"INNER JOIN synsets AS y2 ON r.synset2id = y2.synsetid ";
	}

	static public class SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET
	{
		static public final String TABLE = "semrelations AS r " +
				"INNER JOIN relations USING (relationid) " +
				"INNER JOIN synsets AS y2 ON r.synset2id = y2.synsetid " +
				"LEFT JOIN senses ON y2.synsetid = senses.synsetid " +
				"LEFT JOIN words USING (wordid)";
	static public final String[] PROJECTION = {"GROUP_CONCAT(words.word, ', ' ) AS members2"};
		static public final String GROUPBY = "y2.synsetid";
	}

	static public class LEXRELATIONS_SENSES
	{
		static public final String TABLE = "lexrelations AS r " +
				"INNER JOIN synsets AS y2 ON r.synset2id = y2.synsetid " +
				"INNER JOIN words AS w ON r.word2id = w.wordid";
	}

	static public class LEXRELATIONS_SENSES_X
	{
		static public final String TABLE = "lexrelations AS r " +
				"INNER JOIN relations USING (relationid) " +
				"INNER JOIN synsets AS y2 ON r.synset2id = y2.synsetid " +
				"INNER JOIN words AS w ON r.word2id = w.wordid ";
	}

	static public class LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET
	{
		static public final String TABLE = "lexrelations AS r " +
				"INNER JOIN relations USING (relationid) " +
				"INNER JOIN synsets AS y2 ON r.synset2id = y2.synsetid " +
				"INNER JOIN words AS w ON r.word2id = w.wordid " +
				"LEFT JOIN senses AS s ON y2.synsetid = s.synsetid " +
				"LEFT JOIN words AS w2 USING (wordid)";
	static public final String[] PROJECTION = {"GROUP_CONCAT(DISTINCT w2.word) AS members2"};
		static public final String GROUPBY = "y2.synsetid";
	}

	// S E A R C H

	static public class LOOKUP_FTS_DEFINITIONS
	{
		static public final String TABLE = "synsets_definition_fts4";
	}

	static public class LOOKUP_FTS_SAMPLES
	{
		static public final String TABLE = "samples_sample_fts4";
	}

	static public class LOOKUP_FTS_WORDS
	{
		static public final String TABLE = "words_word_fts4";
	}

	// S U G G E S T

	static public class SUGGEST_FTS_DEFINITIONS
	{
		static public final String TABLE = "synsets_definition_fts4";
		static public final String[] PROJECTION = {"synsetid AS _id", "definition AS #{suggest_text_1}", "definition AS #{suggest_query}"};
		static public final String SELECTION = "definition MATCH ?";
		static public final String[] ARGS = {"#{uri_last}*"};
	}

	static public class SUGGEST_FTS_SAMPLES
	{
		static public final String TABLE = "samples_sample_fts4";
		static public final String[] PROJECTION = {"sampleid AS _id", "sample AS #{suggest_text_1}", "sample AS #{suggest_query}"};
		static public final String SELECTION = "sample MATCH ?";
		static public final String[] ARGS = {"#{uri_last}*"};
	}

	static public class SUGGEST_FTS_WORDS
	{
		static public final String TABLE = "words_word_fts4";
		static public final String[] PROJECTION = {"wordid AS _id", "word AS #{suggest_text_1}", "word AS #{suggest_query}"};
		static public final String SELECTION = "word MATCH ?";
		static public final String[] ARGS = {"#{uri_last}*"};
	}

	static public class SUGGEST_WORDS
	{
		static public final String TABLE = "words";
		static public final String[] PROJECTION = {"wordid AS _id", "word AS #{suggest_text_1}", "word AS #{suggest_query}"};
		static public final String SELECTION = "word LIKE ? || '%'";
		static public final String[] ARGS = {"#{uri_last}"};
	}
}
