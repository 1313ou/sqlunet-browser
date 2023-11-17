/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.history;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;

import org.jetbrains.annotations.NotNull;
import org.sqlunet.browser.common.R;

import androidx.annotation.NonNull;

public class History
{
	/**
	 * Try to start Treebolic activity from source
	 *
	 * @param context context
	 * @param query   source
	 */
	@NotNull
	public static Intent makeSearchIntent(@NonNull final Context context, final String query)
	{
		try
		{
			final String browserClass = context.getString(R.string.activity_browse);
			assert browserClass != null;
			final Intent intent = new Intent(context, Class.forName(browserClass));
			intent.setAction(Intent.ACTION_VIEW);
			intent.putExtra(SearchManager.QUERY, query);
			return intent;
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Record query
	 *
	 * @param context context
	 * @param query   query
	 */
	static public void recordQuery(@NonNull final Context context, final String query)
	{
		final android.provider.SearchRecentSuggestions suggestions = new android.provider.SearchRecentSuggestions(context, org.sqlunet.history.SearchRecentSuggestions.getAuthority(context), SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES);
		suggestions.saveRecentQuery(query, null);
	}
}
