/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * Query for synsets of a given part-of-speech or domain type and containing a given word
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
	 * <code>QUERYWITHDOMAIN</code> is the (domain based) SQL statement
	 */
	static private String QUERYWITHDOMAIN;

	/**
	 * Init data (resources, ...)
	 */
	static void init()
	{
		SynsetsQueryFromWordIdAndCondition.QUERYWITHDOMAIN = SqLiteDialect.SynsetsQueryFromWordIdAndDomainId;
		SynsetsQueryFromWordIdAndCondition.QUERYWITHPOS = SqLiteDialect.SynsetsQueryFromWordIdAndPos;
	}

	/**
	 * Constructor
	 *
	 * @param connection     connection
	 * @param domainBased is whether the query is domain based
	 */
	public SynsetsQueryFromWordIdAndCondition(final SQLiteDatabase connection, final boolean domainBased)
	{
		super(connection, domainBased ? SynsetsQueryFromWordIdAndCondition.QUERYWITHDOMAIN : SynsetsQueryFromWordIdAndCondition.QUERYWITHPOS);
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
	 * Set domain type parameter in prepared SQL statement
	 *
	 * @param type target domain type
	 */
	public void setDomainType(final int type)
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
		assert this.cursor != null;
		return this.cursor.getLong(0);
	}

	/**
	 * Get synset definition
	 *
	 * @return definition
	 */
	public String getDefinition()
	{
		assert this.cursor != null;
		return this.cursor.getString(1);
	}

	/**
	 * Get synset domain id
	 *
	 * @return synset domain id
	 */
	public int getDomainId()
	{
		assert this.cursor != null;
		return this.cursor.getInt(2);
	}

	/**
	 * Get sample data
	 *
	 * @return samples in a semicolon-separated string
	 */
	public String getSample()
	{
		assert this.cursor != null;
		return this.cursor.getString(3);
	}
}
