/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.web;

import androidx.annotation.Nullable;

/**
 * Document string loader
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@FunctionalInterface
interface DocumentStringLoader
{
	/**
	 * Get document
	 *
	 * @return document
	 */
	@Nullable
	String getDoc();
}
