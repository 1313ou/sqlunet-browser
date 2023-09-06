/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.bnc.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * BNC query
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class BncQuery extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.BNCQueryFromWordId;

	/**
	 * <code>QUERYWITHPOS</code> is the SQL statement
	 */
	static private final String QUERYWITHPOS = SqLiteDialect.BNCQueryFromWordIdAndPos;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param params     parameters
	 */
	public BncQuery(final SQLiteDatabase connection, @NonNull final Object... params)
	{
		super(connection, params.length > 1 ? BncQuery.QUERYWITHPOS : BncQuery.QUERY);
		setParams(params);
	}

	/**
	 * Get the part-of-speech from the result set
	 *
	 * @return the part-of-speech from the result set
	 */
	private String getPos()
	{
		assert this.cursor != null;
		return this.cursor.getString(0);
	}

	/**
	 * Get the part-of-speech from the result set
	 *
	 * @return the part-of-speech from the result set
	 */
	private String getPosName()
	{
		assert this.cursor != null;
		return this.cursor.getString(1);
	}

	/**
	 * Get the frequency from the result set
	 *
	 * @return the frequency from the result set
	 */
	@Nullable
	@SuppressWarnings("boxing")
	private Integer getFreq()
	{
		assert this.cursor != null;
		if (this.cursor.isNull(2))
		{
			return null;
		}
		return this.cursor.getInt(2);
	}

	/**
	 * Get the range from the result set
	 *
	 * @return the range from the result set
	 */
	@Nullable
	@SuppressWarnings("boxing")
	private Integer getRange()
	{
		assert this.cursor != null;
		if (this.cursor.isNull(3))
		{
			return null;
		}
		return this.cursor.getInt(3);
	}

	/**
	 * Get the disp from the result set
	 *
	 * @return the disp from the result set
	 */
	@Nullable
	@SuppressWarnings("boxing")
	private Float getDisp()
	{
		assert this.cursor != null;
		if (this.cursor.isNull(4))
		{
			return null;
		}
		return this.cursor.getFloat(4);
	}

	// conversation / task

	/**
	 * Get the conversation frequency from the result set
	 *
	 * @return the conversation frequency from the result set
	 */
	@Nullable
	@SuppressWarnings("boxing")
	private Integer getConvFreq()
	{
		assert this.cursor != null;
		if (this.cursor.isNull(5))
		{
			return null;
		}
		return this.cursor.getInt(5);
	}

	/**
	 * Get the conversation range from the result set
	 *
	 * @return the conversation range from the result set
	 */
	@Nullable
	@SuppressWarnings("boxing")
	private Integer getConvRange()
	{
		assert this.cursor != null;
		if (this.cursor.isNull(6))
		{
			return null;
		}
		return this.cursor.getInt(6);
	}

	/**
	 * Get the conversation disp from the result set
	 *
	 * @return the conversation disp from the result set
	 */
	@Nullable
	@SuppressWarnings("boxing")
	private Float getConvDisp()
	{
		assert this.cursor != null;
		if (this.cursor.isNull(7))
		{
			return null;
		}
		return this.cursor.getFloat(7);
	}

	/**
	 * Get the task frequency from the result set
	 *
	 * @return the task frequency from the result set
	 */
	@Nullable
	@SuppressWarnings("boxing")
	private Integer getTaskFreq()
	{
		assert this.cursor != null;
		if (this.cursor.isNull(8))
		{
			return null;
		}
		return this.cursor.getInt(8);
	}

	/**
	 * Get the task range from the result set
	 *
	 * @return the task range from the result set
	 */
	@Nullable
	@SuppressWarnings("boxing")
	private Integer getTaskRange()
	{
		assert this.cursor != null;
		if (this.cursor.isNull(9))
		{
			return null;
		}
		return this.cursor.getInt(9);
	}

	/**
	 * Get the task disp from the result set
	 *
	 * @return the disp from the result set
	 */
	@Nullable
	@SuppressWarnings("boxing")
	private Float getTaskDisp()
	{
		assert this.cursor != null;
		if (this.cursor.isNull(10))
		{
			return null;
		}
		return this.cursor.getFloat(10);
	}

	// imagination / information

	/**
	 * Get the imagination frequency from the result set
	 *
	 * @return the imagination frequency from the result set
	 */
	@Nullable
	@SuppressWarnings("boxing")
	private Integer getImagFreq()
	{
		assert this.cursor != null;
		if (this.cursor.isNull(11))
		{
			return null;
		}
		return this.cursor.getInt(11);
	}

	/**
	 * Get the imagination range from the result set
	 *
	 * @return the imagination range from the result set
	 */
	@Nullable
	@SuppressWarnings("boxing")
	private Integer getImagRange()
	{
		assert this.cursor != null;
		if (this.cursor.isNull(12))
		{
			return null;
		}
		return this.cursor.getInt(12);
	}

	/**
	 * Get the imagination disp from the result set
	 *
	 * @return the imagination disp from the result set
	 */
	@Nullable
	@SuppressWarnings("boxing")
	private Float getImagDisp()
	{
		assert this.cursor != null;
		if (this.cursor.isNull(13))
		{
			return null;
		}
		return this.cursor.getFloat(13);
	}

	/**
	 * Get the information frequency from the result set
	 *
	 * @return the information frequency from the result set
	 */
	@Nullable
	@SuppressWarnings("boxing")
	private Integer getInfFreq()
	{
		assert this.cursor != null;
		if (this.cursor.isNull(14))
		{
			return null;
		}
		return this.cursor.getInt(14);
	}

	/**
	 * Get the information range from the result set
	 *
	 * @return the information range from the result set
	 */
	@Nullable
	@SuppressWarnings("boxing")
	private Integer getInfRange()
	{
		assert this.cursor != null;
		if (this.cursor.isNull(15))
		{
			return null;
		}
		return this.cursor.getInt(15);
	}

	/**
	 * Get the information disp from the result set
	 *
	 * @return the information from the result set
	 */
	@Nullable
	@SuppressWarnings("boxing")
	private Float getInfDisp()
	{
		assert this.cursor != null;
		if (this.cursor.isNull(16))
		{
			return null;
		}
		return this.cursor.getFloat(16);
	}

	// spoken / written

	/**
	 * Get the spoken frequency from the result set
	 *
	 * @return the spoken frequency from the result set
	 */
	@Nullable
	@SuppressWarnings("boxing")
	private Integer getSpokenFreq()
	{
		assert this.cursor != null;
		if (this.cursor.isNull(17))
		{
			return null;
		}
		return this.cursor.getInt(17);
	}

	/**
	 * Get the spoken range from the result set
	 *
	 * @return the spoken range from the result set
	 */
	@Nullable
	@SuppressWarnings("boxing")
	private Integer getSpokenRange()
	{
		assert this.cursor != null;
		if (this.cursor.isNull(18))
		{
			return null;
		}
		return this.cursor.getInt(18);
	}

	/**
	 * Get the spoken disp from the result set
	 *
	 * @return the spoken disp from the result set
	 */
	@Nullable
	@SuppressWarnings("boxing")
	private Float getSpokenDisp()
	{
		assert this.cursor != null;
		if (this.cursor.isNull(19))
		{
			return null;
		}
		return this.cursor.getFloat(19);
	}

	/**
	 * Get the written frequency from the result set
	 *
	 * @return the written frequency from the result set
	 */
	@Nullable
	@SuppressWarnings("boxing")
	private Integer getWrittenFreq()
	{
		assert this.cursor != null;
		if (this.cursor.isNull(20))
		{
			return null;
		}
		return this.cursor.getInt(20);
	}

	/**
	 * Get the written range from the result set
	 *
	 * @return the written range from the result set
	 */
	@Nullable
	@SuppressWarnings("boxing")
	private Integer getWrittenRange()
	{
		assert this.cursor != null;
		if (this.cursor.isNull(21))
		{
			return null;
		}
		return this.cursor.getInt(21);
	}

	/**
	 * Get the written disp from the result set
	 *
	 * @return the written disp from the result set
	 */
	@Nullable
	@SuppressWarnings("boxing")
	private Float getWrittenDisp()
	{
		assert this.cursor != null;
		if (this.cursor.isNull(22))
		{
			return null;
		}
		return this.cursor.getFloat(22);
	}

	/**
	 * Get the written disp from the result set
	 *
	 * @return the written from the result set
	 */
	@NonNull
	public BncData getData()
	{
		final BncData data = new BncData();
		data.pos = getPos();
		data.posName = getPosName();
		data.freq = getFreq();
		data.range = getRange();
		data.disp = getDisp();
		data.convFreq = getConvFreq();
		data.convRange = getConvRange();
		data.convDisp = getConvDisp();
		data.taskFreq = getTaskFreq();
		data.taskRange = getTaskRange();
		data.taskDisp = getTaskDisp();
		data.imagFreq = getImagFreq();
		data.imagRange = getImagRange();
		data.imagDisp = getImagDisp();
		data.infFreq = getInfFreq();
		data.infRange = getInfRange();
		data.infDisp = getInfDisp();
		data.spokenFreq = getSpokenFreq();
		data.spokenRange = getSpokenRange();
		data.spokenDisp = getSpokenDisp();
		data.writtenFreq = getWrittenFreq();
		data.writtenRange = getWrittenRange();
		data.writtenDisp = getWrittenDisp();
		return data;
	}
}
