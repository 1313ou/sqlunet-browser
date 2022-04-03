/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.bnc.sql;

import androidx.annotation.NonNull;

class SqLiteDialect0
{
	static private final String BNCBaseWordQuery = "SELECT " + //
			"posid, pos, freq, range, disp, " + //
			"bnc_convtasks.freq1, bnc_convtasks.range1, bnc_convtasks.disp1, " + //
			"bnc_convtasks.freq2, bnc_convtasks.range2, bnc_convtasks.disp2, " + //
			"bnc_imaginfs.freq1, bnc_imaginfs.range1, bnc_imaginfs.disp1, " + //
			"bnc_imaginfs.freq2, bnc_imaginfs.range2, bnc_imaginfs.disp2, " + //
			"bnc_spwrs.freq1, bnc_spwrs.range1, bnc_spwrs.disp1, " + //
			"bnc_spwrs.freq2, bnc_spwrs.range2, bnc_spwrs.disp2, " + //
			"wordid " + //
			"FROM words " + //
			"LEFT JOIN bnc_bncs USING (wordid) " + //
			"LEFT JOIN bnc_spwrs USING (wordid, posid) " + //
			"LEFT JOIN bnc_convtasks USING (wordid, posid) " + //
			"LEFT JOIN bnc_imaginfs USING (wordid, posid) " + //
			"LEFT JOIN poses USING (posid) ";

	@NonNull
	static String BNCWordPosQuery = SqLiteDialect0.BNCBaseWordQuery + //
			"WHERE word = ? AND posid = ?;";

	@NonNull
	static String BNCWordQuery = SqLiteDialect0.BNCBaseWordQuery + //
			"WHERE word = ?;";

	static private final String BNCBaseQuery = "SELECT " + //
			"posid, pos, freq, range, disp, " + //
			"bnc_convtasks.freq1, bnc_convtasks.range1, bnc_convtasks.disp1, " + //
			"bnc_convtasks.freq2, bnc_convtasks.range2, bnc_convtasks.disp2, " + //
			"bnc_imaginfs.freq1, bnc_imaginfs.range1, bnc_imaginfs.disp1, " + //
			"bnc_imaginfs.freq2, bnc_imaginfs.range2, bnc_imaginfs.disp2, " + //
			"bnc_spwrs.freq1, bnc_spwrs.range1, bnc_spwrs.disp1, " + //
			"bnc_spwrs.freq2, bnc_spwrs.range2, bnc_spwrs.disp2 " + //
			"FROM bnc_bncs " + //
			"LEFT JOIN bnc_spwrs USING (wordid, posid) " + //
			"LEFT JOIN bnc_convtasks USING (wordid, posid) " + //
			"LEFT JOIN bnc_imaginfs USING (wordid, posid) " + //
			"LEFT JOIN poses USING (posid) ";

	static final String BNCQueryFromWordIdAndPos = SqLiteDialect0.BNCBaseQuery + //
			"WHERE wordid = ? AND posid = ?;";

	static final String BNCQueryFromWordId = SqLiteDialect0.BNCBaseQuery + //
			"WHERE wordid = ?;";
}
