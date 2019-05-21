/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet;

import androidx.annotation.NonNull;

/**
 * Has part-of-speech interface
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@FunctionalInterface
public interface HasPos
{
	/**
	 * Get pos
	 *
	 * @return pos
	 */
	@NonNull
	Character getPos();
}
