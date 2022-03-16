/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.provider;

import android.app.SearchManager;

/**
 * WordNet provider contract
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class WordNetContract
{
	// A L I A S E S

	static public final String WORD = "w";
	static public final String SENSE = "s";
	static public final String SYNSET = "y";
	static public final String TYPE = "t";
	static public final String RELATION = "r";
	static public final String WORD2 = "w2";
	static public final String SYNSET2 = "y2";
	static public final String POS = "p";
	static public final String CASED = "c";
	static public final String DOMAIN = "d";

	static public final String MEMBERS = "members";
	static public final String MEMBERS2 = "members2";

	// T A B L E S

	static public final class Words
	{
		static public final String TABLE = "words";
		static public final String CONTENT_URI_TABLE = Words.TABLE;
		static public final String WORDID = "wordid";
		static public final String WORD = "word";
	}

	static public final class CasedWords
	{
		static public final String TABLE = "casedwords";
		static public final String CONTENT_URI_TABLE = CasedWords.TABLE;
		static public final String CASEDWORDID = "casedwordid";
		static public final String WORDID = "wordid";
		static public final String CASED = "casedword";
	}

	static public final class Senses
	{
		static public final String TABLE = "senses";
		static public final String CONTENT_URI_TABLE = Senses.TABLE;
		static public final String WORDID = "wordid";
		static public final String SYNSETID = "synsetid";
		static public final String CASEDWORDID = "casedwordid";
		static public final String SENSEID = "senseid";
		static public final String SENSENUM = "sensenum";
		static public final String SENSEKEY = "sensekey";
		static public final String LEXID = "lexid";
		static public final String TAGCOUNT = "tagcount";
	}

	static public final class Synsets
	{
		static public final String TABLE = "synsets";
		static public final String CONTENT_URI_TABLE = Synsets.TABLE;
		static public final String SYNSETID = "synsetid";
		static public final String POSID = "posid";
		static public final String SENSEID = "senseid";
		static public final String DOMAINID = "domainid";
		static public final String DEFINITION = "definition";
	}

	static public final class BaseRelations
	{
		static public final String TABLE = "semrelations";
		static public final String CONTENT_URI_TABLE = SemRelations.TABLE;
		static public final String WORDID1 = "word1id";
		static public final String SYNSETID1 = "synset1id";
		static public final String WORDID2 = "word2id";
		static public final String SYNSETID2 = "synset2id";
		static public final String RELATIONID = "relationid";
	}

	static public final class SemRelations
	{
		static public final String TABLE = "semrelations";
		static public final String CONTENT_URI_TABLE = SemRelations.TABLE;
		static public final String SYNSETID1 = "synset1id";
		static public final String SYNSETID2 = "synset2id";
		static public final String RELATIONID = "relationid";
	}

	static public final class LexRelations
	{
		static public final String TABLE = "lexrelations";
		static public final String CONTENT_URI_TABLE = LexRelations.TABLE;
		static public final String WORDID1 = "word1id";
		static public final String SYNSETID1 = "synset1id";
		static public final String WORDID2 = "word2id";
		static public final String SYNSETID2 = "synset2id";
		static public final String RELATIONID = "relationid";
	}

	static public final class Relations
	{
		static public final String TABLE = "relations";
		static public final String CONTENT_URI_TABLE = Relations.TABLE;
		static public final String RELATIONID = "relationid";
		static public final String RELATION = "relation";
		static public final String RECURSES = "recurses";
		static final String RECURSESSTR = "recursesstr";
		static public final String RECURSESSELECT = "(CASE WHEN " + Relations.RECURSES + " <> 0 THEN 'recurses' ELSE '' END) AS " + Relations.RECURSESSTR;
	}

	static public final class Poses
	{
		static public final String TABLE = "poses";
		static public final String CONTENT_URI_TABLE = Poses.TABLE;
		static public final String POSID = "posid";
		static public final String POS = "pos";
	}

	static public final class AdjPositions
	{
		static public final String TABLE = "adjpositions";
		static public final String CONTENT_URI_TABLE = AdjPositions.TABLE;
		static public final String POSITIONID = "positionid";
		static public final String POSITION = "position";
	}

	static public final class Domains
	{
		static public final String TABLE = "domains";
		static public final String CONTENT_URI_TABLE = Domains.TABLE;
		static public final String DOMAINID = "domainid";
		static public final String DOMAIN = "domain";
		static public final String DOMAINNAME = "domainname";
		static public final String POSID = "posid";
	}

	static public final class Samples
	{
		static public final String TABLE = "samples";
		static public final String CONTENT_URI_TABLE = Samples.TABLE;
		static public final String SYNSETID = "synsetid";
		static public final String SAMPLEID = "sampleid";
		static public final String SAMPLE = "sample";
	}

	// J O I N S

	static public final class Words_Senses_Synsets
	{
		static public final String TABLE = "words_senses_synsets";
		// static public final String CONTENT_URI_TABLE = Words_Senses_Synsets.TABLE;
		// words LEFT JOIN senses LEFT JOIN synsets
	}

	static public final class Words_Senses_CasedWords_Synsets
	{
		static public final String TABLE = "words_senses_casedwords_synsets";
		static public final String CONTENT_URI_TABLE = Words_Senses_CasedWords_Synsets.TABLE;
		// words LEFT JOIN senses LEFT JOIN casedwords LEFT JOIN synsets
	}

	static public final class Words_Senses_CasedWords_Synsets_Poses_Domains
	{
		static public final String TABLE = "words_senses_casedwords_synsets_poses_domains";
		static public final String CONTENT_URI_TABLE = Words_Senses_CasedWords_Synsets_Poses_Domains.TABLE;
		static public final String WORD = "word";
		static public final String CASED = "casedword";
		static public final String WORDID = "wordid";
		static public final String SYNSETID = "synsetid";
		static public final String SENSEID = "senseid";
		static public final String SENSENUM = "sensenum";
		static public final String SENSEKEY = "sensekey";
		static public final String DEFINITION = "definition";
		static public final String LEXID = "lexid";
		static public final String DOMAIN = "domain";
		static public final String POSID = "posid";
		static public final String POS = "pos";
		static public final String TAGCOUNT = "tagcount";
		// words LEFT JOIN senses LEFT JOIN casedwords LEFT JOIN synsets
	}

	static public final class Senses_Words
	{
		static public final String TABLE = "senses_words";
		static public final String TABLE_BY_SYNSET = "senses_words_by_synset";
		static public final String CONTENT_URI_TABLE = Senses_Words.TABLE;
		static public final String CONTENT_URI_TABLE_BY_SYNSET = Senses_Words.TABLE_BY_SYNSET;
		static public final String SYNSETID = "synsetid";
		static public final String WORDID = "wordid";
		static public final String MEMBER = "word";
		static public final String MEMBERS = "members";
		// synsets LEFT JOIN senses LEFT JOIN words
	}

	static public final class Senses_Synsets_Poses_Domains
	{
		static public final String TABLE = "senses_synsets_poses_domains";
		static public final String CONTENT_URI_TABLE = Senses_Synsets_Poses_Domains.TABLE;
		// senses INNER JOIN synsets LEFT JOIN poses LEFT JOIN domains
	}

	static public final class Synsets_Poses_Domains
	{
		static public final String TABLE = "synsets_poses_domains";
		static public final String CONTENT_URI_TABLE = Synsets_Poses_Domains.TABLE;
		static public final String SYNSETID = "synsetid";
		// synsets LEFT JOIN poses LEFT JOIN domains
	}

	static public final class BaseRelations_Senses_Words_X
	{
		static public final String TABLE_BY_SYNSET = "baserelations_senses_relations_senses_words_by_synset";
		static public final String CONTENT_URI_TABLE = BaseRelations_Senses_Words_X.TABLE_BY_SYNSET;
		static public final String SYNSET1ID = "synset1id";
		static public final String SYNSET2ID = "synset2id";
		static public final String MEMBERS2 = WordNetContract.MEMBERS2;
		static public final String RECURSES = "recurses";
		// semrelations INNER JOIN synsets LEFT JOIN relations LEFT JOIN senses LEFT JOIN words
		// union
		// lexrelations INNER JOIN synsets INNER JOIN words LEFT JOIN relations LEFT JOIN senses LEFT JOIN words
	}

	static public final class SemRelations_Synsets
	{
		static public final String TABLE = "semrelations_synsets";
		static public final String CONTENT_URI_TABLE = SemRelations_Synsets.TABLE;
		// semrelations INNER JOIN synsets
	}

	static public final class SemRelations_Synsets_X
	{
		static public final String TABLE = "semrelations_synsets_relations";
		static public final String CONTENT_URI_TABLE = SemRelations_Synsets_X.TABLE;
		// semrelations INNER JOIN synsets LEFT JOIN relations
	}

	static public final class SemRelations_Synsets_Words_X
	{
		static public final String TABLE_BY_SYNSET = "semrelations_synsets_relations_senses_words_by_synset";
		static public final String CONTENT_URI_TABLE = SemRelations_Synsets_Words_X.TABLE_BY_SYNSET;
		static public final String SYNSET1ID = "synset1id";
		static public final String SYNSET2ID = "synset2id";
		static public final String MEMBERS2 = WordNetContract.MEMBERS2;
		static public final String RECURSES = "recurses";
		// semrelations INNER JOIN synsets LEFT JOIN relations LEFT JOIN senses LEFT JOIN words
	}

	static public final class LexRelations_Senses
	{
		static public final String TABLE = "lexrelations_synsets_words";
		static public final String CONTENT_URI_TABLE = LexRelations_Senses.TABLE;
		static public final String QUERY = String.format( //
				"%s AS %s " + //
						"INNER JOIN %s AS %s ON %s.synset2id = %s.synsetid " + //
						"INNER JOIN %s AS %s ON %s.word2id = %s.wordid", //
				"lexrelations",	WordNetContract.RELATION, //
				"synsets", WordNetContract.SYNSET2, WordNetContract.RELATION, WordNetContract.SYNSET2, //
				"words", WordNetContract.WORD, WordNetContract.RELATION, WordNetContract.WORD //
		);
		// lexrelations INNER JOIN synsets INNER JOIN words
	}

	static public final class LexRelations_Senses_X
	{
		static public final String TABLE = "lexrelations_synsets_words_relations";
		static public final String CONTENT_URI_TABLE = LexRelations_Senses_X.TABLE;
		// lexrelations INNER JOIN synsets INNER JOIN words LEFT JOIN relations
	}

	static public final class LexRelations_Senses_Words_X
	{
		static public final String TABLE_BY_SYNSET = "lexrelations_synsets_words_relations_senses_words_by_synset";
		static public final String CONTENT_URI_TABLE = LexRelations_Senses_Words_X.TABLE_BY_SYNSET;
		static public final String SYNSET1ID = "synset1id";
		static public final String SYNSET2ID = "synset2id";
		static public final String MEMBERS2 = WordNetContract.MEMBERS2;
		// lexrelations INNER JOIN synsets INNER JOIN words LEFT JOIN relations LEFT JOIN senses LEFT JOIN words
	}

	static public final class Senses_VerbFrames
	{
		static public final String TABLE = "senses_vframes";
		static public final String CONTENT_URI_TABLE = Senses_VerbFrames.TABLE;
		static public final String SYNSETID = "synsetid";
		static public final String WORDID = "wordid";
		static public final String FRAME = "frame";
		// senses_vframes LEFT JOIN vframes
	}

	static public final class Senses_VerbTemplates
	{
		static public final String TABLE = "senses_vtemplates";
		static public final String CONTENT_URI_TABLE = Senses_VerbTemplates.TABLE;
		static public final String SYNSETID = "synsetid";
		static public final String WORDID = "wordid";
		static public final String TEMPLATE = "template";
		// senses_vtemplates LEFT JOIN vtemplates
	}

	static public final class Senses_AdjPositions
	{
		static public final String TABLE = "senses_adjpositions";
		static public final String CONTENT_URI_TABLE = Senses_AdjPositions.TABLE;
		static public final String SYNSETID = "synsetid";
		static public final String WORDID = "wordid";
		static public final String POSITIONID = "positionid";
		static public final String POSITION = "position";
		// senses_adjpositions LEFT JOIN adjpositions
	}

	static public final class Lexes_Morphs
	{
		static public final String TABLE = "lexes_morphs";
		static public final String CONTENT_URI_TABLE = Lexes_Morphs.TABLE;
		static public final String WORDID = "wordid";
		static public final String MORPH = "morph";
		static public final String POSID = "posid";
		// lexes_morphs LEFT JOIN morphs
	}

	static public final class Words_Lexes_Morphs
	{
		static public final String TABLE = "words_lexes_morphs";
		static public final String TABLE_BY_WORD = "words_lexes_morphs_by_word";
		static public final String CONTENT_URI_TABLE = Words_Lexes_Morphs.TABLE;
		static public final String CONTENT_URI_TABLE_BY_WORD = Words_Lexes_Morphs.TABLE_BY_WORD;
		static public final String WORD = "word";
		static public final String WORDID = "wordid";
		static public final String MORPH = "morph";
		static public final String POSID = "posid";
		// words LEFT JOIN lexes_morphs LEFT JOIN morphs
	}

	// V I E W S

	static public final class Dict
	{
		static public final String TABLE = "dict";
		static public final String CONTENT_URI_TABLE = Dict.TABLE;
		// words LEFT JOIN senses LEFT JOIN casedwords LEFT JOIN synsets
	}

	// T E X T S E A R C H

	static public final class Lookup_Words
	{
		static public final String TABLE = "fts_words";
		static public final String CONTENT_URI_TABLE = Lookup_Words.TABLE;
		static public final String WORDID = "wordid";
		static public final String WORD = "word";
	}

	static public final class Lookup_Definitions
	{
		static public final String TABLE = "fts_definitions";
		static public final String CONTENT_URI_TABLE = Lookup_Definitions.TABLE;
		static public final String SYNSETID = "synsetid";
		static public final String DEFINITION = "definition";
	}

	static public final class Lookup_Samples
	{
		static public final String TABLE = "fts_samples";
		static public final String CONTENT_URI_TABLE = Lookup_Samples.TABLE;
		static public final String SYNSETID = "synsetid";
		static public final String SAMPLE = "sample";
	}

	// S U G G E S T

	static public final class Suggest_Words
	{
		static final String SEARCH_WORD_PATH = "suggest_word";
		static public final String TABLE = Suggest_Words.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
	}

	static public final class Suggest_FTS_Words
	{
		static final String SEARCH_WORD_PATH = "suggest_fts_word";
		static public final String TABLE = Suggest_FTS_Words.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
	}

	static public final class Suggest_FTS_Definitions
	{
		static final String SEARCH_DEFINITION_PATH = "suggest_fts_definition";
		static public final String TABLE = Suggest_FTS_Definitions.SEARCH_DEFINITION_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
	}

	static public final class Suggest_FTS_Samples
	{
		static final String SEARCH_SAMPLE_PATH = "suggest_fts_definition";
		static public final String TABLE = Suggest_FTS_Samples.SEARCH_SAMPLE_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
	}
}
