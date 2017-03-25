package org.sqlunet.wordnet.sql;

/**
 * BasicSynset
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class BasicSynset
{
	/**
	 * <code>synsetId</code> is the synset's id in the database
	 */
	public final long synsetId;

	/**
	 * <code>definition</code> is the synset's definition
	 */
	public final String definition;

	/**
	 * <code>lexDomainId</code> is the synset's lexdomain
	 */
	public final int lexDomainId;

	/**
	 * <code>sample</code> is a string concatenating the synset's samples
	 */
	public final String sample;

	/**
	 * Constructor from data
	 *
	 * @param synsetId    is the synset id
	 * @param definition  is the definition
	 * @param lexDomainId is the lexdomain id
	 * @param sample      is the sample
	 */
	protected BasicSynset(final long synsetId, final String definition, final int lexDomainId, final String sample)
	{
		this.synsetId = synsetId;
		this.definition = definition;
		this.lexDomainId = lexDomainId;
		this.sample = sample;
	}
}
