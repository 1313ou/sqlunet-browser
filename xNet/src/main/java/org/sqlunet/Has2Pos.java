/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet;

import androidx.annotation.Nullable;

/**
 * Has part-of-speech interface
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public interface Has2Pos extends HasPos
{
	/**
	 * Get pos
	 *
	 * @return pos
	 */
	@Nullable
	Character getPos2();
}
