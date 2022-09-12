/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet;

/**
 * Has synset-id interface
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@FunctionalInterface
public interface HasSynsetId
{
	/**
	 * Get synset id
	 *
	 * @return synset id
	 */
	long getSynsetId();
}
