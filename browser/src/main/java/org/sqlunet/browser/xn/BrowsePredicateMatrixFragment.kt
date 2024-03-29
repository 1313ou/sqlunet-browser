/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.xn

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import org.sqlunet.Word
import org.sqlunet.browser.BaseSearchFragment
import org.sqlunet.browser.R
import org.sqlunet.browser.SplashFragment
import org.sqlunet.predicatematrix.PmRolePointer
import org.sqlunet.predicatematrix.browser.PredicateMatrixFragment
import org.sqlunet.predicatematrix.settings.Settings.PMMode
import org.sqlunet.provider.ProviderArgs

/**
 * PredicateMatrix fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class BrowsePredicateMatrixFragment : BaseSearchFragment() {

    /**
     * Pointer
     */
    private var pointer: PmRolePointer? = null

    /**
     * Query
     */
    private var query2: String? = null

    init {
        layoutId = R.layout.fragment_browse_predicatematrix
        menuId = R.menu.predicate_matrix
        colorAttrId = R.attr.colorPrimaryVariant
        spinnerLabels = R.array.predicatematrix_modes
        spinnerIcons = R.array.predicatematrix_icons
    }

    // R E S T O R E

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // restore data
        if (savedInstanceState != null) {
            this.query2 = savedInstanceState.getString(STATE_QUERY)
            this.pointer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) savedInstanceState.getParcelable(STATE_POINTER, PmRolePointer::class.java) else savedInstanceState.getParcelable(STATE_POINTER)
        } else {
            // splash fragment
            val fragment: Fragment = BrowsePredicateMatrixSplashFragment()
            getChildFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.container_predicatematrix, fragment, SplashFragment.FRAGMENT_TAG)
                // .addToBackStack(SplashFragment.FRAGMENT_TAG) 
                .commit()
        }
    }

    override fun onStop() {
        super.onStop()

        // remove data fragments and replace with splash before onSaveInstanceState takes place (between -3 and -4)
        beforeSaving(BrowsePredicateMatrixSplashFragment(), SplashFragment.FRAGMENT_TAG, R.id.container_predicatematrix, PredicateMatrixFragment.FRAGMENT_TAG)
    }

    // S A V E

    override fun onSaveInstanceState(outState: Bundle) {
        // always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)

        // save data
        if (this.query2 != null) {
            outState.putString(STATE_QUERY, this.query2)
        }
        if (pointer != null) {
            outState.putParcelable(STATE_POINTER, pointer)
        }
    }

    // S P I N N E R

    override fun acquireSpinner(spinner: Spinner) {
        // to set position
        super.acquireSpinner(spinner)

        // visible
        spinner.visibility = View.VISIBLE

        // apply spinner adapter
        spinner.setAdapter(spinnerAdapter)

        // saved mode
        val mode = PMMode.getPref(requireContext())

        // no listener yet
        spinner.onItemSelectedListener = null
        spinner.setSelection(mode.ordinal)

        // spinner listener
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long) {
                val mode2: PMMode = PMMode.entries.toTypedArray()[position]
                val hasChanged = mode2.setPref(requireContext())
                Log.d(TAG, "mode=" + mode2.name + " has changed=" + hasChanged)

                // restart
                if (hasChanged) {
                    if (this@BrowsePredicateMatrixFragment.pointer != null) {
                        search(this@BrowsePredicateMatrixFragment.pointer!!)
                    } else if (this@BrowsePredicateMatrixFragment.query2 != null) {
                        search(this@BrowsePredicateMatrixFragment.query2!!)
                    }
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }
    }

    // S E A R C H

    /**
     * Handle search
     *
     * @param pointer query pointer
     */
    fun search(pointer: PmRolePointer?) {
        if (pointer == null) {
            return
        }

        // super
        super.search(this.query2!!)

        // log
        Log.d(TAG, "Search PM $pointer")

        // set
        this.pointer = pointer
        this.query2 = null

        // arguments
        val args = Bundle()
        args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer)
        args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_PM)

        // fragment
        val fragment: Fragment = PredicateMatrixFragment()
        fragment.setArguments(args)
        if (!isAdded) {
            return
        }
        getChildFragmentManager()
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.container_predicatematrix, fragment, PredicateMatrixFragment.FRAGMENT_TAG)
            .addToBackStack(PredicateMatrixFragment.FRAGMENT_TAG)
            .commit()
    }

    /**
     * Handle search
     *
     * @param query query string
     */
    override fun search(query: String) {
        val trimmedQuery = query.trim { it <= ' ' }
        if (trimmedQuery.isEmpty()) {
            return
        }

        // super
        super.search(trimmedQuery)

        // log
        Log.d(TAG, "Search PM $trimmedQuery")

        // set
        this.query2 = trimmedQuery
        this.pointer = null

        // pointer
        val pointer: Parcelable = if (trimmedQuery.startsWith("#mr")) {
            val roleId = trimmedQuery.substring(3).toLong()
            PmRolePointer(roleId)
        } else {
            Word(trimmedQuery)
        }

        // arguments
        val args = Bundle()
        args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer)
        args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_PM)

        // fragment
        val fragment: Fragment = PredicateMatrixFragment()
        fragment.setArguments(args)
        if (!isAdded) {
            return
        }
        getChildFragmentManager()
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.container_predicatematrix, fragment, PredicateMatrixFragment.FRAGMENT_TAG)
            .addToBackStack(PredicateMatrixFragment.FRAGMENT_TAG)
            .commit()
    }

    override fun triggerFocusSearch(): Boolean {
        if (!isAdded) {
            return false
        }
        val active = getChildFragmentManager().findFragmentById(R.id.container_predicatematrix)
        return active != null && SplashFragment.FRAGMENT_TAG == active.tag
    }

    companion object {

        private const val TAG = "BrowsePmF"

        /**
         * Saved query
         */
        private const val STATE_QUERY = "query"

        /**
         * Saved pointer
         */
        private const val STATE_POINTER = "pointer"
    }
}
