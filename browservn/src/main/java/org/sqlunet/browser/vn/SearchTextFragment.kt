/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.vn

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import org.sqlunet.browser.BaseSearchFragment
import org.sqlunet.browser.SearchTextSplashFragment
import org.sqlunet.browser.SplashFragment
import org.sqlunet.propbank.provider.PropBankContract
import org.sqlunet.propbank.provider.PropBankProvider
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.settings.Settings
import org.sqlunet.verbnet.provider.VerbNetContract
import org.sqlunet.verbnet.provider.VerbNetProvider

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

    // S P I N N E R

    override fun acquireSpinner(spinner: Spinner) {
        // to set position
        super.acquireSpinner(spinner)

        // visible
        spinner.visibility = View.VISIBLE

        // apply spinner adapter
        spinner.setAdapter(spinnerAdapter)

        // spinner listener
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long) {
                searchModePosition = position
                Settings.setSearchModePref(requireContext(), position)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }

        // spinner position
        val position: Int = Settings.getSearchModePref(requireContext())
        spinner.setSelection(position)
    }

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

        // mode
        val modePosition = searchModePosition

        // status
        // val textSearches = getResources().getTextArray(R.array.searchtext_modes)

        // as per selected mode
        val searchUri: String
        val id: String
        val idType: String
        val target: String
        val columns: Array<String>
        val hiddenColumns: Array<String>
        val database: String
        when (modePosition) {
            0 -> {
                searchUri = VerbNetProvider.makeUri(VerbNetContract.Lookup_VnExamples_X.URI_BY_EXAMPLE)
                id = VerbNetContract.Lookup_VnExamples_X.EXAMPLEID
                idType = "vn_example"
                target = VerbNetContract.Lookup_VnExamples_X.EXAMPLE
                columns = arrayOf(VerbNetContract.Lookup_VnExamples_X.EXAMPLE)
                hiddenColumns = arrayOf(
                    "GROUP_CONCAT(class || '@' || classid) AS " + VerbNetContract.Lookup_VnExamples_X.CLASSES
                )
                database = "vn"
            }

            1 -> {
                searchUri = PropBankProvider.makeUri(PropBankContract.Lookup_PbExamples_X.URI_BY_EXAMPLE)
                id = PropBankContract.Lookup_PbExamples_X.EXAMPLEID
                idType = "pb_example"
                target = PropBankContract.Lookup_PbExamples_X.TEXT
                columns = arrayOf(PropBankContract.Lookup_PbExamples_X.TEXT)
                hiddenColumns = arrayOf(
                    "GROUP_CONCAT(rolesetname ||'@'||rolesetid) AS " + PropBankContract.Lookup_PbExamples_X.ROLESETS
                )
                database = "pb"
            }

            else -> return
        }

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
