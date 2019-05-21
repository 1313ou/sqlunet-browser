/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

/**
 * Search interface for fragments
 */

@FunctionalInterface
interface SearchListener
{
	void search(final String query);
}
