/*
 * Copyright (c) 2019-2023. Bernard Bou
 */

package org.sqlunet.history;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import org.sqlunet.browser.common.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.CursorLoader;
import androidx.preference.PreferenceManager;

/**
 * Search recent suggestion. Access to suggestions provider, after standard SearchRecentSuggestions which has private members and can hardly be extended.
 *
 * @author Bernard Bou
 */
@SuppressWarnings("WeakerAccess")
public class SearchRecentSuggestions
{
	static private final String TAG = "SearchRecentSuggestions";

	// a superset of all possible column names (need not all be in table)
	public static class SuggestionColumns implements BaseColumns
	{
		public static final String DISPLAY1 = "display1";
		// public static final String DISPLAY2 = "display2";
		public static final String QUERY = "query";
		public static final String DATE = "date";
	}

	// client-provided configuration values
	private final Context mContext;
	private final Uri mSuggestionsUri;

	/**
	 * Constructor
	 *
	 * @param context context
	 * @param mode    item_mode
	 */
	public SearchRecentSuggestions(final Context context, @SuppressWarnings("SameParameterValue") final int mode)
	{
		if ((mode & SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES) == 0)
		{
			throw new IllegalArgumentException();
		}

		// saved values
		this.mContext = context;
		String mAuthority = getAuthority();

		// derived values
		this.mSuggestionsUri = Uri.parse("content://" + mAuthority + "/suggestions");
	}

	// Q U E R Y

	/**
	 * Get cursor
	 *
	 * @return cursor
	 */
	@Nullable
	public Cursor cursor()
	{
		final ContentResolver contentResolver = this.mContext.getContentResolver();
		try
		{
			final String[] projection = {/*"DISTINCT " +*/ SuggestionColumns.DISPLAY1, "_id"};
			final String sortOrder = getSortOrder();
			// final String selection = getSelection();
			// final String[] selectionArgs = null; // {};
			return contentResolver.query(this.mSuggestionsUri, projection, null, null, sortOrder);
		}
		catch (@NonNull final RuntimeException e)
		{
			Log.e(SearchRecentSuggestions.TAG, "While getting cursor", e);
		}
		return null;
	}

	/**
	 * Get cursor loader
	 *
	 * @return cursor loader
	 */
	@NonNull
	public CursorLoader cursorLoader()
	{
		final String[] projection = {/* "DISTINCT " +*/ SuggestionColumns.DISPLAY1, "_id"};
		final String sortOrder = getSortOrder();
		// final String selection = getSelection();
		// final String[] selectionArgs = null; // {};
		return new CursorLoader(this.mContext, this.mSuggestionsUri, projection, null, null, sortOrder);
	}

	// S O R T

	private static final String PREF_KEY_HISTORY_SORT_BY_DATE = "pref_history_sort_by_date";

	private String getSortOrder()
	{
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		final boolean byDate = prefs.getBoolean(PREF_KEY_HISTORY_SORT_BY_DATE, true);
		return byDate ? SuggestionColumns.DATE + " DESC" : SuggestionColumns.DISPLAY1 + " ASC";
	}

	// D E L E T E

	/**
	 * Delete item by _id
	 */
	public void delete(final String id)
	{
		final String selection = "_id = ?";
		final String[] selectArgs = new String[]{id};
		final ContentResolver cr = this.mContext.getContentResolver();
		try
		{
			cr.delete(this.mSuggestionsUri, selection, selectArgs);
		}
		catch (@NonNull final RuntimeException e)
		{
			Log.e(SearchRecentSuggestions.TAG, "While deleting suggestion", e);
		}
	}

	// A U T H O R I T Y

	@NonNull
	private String getAuthority()
	{
		return getAuthority(this.mContext);
	}

	@NonNull
	static public String getAuthority(@NonNull final Context context)
	{
		return context.getString(R.string.history_authority);
	}
}
