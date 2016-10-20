package org.sqlunet.wordnet.provider;

import android.app.SearchManager;

public class WordNetContract
{
	static public final String AUTHORITY = "org.sqlunet.wordnet.provider"; //$NON-NLS-1$

	// T A B L E S

	@SuppressWarnings("unused")
	static public final class Words
	{
		static public final String TABLE = "words"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + Words.TABLE; //$NON-NLS-1$
		static public final String WORDID = "wordid"; //$NON-NLS-1$
		static public final String LEMMA = "lemma"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class CasedWords
	{
		static public final String TABLE = "casedwords"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + CasedWords.TABLE; //$NON-NLS-1$
		static public final String CASEDWORDID = "casedwordid"; //$NON-NLS-1$
		static public final String WORDID = "wordid"; //$NON-NLS-1$
		static public final String CASED = "cased"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class Senses
	{
		static public final String TABLE = "senses"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + Senses.TABLE; //$NON-NLS-1$
		static public final String WORDID = "wordid"; //$NON-NLS-1$
		static public final String SYNSETID = "synsetid"; //$NON-NLS-1$
		static public final String CASEDWORDID = "casedwordid"; //$NON-NLS-1$
		static public final String SENSEID = "senseid"; //$NON-NLS-1$
		static public final String SENSENUM = "sensenum"; //$NON-NLS-1$
		static public final String SENSEKEY = "sensekey"; //$NON-NLS-1$
		static public final String LEXID = "lexid"; //$NON-NLS-1$
		static public final String TAGCOUNT = "tagcount"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class Synsets
	{
		static public final String TABLE = "synsets"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + Synsets.TABLE; //$NON-NLS-1$
		static public final String SYNSETID = "synsetid"; //$NON-NLS-1$
		static public final String POS = "pos"; //$NON-NLS-1$
		static public final String SENSEID = "senseid"; //$NON-NLS-1$
		static public final String LEXDOMAINID = "lexdomainid"; //$NON-NLS-1$
		static public final String DEFINITION = "definition"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class SemLinks
	{
		static public final String TABLE = "semlinks"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + SemLinks.TABLE; //$NON-NLS-1$
		static public final String SYNSETID1 = "synset1id"; //$NON-NLS-1$
		static public final String SYNSETID2 = "synset2id"; //$NON-NLS-1$
		static public final String LINKID = "linkid"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class LexLinks
	{
		static public final String TABLE = "lexlinks"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + LexLinks.TABLE; //$NON-NLS-1$
		static public final String WORDID1 = "word1id"; //$NON-NLS-1$
		static public final String SYNSETID1 = "synset1id"; //$NON-NLS-1$
		static public final String WORDID2 = "word2id"; //$NON-NLS-1$
		static public final String SYNSETID2 = "synset2id"; //$NON-NLS-1$
		static public final String LINKID = "linkid"; //$NON-NLS-1$
	}

	static public final class LinkTypes
	{
		static public final String TABLE = "linktypes"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + LinkTypes.TABLE; //$NON-NLS-1$
		static public final String LINKID = "linkid"; //$NON-NLS-1$
		static public final String LINK = "link"; //$NON-NLS-1$
		static public final String RECURSES = "recurses"; //$NON-NLS-1$
		static public final String RECURSESSTR = "recursesstr"; //$NON-NLS-1$
		static public final String RECURSESSELECT = "(CASE WHEN recurses <> 0 THEN 'recurses' ELSE '' END) AS " + LinkTypes.RECURSESSTR; //$NON-NLS-1$
	}

	static public final class PosTypes
	{
		static public final String TABLE = "postypes"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + PosTypes.TABLE; //$NON-NLS-1$
		static public final String POS = "pos"; //$NON-NLS-1$
		static public final String POSNAME = "posname"; //$NON-NLS-1$
	}

	static public final class AdjPositionTypes
	{
		static public final String TABLE = "adjpositiontypes"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + AdjPositionTypes.TABLE; //$NON-NLS-1$
		static public final String POSITION = "position"; //$NON-NLS-1$
		static public final String POSITIONNAME = "positionname"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class LexDomains
	{
		static public final String TABLE = "lexdomains"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + LexDomains.TABLE; //$NON-NLS-1$
		static public final String LEXDOMAINID = "lexdomainid"; //$NON-NLS-1$
		static public final String LEXDOMAINNAME = "lexdomainname"; //$NON-NLS-1$
		static public final String LEXDOMAIN = "lexdomain"; //$NON-NLS-1$
		static public final String POS = "pos"; //$NON-NLS-1$
	}

	static public final class Samples
	{
		static public final String TABLE = "samples"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + Samples.TABLE; //$NON-NLS-1$
		static public final String SYNSETID = "synsetid"; //$NON-NLS-1$
		static public final String SAMPLEID = "sampleid"; //$NON-NLS-1$
		static public final String SAMPLE = "sample"; //$NON-NLS-1$
	}

	// J O I N S

	static public final class Words_Senses_Synsets
	{
		static public final String TABLE = "words_senses_synsets"; //$NON-NLS-1$
		// static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + Words_Senses_Synsets.TABLE;
		// words LEFT JOIN senses LEFT JOIN synsets
	}

	@SuppressWarnings("unused")
	static public final class Words_Senses_CasedWords_Synsets
	{
		static public final String TABLE = "words_senses_casedwords_synsets"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + Words_Senses_CasedWords_Synsets.TABLE; //$NON-NLS-1$
		// words LEFT JOIN senses LEFT JOIN casedwords LEFT JOIN synsets
	}

	@SuppressWarnings("unused")
	static public final class Words_Senses_CasedWords_Synsets_PosTypes_LexDomains
	{
		static public final String TABLE = "words_senses_casedwords_synsets_postypes_lexdomains"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.TABLE; //$NON-NLS-1$
		static public final String LEMMA = "lemma"; //$NON-NLS-1$
		static public final String CASED = "cased"; //$NON-NLS-1$
		static public final String SYNSETID = "synsetid"; //$NON-NLS-1$
		static public final String SENSENUM = "sensenum"; //$NON-NLS-1$
		static public final String SENSEKEY = "sensekey"; //$NON-NLS-1$
		static public final String DEFINITION = "definition"; //$NON-NLS-1$
		static public final String LEXDOMAIN = "lexdomain"; //$NON-NLS-1$
		static public final String POS = "synsets.pos"; //$NON-NLS-1$
		// words LEFT JOIN senses LEFT JOIN casedwords LEFT JOIN synsets
	}

	static public final class Senses_Words
	{
		static public final String TABLE = "senses_words"; //$NON-NLS-1$
		static public final String TABLE_BY_SYNSET = "senses_words_by_synset"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + Senses_Words.TABLE; //$NON-NLS-1$
		static public final String CONTENT_URI_BY_SYNSET = "content://" + WordNetContract.AUTHORITY + '/' + Senses_Words.TABLE_BY_SYNSET; //$NON-NLS-1$
		static public final String SYNSETID = "synsetid"; //$NON-NLS-1$
		static public final String MEMBERS = "members"; //$NON-NLS-1$
		// synsets LEFT JOIN senses LEFT JOIN words
	}

	@SuppressWarnings("unused")
	static public final class Senses_Synsets_PosTypes_LexDomains
	{
		static public final String TABLE = "senses_synsets_postypes_lexdomains"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + Senses_Synsets_PosTypes_LexDomains.TABLE; //$NON-NLS-1$
		// senses INNER JOIN synsets LEFT JOIN postypes LEFT JOIN lexdomains
	}

	static public final class Synsets_PosTypes_LexDomains
	{
		static public final String TABLE = "synsets_postypes_lexdomains"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + Synsets_PosTypes_LexDomains.TABLE; //$NON-NLS-1$
		static public final String SYNSETID = "synsetid"; //$NON-NLS-1$
		// synsets LEFT JOIN postypes LEFT JOIN lexdomains
	}

	@SuppressWarnings("unused")
	static public final class SemLinks_Synsets
	{
		static public final String TABLE = "semlinks_synsets"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + SemLinks_Synsets.TABLE; //$NON-NLS-1$
		// semlinks INNER JOIN synsets
	}

	@SuppressWarnings("unused")
	static public final class SemLinks_Synsets_X
	{
		static public final String TABLE = "semlinks_synsets_linktypes"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + SemLinks_Synsets_X.TABLE; //$NON-NLS-1$
		static public final String SYNSETID = "d_synsetid"; //$NON-NLS-1$
		static public final String DEFINITION = "d_definition"; //$NON-NLS-1$
		// semlinks INNER JOIN synsets LEFT JOIN linktypes
	}

	static public final class SemLinks_Synsets_Words_X
	{
		static public final String TABLE_BY_SYNSET = "semlinks_synsets_linktypes_senses_words_by_synset"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + SemLinks_Synsets_Words_X.TABLE_BY_SYNSET; //$NON-NLS-1$
		static public final String SYNSET1ID = "synset1id"; //$NON-NLS-1$
		static public final String MEMBERS = "members"; //$NON-NLS-1$
		static public final String RECURSES = "recurses"; //$NON-NLS-1$
		// semlinks INNER JOIN synsets LEFT JOIN linktypes LEFT JOIN senses LEFT JOIN words
	}

	@SuppressWarnings("unused")
	static public final class LexLinks_Senses
	{
		static public final String TABLE = "lexlinks_synsets_words"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + LexLinks_Senses.TABLE; //$NON-NLS-1$
		// lexlinks INNER JOIN synsets
	}

	@SuppressWarnings("unused")
	static public final class LexLinks_Senses_X
	{
		static public final String TABLE = "lexlinks_synsets_words_linktypes"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + LexLinks_Senses_X.TABLE; //$NON-NLS-1$
		static public final String SYNSETID = "d_synsetid"; //$NON-NLS-1$
		static public final String DEFINITION = "d_definition"; //$NON-NLS-1$
		static public final String TARGET_LEMMA = "w_lemma"; //$NON-NLS-1$
		static public final String TARGET_WORDID = "w_wordid"; //$NON-NLS-1$
		// semlinks INNER JOIN synsets INNER JOIN words LEFT JOIN linktypes
	}

	static public final class LexLinks_Senses_Words_X
	{
		static public final String TABLE_BY_SYNSET = "lexlinks_synsets_words_linktypes_senses_words_by_synset"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + LexLinks_Senses_Words_X.TABLE_BY_SYNSET; //$NON-NLS-1$
		static public final String SYNSET1ID = "synset1id"; //$NON-NLS-1$
		static public final String MEMBERS = "members"; //$NON-NLS-1$
		// semlinks INNER JOIN synsets INNER JOIN words LEFT JOIN linktypes LEFT JOIN senses LEFT JOIN words
	}

	static public final class VerbFrameMaps_VerbFrames
	{
		static public final String TABLE = "vframemaps_vframes"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + VerbFrameMaps_VerbFrames.TABLE; //$NON-NLS-1$
		static public final String SYNSETID = "synsetid"; //$NON-NLS-1$
		static public final String WORDID = "wordid"; //$NON-NLS-1$
		static public final String FRAME = "frame"; //$NON-NLS-1$
		// vframemap LEFT JOIN vframes
	}

	static public final class VerbFrameSentenceMaps_VerbFrameSentences
	{
		static public final String TABLE = "vframesentencemaps_vframesentences"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + VerbFrameSentenceMaps_VerbFrameSentences.TABLE; //$NON-NLS-1$
		static public final String SYNSETID = "synsetid"; //$NON-NLS-1$
		static public final String WORDID = "wordid"; //$NON-NLS-1$
		static public final String SENTENCE = "sentence"; //$NON-NLS-1$
		// vframesentencemaps LEFT JOIN vframesentences
	}

	@SuppressWarnings("unused")
	static public final class AdjPositions_AdjPositionTypes
	{
		static public final String TABLE = "adjpositions_adjpositiontypes"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + AdjPositions_AdjPositionTypes.TABLE; //$NON-NLS-1$
		static public final String SYNSETID = "synsetid"; //$NON-NLS-1$
		static public final String WORDID = "wordid"; //$NON-NLS-1$
		static public final String POSITION = "position"; //$NON-NLS-1$
		static public final String POSITIONNAME = "positionname"; //$NON-NLS-1$
		// adjpositions LEFT JOIN adjpositiontypes
	}

	static public final class MorphMaps_Morphs
	{
		static public final String TABLE = "morphmaps_morphs"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + MorphMaps_Morphs.TABLE; //$NON-NLS-1$
		static public final String WORDID = "wordid"; //$NON-NLS-1$
		static public final String MORPH = "morph"; //$NON-NLS-1$
		static public final String POS = "pos"; //$NON-NLS-1$
		// morphmaps LEFT JOIN morphs
	}

	// V I E W S

	@SuppressWarnings("unused")
	static public final class Dict
	{
		static public final String TABLE = "dict"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + Dict.TABLE; //$NON-NLS-1$
		// words LEFT JOIN senses LEFT JOIN casedwords LEFT JOIN synsets
	}

	// T E X T S E A R C H

	static public final class Lookup_Words
	{
		static public final String TABLE = "fts_words"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + Lookup_Words.TABLE; //$NON-NLS-1$
		static public final String WORDID = "wordid"; //$NON-NLS-1$
		static public final String LEMMA = "lemma"; //$NON-NLS-1$
	}

	static public final class Lookup_Definitions
	{
		static public final String TABLE = "fts_definitions"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + Lookup_Definitions.TABLE; //$NON-NLS-1$
		static public final String SYNSETID = "synsetid"; //$NON-NLS-1$
		static public final String DEFINITION = "definition"; //$NON-NLS-1$
	}

	static public final class Lookup_Samples
	{
		static public final String TABLE = "fts_samples"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + WordNetContract.AUTHORITY + '/' + Lookup_Samples.TABLE; //$NON-NLS-1$
		static public final String SYNSETID = "synsetid"; //$NON-NLS-1$
		static public final String SAMPLE = "sample"; //$NON-NLS-1$
	}

	// S E A R C H

	static public final class Search_Words
	{
		static public final String SEARCH_WORD_PATH = "lookup_word"; //$NON-NLS-1$
		static public final String TABLE = Search_Words.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY; //$NON-NLS-1$
	}

	static public final class Search_Definitions
	{
		static public final String SEARCH_DEFINITION_PATH = "lookup_definition"; //$NON-NLS-1$
		static public final String TABLE = Search_Definitions.SEARCH_DEFINITION_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY; //$NON-NLS-1$
	}

	static public final class Search_Samples
	{
		static public final String SEARCH_SAMPLE_PATH = "lookup_definition"; //$NON-NLS-1$
		static public final String TABLE = Search_Samples.SEARCH_SAMPLE_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY; //$NON-NLS-1$
	}
}
