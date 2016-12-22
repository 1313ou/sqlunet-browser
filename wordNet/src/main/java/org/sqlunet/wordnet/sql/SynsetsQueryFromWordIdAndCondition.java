package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * Query for synsets of a given part-of-speech or lexdomain type and containing a given word
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class SynsetsQueryFromWordIdAndCondition extends DBQuery
{
	/**
	 * <code>QUERYWITHPOS</code> is the (part-of-speech based) SQL statement
	 */
	static private String QUERYWITHPOS;

	/**
	 * <code>QUERYWITHLEXDOMAIN</code> is the (lexdomain based) SQL statement
	 */
	static private String QUERYWITHLEXDOMAIN;

	/**
	 * Init data (resources, ...)
	 */
	@SuppressWarnings("unused")
	static void init()
	{
		SynsetsQueryFromWordIdAndCondition.QUERYWITHLEXDOMAIN = SqLiteDialect.SynsetsQueryFromWordIdAndLexDomainId;
		SynsetsQueryFromWordIdAndCondition.QUERYWITHPOS = SqLiteDialect.SynsetsQueryFromWordIdAndPos;
	}

	/**
	 * Constructor
	 *
	 * @param connection     connection
	 * @param lexDomainBased is whether the query is lexdomain based
	 */
	public SynsetsQueryFromWordIdAndCondition(final SQLiteDatabase connection, final boolean lexDomainBased)
	{
		super(connection, lexDomainBased ? SynsetsQueryFromWordIdAndCondition.QUERYWITHLEXDOMAIN : SynsetsQueryFromWordIdAndCondition.QUERYWITHPOS);
	}

	/**
	 * Set word parameter in prepared SQL statement
	 *
	 * @param wordId target word
	 */
	public void setWordId(final long wordId)
	{
		this.statement.setLong(0, wordId);
	}

	/**
	 * Set part-of-speech type parameter in prepared SQL statement
	 *
	 * @param type target part-of-speech type
	 */
	public void setPosType(final int type)
	{
		final String pos = Character.valueOf((char) type).toString();
		this.statement.setString(1, pos);
	}

	/**
	 * Set lexdomain type parameter in prepared SQL statement
	 *
	 * @param type target lexdomain type
	 */
	public void setLexDomainType(final int type)
	{
		this.statement.setInt(1, type);
	}

	/**
	 * Get synset id
	 *
	 * @return synset id
	 */
	public long getSynsetId()
	{
		return this.cursor.getLong(0);
	}

	/**
	 * Get synset definition
	 *
	 * @return definition
	 */
	public String getDefinition()
	{
		return this.cursor.getString(1);
	}

	/**
	 * Get synset lexdomain id
	 *
	 * @return synset lexdomain id
	 */
	public int getLexDomainId()
	{
		return this.cursor.getInt(2);
	}

	/**
	 * Get sample data
	 *
	 * @return samples in a semicolon-separated string
	 */
	public String getSample()
	{
		return this.cursor.getString(3);
	}
}
