package org.sqlunet.bnc.provider;

import org.sqlunet.provider.BaseProvider;

public class BNCContract
{
	static public final String AUTHORITY = "org.sqlunet.bnc.provider";

	@SuppressWarnings("unused")
	static public final class BNCs
	{
		static public final String TABLE = "bncs";
		static public final String CONTENT_URI = BaseProvider.SCHEME + BNCContract.AUTHORITY + '/' + BNCs.TABLE;
		public static final String WORDID = "wordid";
		public static final String POS = "pos";
		public static final String CONTENTS = "contents";
		public static final String FREQ = "freq";
	}

	@SuppressWarnings("unused")
	static public final class Words_BNCs
	{
		static public final String TABLE = "words_bncs";
		static public final String CONTENT_URI = BaseProvider.SCHEME + BNCContract.AUTHORITY + '/' + Words_BNCs.TABLE;
		public static final String LEMMA = "lemma";
		public static final String WORDID = "wordid";
		public static final String POS = "pos";
		public static final String FREQ = "freq";
		public static final String RANGE = "range";
		public static final String DISP = "disp";
		public static final String FREQ1 = "freq1";
		public static final String RANGE1 = "range1";
		public static final String DISP1 = "disp1";
		public static final String FREQ2 = "freq2";
		public static final String RANGE2 = "range2";
		public static final String DISP2 = "disp2";
		public static final String BNCCONVTASKS = "bncconvtasks";
		public static final String BNCIMAGINFS = "bncimaginfs";
		public static final String BNCSPWRS = "bncspwrs";
	}
}
