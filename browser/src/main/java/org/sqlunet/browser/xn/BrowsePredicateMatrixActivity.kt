/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.xn

import android.app.SearchManager
import android.content.Intent
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import org.sqlunet.browser.AbstractBrowseActivity
import org.sqlunet.browser.R
import org.sqlunet.browser.getParcelable
import org.sqlunet.predicatematrix.PmRolePointer
import org.sqlunet.provider.ProviderArgs

/**
 * Predicate Matrix activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class BrowsePredicateMatrixActivity : AbstractBrowseActivity<BrowsePredicateMatrixFragment?>() {

    @get:LayoutRes
    override val layoutId: Int
        get() = R.layout.activity_predicatematrix

    @get:IdRes
    override val fragmentId: Int
        get() = R.id.fragment_predicatematrix

    // S E A R C H

    /**
     * Handle intent dispatched by search view (either onCreate or onNewIntent if activity is single top)
     *
     * @param intent intent
     */
    override fun handleSearchIntent(intent: Intent) {
        val action = intent.action
        val isActionView = Intent.ACTION_VIEW == action
        if (isActionView || Intent.ACTION_SEARCH == action) {
            // search query submit (SEARCH) or suggestion selection (when a suggested item is selected) (VIEW)
            val query = intent.getStringExtra(SearchManager.QUERY)
            if (query != null && fragment != null) {
                if (isActionView) {
                    fragment!!.clearQuery()
                }
                fragment!!.search(query)
            }
            return
        } else if (Intent.ACTION_SEND == action) {
            val type = intent.type
            if ("text/plain" == type) {
                val query = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (query != null && fragment != null) {
                    fragment!!.search(query)
                    return
                }
            }
        }

        // search query from other source
        if (ProviderArgs.ACTION_QUERY == action) {
            val args = intent.extras
            if (args != null) {
                val type = args.getInt(ProviderArgs.ARG_QUERYTYPE)
                if (ProviderArgs.ARG_QUERYTYPE_PM == type || ProviderArgs.ARG_QUERYTYPE_PMROLE == type) {
                    val pointer = getParcelable(args, ProviderArgs.ARG_QUERYPOINTER)
                    if (pointer is PmRolePointer && fragment != null) {
                        fragment!!.search(pointer)
                    }
                }
            }
        }
    }
}
