/*
 * Copyright (c) 2020. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.syntagnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.sql.DBQuery;

/**
 * SyntagNet collocation query
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract class BaseCollocationQuery extends DBQuery
{
	/**
	 * Constructor
	 *
	 * @param connection connection
	 * @param query      query
	 */
	@SuppressWarnings("boxing")
	public BaseCollocationQuery(final SQLiteDatabase connection, final String query)
	{
		super(connection, query);
	}

	// 0         1                   2          3               4
	// "word1id, w1.lemma AS lemma1, synset1id, s1.pos AS pos1, s1.definition AS definition1,"
	// 5         6                   7          8               9
	// "word2id, w2.lemma AS lemma2, synset2id, s2.pos AS pos2, s2.definition AS definition2,"

	/**
	 * Get the word 1 id from the result set
	 *
	 * @return the word 1 id from the result set
	 */
	public long getWord1Id()
	{
		assert this.cursor != null;
		return this.cursor.getLong(0);
	}

	/**
	 * Get the word 2 id from the result set
	 *
	 * @return the word 2 id from the result set
	 */
	public long getWord2Id()
	{
		assert this.cursor != null;
		return this.cursor.getLong(5);
	}

	/**
	 * Get the synset 1  id from the result set
	 *
	 * @return the synset 1 id from the result set
	 */
	public long getSynset1Id()
	{
		assert this.cursor != null;
		return this.cursor.getLong(2);
	}

	/**
	 * Get the synset 2 id from the result set
	 *
	 * @return the synset 2 id from the result set
	 */
	public long getSynset2Id()
	{
		assert this.cursor != null;
		return this.cursor.getLong(7);
	}

	/**
	 * Get the word 1 from the result set
	 *
	 * @return the word 1 from the result set
	 */
	public String getWord1()
	{
		assert this.cursor != null;
		return this.cursor.getString(1);
	}

	/**
	 * Get word 2 from the result set
	 *
	 * @return word 2 from the result set
	 */
	public String getWord2()
	{
		assert this.cursor != null;
		return this.cursor.getString(6);
	}

	/**
	 * Get the pos 1 from the result set
	 *
	 * @return the pos 1 from the result set
	 */
	public String getPos1()
	{
		assert this.cursor != null;
		return this.cursor.getString(3);
	}

	/**
	 * Get pos 2 from the result set
	 *
	 * @return pos 2 from the result set
	 */
	public String getPos2()
	{
		assert this.cursor != null;
		return this.cursor.getString(8);
	}

	/**
	 * Get the definition 1 from the result set
	 *
	 * @return the definition 1 from the result set
	 */
	public String getDefinition1()
	{
		assert this.cursor != null;
		return this.cursor.getString(4);
	}

	/**
	 * Get definition 2 from the result set
	 *
	 * @return definition 2 from the result set
	 */
	public String getDefinition2()
	{
		assert this.cursor != null;
		return this.cursor.getString(9);
	}
}
