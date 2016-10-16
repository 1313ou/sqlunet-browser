package org.sqlunet.bnc.sql;

@SuppressWarnings("unused")
class SqLiteDialect
{
	private static final String BNCBaseWordQuery = "SELECT " + // //$NON-NLS-1$
			"pos, freq, range, disp, " + // //$NON-NLS-1$
			"bncconvtasks.freq1, bncconvtasks.range1, bncconvtasks.disp1, " + // //$NON-NLS-1$
			"bncconvtasks.freq2, bncconvtasks.range2, bncconvtasks.disp2, " + // //$NON-NLS-1$
			"bncimaginfs.freq1, bncimaginfs.range1, bncimaginfs.disp1, " + // //$NON-NLS-1$
			"bncimaginfs.freq2, bncimaginfs.range2, bncimaginfs.disp2, " + // //$NON-NLS-1$
			"bncspwrs.freq1, bncspwrs.range1, bncspwrs.disp1, " + // //$NON-NLS-1$
			"bncspwrs.freq2, bncspwrs.range2, bncspwrs.disp2, " + // //$NON-NLS-1$
			"wordid " + // //$NON-NLS-1$
			"FROM words " + // //$NON-NLS-1$
			"LEFT JOIN bncs USING (wordid) " + // //$NON-NLS-1$
			"LEFT JOIN bncspwrs USING (wordid, pos) " + // //$NON-NLS-1$
			"LEFT JOIN bncconvtasks USING (wordid, pos) " + // //$NON-NLS-1$
			"LEFT JOIN bncimaginfs USING (wordid, pos) "; //$NON-NLS-1$

	static String BNCWordPosQuery = SqLiteDialect.BNCBaseWordQuery + //
			"WHERE lemma = ? AND pos = ?;"; //$NON-NLS-1$

	static String BNCWordQuery = SqLiteDialect.BNCBaseWordQuery + //
			"WHERE lemma = ?;"; //$NON-NLS-1$

	private static final String BNCBaseQuery = "SELECT " + // //$NON-NLS-1$
			"pos, freq, range, disp, " + // //$NON-NLS-1$
			"bncconvtasks.freq1, bncconvtasks.range1, bncconvtasks.disp1, " + // //$NON-NLS-1$
			"bncconvtasks.freq2, bncconvtasks.range2, bncconvtasks.disp2, " + // //$NON-NLS-1$
			"bncimaginfs.freq1, bncimaginfs.range1, bncimaginfs.disp1, " + // //$NON-NLS-1$
			"bncimaginfs.freq2, bncimaginfs.range2, bncimaginfs.disp2, " + // //$NON-NLS-1$
			"bncspwrs.freq1, bncspwrs.range1, bncspwrs.disp1, " + // //$NON-NLS-1$
			"bncspwrs.freq2, bncspwrs.range2, bncspwrs.disp2 " + // //$NON-NLS-1$
			"FROM bncs " + // //$NON-NLS-1$
			"LEFT JOIN bncspwrs USING (wordid, pos) " + // //$NON-NLS-1$
			"LEFT JOIN bncconvtasks USING (wordid, pos) " + // //$NON-NLS-1$
			"LEFT JOIN bncimaginfs USING (wordid, pos) "; //$NON-NLS-1$

	static final String BNCPosQuery = SqLiteDialect.BNCBaseQuery + //
			"WHERE wordid = ? AND pos = ?;"; //$NON-NLS-1$

	static final String BNCQuery = SqLiteDialect.BNCBaseQuery + //
			"WHERE wordid = ?;"; //$NON-NLS-1$
}
