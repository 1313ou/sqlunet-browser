package org.sqlunet.bnc.sql;

@SuppressWarnings("unused")
class SqLiteDialect
{
	private static final String BNCBaseWordQuery = "SELECT " + //
			"pos, freq, range, disp, " + //
			"bncconvtasks.freq1, bncconvtasks.range1, bncconvtasks.disp1, " + //
			"bncconvtasks.freq2, bncconvtasks.range2, bncconvtasks.disp2, " + //
			"bncimaginfs.freq1, bncimaginfs.range1, bncimaginfs.disp1, " + //
			"bncimaginfs.freq2, bncimaginfs.range2, bncimaginfs.disp2, " + //
			"bncspwrs.freq1, bncspwrs.range1, bncspwrs.disp1, " + //
			"bncspwrs.freq2, bncspwrs.range2, bncspwrs.disp2, " + //
			"wordid " + //
			"FROM words " + //
			"LEFT JOIN bncs USING (wordid) " + //
			"LEFT JOIN bncspwrs USING (wordid, pos) " + //
			"LEFT JOIN bncconvtasks USING (wordid, pos) " + //
			"LEFT JOIN bncimaginfs USING (wordid, pos) "; //

	static String BNCWordPosQuery = SqLiteDialect.BNCBaseWordQuery + //
			"WHERE lemma = ? AND pos = ?;"; //

	static String BNCWordQuery = SqLiteDialect.BNCBaseWordQuery + //
			"WHERE lemma = ?;"; //

	private static final String BNCBaseQuery = "SELECT " + //
			"pos, freq, range, disp, " + //
			"bncconvtasks.freq1, bncconvtasks.range1, bncconvtasks.disp1, " + //
			"bncconvtasks.freq2, bncconvtasks.range2, bncconvtasks.disp2, " + //
			"bncimaginfs.freq1, bncimaginfs.range1, bncimaginfs.disp1, " + //
			"bncimaginfs.freq2, bncimaginfs.range2, bncimaginfs.disp2, " + //
			"bncspwrs.freq1, bncspwrs.range1, bncspwrs.disp1, " + //
			"bncspwrs.freq2, bncspwrs.range2, bncspwrs.disp2 " + //
			"FROM bncs " + //
			"LEFT JOIN bncspwrs USING (wordid, pos) " + //
			"LEFT JOIN bncconvtasks USING (wordid, pos) " + //
			"LEFT JOIN bncimaginfs USING (wordid, pos) "; //

	static final String BNCPosQuery = SqLiteDialect.BNCBaseQuery + //
			"WHERE wordid = ? AND pos = ?;"; //

	static final String BNCQuery = SqLiteDialect.BNCBaseQuery + //
			"WHERE wordid = ?;"; //
}
