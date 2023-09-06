/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.bnc.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

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

	@Nullable
	static public List<BncData> makeData(final SQLiteDatabase connection, final String targetWord)
	{
		List<BncData> result = null;
		try (BncQuery query = new BncQuery(connection, targetWord))
		{
			query.execute();

			while (query.next())
			{
				final BncData data = query.getData();
				if (result == null)
				{
					result = new ArrayList<>();
				}
				result.add(data);
			}
		}
		return result;
	}

	@Nullable
	static public List<BncData> makeData(final SQLiteDatabase connection, final long targetWordId, @Nullable final Character targetPos)
	{
		List<BncData> result = null;
		try(BncQuery query = targetPos != null ? new BncQuery(connection, targetWordId, targetPos) : new BncQuery(connection, targetWordId))
		{
			query.execute();

			while (query.next())
			{
				final BncData data = query.getData();
				if (result == null)
				{
					result = new ArrayList<>();
				}
				result.add(data);
			}
		}
		return result;
	}
}
