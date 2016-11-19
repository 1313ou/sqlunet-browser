package org.sqlunet.browser;

/**
 * Search interface for fragments
 */

public interface SearchListener
{
	void suggest(final String query);

	void search(final String query);
}
