package org.sqlunet.syntagnet.provider;

public class Q
{
	// VARIABLES
	public static final String _ID = "_id";
	public static final String ANNOTATIONS = "annotations";
	public static final String AS_ANNOSETS = "an";
	public static final String AS_ARGS = "ar";
	public static final String AS_CASEDS = "c";
	public static final String AS_DOMAINS = "d";
	public static final String AS_EXAMPLES = "e";
	public static final String AS_FES = "fe";
	public static final String AS_FETYPES = "ft";
	public static final String AS_FRAMES = "fr";
	public static final String AS_FUNCS = "fu";
	public static final String AS_LEXUNITS = "lu";
	public static final String AS_MEMBERS = "m";
	public static final String AS_POSES = "p";
	public static final String AS_RELATED_FRAMES = "rf";
	public static final String AS_RELATIONS = "r";
	public static final String AS_SENSES = "s";
	public static final String AS_SENTENCES = "st";
	public static final String AS_SYNSETS = "y";
	public static final String AS_SYNSETS1 = "y1";
	public static final String AS_SYNSETS2 = "y2";
	public static final String AS_TYPES = "t";
	public static final String AS_WORDS = "w";
	public static final String AS_WORDS1 = "w1";
	public static final String AS_WORDS2 = "w2";
	public static final String DEST_FRAME = "df";
	public static final String FNID = "fnid";
	public static final String ISFRAME = "isframe";
	public static final String MEMBERS = "members";
	public static final String MEMBERS2 = "members2";
	public static final String NAME = "name";
	public static final String SAMPLESET = "sampleset";
	public static final String SENSEKEY = "sensekey";
	public static final String SENSEKEY1 = "sensekey1";
	public static final String SENSEKEY2 = "sensekey2";
	public static final String SRC_FRAME = "sf";
	public static final String SYNSET1ID = "synset1id";
	public static final String SYNSET2ID = "synset2id";
	public static final String SYNSETID = "synsetid";
	public static final String SYNTAGMID = "syntagmid";
	public static final String WORD = "word";
	public static final String WORD1ID = "word1id";
	public static final String WORD2 = "word2";
	public static final String WORD2ID = "word2id";
	public static final String WORDID = "wordid";

	public static final String POS = "pos";
	public static final String DEFINITION = "definition";

	static public class COLLOCATIONS
	{
		static public final String TABLE = "sn_syntagms";
		static public final String SELECTION = "syntagmid = ?";
	}

	static public class COLLOCATIONS_X
	{
		static public final String TABLE = "sn_syntagms " +
				"JOIN words AS w1 ON (word1id = w1.wordid) " +
				"JOIN words AS w2 ON (word2id = w2.wordid) " +
				"JOIN synsets AS y1 ON (synset1id = y1.synsetid) " +
				"JOIN synsets AS y2 ON (synset2id = y2.synsetid)";
	}
}
