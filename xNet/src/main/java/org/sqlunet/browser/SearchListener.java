/*
 * Copyright (c) 2022. Bernard Bou
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
