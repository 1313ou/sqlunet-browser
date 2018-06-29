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

	static public final String DEST = "d";
	static public final String WORD = "w";
	static public final String WORD2 = "t";
	static public final String LINK = "l";
	static public final String SENSE = "s";
	static public final String SYNSET = "y";
	static public final String POS = "p";

	// T A B L E S

	@SuppressWarnings("unused")
	static public final class Words
	{
		static public final String TABLE = "words";
		static public final String CONTENT_URI_TABLE = Words.TABLE;
		static public final String WORDID = "wordid";
		static public final String LEMMA = "lemma";
		static public final String MEMBER = "lemma";
	}

	@SuppressWarnings("unused")
	static public final class CasedWords
	{
		static public final String TABLE = "casedwords";
		static public final String CONTENT_URI_TABLE = CasedWords.TABLE;
		static public final String CASEDWORDID = "casedwordid";
		static public final String WORDID = "wordid";
		static public final String CASED = "cased";
	}

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
	static public final class Synsets
	{
		static public final String TABLE = "synsets";
		static public final String CONTENT_URI_TABLE = Synsets.TABLE;
		static public final String SYNSETID = "synsetid";
		static public final String POS = "pos";
		static public final String SENSEID = "senseid";
		static public final String LEXDOMAINID = "lexdomainid";
		static public final String DEFINITION = "definition";
	}

	@SuppressWarnings("unused")
	static public final class SemLinks
	{
		static public final String TABLE = "semlinks";
		static public final String CONTENT_URI_TABLE = SemLinks.TABLE;
		static public final String SYNSETID1 = "synset1id";
		static public final String SYNSETID2 = "synset2id";
		static public final String LINKID = "linkid";
	}

	@SuppressWarnings("unused")
	static public final class LexLinks
	{
		static public final String TABLE = "lexlinks";
		static public final String CONTENT_URI_TABLE = LexLinks.TABLE;
		static public final String WORDID1 = "word1id";
		static public final String SYNSETID1 = "synset1id";
		static public final String WORDID2 = "word2id";
		static public final String SYNSETID2 = "synset2id";
		static public final String LINKID = "linkid";
	}

	static public final class LinkTypes
	{
		static public final String TABLE = "linktypes";
		static public final String CONTENT_URI_TABLE = LinkTypes.TABLE;
		static public final String LINKID = "linkid";
		static public final String LINK = "link";
		static public final String RECURSES = "recurses";
		static final String RECURSESSTR = "recursesstr";
		static public final String RECURSESSELECT = "(CASE WHEN recurses <> 0 THEN 'recurses' ELSE '' END) AS " + LinkTypes.RECURSESSTR;
	}

	static public final class PosTypes
	{
		static public final String TABLE = "postypes";
		static public final String CONTENT_URI_TABLE = PosTypes.TABLE;
		static public final String POS = "pos";
		static public final String POSNAME = "posname";
	}

	static public final class AdjPositionTypes
	{
		static public final String TABLE = "adjpositiontypes";
		static public final String CONTENT_URI_TABLE = AdjPositionTypes.TABLE;
		static public final String POSITION = "position";
		static public final String POSITIONNAME = "positionname";
	}

	@SuppressWarnings("unused")
	static public final class LexDomains
	{
		static public final String TABLE = "lexdomains";
		static public final String CONTENT_URI_TABLE = LexDomains.TABLE;
		static public final String LEXDOMAINID = "lexdomainid";
		static public final String LEXDOMAINNAME = "lexdomainname";
		static public final String LEXDOMAIN = "lexdomain";
		static public final String POS = "pos";
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

	@SuppressWarnings("unused")
	static public final class Words_Senses_CasedWords_Synsets
	{
		static public final String TABLE = "words_senses_casedwords_synsets";
		static public final String CONTENT_URI_TABLE = Words_Senses_CasedWords_Synsets.TABLE;
		// words LEFT JOIN senses LEFT JOIN casedwords LEFT JOIN synsets
	}

	@SuppressWarnings("unused")
	static public final class Words_Senses_CasedWords_Synsets_PosTypes_LexDomains
	{
		static public final String TABLE = "words_senses_casedwords_synsets_postypes_lexdomains";
		static public final String CONTENT_URI_TABLE = Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.TABLE;
		static public final String LEMMA = "lemma";
		static public final String CASED = "cased";
		static public final String WORDID = "wordid";
		static public final String SYNSETID = "synsetid";
		static public final String SENSEID = "senseid";
		static public final String SENSENUM = "sensenum";
		static public final String SENSEKEY = "sensekey";
		static public final String DEFINITION = "definition";
		static public final String LEXID = "lexid";
		static public final String LEXDOMAIN = "lexdomain";
		static public final String POSNAME = "posname";
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
		static public final String MEMBER = "lemma";
		static public final String MEMBERS = "members";
		// synsets LEFT JOIN senses LEFT JOIN words
	}

	@SuppressWarnings("unused")
	static public final class Senses_Synsets_PosTypes_LexDomains
	{
		static public final String TABLE = "senses_synsets_postypes_lexdomains";
		static public final String CONTENT_URI_TABLE = Senses_Synsets_PosTypes_LexDomains.TABLE;
		// senses INNER JOIN synsets LEFT JOIN postypes LEFT JOIN lexdomains
	}

	static public final class Synsets_PosTypes_LexDomains
	{
		static public final String TABLE = "synsets_postypes_lexdomains";
		static public final String CONTENT_URI_TABLE = Synsets_PosTypes_LexDomains.TABLE;
		static public final String SYNSETID = "synsetid";
		// synsets LEFT JOIN postypes LEFT JOIN lexdomains
	}

	@SuppressWarnings("unused")
	static public final class SemLinks_Synsets
	{
		static public final String TABLE = "semlinks_synsets";
		static public final String CONTENT_URI_TABLE = SemLinks_Synsets.TABLE;
		// semlinks INNER JOIN synsets
	}

	@SuppressWarnings("unused")
	static public final class SemLinks_Synsets_X
	{
		static public final String TABLE = "semlinks_synsets_linktypes";
		static public final String CONTENT_URI_TABLE = SemLinks_Synsets_X.TABLE;
		// semlinks INNER JOIN synsets LEFT JOIN linktypes
	}

	@SuppressWarnings("unused")
	static public final class SemLinks_Synsets_Words_X
	{
		static public final String TABLE_BY_SYNSET = "semlinks_synsets_linktypes_senses_words_by_synset";
		static public final String CONTENT_URI_TABLE = SemLinks_Synsets_Words_X.TABLE_BY_SYNSET;
		static public final String SYNSET1ID = "synset1id";
		static public final String SYNSET2ID = "synset2id";
		static public final String MEMBERS2 = "members";
		static public final String RECURSES = "recurses";
		// semlinks INNER JOIN synsets LEFT JOIN linktypes LEFT JOIN senses LEFT JOIN words
	}

	@SuppressWarnings("unused")
	static public final class LexLinks_Senses
	{
		static public final String TABLE = "lexlinks_synsets_words";
		static public final String CONTENT_URI_TABLE = LexLinks_Senses.TABLE;
		// lexlinks INNER JOIN synsets
	}

	@SuppressWarnings("unused")
	static public final class LexLinks_Senses_X
	{
		static public final String TABLE = "lexlinks_synsets_words_linktypes";
		static public final String CONTENT_URI_TABLE = LexLinks_Senses_X.TABLE;
		// semlinks INNER JOIN synsets INNER JOIN words LEFT JOIN linktypes
	}

	@SuppressWarnings("unused")
	static public final class LexLinks_Senses_Words_X
	{
		static public final String TABLE_BY_SYNSET = "lexlinks_synsets_words_linktypes_senses_words_by_synset";
		static public final String CONTENT_URI_TABLE = LexLinks_Senses_Words_X.TABLE_BY_SYNSET;
		static public final String SYNSET1ID = "synset1id";
		static public final String SYNSET2ID = "synset2id";
		static public final String MEMBERS2 = "members";
		// semlinks INNER JOIN synsets INNER JOIN words LEFT JOIN linktypes LEFT JOIN senses LEFT JOIN words
	}

	static public final class VerbFrameMaps_VerbFrames
	{
		static public final String TABLE = "vframemaps_vframes";
		static public final String CONTENT_URI_TABLE = VerbFrameMaps_VerbFrames.TABLE;
		static public final String SYNSETID = "synsetid";
		static public final String WORDID = "wordid";
		static public final String FRAME = "frame";
		// vframemap LEFT JOIN vframes
	}

	static public final class VerbFrameSentenceMaps_VerbFrameSentences
	{
		static public final String TABLE = "vframesentencemaps_vframesentences";
		static public final String CONTENT_URI_TABLE = VerbFrameSentenceMaps_VerbFrameSentences.TABLE;
		static public final String SYNSETID = "synsetid";
		static public final String WORDID = "wordid";
		static public final String SENTENCE = "sentence";
		// vframesentencemaps LEFT JOIN vframesentences
	}

	@SuppressWarnings("unused")
	static public final class AdjPositions_AdjPositionTypes
	{
		static public final String TABLE = "adjpositions_adjpositiontypes";
		static public final String CONTENT_URI_TABLE = AdjPositions_AdjPositionTypes.TABLE;
		static public final String SYNSETID = "synsetid";
		static public final String WORDID = "wordid";
		static public final String POSITION = "position";
		static public final String POSITIONNAME = "positionname";
		// adjpositions LEFT JOIN adjpositiontypes
	}

	static public final class MorphMaps_Morphs
	{
		static public final String TABLE = "morphmaps_morphs";
		static public final String CONTENT_URI_TABLE = MorphMaps_Morphs.TABLE;
		static public final String WORDID = "wordid";
		static public final String MORPH = "morph";
		static public final String POS = "pos";
		// morphmaps LEFT JOIN morphs
	}

	@SuppressWarnings("unused")
	static public final class Words_MorphMaps_Morphs
	{
		static public final String TABLE = "words_morphmaps_morphs";
		static public final String TABLE_BY_WORD = "words_morphmaps_morphs_by_word";
		static public final String CONTENT_URI_TABLE = Words_MorphMaps_Morphs.TABLE;
		static public final String CONTENT_URI_TABLE_BY_WORD = Words_MorphMaps_Morphs.TABLE_BY_WORD;
		static public final String LEMMA = "lemma";
		static public final String WORDID = "wordid";
		static public final String MORPH = "morph";
		static public final String POS = "pos";
		// words LEFT JOIN morphmaps LEFT JOIN morphs
	}

	// V I E W S

	@SuppressWarnings("unused")
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
		static public final String LEMMA = "lemma";
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

	static public final class Suggest_Definitions
	{
		static final String SEARCH_DEFINITION_PATH = "suggest_definition";
		static public final String TABLE = Suggest_Definitions.SEARCH_DEFINITION_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
	}

	static public final class Suggest_Samples
	{
		static final String SEARCH_SAMPLE_PATH = "suggest_definition";
		static public final String TABLE = Suggest_Samples.SEARCH_SAMPLE_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
	}
}
