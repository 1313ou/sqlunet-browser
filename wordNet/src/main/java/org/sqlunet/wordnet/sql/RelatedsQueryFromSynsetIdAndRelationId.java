/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

import androidx.annotation.Nullable;

/**
 * Query for synsets related through a given relation type id
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class RelatedsQueryFromSynsetIdAndRelationId extends DBQuery
{
	/**
	 * <code>QUERY</code> is the SQL statement
	 */
	static private final String QUERY = SqLiteDialect.RelatedsQueryFromSynsetIdAndRelationId;

	/**
	 * Constructor
	 *
	 * @param connection connection
	 */
	public RelatedsQueryFromSynsetIdAndRelationId(final SQLiteDatabase connection)
	{
		super(connection, RelatedsQueryFromSynsetIdAndRelationId.QUERY);
	}

	/**
	 * Set source synset parameter in prepared statement
	 *
	 * @param synsetId is the source synset id
	 */
	public void setFromSynset(final long synsetId)
	{
		this.statement.setLong(0, synsetId);
		this.statement.setLong(2, synsetId);
	}

	/**
	 * Set source word parameter in prepared statement
	 *
	 * @param wordId is the source word id (for lexical relations) or -1 if word is any in which case the query returns all lexical relations whatever the word
	 */
	public void setFromWord(final long wordId)
	{
		this.statement.setLong(4, wordId);
		this.statement.setLong(5, wordId);
	}

	/**
	 * Set source type parameter in prepared statement
	 *
	 * @param type target synset type
	 */
	public void setRelation(final int type)
	{
		this.statement.setInt(1, type);
		this.statement.setInt(3, type);
	}

	// relationid, synsetid, definition, domainid, sampleset, word2id, word, synset1id, word1id

	/**
	 * Get relation type id
	 *
	 * @return relation type id
	 */
	public int getRelationId()
	{
		assert this.cursor != null;
		return this.cursor.getInt(0);
	}

	/**
	 * Get synset id
	 *
	 * @return synset id
	 */
	public long getSynsetId()
	{
		assert this.cursor != null;
		return this.cursor.getLong(1);
	}

	/**
	 * Get synset definition
	 *
	 * @return definition
	 */
	public String getDefinition()
	{
		assert this.cursor != null;
		return this.cursor.getString(2);
	}

	/**
	 * Get synset domain id
	 *
	 * @return synset domain id
	 */
	public int getDomainId()
	{
		assert this.cursor != null;
		return this.cursor.getInt(3);
	}

	/**
	 * Get sample data
	 *
	 * @return samples in a bar-separated string
	 */
	public String getSamples()
	{
		assert this.cursor != null;
		return this.cursor.getString(4);
	}

	/**
	 * Get target word ids
	 *
	 * @return source synset id
	 */
	@Nullable
	public long[] getWordIds()
	{
		assert this.cursor != null;
		final String resultString = this.cursor.getString(5);
		if (resultString == null)
		{
			return null;
		}
		final String[] resultStrings = resultString.split(",");
		final long[] result = new long[resultStrings.length];
		for (int i = 0; i < result.length; i++)
		{
			result[i] = Long.parseLong(resultStrings[i]);
		}
		return result;
	}

	/**
	 * Get target words
	 *
	 * @return source synset id
	 */
	@Nullable
	public String[] getWords()
	{
		assert this.cursor != null;
		final String results = this.cursor.getString(6);
		if (results == null)
		{
			return null;
		}
		return results.split(",");
	}

	/**
	 * Get source synset id
	 *
	 * @return source synset id
	 */
	public long getFromSynset()
	{
		assert this.cursor != null;
		return this.cursor.getLong(7);
	}

	/**
	 * Get source synset id
	 *
	 * @return source synset id
	 */
	public long getFromWord()
	{
		assert this.cursor != null;
		return this.cursor.getLong(8);
	}
}