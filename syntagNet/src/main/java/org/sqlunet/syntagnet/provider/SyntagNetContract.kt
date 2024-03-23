/*
 * Copyright (c) 2023. Bernard Bou
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

	static public final String AS_WORDS1 = V.AS_WORDS1;
	static public final String AS_WORDS2 = V.AS_WORDS2;
	static public final String AS_SYNSETS1 = V.AS_SYNSETS1;
	static public final String AS_SYNSETS2 = V.AS_SYNSETS2;

	static public final String WORD1 = V.WORD1;
	static public final String WORD2 = V.WORD2;
	static public final String POS1 = V.POS1;
	static public final String POS2 = V.POS2;
	static public final String DEFINITION1 = V.DEFINITION1;
	static public final String DEFINITION2 = V.DEFINITION2;

	// T A B L E S

	public interface SnCollocations
	{
		String TABLE = Q.COLLOCATIONS.TABLE;
		String URI = TABLE;
		String COLLOCATIONID = V.SYNTAGMID;
		String WORD1ID = V.WORD1ID;
		String WORD2ID = V.WORD2ID;
		String SYNSET1ID = V.SYNSET1ID;
		String SYNSET2ID = V.SYNSET2ID;
		String WORD = V.WORD;
	}

	public interface SnCollocations_X
	{
		String URI = "sn_syntagms_x";
		String COLLOCATIONID = V.SYNTAGMID;
		String WORD1ID = V.WORD1ID;
		String WORD2ID = V.WORD2ID;
		String SYNSET1ID = V.SYNSET1ID;
		String SYNSET2ID = V.SYNSET2ID;
		String WORD = V.WORD;
		String POS = V.POSID;
		String DEFINITION = V.DEFINITION;
	}
}
