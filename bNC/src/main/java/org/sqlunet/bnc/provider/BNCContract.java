package org.sqlunet.bnc.provider;

public class BNCContract
{
	@SuppressWarnings("unused")
	static public final class BNCs
	{
		static public final String TABLE = "bncs";
		static public final String CONTENT_URI_TABLE = BNCs.TABLE;
		static public final String WORDID = "wordid";
		static public final String POS = "pos";
		static public final String CONTENTS = "contents";
		static public final String FREQ = "freq";
	}

	@SuppressWarnings("unused")
	static public final class Words_BNCs
	{
		static public final String TABLE = "words_bncs";
		static public final String CONTENT_URI_TABLE = Words_BNCs.TABLE;
		static public final String LEMMA = "lemma";
		static public final String WORDID = "wordid";
		static public final String POS = "pos";
		static public final String FREQ = "freq";
		static public final String RANGE = "range";
		static public final String DISP = "disp";
		static public final String FREQ1 = "freq1";
		static public final String RANGE1 = "range1";
		static public final String DISP1 = "disp1";
		static public final String FREQ2 = "freq2";
		static public final String RANGE2 = "range2";
		static public final String DISP2 = "disp2";
		static public final String BNCCONVTASKS = "bncconvtasks";
		static public final String BNCIMAGINFS = "bncimaginfs";
		static public final String BNCSPWRS = "bncspwrs";
	}
}
