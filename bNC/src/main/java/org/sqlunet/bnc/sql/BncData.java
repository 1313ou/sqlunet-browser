package org.sqlunet.bnc.sql;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

public class BncData
{
	public String thePos;

	public Integer theFreq;
	public Integer theRange;
	public Float theDisp;

	public Integer theConvFreq;
	public Integer theConvRange;
	public Float theConvDisp;

	public Integer theTaskFreq;
	public Integer theTaskRange;
	public Float theTaskDisp;

	public Integer theImagFreq;
	public Integer theImagRange;
	public Float theImagDisp;

	public Integer theInfFreq;
	public Integer theInfRange;
	public Float theInfDisp;

	public Integer theSpokenFreq;
	public Integer theSpokenRange;
	public Float theSpokenDisp;

	public Integer theWrittenFreq;
	public Integer theWrittenRange;
	public Float theWrittenDisp;

	public static List<BncData> makeData(final SQLiteDatabase thisConnection, final String thisTargetWord)
	{
		final List<BncData> thisResult = new ArrayList<>();
		BncQueryCommand thisQuery = null;
		try
		{
			thisQuery = new BncQueryCommand(thisConnection, thisTargetWord);
			thisQuery.execute();

			while (thisQuery.next())
			{
				final BncData thisData = thisQuery.getData();
				thisResult.add(thisData);
			}
		}
		finally
		{
			if (thisQuery != null)
			{
				thisQuery.release();
			}
		}
		return thisResult;
	}

	@SuppressWarnings("boxing")
	public static List<BncData> makeData(final SQLiteDatabase thisConnection, final long thisTargetWordId, final Character thisTargetPos)
	{
		final List<BncData> thisResult = new ArrayList<>();
		BncQueryCommand thisQuery = null;
		try
		{
			thisQuery = new BncQueryCommand(thisConnection, thisTargetWordId, thisTargetPos);
			thisQuery.execute();

			while (thisQuery.next())
			{
				final BncData thisData = thisQuery.getData();
				thisResult.add(thisData);
			}
		}
		finally
		{
			if (thisQuery != null)
			{
				thisQuery.release();
			}
		}
		return thisResult;
	}
}
