/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.sql;

/**
 * BasicWord
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class BasicWord
{
	/**
	 * <code>lemma</code> is the string
	 */
	public final String lemma;

	/**
	 * <code>id</code> is the word id
	 */
	public final long id;

	/**
	 * BasicWord
	 *
	 * @param lemma is the lemma
	 * @param id    is the id
	 */
	public BasicWord(final String lemma, final long id)
	{
		this.lemma = lemma;
		this.id = id;
	}
}