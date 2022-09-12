/*
 * Copyright (c) 2022. Bernard Bou
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
