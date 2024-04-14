/*
 * Copyright (c) 2024. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.history

import android.content.Context
import android.content.SearchRecentSuggestionsProvider
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns
import android.util.Log
import androidx.loader.content.CursorLoader
import androidx.preference.PreferenceManager
import org.sqlunet.browser.common.R

/**
 * Search recent suggestion. Access to suggestions provider, after standard SearchRecentSuggestions which has private members and can hardly be extended.
 *
 * @param context context
 * @param mode    item_mode
 *
 * @author Bernard Bou
 */
class SearchRecentSuggestions(context: Context, mode: Int) {

    // a superset of all possible column names (need not all be in table)
    object SuggestionColumns : BaseColumns {

        const val DISPLAY1 = "display1"

        // const val DISPLAY2 = "display2"
        // const val QUERY = "query"
        const val DATE = "date"
    }

    // client-provided configuration values
    private val mContext: Context
    private val mSuggestionsUri: Uri

    // Q U E R Y

    /**
     * Get cursor
     *
     * @return cursor
     */
    fun cursor(): Cursor? {
        val contentResolver = mContext.contentResolver
        try {
            val projection = arrayOf( /*"DISTINCT " +*/SuggestionColumns.DISPLAY1, "_id")
            val sortOrder = sortOrder
            // val selection = getSelection()
            // val selectionArgs : Array<String>? = null
            return contentResolver.query(mSuggestionsUri, projection, null, null, sortOrder)
        } catch (e: RuntimeException) {
            Log.e(TAG, "While getting cursor", e)
        }
        return null
    }

    /**
     * Get cursor loader
     *
     * @return cursor loader
     */
    fun cursorLoader(): CursorLoader {
        val projection = arrayOf( /* "DISTINCT " +*/SuggestionColumns.DISPLAY1, "_id")
        val sortOrder = sortOrder
        // val selection = getSelection()
        // val selectionArgs : Array<String>? = null
        return CursorLoader(mContext, mSuggestionsUri, projection, null, null, sortOrder)
    }

    init {
        require(mode and SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES != 0)

        // saved values
        mContext = context
        val mAuthority = authority

        // derived values
        mSuggestionsUri = Uri.parse("content://$mAuthority/suggestions")
    }

    private val sortOrder: String
        get() {
            val prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
            val byDate = prefs.getBoolean(PREF_KEY_HISTORY_SORT_BY_DATE, true)
            return if (byDate) SuggestionColumns.DATE + " DESC" else SuggestionColumns.DISPLAY1 + " ASC"
        }

    // D E L E T E

    /**
     * Delete item by _id
     */
    fun delete(id: String) {
        val selection = "_id = ?"
        val selectArgs = arrayOf(id)
        val cr = mContext.contentResolver
        try {
            cr.delete(mSuggestionsUri, selection, selectArgs)
        } catch (e: RuntimeException) {
            Log.e(TAG, "While deleting suggestion", e)
        }
    }

    // A U T H O R I T Y

    private val authority: String
        get() = getAuthority(mContext)

    companion object {

        private const val TAG = "SearchRecentSuggestions"

        fun getAuthority(context: Context): String {
            return context.getString(R.string.history_authority)
        }

        // S O R T

        private const val PREF_KEY_HISTORY_SORT_BY_DATE = "pref_history_sort_by_date"
    }
}
