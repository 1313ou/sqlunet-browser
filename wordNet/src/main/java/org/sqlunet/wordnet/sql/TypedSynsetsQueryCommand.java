/*
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 * Created on 31 dec. 2004
 * Filename : TypedSynsetsQueryCommand.java
 * Class encapsulating query for synsets of a given part-of-speech or lexdomain type and containing a given word
 */
package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQueryCommand;

/**
 * Query command for synsets of a given part-of-speech or lexdomain type and containing a given word
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class TypedSynsetsQueryCommand extends DBQueryCommand
{
	/**
	 * <code>posQuery</code> is the (part-of-speech based) SQL statement
	 */
	private static String posQuery;

	/**
	 * <code>lexDomainQuery</code> is the (lexdomain based) SQL statement
	 */
	private static String lexDomainQuery;

	/**
	 * Init data (resources, ...)
	 */
	@SuppressWarnings("unused")
	static void init()
	{
		TypedSynsetsQueryCommand.lexDomainQuery = SqLiteDialect.LexDomainTypedSynsetsQuery;
		TypedSynsetsQueryCommand.posQuery = SqLiteDialect.PosTypedSynsetsQuery;
	}

	/**
	 * Constructor
	 *
	 * @param connection     is the database connection
	 * @param lexDomainBased is whether the query is lexdomain based
	 */
	public TypedSynsetsQueryCommand(final SQLiteDatabase connection, final boolean lexDomainBased)
	{
		super(connection, lexDomainBased ? TypedSynsetsQueryCommand.lexDomainQuery : TypedSynsetsQueryCommand.posQuery);
	}

	/**
	 * Set word parameter in prepared SQL statement
	 *
	 * @param wordId is the target word
	 */
	public void setWordId(final long wordId)
	{
		this.statement.setLong(0, wordId);
	}

	/**
	 * Set part-of-speech type parameter in prepared SQL statement
	 *
	 * @param type is the target part-of-speech type
	 */
	public void setPosType(final int type)
	{
		final String pos = Character.valueOf((char) type).toString();
		this.statement.setString(1, pos);
	}

	/**
	 * Set lexdomain type parameter in prepared SQL statement
	 *
	 * @param type is the target lexdomain type
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
