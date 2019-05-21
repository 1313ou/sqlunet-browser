/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet;

/**
 * Has sensekey interface
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@FunctionalInterface
public interface HasSenseKey
{
	/**
	 * Get sensekey
	 *
	 * @return sensekey
	 */
	String getSenseKey();
}
