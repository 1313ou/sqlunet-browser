/*
 * Copyright (c) 2024. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.history

import android.content.SearchRecentSuggestionsProvider

/**
 * Recent suggestion provider
 *
 * @author Bernard Bou
 */
class SearchSuggestionsProvider : SearchRecentSuggestionsProvider() {

    override fun onCreate(): Boolean {
        val context = context!!
        val authority = SearchRecentSuggestions.getAuthority(context)
        setupSuggestions(authority, MODE)
        return super.onCreate()
    }

    companion object {

        private const val MODE = DATABASE_MODE_QUERIES
    }
}
