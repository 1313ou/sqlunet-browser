/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet;

/**
 * Has 2 word-ids interface
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public interface Has2WordId extends HasWordId
{
	/**
	 * Get word 2 id
	 *
	 * @return word 2 id
	 */
	long getWord2Id();
}
