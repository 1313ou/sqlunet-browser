package org.sqlunet.bnc.provider;

public class Queries
{
	static public class BNCS
	{
		static public final String TABLE = "bnc_bncs";
		static public final String SELECTION = "posid = ?";
	}

	static public class WORDS_BNCS
	{
		static public final String TABLE = "bnc_bncs LEFT JOIN bnc_spwrs USING (wordid, posid) LEFT JOIN bnc_convtasks USING (wordid, posid) LEFT JOIN bnc_imaginfs USING (wordid, posid) ";
	}
}
