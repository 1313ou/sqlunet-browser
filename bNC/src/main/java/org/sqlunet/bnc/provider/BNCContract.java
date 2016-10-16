package org.sqlunet.bnc.provider;

public class BNCContract
{
	static public final String AUTHORITY = "org.sqlunet.bnc.provider"; //$NON-NLS-1$

	@SuppressWarnings("unused")
	static public final class BNCs
	{
		static public final String TABLE = "bncs"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + BNCContract.AUTHORITY + '/' + BNCs.TABLE; //$NON-NLS-1$
		public static final String WORDID = "wordid"; //$NON-NLS-1$
		public static final String POS = "pos"; //$NON-NLS-1$
		public static final String CONTENTS = "contents"; //$NON-NLS-1$
		public static final String FREQ = "freq"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class Words_BNCs
	{
		static public final String TABLE = "words_bncs"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + BNCContract.AUTHORITY + '/' + Words_BNCs.TABLE; //$NON-NLS-1$
		public static final String LEMMA = "lemma"; //$NON-NLS-1$
		public static final String WORDID = "wordid"; //$NON-NLS-1$
		public static final String POS = "pos"; //$NON-NLS-1$
		public static final String FREQ = "freq"; //$NON-NLS-1$
		public static final String RANGE = "range"; //$NON-NLS-1$
		public static final String DISP = "disp"; //$NON-NLS-1$
		public static final String FREQ1 = "freq1"; //$NON-NLS-1$
		public static final String RANGE1 = "range1"; //$NON-NLS-1$
		public static final String DISP1 = "disp1"; //$NON-NLS-1$
		public static final String FREQ2 = "freq2"; //$NON-NLS-1$
		public static final String RANGE2 = "range2"; //$NON-NLS-1$
		public static final String DISP2 = "disp2"; //$NON-NLS-1$
		public static final String BNCCONVTASKS = "bncconvtasks"; //$NON-NLS-1$
		public static final String BNCIMAGINFS = "bncimaginfs"; //$NON-NLS-1$
		public static final String BNCSPWRS = "bncspwrs"; //$NON-NLS-1$
	}
}
