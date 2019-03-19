package org.sqlunet.bnc.sql;

import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BncData
{
	public String pos;
	public String posName;

	@Nullable
	public Integer freq;
	@Nullable
	public Integer range;
	@Nullable
	public Float disp;

	@Nullable
	public Integer convFreq;
	@Nullable
	public Integer convRange;
	@Nullable
	public Float convDisp;

	@Nullable
	public Integer taskFreq;
	@Nullable
	public Integer taskRange;
	@Nullable
	public Float taskDisp;

	@Nullable
	public Integer imagFreq;
	@Nullable
	public Integer imagRange;
	@Nullable
	public Float imagDisp;

	@Nullable
	public Integer infFreq;
	@Nullable
	public Integer infRange;
	@Nullable
	public Float infDisp;

	@Nullable
	public Integer spokenFreq;
	@Nullable
	public Integer spokenRange;
	@Nullable
	public Float spokenDisp;

	@Nullable
	public Integer writtenFreq;
	@Nullable
	public Integer writtenRange;
	@Nullable
	public Float writtenDisp;

	@NonNull
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

	@NonNull
	@SuppressWarnings("boxing")
	static public List<BncData> makeData(final SQLiteDatabase connection, final long targetWordId, @Nullable final Character targetPos)
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
