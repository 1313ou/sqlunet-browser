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
	 */
	/**
	 * @param thisSynsetId    is the synset id
	 * @param thisDefinition  is the definition
	 * @param thisLexDomainId is the lexdomainid
	 * @param thisSample      is the sample
	 */
	protected BasicSynset(final long thisSynsetId, final String thisDefinition, final int thisLexDomainId, final String thisSample)
	{
		this.synsetId = thisSynsetId;
		this.definition = thisDefinition;
		this.lexDomainId = thisLexDomainId;
		this.sample = thisSample;
	}
}
