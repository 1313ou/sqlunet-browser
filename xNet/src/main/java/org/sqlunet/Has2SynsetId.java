/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet;

/**
 * Has 2 synset-ids interface
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public interface Has2SynsetId extends HasSynsetId
{
	/**
	 * Get synset 2 id
	 *
	 * @return synset 2 id
	 */
	long getSynset2Id();
}
