/*
 * Copyright (c) 2024. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.history

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SearchRecentSuggestionsProvider
import org.sqlunet.browser.common.R

object History {

    /**
     * Try to start Treebolic activity from source
     *
     * @param context context
     * @param query   source
     */
    fun makeSearchIntent(context: Context, query: String?): Intent {
        return try {
            val browserClass = context.getString(R.string.activity_browse)
            val intent = Intent(context, Class.forName(browserClass))
            intent.setAction(Intent.ACTION_VIEW)
            intent.putExtra(SearchManager.QUERY, query)
            intent
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Record query
     *
     * @param context context
     * @param query   query
     */
    fun recordQuery(context: Context, query: String?) {
        val suggestions = android.provider.SearchRecentSuggestions(context, SearchRecentSuggestions.getAuthority(context), SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES)
        suggestions.saveRecentQuery(query, null)
    }
}
