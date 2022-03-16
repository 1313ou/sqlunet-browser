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
	 * <code>word</code> is the string
	 */
	public final String word;

	/**
	 * <code>id</code> is the word id
	 */
	public final long id;

	/**
	 * BasicWord
	 *
	 * @param word is the word
	 * @param id   is the id
	 */
	public BasicWord(final String word, final long id)
	{
		this.word = word;
		this.id = id;
	}
}