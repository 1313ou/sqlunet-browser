/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.syntagnet.provider;

/**
 * SyntagNet provider contract
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SyntagNetContract
{
	// A L I A S E S

	static public final String W1 = "w1";
	static public final String W2 = "w2";
	static public final String S1 = "y1";
	static public final String S2 = "y2";

	static public final String WORD1 = "word1";
	static public final String WORD2 = "word2";
	static public final String POS1 = "pos1";
	static public final String POS2 = "pos2";
	static public final String DEFINITION1 = "definition1";
	static public final String DEFINITION2 = "definition2";

	// T A B L E S

	static public final class SnCollocations
	{
		static public final String TABLE = "syntagms";
		static public final String CONTENT_URI_TABLE = SnCollocations.TABLE;
		static public final String COLLOCATIONID = "syntagmid";
		static public final String WORD1ID = "word1id";
		static public final String WORD2ID = "word2id";
		static public final String SYNSET1ID = "synset1id";
		static public final String SYNSET2ID = "synset2id";
		static public final String WORD = "word";
	}

	static public final class SnCollocations_X
	{
		static public final String TABLE = "syntagms_x";
		static public final String CONTENT_URI_TABLE = SnCollocations_X.TABLE;
		static public final String COLLOCATIONID = "syntagmid";
		static public final String WORD1ID = "word1id";
		static public final String WORD2ID = "word2id";
		static public final String SYNSET1ID = "synset1id";
		static public final String SYNSET2ID = "synset2id";
		static public final String WORD = "word";
		static public final String POS = "pos";
		static public final String DEFINITION = "definition";
	}
}
