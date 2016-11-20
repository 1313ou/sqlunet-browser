package org.sqlunet.browser;

/**
 * Search interface for fragments
 */

interface SearchListener
{
	void suggest(final String query);

	void search(final String query);
}
