/*
 * Copyright (c) 2023. Bernard Bou
 */

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
	 * <code>domainId</code> is the synset's domain
	 */
	public final int domainId;

	/**
	 * <code>sample</code> is a string concatenating the synset's samples
	 */
	public final String sample;

	/**
	 * Constructor from data
	 *
	 * @param synsetId    is the synset id
	 * @param definition  is the definition
	 * @param domainId is the domain id
	 * @param sample      is the sample
	 */
	protected BasicSynset(final long synsetId, final String definition, final int domainId, final String sample)
	{
		this.synsetId = synsetId;
		this.definition = definition;
		this.domainId = domainId;
		this.sample = sample;
	}
}
