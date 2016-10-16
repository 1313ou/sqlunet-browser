package org.sqlunet.bnc.sql;

import org.sqlunet.sql.DBQueryCommand;

import android.database.sqlite.SQLiteDatabase;

/**
 * BNC query command
 *
 * @author Bernard Bou
 */
class BncQueryCommand extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.BNCQuery;

	/**
	 * <code>theQuery2</code> is the SQL statement
	 */
	private static final String theQuery2 = SqLiteDialect.BNCPosQuery;

	/**
	 * Constructor
	 *
	 * @param thisConnection
	 *            is the database connection
	 * @param theseParams
	 *            parameters
	 */
	public BncQueryCommand(final SQLiteDatabase thisConnection, final Object... theseParams)
	{
		super(thisConnection, theseParams.length > 1 ? BncQueryCommand.theQuery2 : BncQueryCommand.theQuery);
		setParams(theseParams);
	}

	/**
	 * Get the part-of-speech from the result set
	 *
	 * @return the part-of-speech from the result set
	 */
	private String getPos()
	{
		return this.cursor.getString(0);
	}

	/**
	 * Get the frequency from the result set
	 *
	 * @return the frequency from the result set
	 */
	@SuppressWarnings("boxing")
	private Integer getFreq()
	{
		if (this.cursor.isNull(1))
			return null;
		return this.cursor.getInt(1);
	}

	/**
	 * Get the range from the result set
	 *
	 * @return the range from the result set
	 */
	@SuppressWarnings("boxing")
	private Integer getRange()
	{
		if (this.cursor.isNull(2))
			return null;
		return this.cursor.getInt(2);
	}

	/**
	 * Get the disp from the result set
	 *
	 * @return the disp from the result set
	 */
	@SuppressWarnings("boxing")
	private Float getDisp()
	{
		if (this.cursor.isNull(3))
			return null;
		return this.cursor.getFloat(3);
	}

	// conversation / task

	/**
	 * Get the conversation frequency from the result set
	 *
	 * @return the conversation frequency from the result set
	 */
	@SuppressWarnings("boxing")
	private Integer getConvFreq()
	{
		if (this.cursor.isNull(4))
			return null;
		return this.cursor.getInt(4);
	}

	/**
	 * Get the conversation range from the result set
	 *
	 * @return the conversation range from the result set
	 */
	@SuppressWarnings("boxing")
	private Integer getConvRange()
	{
		if (this.cursor.isNull(5))
			return null;
		return this.cursor.getInt(5);
	}

	/**
	 * Get the conversation disp from the result set
	 *
	 * @return the conversation disp from the result set
	 */
	@SuppressWarnings("boxing")
	private Float getConvDisp()
	{
		if (this.cursor.isNull(6))
			return null;
		return this.cursor.getFloat(6);
	}

	/**
	 * Get the task frequency from the result set
	 *
	 * @return the task frequency from the result set
	 */
	@SuppressWarnings("boxing")
	private Integer getTaskFreq()
	{
		if (this.cursor.isNull(7))
			return null;
		return this.cursor.getInt(7);
	}

	/**
	 * Get the task range from the result set
	 *
	 * @return the task range from the result set
	 */
	@SuppressWarnings("boxing")
	private Integer getTaskRange()
	{
		if (this.cursor.isNull(8))
			return null;
		return this.cursor.getInt(8);
	}

	/**
	 * Get the task disp from the result set
	 *
	 * @return the disp from the result set
	 */
	@SuppressWarnings("boxing")
	private Float getTaskDisp()
	{
		if (this.cursor.isNull(9))
			return null;
		return this.cursor.getFloat(9);
	}

	// imagination / information

	/**
	 * Get the imagination frequency from the result set
	 *
	 * @return the imagination frequency from the result set
	 */
	@SuppressWarnings("boxing")
	private Integer getImagFreq()
	{
		if (this.cursor.isNull(10))
			return null;
		return this.cursor.getInt(10);
	}

	/**
	 * Get the imagination range from the result set
	 *
	 * @return the imagination range from the result set
	 */
	@SuppressWarnings("boxing")
	private Integer getImagRange()
	{
		if (this.cursor.isNull(11))
			return null;
		return this.cursor.getInt(11);
	}

	/**
	 * Get the imagination disp from the result set
	 *
	 * @return the imagination disp from the result set
	 */
	@SuppressWarnings("boxing")
	private Float getImagDisp()
	{
		if (this.cursor.isNull(12))
			return null;
		return this.cursor.getFloat(12);
	}

	/**
	 * Get the information frequency from the result set
	 *
	 * @return the information frequency from the result set
	 */
	@SuppressWarnings("boxing")
	private Integer getInfFreq()
	{
		if (this.cursor.isNull(13))
			return null;
		return this.cursor.getInt(13);
	}

	/**
	 * Get the information range from the result set
	 *
	 * @return the information range from the result set
	 */
	@SuppressWarnings("boxing")
	private Integer getInfRange()
	{
		if (this.cursor.isNull(14))
			return null;
		return this.cursor.getInt(14);
	}

	/**
	 * Get the information disp from the result set
	 *
	 * @return the information from the result set
	 */
	@SuppressWarnings("boxing")
	private Float getInfDisp()
	{
		if (this.cursor.isNull(15))
			return null;
		return this.cursor.getFloat(15);
	}

	// spoken / written

	/**
	 * Get the spoken frequency from the result set
	 *
	 * @return the spoken frequency from the result set
	 */
	@SuppressWarnings("boxing")
	private Integer getSpokenFreq()
	{
		if (this.cursor.isNull(16))
			return null;
		return this.cursor.getInt(16);
	}

	/**
	 * Get the spoken range from the result set
	 *
	 * @return the spoken range from the result set
	 */
	@SuppressWarnings("boxing")
	private Integer getSpokenRange()
	{
		if (this.cursor.isNull(17))
			return null;
		return this.cursor.getInt(17);
	}

	/**
	 * Get the spoken disp from the result set
	 *
	 * @return the spoken disp from the result set
	 */
	@SuppressWarnings("boxing")
	private Float getSpokenDisp()
	{
		if (this.cursor.isNull(18))
			return null;
		return this.cursor.getFloat(18);
	}

	/**
	 * Get the written frequency from the result set
	 *
	 * @return the written frequency from the result set
	 */
	@SuppressWarnings("boxing")
	private Integer getWrittenFreq()
	{
		if (this.cursor.isNull(19))
			return null;
		return this.cursor.getInt(19);
	}

	/**
	 * Get the written range from the result set
	 *
	 * @return the written range from the result set
	 */
	@SuppressWarnings("boxing")
	private Integer getWrittenRange()
	{
		if (this.cursor.isNull(20))
			return null;
		return this.cursor.getInt(20);
	}

	/**
	 * Get the written disp from the result set
	 *
	 * @return the written disp from the result set
	 */
	@SuppressWarnings("boxing")
	private Float getWrittenDisp()
	{
		if (this.cursor.isNull(21))
			return null;
		return this.cursor.getFloat(21);
	}

	/**
	 * Get the written disp from the result set
	 *
	 * @return the written from the result set
	 */
	public BncData getData()
	{
		final BncData thisData = new BncData();
		thisData.thePos = getPos();
		thisData.theFreq = getFreq();
		thisData.theRange = getRange();
		thisData.theDisp = getDisp();
		thisData.theConvFreq = getConvFreq();
		thisData.theConvRange = getConvRange();
		thisData.theConvDisp = getConvDisp();
		thisData.theTaskFreq = getTaskFreq();
		thisData.theTaskRange = getTaskRange();
		thisData.theTaskDisp = getTaskDisp();
		thisData.theImagFreq = getImagFreq();
		thisData.theImagRange = getImagRange();
		thisData.theImagDisp = getImagDisp();
		thisData.theInfFreq = getInfFreq();
		thisData.theInfRange = getInfRange();
		thisData.theInfDisp = getInfDisp();
		thisData.theSpokenFreq = getSpokenFreq();
		thisData.theSpokenRange = getSpokenRange();
		thisData.theSpokenDisp = getSpokenDisp();
		thisData.theWrittenFreq = getWrittenFreq();
		thisData.theWrittenRange = getWrittenRange();
		thisData.theWrittenDisp = getWrittenDisp();
		return thisData;
	}
}
