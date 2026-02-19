/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.annotation.SuppressLint
import android.app.Activity
import android.app.SearchManager
import android.app.SearchableInfo
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.ArrayRes
import androidx.annotation.AttrRes
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import org.sqlunet.browser.ColorUtils.fetchColor
import org.sqlunet.browser.ColorUtils.getDrawable
import org.sqlunet.browser.ColorUtils.tint
import org.sqlunet.browser.MenuHandler.menuDispatch
import org.sqlunet.browser.common.R
import androidx.core.view.size
import androidx.core.graphics.drawable.toDrawable
import com.google.android.material.R as MaterialR

/**
 * Base search fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class BaseSearchFragment : LoggingFragment(), SearchListener {

    // Q U E R Y

    protected var query: String? = null

    // C O M P O N E N T S

    /**
     * Search view (held in search menuitem) that holds query
     */
    private var searchView: SearchView? = null

    /**
     * Stored between onViewStateRestored and onResume
     */
    private var spinnerPosition = 0

    // R E S O U R C E S

    @LayoutRes
    protected var layoutId = 0

    @MenuRes
    protected var menuId = 0

    // @AttrRes
    // protected var colorAttrId = 0

    @ArrayRes
    protected var spinnerLabels = 0

    @ArrayRes
    protected var spinnerIcons = 0

    // C R E A T I O N

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val manager = getChildFragmentManager()
        manager.addOnBackStackChangedListener {
            val count = manager.backStackEntryCount
            Log.d(TAG, "BackStack: $count")
            val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)!!
            if (count > 0) {
                toolbar.setSubtitle(query ?: getString(R.string.app_subname))
            } else {
                toolbar.setSubtitle(R.string.app_subname)
            }
        }
    }

    // V I E W

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // menu provider
        val menuProvider: MenuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // inflate
                menu.clear()
                menuInflater.inflate(R.menu.main_safedata, menu)
                menuInflater.inflate(menuId, menu)
                // MenuCompat.setGroupDividerEnabled(menu, true)
                Log.d(TAG, "MenuProvider: onCreateMenu() size=" + menu.size)

                // set up search view
                searchView = getSearchView(menu)
                setupSearchView(searchView!!, getSearchInfo(requireActivity()))

                // toolbar
                // must have
                val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)!!
                setupToolBar(toolbar)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                @Suppress("DEPRECATION")
                val handled = onOptionsItemSelected(menuItem) // use it a normal function
                return if (handled) {
                    true
                } else menuDispatch((requireActivity() as AppCompatActivity), menuItem)
            }
        }
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(menuProvider, getViewLifecycleOwner(), Lifecycle.State.RESUMED)
    }

    override fun onResume() {
        super.onResume()

        // spinner, added to toolbar if it does not have one
        val spinner = ensureSpinner()
        // acquire it
        acquireSpinner(spinner)
    }

    override fun onPause() {
        super.onPause()
        closeKeyboard()
        // after resume
        val spinner = spinner!!
        releaseSpinner(spinner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)!!
        toolbar.setSubtitle(R.string.app_subname)
    }

    // T O O L B A R

    /**
     * Set up toolbar's custom view, its spinner, title, background
     *
     * @param toolbar toolbar
     */
    @SuppressLint("InflateParams")
    fun setupToolBar(toolbar: Toolbar) {
        Log.d(TAG, "Toolbar: set up in $this")

        // title
        toolbar.setTitle(R.string.title_activity_browse)
        // toolbar.setSubtitle(R.string.app_subname)

        // background
        // val color = fetchColor(requireContext(), colorAttrId)
        // toolbar.background = color.toDrawable()
    }

    /**
     * Set up search view
     *
     * @param searchView     search view
     * @param searchableInfo searchable info
     */
    private fun setupSearchView(searchView: SearchView, searchableInfo: SearchableInfo?) {
        // search view
        searchView.setSearchableInfo(searchableInfo)
        searchView.setIconifiedByDefault(true)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                clearSearchView(searchView)
                closeKeyboard()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        // trigger focus
        if (triggerFocusSearch()) {
            Handler(Looper.getMainLooper()).postDelayed({ searchView.isIconified = false }, 1500)
        }
    }

    protected open fun triggerFocusSearch(): Boolean {
        return true
    }

    fun clearQuery() {
        if (searchView != null) {
            clearSearchView(searchView!!)
        }
        closeKeyboard()
    }

    private fun closeKeyboard() {
        // activity
        val activity: Activity = requireActivity()

        // view
        val view = activity.currentFocus
        if (view != null) {
            val imm = (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    // S P I N N E R

    /**
     * Toolbar's spinner
     */
    private val spinner: Spinner?
        get() {
            // must have
            val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)!!
            return toolbar.findViewById(R.id.spinner) // must be non-null after resume
        }

    /**
     * Ensure toolbar has a spinner
     *
     * @return spinner
     */
    private fun ensureSpinner(): Spinner {
        // must have
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)!!
        var spinner = toolbar.findViewById<Spinner>(R.id.spinner)
        if (spinner == null) {
            // toolbar customized view if toolbar does not already contain spinner
            @SuppressLint("InflateParams") val customView = getLayoutInflater().inflate(R.layout.actionbar_custom, null) // raises "The specified child already has a parent" if toolbar
            toolbar.addView(customView)
            spinner = toolbar.findViewById(R.id.spinner)
        }
        return spinner
    }

    /**
     * Acquire the spinner
     *
     * @param spinner spinner
     */
    protected open fun acquireSpinner(spinner: Spinner) {
        // leave in limbo if spinner is not needed
        spinner.setSelection(spinnerPosition)
    }

    /**
     * Release spinner
     *
     * @param spinner spinner
     */
    private fun releaseSpinner(spinner: Spinner) {
        spinner.setSelection(0)
        spinner.onItemSelectedListener = null
        spinner.adapter = null
        spinner.visibility = View.GONE
    }

    /**
     * Spinner adapter
     */
    protected val spinnerAdapter: BaseAdapter
        get() {

            // adapter values and icons
            val modeLabels = resources.getTextArray(spinnerLabels)
            val modeIcons: IntArray
            resources.obtainTypedArray(spinnerIcons).let {
                val n = it.length()
                modeIcons = IntArray(n)
                for (i in 0 until n) {
                    modeIcons[i] = it.getResourceId(i, -1)
                }
                it.recycle()
            }

            // adapter
            val adapter = object : ArrayAdapter<CharSequence?>(requireContext(), R.layout.spinner_item_actionbar, android.R.id.text1, modeLabels) {

                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent)
                    val textView = view.findViewById<TextView>(android.R.id.text1)
                    textView.text = ""
                    val resId = modeIcons[position]
                    val color = fetchColor(context, MaterialR.attr.colorOnPrimary)
                    val drawable = getDrawable(context, resId)
                    tint(color, drawable!!)
                    textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
                    return view
                }

                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val rowItem = getItem(position)!!
                    val view = super.getDropDownView(position, convertView, parent)
                    val textView = view.findViewById<TextView>(android.R.id.text1)
                    textView.text = rowItem
                    val resId = modeIcons[position]
                    val color = fetchColor(context, MaterialR.attr.colorOnPrimary)
                    val drawable = getDrawable(context, resId)
                    tint(color, drawable!!)
                    textView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
                    return view
                }
            }
            adapter.setDropDownViewResource(R.layout.spinner_item_actionbar_dropdown)
            return adapter
        }

    /**
     * Search type position, obtained by peeking at spinner state
     */
    @Suppress("unused")
    protected val spinnerSearchModePosition: Int
        get() {
            if (spinner != null) {
                return spinner!!.selectedItemPosition
            }
            return -1
        }

    /**
     * Search type position, obtained by peeking at spinner state or registry if spinner is still null
     */
    protected var searchModePosition: Int = -1

    // S E A R C H   L I S T E N E R

    override fun search(query: String) {
        this.query = query

        // subtitle
        // var toolbar = requireActivity().findViewById(R.id.toolbar)
        // toolbar.setSubtitle(query)
    }

    // S A V E / R E S T O R E

    override fun onSaveInstanceState(outState: Bundle) {
        // always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)

        // spinner
        if (spinner != null) {
            // serialize the current dropdown position
            val position = spinner!!.selectedItemPosition
            outState.putInt(STATE_SPINNER, position)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        // restore from saved instance
        if (savedInstanceState != null) {
            spinnerPosition = savedInstanceState.getInt(STATE_SPINNER)
        }
    }

    // F R A G M E N T   M A N A G E M E N T

    // /**
    //  * Remove children fragments with tags and insert given fragment with at given location
    //  *
    //  * @param fragment          new fragment
    //  * @param tag               new fragment's tag
    //  * @param where             new fragment's location
    //  * @param childFragmentTags removed children's tags
    //  * @noinspection SameParameterValue, EmptyMethod
    //  */
    // protected fun beforeSaving(fragment: Fragment, tag: String?, @IdRes where: Int, vararg childFragmentTags: String) {
    //     // FragmentUtils.removeAllChildFragment(getChildFragmentManager(), fragment, tag, where, *childFragmentTags)
    // }

    companion object {

        private const val TAG = "BaseSearchF"

        /**
         * Saved state of spinner
         */
        private const val STATE_SPINNER = "selected_mode"

        // S E A R C H V I E W

        private fun getSearchView(menu: Menu): SearchView? {
            // menu item
            val searchMenuItem = menu.findItem(R.id.search) ?: return null
            // search view
            return searchMenuItem.actionView as SearchView?
        }

        private fun getSearchInfo(activity: Activity): SearchableInfo? {
            val componentName = activity.componentName
            val searchManager = (activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager)
            return searchManager.getSearchableInfo(componentName)
        }

        private fun clearSearchView(searchView: SearchView) {
            searchView.clearFocus()
            searchView.isFocusable = false
            searchView.setQuery("", false)
            searchView.isIconified = true
        }
    }
}
