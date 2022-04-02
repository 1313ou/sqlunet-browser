/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.bnc;

class SqLiteDialect
{
	static private final String BNCBaseWordQuery = "SELECT " + //
			"${wnposes.posid},${wnposes.pos},${bncs.freq},${bncs.range},${bncs.disp}," + //
			"${convtasks.table}.${bncs.freq1},${convtasks.table}.${bncs.range1},${convtasks.table}.${bncs.disp1}," + //
			"${convtasks.table}.${bncs.freq2},${convtasks.table}.${bncs.range2},${convtasks.table}.${bncs.disp2}," + //
			"${imaginfs.table}.${bncs.freq1},${imaginfs.table}.${bncs.range1},${imaginfs.table}.${bncs.disp1}," + //
			"${imaginfs.table}.${bncs.freq2},${imaginfs.table}.${bncs.range2},${imaginfs.table}.${bncs.disp2}," + //
			"${spwrs.table}.${bncs.freq1},${spwrs.table}.${bncs.range1},${spwrs.table}.${bncs.disp1}," + //
			"${spwrs.table}.${bncs.freq2},${spwrs.table}.${bncs.range2},${spwrs.table}.${bncs.disp2}," + //
			"${bncs.wordid} " + //
			"FROM ${wnwords.table} " + //
			"LEFT JOIN ${bncs.table} USING (wnwords.wordid) " + //
			"LEFT JOIN ${spwrs.table} USING (${wnwords.wordid},${wnposes.posid}) " + //
			"LEFT JOIN ${convtasks.table} USING (${wnwords.wordid},${wnposes.posid}) " + //
			"LEFT JOIN ${imaginfs.table} USING (${wnwords.wordid},${wnposes.posid}) " + //
			"LEFT JOIN ${wnposes.table} USING (${wnposes.posid}) ";

	static String BNCWordPosQuery = SqLiteDialect.BNCBaseWordQuery + //
			"WHERE ${wnwords.word} = ? AND ${wnposes.posid} = ?;";

	static String BNCWordQuery = SqLiteDialect.BNCBaseWordQuery + //
			"WHERE ${wnwords.word} = ?;";

	static private final String BNCBaseQuery = "SELECT " + //
			"${wnposes.posid},${wnposes.pos},${bncs.freq},${bncs.range},${bncs.disp}," + //
			"${convtasks.table}.${bncs.freq1},${convtasks.table}.${bncs.range1},${convtasks.table}.${bncs.disp1}," + //
			"${convtasks.table}.${bncs.freq2},${convtasks.table}.${bncs.range2},${convtasks.table}.${bncs.disp2}," + //
			"${imaginfs.table}.${bncs.freq1},${imaginfs.table}.${bncs.range1},${imaginfs.table}.${bncs.disp1}," + //
			"${imaginfs.table}.${bncs.freq2},${imaginfs.table}.${bncs.range2},${imaginfs.table}.${bncs.disp2}," + //
			"${spwrs.table}.${bncs.freq1},${spwrs.table}.${bncs.range1},${spwrs.table}.${bncs.disp1}," + //
			"${spwrs.table}.${bncs.freq2},${spwrs.table}.${bncs.range2},${spwrs.table}.${bncs.disp2} " + //
			"FROM ${bncs.table} " + //
			"LEFT JOIN ${spwrs.table} USING (${wnwords.wordid},${wnposes.posid}) " + //
			"LEFT JOIN ${convtasks.table} USING (${wnwords.wordid},${wnposes.posid}) " + //
			"LEFT JOIN ${imaginfs.table} USING (${wnwords.wordid},${wnposes.posid}) " + //
			"LEFT JOIN ${wnposes.table} USING (${wnposes.posid}) ";

	static final String BNCQueryFromWordIdAndPos = SqLiteDialect.BNCBaseQuery + //
			"WHERE ${wnwords.wordid} = ? AND ${wnposes.posid} = ?;";

	static final String BNCQueryFromWordId = SqLiteDialect.BNCBaseQuery + //
			"WHERE ${wnwords.wordid} = ?;";
}
