package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import org.sqlunet.sql.DBQuery;

/**
 * FrameNet lex unit query
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnLexUnitQueryFromWordId extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.FrameNetLexUnitQueryFromWordId;

	/**
	 * <code>QUERYWITHPOS</code> is the SQL statement with Pos input
	 */
	static private final String QUERYWITHPOS = SqLiteDialect.FrameNetLexUnitQueryFromWordIdAndPos;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param wordId     target word id
	 * @param pos        target pos or null
	 */
	@SuppressWarnings("boxing")
	public FnLexUnitQueryFromWordId(final SQLiteDatabase connection, final long wordId, @Nullable final Character pos)
	{
		super(connection, pos != null ? FnLexUnitQueryFromWordId.QUERYWITHPOS : FnLexUnitQueryFromWordId.QUERY);
		setParams(wordId, FnLexUnitQueryFromWordId.mapPos(pos));
	}

	/**
	 * Get the lex unit id from the result set
	 *
	 * @return the lex unit id from the result set
	 */
	public long getLuId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(0);
	}

	/**
	 * Get the lex unit from the result set
	 *
	 * @return the lex unit from the result set
	 */
	public String getLexUnit()
	{
		assert this.cursor != null;
		return this.cursor.getString(1);
	}

	/**
	 * Get the pos from the result set
	 *
	 * @return the pos from the result set
	 */
	public String getPos()
	{
		assert this.cursor != null;
		return this.cursor.getString(2);
	}

	/**
	 * Get the definition from the result set
	 *
	 * @return the definition from the result set
	 */
	public String getDefinition()
	{
		assert this.cursor != null;
		return this.cursor.getString(3);
	}

	/**
	 * Get the definition dictionary from the result set
	 *
	 * @return the definition dictionary from the result set
	 */
	public String getDictionary()
	{
		assert this.cursor != null;
		return this.cursor.getString(4);
	}

	/**
	 * Get the incorporated FE from the result set
	 *
	 * @return the incorporated FE from the result set or null if none
	 */
	public String getIncorporatedFe()
	{
		assert this.cursor != null;
		return this.cursor.getString(5);
	}

	/**
	 * Get the frame id from the result set
	 *
	 * @return the frame id from the result set
	 */
	public long getFrameId()
	{
		assert this.cursor != null;
		return this.cursor.getInt(6);
	}

	/**
	 * Get the frame from the result set
	 *
	 * @return the frame from the result set or null if none
	 */
	public String getFrame()
	{
		assert this.cursor != null;
		return this.cursor.getString(7);
	}

	/**
	 * Get the frame description from the result set
	 *
	 * @return the frame description from the result set or null if none
	 */
	public String getFrameDescription()
	{
		assert this.cursor != null;
		return this.cursor.getString(8);
	}

	/**
	 * Map character to pos
	 *
	 * @param pos pos character
	 * @return pos code
	 */
	@Nullable
	@SuppressWarnings("boxing")
	static private Integer mapPos(@Nullable final Character pos)
	{
		if (pos == null)
		{
			return null;
		}
		switch (pos)
		{
			case 'n':
				return 1;
			case 'v':
				return 2;
			case 'a':
				return 3;
			case 'r':
				return 4;
		}
		return null;
	}
}
