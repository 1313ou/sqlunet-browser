package org.sqlunet.bnc.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class BncData
{
	public String pos;

	public Integer freq;
	public Integer range;
	public Float disp;

	public Integer convFreq;
	public Integer convRange;
	public Float convDisp;

	public Integer taskFreq;
	public Integer taskRange;
	public Float taskDisp;

	public Integer imagFreq;
	public Integer imagRange;
	public Float imagDisp;

	public Integer infFreq;
	public Integer infRange;
	public Float infDisp;

	public Integer spokenFreq;
	public Integer spokenRange;
	public Float spokenDisp;

	public Integer writtenFreq;
	public Integer writtenRange;
	public Float writtenDisp;

	public static List<BncData> makeData(final SQLiteDatabase connection, final String targetWord)
	{
		final List<BncData> result = new ArrayList<>();
		BncQueryCommand query = null;
		try
		{
			query = new BncQueryCommand(connection, targetWord);
			query.execute();

			while (query.next())
			{
				final BncData data = query.getData();
				result.add(data);
			}
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return result;
	}

	@SuppressWarnings("boxing")
	public static List<BncData> makeData(final SQLiteDatabase connection, final long targetWordId, final Character targetPos)
	{
		final List<BncData> result = new ArrayList<>();
		BncQueryCommand query = null;
		try
		{
			query = new BncQueryCommand(connection, targetWordId, targetPos);
			query.execute();

			while (query.next())
			{
				final BncData data = query.getData();
				result.add(data);
			}
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return result;
	}
}
