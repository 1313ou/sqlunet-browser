/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.bnc.provider;

public class BNCContract
{
	public interface BNCs
	{
		String TABLE = Q.BNCS.TABLE;
		String URI = BNCs.TABLE;
		String WORDID = V.WORDID;
		String POSID = V.POSID;
		String FREQ = V.FREQ;
	}

	public interface Words_BNCs
	{
		String URI = "words_bncs";
		String WORD = V.WORD;
		String WORDID = V.WORDID;
		String POSID = V.POSID;
		String FREQ = V.FREQ;
		String RANGE = V.RANGE;
		String DISP = V.DISP;
		String FREQ1 = V.FREQ1;
		String RANGE1 = V.RANGE1;
		String DISP1 = V.DISP1;
		String FREQ2 = V.FREQ2;
		String RANGE2 = V.RANGE2;
		String DISP2 = V.DISP2;
		String BNCCONVTASKS = V.BNCCONVTASKS;
		String BNCIMAGINFS = V.BNCIMAGINFS;
		String BNCSPWRS = V.BNCSPWRS;
	}
}
