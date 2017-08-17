package org.sqlunet.bnc.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class BncData
{
	public String pos;
	public String posName;

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

	static public List<BncData> makeData(final SQLiteDatabase connection, final String targetWord)
	{
		final List<BncData> result = new ArrayList<>();
		BncQuery query = null;
		try
		{
			query = new BncQuery(connection, targetWord);
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
	static public List<BncData> makeData(final SQLiteDatabase connection, final long targetWordId, final Character targetPos)
	{
		final List<BncData> result = new ArrayList<>();
		BncQuery query = null;
		try
		{
			query = targetPos != null ? new BncQuery(connection, targetWordId, targetPos) : new BncQuery(connection, targetWordId);
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
