/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.fn

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import org.sqlunet.browser.BaseSearchFragment
import org.sqlunet.browser.SearchTextSplashFragment
import org.sqlunet.browser.SplashFragment
import org.sqlunet.framenet.provider.FrameNetContract.Lookup_FTS_FnSentences_X
import org.sqlunet.framenet.provider.FrameNetProvider.Companion.makeUri
import org.sqlunet.provider.ProviderArgs

/**
 * Search text fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SearchTextFragment : BaseSearchFragment() {

    init {
        layoutId = R.layout.fragment_searchtext
        menuId = R.menu.searchtext
        colorAttrId = R.attr.colorPrimaryVariant
        spinnerLabels = R.array.searchtext_modes
        spinnerIcons = R.array.searchtext_icons
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            // splash fragment
            val fragment: Fragment = SearchTextSplashFragment()
            getChildFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.container_searchtext, fragment, SplashFragment.FRAGMENT_TAG)
                //.addToBackStack(SplashFragment.FRAGMENT_TAG)
                .commit()
        }
    }

    // override fun onStop() {
    //     super.onStop()
    //     // remove data fragments and replace with splash before onSaveInstanceState takes place (between -3 and -4)
    //     beforeSaving(SearchTextSplashFragment(), SplashFragment.FRAGMENT_TAG, R.id.container_searchtext, TextFragment.FRAGMENT_TAG)
    // }

    // S E A R C H

    /**
     * Handle query
     *
     * @param query query
     */
    override fun search(query: String) {
        val trimmedQuery = query.trim { it <= ' ' }
        if (trimmedQuery.isEmpty()) {
            return
        }

        // super
        super.search(trimmedQuery)

        // log
        Log.d(TAG, "Search text $trimmedQuery")

        // status
        // var[] textSearches = getResources().getTextArray(R.array.searchtext_modes)

        // arguments
        val searchUri: String = makeUri(Lookup_FTS_FnSentences_X.URI_BY_SENTENCE)
        val id: String = Lookup_FTS_FnSentences_X.SENTENCEID
        val idType = "fnsentence"
        val target: String = Lookup_FTS_FnSentences_X.TEXT
        val columns: Array<String> = arrayOf(Lookup_FTS_FnSentences_X.TEXT)
        val hiddenColumns: Array<String> = arrayOf(
            Lookup_FTS_FnSentences_X.SENTENCEID,
            "GROUP_CONCAT(DISTINCT  frame || '@' || frameid) AS " + Lookup_FTS_FnSentences_X.FRAMES,
            "GROUP_CONCAT(DISTINCT  lexunit || '@' || luid) AS " + Lookup_FTS_FnSentences_X.LEXUNITS
        )
        val database = "fn"

        // parameters
        val args = Bundle()
        args.putString(ProviderArgs.ARG_QUERYURI, searchUri)
        args.putString(ProviderArgs.ARG_QUERYID, id)
        args.putString(ProviderArgs.ARG_QUERYIDTYPE, idType)
        args.putStringArray(ProviderArgs.ARG_QUERYITEMS, columns)
        args.putStringArray(ProviderArgs.ARG_QUERYHIDDENITEMS, hiddenColumns)
        args.putString(ProviderArgs.ARG_QUERYFILTER, "$target MATCH ?")
        args.putString(ProviderArgs.ARG_QUERYARG, trimmedQuery)
        args.putInt(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_searchtext)
        args.putString(ProviderArgs.ARG_QUERYDATABASE, database)

        // fragment
        val fragment: Fragment = TextFragment()
        fragment.setArguments(args)
        if (!isAdded) {
            return
        }
        getChildFragmentManager()
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.container_searchtext, fragment, TextFragment.FRAGMENT_TAG)
            .addToBackStack(TextFragment.FRAGMENT_TAG)
            .commit()
    }

    override fun triggerFocusSearch(): Boolean {
        if (!isAdded) {
            return false
        }
        val active = getChildFragmentManager().findFragmentById(R.id.container_searchtext)
        return active != null && SplashFragment.FRAGMENT_TAG == active.tag
    }

    companion object {

        private const val TAG = "SearchTextF"
    }
}
