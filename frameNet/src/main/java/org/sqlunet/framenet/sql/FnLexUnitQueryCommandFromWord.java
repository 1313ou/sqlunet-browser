package org.sqlunet.framenet.sql;

import org.sqlunet.sql.DBQueryCommand;

import android.database.sqlite.SQLiteDatabase;

/**
 * Query command for FrameNet lex unit
 *
 * @author Bernard Bou
 */
class FnLexUnitQueryCommandFromWord extends DBQueryCommand
{
	/**
	 * <code>theQuery</code> is the SQL statement
	 */
	private static final String theQuery = SqLiteDialect.FrameNetLexUnitQueryFromWord;

	/**
	 * Constructor
	 *
	 * @param thisConnection
	 *        is the database connection
	 * @param thisLemma
	 *        is the target word
	 */
	public FnLexUnitQueryCommandFromWord(final SQLiteDatabase thisConnection, final String thisLemma)
	{
		super(thisConnection, FnLexUnitQueryCommandFromWord.theQuery);
		setParams(thisLemma);
	}

	/**
	 * Get the word id from the result set
	 *
	 * @return the word id from the result set
	 */
	public long getWordId()
	{
		return this.cursor.getLong(0);
	}

	/**
	 * Get the lexical unit id from the result set
	 *
	 * @return the lexical unit id from the result set
	 */
	public long getLuId()
	{
		return this.cursor.getLong(1);
	}

	/**
	 * Get the lex unit from the result set
	 *
	 * @return the lex unit from the result set
	 */
	public String getLexUnit()
	{
		return this.cursor.getString(2);
	}

	/**
	 * Get the pos from the result set
	 *
	 * @return the pos from the result set
	 */
	public String getPos()
	{
		return this.cursor.getString(3);
	}

	/**
	 * Get the lex unit definition from the result set
	 *
	 * @return the lex unit definition from the result set
	 */
	public String getLexUnitDefinition()
	{
		return this.cursor.getString(4);
	}

	/**
	 * Get the lex unit dictionary from the result set
	 *
	 * @return the lex unit dictionary from the result set
	 */
	public String getLexUnitDictionary()
	{
		return this.cursor.getString(5);
	}

	/**
	 * Get the incorporated fe from the result set
	 *
	 * @return the incorporated fe from the result set
	 */
	public String getIncoporatedFe()
	{
		return this.cursor.getString(6);
	}

	/**
	 * Get the frame id from the result set
	 *
	 * @return the frame id from the result set
	 */
	public long getFrameId()
	{
		return this.cursor.getLong(7);
	}

	/**
	 * Get the frame
	 * 
	 * @return the frame
	 */
	public String getFrame()
	{
		return this.cursor.getString(8);
	}

	/**
	 * Get the frame definition
	 *
	 * @return the frame definition
	 */
	public String getFrameDefinition()
	{
		return this.cursor.getString(9);
	}
}
