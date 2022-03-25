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
	static public final String RECURSES = "recurses";

	// T A B L E S

	static public final class Words
	{
		static public final String TABLE = Q.WORDS.TABLE;
		static public final String CONTENT_URI_TABLE = Words.TABLE;
		static public final String WORDID = Q.WORDID;
		static public final String WORD = Q.WORD;
	}

	static public final class CasedWords
	{
		static public final String TABLE = Q.CASEDWORDS.TABLE;
		static public final String CONTENT_URI_TABLE = CasedWords.TABLE;
		static public final String CASEDWORDID = Q.CASEDWORDID;
		static public final String WORDID = Q.WORDID;
		static public final String CASED = Q.CASEDWORD;
	}

	static public final class Senses
	{
		static public final String TABLE = Q.SENSES.TABLE;
		static public final String CONTENT_URI_TABLE = Senses.TABLE;
		static public final String WORDID = Q.WORDID;
		static public final String SYNSETID = Q.SYNSETID;
		static public final String CASEDWORDID = Q.CASEDWORDID;
		static public final String SENSEID = Q.SENSEID;
		static public final String SENSENUM = Q.SENSENUM;
		static public final String SENSEKEY = Q.SENSEKEY;
		static public final String LEXID = Q.LEXID;
		static public final String TAGCOUNT = Q.TAGCOUNT;
	}

	static public final class Synsets
	{
		static public final String TABLE = Q.SYNSETS.TABLE;
		static public final String CONTENT_URI_TABLE = Synsets.TABLE;
		static public final String SYNSETID = Q.SYNSETID;
		static public final String POSID = Q.POSID;
		static public final String SENSEID = Q.SENSEID;
		static public final String DOMAINID = Q.DOMAINID;
		static public final String DEFINITION = Q.DEFINITION;
	}

	static public final class AllRelations
	{
		static public final String TABLE = "allrelations";
		static public final String CONTENT_URI_TABLE = AllRelations.TABLE;
		static public final String WORDID1 = Q.WORD1ID;
		static public final String SYNSETID1 = Q.SYNSET1ID;
		static public final String WORDID2 = Q.WORD2ID;
		static public final String SYNSETID2 = Q.SYNSET2ID;
		static public final String RELATIONID = Q.RELATIONID;
	}

	static public final class SemRelations
	{
		static public final String TABLE = Q.SEMRELATIONS.TABLE;
		static public final String CONTENT_URI_TABLE = SemRelations.TABLE;
		static public final String SYNSETID1 = Q.SYNSET1ID;
		static public final String SYNSETID2 = Q.SYNSET2ID;
		static public final String RELATIONID = Q.RELATIONID;
	}

	static public final class LexRelations
	{
		static public final String TABLE = Q.LEXRELATIONS.TABLE;
		static public final String CONTENT_URI_TABLE = LexRelations.TABLE;
		static public final String WORDID1 = Q.WORD1ID;
		static public final String SYNSETID1 = Q.SYNSET1ID;
		static public final String WORDID2 = Q.WORD2ID;
		static public final String SYNSETID2 = Q.SYNSET2ID;
		static public final String RELATIONID = Q.RELATIONID;
	}

	static public final class Relations
	{
		static public final String TABLE = Q.RELATIONS.TABLE;
		static public final String CONTENT_URI_TABLE = Relations.TABLE;
		static public final String RELATIONID = Q.RELATIONID;
		static public final String RELATION = Q.RELATION;
		static public final String RECURSES = Q.RECURSES;
		static public final String RECURSESSTR = RECURSES;
		static public final String RECURSESSELECT = "CASE WHEN " + RECURSES + " <> 0 THEN 'recurses' ELSE '' END) AS " + RECURSESSTR;
	}

	static public final class Poses
	{
		static public final String TABLE = Q.POSES.TABLE;
		static public final String CONTENT_URI_TABLE = Poses.TABLE;
		static public final String POSID = Q.POSID;
		static public final String POS = Q.POS;
	}

	static public final class AdjPositions
	{
		static public final String TABLE = Q.ADJPOSITIONS.TABLE;
		static public final String CONTENT_URI_TABLE = AdjPositions.TABLE;
		static public final String POSITIONID = Q.POSITIONID;
		static public final String POSITION = Q.POSITION;
	}

	static public final class Domains
	{
		static public final String TABLE = Q.DOMAINS.TABLE;
		static public final String CONTENT_URI_TABLE = Domains.TABLE;
		static public final String DOMAINID = Q.DOMAINID;
		static public final String DOMAIN = Q.DOMAIN;
		static public final String DOMAINNAME = Q.DOMAINNAME;
		static public final String POSID = Q.POSID;
	}

	static public final class Samples
	{
		static public final String TABLE = Q.SAMPLES.TABLE;
		static public final String CONTENT_URI_TABLE = Samples.TABLE;
		static public final String SYNSETID = Q.SYNSETID;
		static public final String SAMPLEID = Q.SAMPLEID;
		static public final String SAMPLE = Q.SAMPLE;
	}

	// J O I N S

	static public final class Words_Senses_Synsets
	{
		static public final String TABLE = "words_senses_synsets";
	}

	static public final class Words_Senses_CasedWords_Synsets
	{
		static public final String TABLE = "words_senses_casedwords_synsets";
		static public final String CONTENT_URI_TABLE = Words_Senses_CasedWords_Synsets.TABLE;
	}

	static public final class Words_Senses_CasedWords_Synsets_Poses_Domains
	{
		static public final String TABLE = "words_senses_casedwords_synsets_poses_domains";
		static public final String CONTENT_URI_TABLE = Words_Senses_CasedWords_Synsets_Poses_Domains.TABLE;
		static public final String WORD = Q.WORD;
		static public final String CASED = Q.CASEDWORD;
		static public final String WORDID = Q.WORDID;
		static public final String SYNSETID = Q.SYNSETID;
		static public final String SENSEID = Q.SENSEID;
		static public final String SENSENUM = Q.SENSENUM;
		static public final String SENSEKEY = Q.SENSEKEY;
		static public final String DEFINITION = Q.DEFINITION;
		static public final String LEXID = Q.LEXID;
		static public final String DOMAIN = Q.DOMAIN;
		static public final String POSID = Q.POSID;
		static public final String POS = Q.POS;
		static public final String TAGCOUNT = Q.TAGCOUNT;
	}

	static public final class Senses_Words
	{
		static public final String TABLE = "senses_words";
		static public final String TABLE_BY_SYNSET = "senses_words_by_synset";
		static public final String CONTENT_URI_TABLE = Senses_Words.TABLE;
		static public final String CONTENT_URI_TABLE_BY_SYNSET = Senses_Words.TABLE_BY_SYNSET;
		static public final String SYNSETID = Q.SYNSETID;
		static public final String WORDID = Q.WORDID;
		static public final String MEMBER = Q.WORD;
		static public final String MEMBERS = Q.MEMBERS;
	}

	static public final class Senses_Synsets_Poses_Domains
	{
		static public final String TABLE = "senses_synsets_poses_domains";
		static public final String CONTENT_URI_TABLE = Senses_Synsets_Poses_Domains.TABLE;
	}

	static public final class Synsets_Poses_Domains
	{
		static public final String TABLE = "synsets_poses_domains";
		static public final String CONTENT_URI_TABLE = Synsets_Poses_Domains.TABLE;
		static public final String SYNSETID = Q.SYNSETID;
	}

	static public final class AllRelations_Senses_Words_X
	{
		static public final String TABLE_BY_SYNSET = "allrelations_senses_relations_senses_words_by_synset";
		static public final String CONTENT_URI_TABLE = AllRelations_Senses_Words_X.TABLE_BY_SYNSET;
		static public final String SYNSET1ID = Q.SYNSET1ID;
		static public final String SYNSET2ID = Q.SYNSET2ID;
		static public final String MEMBERS2 = WordNetContract.MEMBERS2;
		static public final String RECURSES = Q.RECURSES;
	}

	static public final class SemRelations_Synsets
	{
		static public final String TABLE = "semrelations_synsets";
		static public final String CONTENT_URI_TABLE = SemRelations_Synsets.TABLE;
	}

	static public final class SemRelations_Synsets_X
	{
		static public final String TABLE = "semrelations_synsets_relations";
		static public final String CONTENT_URI_TABLE = SemRelations_Synsets_X.TABLE;
	}

	static public final class SemRelations_Synsets_Words_X
	{
		static public final String TABLE_BY_SYNSET = "semrelations_synsets_relations_senses_words_by_synset";
		static public final String CONTENT_URI_TABLE = SemRelations_Synsets_Words_X.TABLE_BY_SYNSET;
		static public final String SYNSET1ID = Q.SYNSET1ID;
		static public final String SYNSET2ID = Q.SYNSET2ID;
		static public final String MEMBERS2 = WordNetContract.MEMBERS2;
		static public final String RECURSES = Q.RECURSES;
	}

	static public final class LexRelations_Senses
	{
		static public final String TABLE = "lexrelations_synsets_words";
		static public final String CONTENT_URI_TABLE = LexRelations_Senses.TABLE;
	}

	static public final class LexRelations_Senses_X
	{
		static public final String TABLE = "lexrelations_synsets_words_relations";
		static public final String CONTENT_URI_TABLE = LexRelations_Senses_X.TABLE;
	}

	static public final class LexRelations_Senses_Words_X
	{
		static public final String TABLE_BY_SYNSET = "lexrelations_synsets_words_relations_senses_words_by_synset";
		static public final String CONTENT_URI_TABLE = LexRelations_Senses_Words_X.TABLE_BY_SYNSET;
		static public final String SYNSET1ID = Q.SYNSET1ID;
		static public final String SYNSET2ID = Q.SYNSET2ID;
		static public final String MEMBERS2 = WordNetContract.MEMBERS2;
	}

	static public final class Senses_VerbFrames
	{
		static public final String TABLE = "senses_vframes";
		static public final String CONTENT_URI_TABLE = Senses_VerbFrames.TABLE;
		static public final String SYNSETID = Q.SYNSETID;
		static public final String WORDID = Q.WORDID;
		static public final String FRAME = Q.FRAME;
	}

	static public final class Senses_VerbTemplates
	{
		static public final String TABLE = "senses_vtemplates";
		static public final String CONTENT_URI_TABLE = Senses_VerbTemplates.TABLE;
		static public final String SYNSETID = Q.SYNSETID;
		static public final String WORDID = Q.WORDID;
		static public final String TEMPLATE = Q.TEMPLATE;
	}

	static public final class Senses_AdjPositions
	{
		static public final String TABLE = "senses_adjpositions";
		static public final String CONTENT_URI_TABLE = Senses_AdjPositions.TABLE;
		static public final String SYNSETID = Q.SYNSETID;
		static public final String WORDID = Q.WORDID;
		static public final String POSITIONID = Q.POSITIONID;
		static public final String POSITION = Q.POSITION;
	}

	static public final class Lexes_Morphs
	{
		static public final String TABLE = "lexes_morphs";
		static public final String CONTENT_URI_TABLE = Lexes_Morphs.TABLE;
		static public final String WORDID = Q.WORDID;
		static public final String MORPH = Q.MORPH;
		static public final String POSID = Q.POSID;
	}

	static public final class Words_Lexes_Morphs
	{
		static public final String TABLE = "words_lexes_morphs";
		static public final String TABLE_BY_WORD = "words_lexes_morphs_by_word";
		static public final String CONTENT_URI_TABLE = Words_Lexes_Morphs.TABLE;
		static public final String CONTENT_URI_TABLE_BY_WORD = Words_Lexes_Morphs.TABLE_BY_WORD;
		static public final String WORD = Q.WORD;
		static public final String WORDID = Q.WORDID;
		static public final String MORPH = Q.MORPH;
		static public final String POSID = Q.POSID;
	}

	// V I E W S

	static public final class Dict
	{
		static public final String TABLE = "dict";
		static public final String CONTENT_URI_TABLE = Dict.TABLE;
	}

	// T E X T S E A R C H

	static public final class Lookup_Words
	{
		static public final String TABLE = "fts_words";
		static public final String CONTENT_URI_TABLE = Lookup_Words.TABLE;
		static public final String WORDID = Q.WORDID;
		static public final String WORD = Q.WORD;
	}

	static public final class Lookup_Definitions
	{
		static public final String TABLE = "fts_definitions";
		static public final String CONTENT_URI_TABLE = Lookup_Definitions.TABLE;
		static public final String SYNSETID = Q.SYNSETID;
		static public final String DEFINITION = Q.DEFINITION;
	}

	static public final class Lookup_Samples
	{
		static public final String TABLE = "fts_samples";
		static public final String CONTENT_URI_TABLE = Lookup_Samples.TABLE;
		static public final String SYNSETID = Q.SYNSETID;
		static public final String SAMPLE = Q.SAMPLE;
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
