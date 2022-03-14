/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.bnc.provider;

public class BNCContract
{
	static public final class BNCs
	{
		static public final String TABLE = "bnc_bncs";
		static public final String CONTENT_URI_TABLE = BNCs.TABLE;
		static public final String WORDID = "wordid";
		static public final String POSID = "posid";
		static public final String CONTENTS = "contents";
		static public final String FREQ = "freq";
	}

	static public final class Words_BNCs
	{
		static public final String TABLE = "words_bncs";
		static public final String CONTENT_URI_TABLE = Words_BNCs.TABLE;
		static public final String WORD = "word";
		static public final String WORDID = "wordid";
		static public final String POSID = "posid";
		static public final String FREQ = "freq";
		static public final String RANGE = "range";
		static public final String DISP = "disp";
		static public final String FREQ1 = "freq1";
		static public final String RANGE1 = "range1";
		static public final String DISP1 = "disp1";
		static public final String FREQ2 = "freq2";
		static public final String RANGE2 = "range2";
		static public final String DISP2 = "disp2";
		static public final String BNCCONVTASKS = "bnc_convtasks";
		static public final String BNCIMAGINFS = "bnc_imaginfs";
		static public final String BNCSPWRS = "bnc_spwrs";
	}
}
