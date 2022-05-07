/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.bnc.provider;

public class BNCContract
{
	static public final class BNCs
	{
		static public final String TABLE = Q.BNCS.TABLE;
		static public final String CONTENT_URI_TABLE = BNCs.TABLE;
		static public final String WORDID = V.WORDID;
		static public final String POSID = V.POSID;
		static public final String FREQ = V.FREQ;
	}

	static public final class Words_BNCs
	{
		static public final String TABLE = "words_bncs";
		static public final String CONTENT_URI_TABLE = Words_BNCs.TABLE;
		static public final String WORD = V.WORD;
		static public final String WORDID = V.WORDID;
		static public final String POSID = V.POSID;
		static public final String FREQ = V.FREQ;
		static public final String RANGE = V.RANGE;
		static public final String DISP = V.DISP;
		static public final String FREQ1 = V.FREQ1;
		static public final String RANGE1 = V.RANGE1;
		static public final String DISP1 = V.DISP1;
		static public final String FREQ2 = V.FREQ2;
		static public final String RANGE2 = V.RANGE2;
		static public final String DISP2 = V.DISP2;
		static public final String BNCCONVTASKS = V.BNCCONVTASKS;
		static public final String BNCIMAGINFS = V.BNCIMAGINFS;
		static public final String BNCSPWRS = V.BNCSPWRS;
	}
}
