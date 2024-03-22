/*
 * Copyright (c) 2024. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.history;

import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;

/**
 * Recent suggestion provider
 *
 * @author Bernard Bou
 */
public class SearchSuggestionsProvider extends SearchRecentSuggestionsProvider
{
	/**
	 * Mode
	 */
	private final static int MODE = SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES;

	/**
	 * Constructor
	 */
	@SuppressWarnings("EmptyMethod")
	public SearchSuggestionsProvider()
	{
	}

	@Override
	public boolean onCreate()
	{
		final Context context = getContext();
		assert context != null;
		String authority = SearchRecentSuggestions.getAuthority(context);
		setupSuggestions(authority, MODE);
		return super.onCreate();
	}
}
