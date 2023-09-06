/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet;

/**
 * Has word-id interface
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@FunctionalInterface
public interface HasWordId
{
	/**
	 * Get word id
	 *
	 * @return word id
	 */
	long getWordId();
}
