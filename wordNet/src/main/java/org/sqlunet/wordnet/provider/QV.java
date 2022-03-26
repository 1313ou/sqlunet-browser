package org.sqlunet.wordnet.provider;

public class QV
{
	// T A B L E S

	static public class LEXES
	{
		static public final String TABLE = "${lexes.table}";
	}

	static public class WORDS
	{
		static public final String TABLE = "${words.table}";
	}

	static public class CASEDWORDS
	{
		static public final String TABLE = "${casedwords.table}";
	}

	static public class PRONUNCIATIONS
	{
		static public final String TABLE = "${pronunciations.table}";
	}

	static public class SENSES
	{
		static public final String TABLE = "${senses.table}";
	}

	static public class SYNSETS
	{
		static public final String TABLE = "${synsets.table}";
	}

	static public class POSES
	{
		static public final String TABLE = "${poses.table}";
	}

	static public class DOMAINS
	{
		static public final String TABLE = "${domains.table}";
	}

	static public class RELATIONS
	{
		static public final String TABLE = "${relations.table}";
	}

	static public class SEMRELATIONS
	{
		static public final String TABLE = "${semrelations.table}";
	}

	static public class LEXRELATIONS
	{
		static public final String TABLE = "${lexrelations.table}";
	}

	static public class ADJPOSITIONS
	{
		static public final String TABLE = "${adjpositions.table}";
	}

	static public class MORPHS
	{
		static public final String TABLE = "${morphs.table}";
	}

	static public class SAMPLES
	{
		static public final String TABLE = "${samples.table}";
	}

	static public class VFRAMES
	{
		static public final String TABLE = "${vframes.table}";
	}

	static public class VTEMPLATES
	{
		static public final String TABLE = "${vtemplates.table}";
	}

	static public class LEXES_MORPHS
	{
		static public final String TABLE = "${lexes_morphs.table} " +
				"LEFT JOIN ${morphs.table} USING (${morphs.morphid})";
	}

	static public class SENSES_VFRAMES
	{
		static public final String TABLE = "${senses_vframes.table} " +
				"LEFT JOIN ${vframes.table} USING (${vframes.frameid})";
	}

	static public class SENSES_VTEMPLATES
	{
		static public final String TABLE = "${senses_vtemplates.table} " +
				"LEFT JOIN ${vtemplates.table} USING (${vtemplates.templateid})";
	}

	static public class SENSES_ADJPOSITIONS
	{
		static public final String TABLE = "${senses_adjpositions.table} " +
				"LEFT JOIN ${adjpositions.table} USING (${adjpositions.positionid})";
	}

	// V I E W

	static public class DICT
	{
		static public final String TABLE = "${dict.table}";
	}

	// E L E M E N T

	static public class WORD1
	{
		static public final String TABLE = "${words.table}";
		static public final String SELECTION = "${words.wordid} = #{uri_last}";
	}

	static public class SENSE1
	{
		static public final String TABLE = "${senses.table}";
		static public final String SELECTION = "${senses.senseid} = #{uri_last}";
	}

	static public class SYNSET1
	{
		static public final String TABLE = "${synsets.table}";
		static public final String SELECTION = "${synsets.synsetid} = #{uri_last}";
	}

	// W O R D S ...

	static public class WORDS_LEXES_MORPHS
	{
		static public final String TABLE = "${words.table} " +
				"LEFT JOIN ${lexes_morphs.table} USING (${words.wordid}) " +
				"LEFT JOIN ${morphs.table} USING (${morphs.morphid})";
	}

	static public class WORDS_LEXES_MORPHS_BY_WORD
	{
		static public final String TABLE = "${words.table} " +
				"LEFT JOIN ${lexes_morphs.table} USING (${words.wordid}) " +
				"LEFT JOIN ${morphs.table} USING (${morphs.morphid})";
		static public final String GROUPBY = "${words.wordid}";
	}

	static public class WORDS_SENSES_SYNSETS
	{
		static public final String TABLE = "${words.table} AS ${as_words} " +
				"LEFT JOIN ${senses.table} AS ${as_senses} USING (${senses.wordid}) " +
				"LEFT JOIN ${synsets.table} AS ${as_synsets} USING (${synsets.synsetid})";
	}

	static public class WORDS_SENSES_CASEDWORDS_SYNSETS
	{
		static public final String TABLE = "${words.table} AS ${as_words} " +
				"LEFT JOIN ${senses.table} AS ${as_senses} USING (${senses.wordid}) " +
				"LEFT JOIN ${casedwords.table} AS ${as_caseds} USING (${casedwords.wordid},${casedwords.casedwordid}) " +
				"LEFT JOIN ${synsets.table} AS ${as_synsets} USING (${synsets.synsetid})";
	}

	static public class WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS
	{
		static public final String TABLE = "${words.table} AS ${as_words} " +
				"LEFT JOIN ${senses.table} AS ${as_senses} USING (${senses.wordid}) " +
				"LEFT JOIN ${casedwords.table} AS ${as_caseds} USING (${casedwords.wordid},${casedwords.casedwordid}) " +
				"LEFT JOIN ${synsets.table} AS ${as_synsets} USING (${synsets.synsetid}) " +
				"LEFT JOIN ${poses.table} AS ${as_poses} USING (${poses.posid}) " +
				"LEFT JOIN ${domains.table} AS ${as_domains} USING (${domains.domainid})";
	}

	// S E N S E S ...

	static public class SENSES_WORDS
	{
		static public final String TABLE = "${senses.table} AS ${as_senses} " +
				"LEFT JOIN ${words.table} AS ${as_words} USING (${senses.wordid})";
	}

	static public class SENSES_WORDS_BY_SYNSET
	{
		static public final String TABLE = "${senses.table} AS ${as_senses} " +
				"LEFT JOIN ${words.table} AS ${as_words} USING (${words.wordid})";
		static public final String[] PROJECTION = {"GROUP_CONCAT(${words.table}.${words.word}, ', ' ) AS #{members}"};
		static public final String GROUPBY = "${synsets.synsetid}";
	}

	static public class SENSES_SYNSETS_POSES_DOMAINS
	{
		static public final String TABLE = "${senses.table} AS ${as_senses} " +
				"INNER JOIN ${synsets.table} AS ${as_synsets} USING (${synsets.synsetid}) " +
				"LEFT JOIN ${poses.table} AS ${as_poses} USING (${poses.posid}) " +
				"LEFT JOIN ${domains.table} AS ${as_domains} USING (${domains.domainid})";
	}

	// S Y N S E T S ...

	static public class SYNSETS_POSES_DOMAINS
	{
		static public final String TABLE = "${synsets.table} AS ${as_synsets} " +
				"LEFT JOIN ${poses.table} AS ${as_poses} USING (${poses.posid}) " +
				"LEFT JOIN ${domains.table} AS ${as_domains} USING (${domains.domainid})";
	}

	// R E L A T I O N S ...

	static public class ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET
	{
		static public final String TABLE = "( #{query} ) AS ${as_relations} " +
				"INNER JOIN ${relations.table} USING (${relations.relationid}) " +
				"INNER JOIN ${synsets.table} AS ${as_synsets2} ON ${as_relations}.${baserelations.synset2id} = ${as_synsets2}.${synsets.synsetid} LEFT JOIN ${senses.table} ON ${as_synsets2}.${synsets.synsetid} = ${senses.table}.${senses.synsetid} " +
				"LEFT JOIN ${words.table} AS ${as_words} USING (${words.wordid}) " +
				"LEFT JOIN ${words.table} AS ${as_words2} ON ${as_relations}.${baserelations.word2id} = ${as_words2}.${words.wordid}";
		static public final String GROUPBY = "#{query_target_synsetid},${relationtype},${relations.relation},${relations.relationid},#{query_target_wordid},#{query_target_word}";
	}

	static public class SEMRELATIONS_SYNSETS
	{
		static public final String TABLE = "${semrelations.table} AS ${as_relations} " +
				"INNER JOIN ${synsets.table} AS ${as_synsets2} ON ${as_relations}.${semrelations.synset2id} = ${as_synsets2}.${synsets.synsetid}";
	}

	static public class SEMRELATIONS_SYNSETS_X
	{
		static public final String TABLE = "${semrelations.table} AS ${as_relations} " +
				"INNER JOIN ${relations.table} USING (${relations.relationid}) " +
				"INNER JOIN ${synsets.table} AS ${as_synsets2} ON ${as_relations}.${semrelations.synset2id} = ${as_synsets2}.${synsets.synsetid} ";
	}

	static public class SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET
	{
		static public final String TABLE = "${semrelations.table} AS ${as_relations} " +
				"INNER JOIN ${relations.table} USING (${relations.relationid}) " +
				"INNER JOIN ${synsets.table} AS ${as_synsets2} ON ${as_relations}.${semrelations.synset2id} = ${as_synsets2}.${synsets.synsetid} " +
				"LEFT JOIN ${senses.table} ON ${as_synsets2}.${synsets.synsetid} = ${senses.table}.${senses.synsetid} " +
				"LEFT JOIN ${words.table} USING (${words.wordid})";
		static public final String[] PROJECTION = {"GROUP_CONCAT(${words.table}.${words.word}, ', ' ) AS #{members2}"};
		static public final String GROUPBY = "${as_synsets2}.${synsets.synsetid}";
	}

	static public class LEXRELATIONS_SENSES
	{
		static public final String TABLE = "${lexrelations.table} AS ${as_relations} " +
				"INNER JOIN ${synsets.table} AS ${as_synsets2} ON ${as_relations}.${lexrelations.synset2id} = ${as_synsets2}.${synsets.synsetid} " +
				"INNER JOIN ${words.table} AS ${as_words} ON ${as_relations}.${lexrelations.word2id} = ${as_words}.${words.wordid}";
	}

	static public class LEXRELATIONS_SENSES_X
	{
		static public final String TABLE = "${lexrelations.table} AS ${as_relations} " +
				"INNER JOIN ${relations.table} USING (${relations.relationid}) " +
				"INNER JOIN ${synsets.table} AS ${as_synsets2} ON ${as_relations}.${lexrelations.synset2id} = ${as_synsets2}.${synsets.synsetid} INNER JOIN ${words.table} AS ${as_words} ON ${as_relations}.${lexrelations.word2id} = ${as_words}.${words.wordid} ";
	}

	static public class LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET
	{
		static public final String TABLE = "${lexrelations.table} AS ${as_relations} " +
				"INNER JOIN ${relations.table} USING (${relations.relationid}) " +
				"INNER JOIN ${synsets.table} AS ${as_synsets2} ON ${as_relations}.${lexrelations.synset2id} = ${as_synsets2}.${synsets.synsetid} " +
				"INNER JOIN ${words.table} AS ${as_words} ON ${as_relations}.${lexrelations.word2id} = ${as_words}.${words.wordid} " +
				"LEFT JOIN ${senses.table} AS ${as_senses} ON ${as_synsets2}.${senses.synsetid} = ${as_senses}.${senses.synsetid} " +
				"LEFT JOIN ${words.table} AS ${as_words2} USING (${words.wordid})";
		static public final String[] PROJECTION = {"GROUP_CONCAT(DISTINCT ${as_words2}.${words.word}) AS #{members2}"};
		static public final String GROUPBY = "${as_synsets2}.${synsets.synsetid}";
	}

	// SEARCH

	static public class LOOKUP_FTS_DEFINITIONS
	{
		static public final String TABLE = "${synsets.table}_${synsets.definition}_fts4";
	}

	static public class LOOKUP_FTS_SAMPLES
	{
		static public final String TABLE = "${samples.table}_${samples.sample}_fts4";
	}

	static public class LOOKUP_FTS_WORDS
	{
		static public final String TABLE = "${words.table}_${words.word}_fts4";
	}

	// SUGGEST

	static public class SUGGEST_FTS_DEFINITIONS
	{
		static public final String TABLE = "@{synsets.table}_@{synsets.definition}_fts4";
		static public final String[] PROJECTION = {"${synsets.synsetid} AS _id", "${synsets.definition} AS SearchManager.SUGGEST_COLUMN_TEXT_1", "${synsets.definition} AS SearchManager.SUGGEST_COLUMN_QUERY"};
		static public final String SELECTION = "${synsets.definition} MATCH ?";
		static public final String[] ARGS = {"#{uri_last}*"};
	}

	static public class SUGGEST_FTS_SAMPLES
	{
		static public final String TABLE = "@{samples.table}_@{samples.sample}_fts4";
		static public final String[] PROJECTION = {"${samples.sampleid} AS _id", "${samples.sample} AS SearchManager.SUGGEST_COLUMN_TEXT_1", "${samples.sample} AS SearchManager.SUGGEST_COLUMN_QUERY"};
		static public final String SELECTION = "${samples.sample} MATCH ?";
		static public final String[] ARGS = {"#{uri_last}*"};
	}

	static public class SUGGEST_FTS_WORDS
	{
		static public final String TABLE = "@{words.table}_@{words.word}_fts4";
		static public final String[] PROJECTION = {"${words.wordid} AS _id", "${words.word} AS SearchManager.SUGGEST_COLUMN_TEXT_1", "${words.word} AS SearchManager.SUGGEST_COLUMN_QUERY"};
		static public final String SELECTION = "${words.word} MATCH ?";
		static public final String[] ARGS = {"#{uri_last}*"};
	}

	static public class SUGGEST_WORDS
	{
		static public final String TABLE = "${words.table}";
		static public final String[] PROJECTION = {"${words.wordid} AS _id", "${words.word} AS SearchManager.SUGGEST_COLUMN_TEXT_1", "${words.word} AS SearchManager.SUGGEST_COLUMN_QUERY"};
		static public final String SELECTION = "${words.word} LIKE ? || '%'";
		static public final String[] ARGS = {"#{uri_last}"};
	}
}
