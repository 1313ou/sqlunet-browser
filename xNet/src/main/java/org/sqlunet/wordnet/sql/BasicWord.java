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
	 * @param thisLemma is the word string
	 * @param thisId    is the id
	 */
	public BasicWord(final String thisLemma, final long thisId)
	{
		this.lemma = thisLemma;
		this.id = thisId;
	}
}