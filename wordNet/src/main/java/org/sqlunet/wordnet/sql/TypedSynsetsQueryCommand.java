/*
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
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
 * @author <a href="mailto:bbou@ac-toulouse.fr">Bernard Bou</a>
 */
class TypedSynsetsQueryCommand extends DBQueryCommand
{
	/**
	 * <code>thePosQuery</code> is the (part-of-speech based) SQL statement
	 */
	private static String thePosQuery = null;

	/**
	 * <code>theLexDomainQuery</code> is the (lexdomain based) SQL statement
	 */
	private static String theLexDomainQuery = null;

	/**
	 * Init data (resources, ...)
	 */
	@SuppressWarnings("unused")
	static void init()
	{
		TypedSynsetsQueryCommand.theLexDomainQuery = SqLiteDialect.LexDomainTypedSynsetsQuery;
		TypedSynsetsQueryCommand.thePosQuery = SqLiteDialect.PosTypedSynsetsQuery;
	}

	/**
	 * Constructor
	 *
	 * @param thisConnection is the database connection
	 * @param lexDomainBased is whether the query is lexdomain based
	 */
	public TypedSynsetsQueryCommand(final SQLiteDatabase thisConnection, final boolean lexDomainBased)
	{
		super(thisConnection, lexDomainBased ?
				TypedSynsetsQueryCommand.theLexDomainQuery :
				TypedSynsetsQueryCommand.thePosQuery);
	}

	/**
	 * Set word parameter in prepared SQL statement
	 *
	 * @param thisWordId is the target word
	 */
	public void setWordId(final long thisWordId)
	{
		this.statement.setLong(0, thisWordId);
	}

	/**
	 * Set part-of-speech type parameter in prepared SQL statement
	 *
	 * @param thisType is the target part-of-speech type
	 */
	public void setPosType(final int thisType)
	{
		final String thisString = Character.valueOf((char) thisType).toString();
		this.statement.setString(1, thisString);
	}

	/**
	 * Set lexdomain type parameter in prepared SQL statement
	 *
	 * @param thisType is the target lexdomain type
	 */
	public void setLexDomainType(final int thisType)
	{
		this.statement.setInt(1, thisType);
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
