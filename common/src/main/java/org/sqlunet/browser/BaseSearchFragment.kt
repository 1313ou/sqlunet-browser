/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.annotation.SuppressLint
import android.app.Activity
import android.app.SearchManager
import android.app.SearchableInfo
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ArrayRes
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.size
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.sqlunet.browser.ColorUtils.getDrawable
import org.sqlunet.browser.MenuHandler.menuDispatch
import org.sqlunet.browser.common.R
import org.sqlunet.core.R as CoreR

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
     * Toolbar
     */
    private lateinit var toolbar: Toolbar

    /**
     * Search bar
     */
    private lateinit var searchBar: SearchBar

    /**
     * Search view
     */
    private lateinit var searchView: SearchView

    /**
     * Stored between onViewStateRestored and onResume
     */
    private var spinnerPosition = 0

    // R E S O U R C E S

    @LayoutRes
    protected var layoutId = 0

    @MenuRes
    protected var menuId = 0

    @ArrayRes
    protected var spinnerLabels = 0

    @ArrayRes
    protected var spinnerIcons = 0

    // S E A R C H   L I S T E N E R

    override fun search(query: String) {
        this.query = query

        // subtitle
        // toolbar.setSubtitle(query)
    }

    // C R E A T I O N

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val manager = getChildFragmentManager()
        manager.addOnBackStackChangedListener {
            val count = manager.backStackEntryCount
            Log.d(TAG, "BackStack: $count")
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
        menuHost.addMenuProvider(menuProvider, getViewLifecycleOwner(), Lifecycle.State.DESTROYED)

        // toolbar
        toolbar = requireActivity().findViewById(R.id.toolbar)

        // search bar and view
        searchBar = requireActivity().findViewById(CoreR.id.search_bar)
        searchView = requireActivity().findViewById(CoreR.id.search_view)

        // connect searchbar and searchview
        searchView.setupWithSearchBar(searchBar)

        // set ups
        setupToolBar()
        setUpSearchBar()
        setupSearchView()

        // search mode
        enterSearch()
    }

    override fun onStart() {
        super.onStart()

        // trigger focus in 1.5s
        if (triggerFocusSearch()) {
            Handler(Looper.getMainLooper())
                .postDelayed({
                                 searchView.show()
                             }, 1500)
        }
    }

    /**
     * On resume
     * The fragment is responsible for activating the spinner while active.
     * Activate spinner if not already activated.
     */
    override fun onResume() {
        super.onResume()

        // spinner, added to toolbar if it does not have one
        val spinner = ensureSpinner()
        // acquire it
        acquireSpinner(spinner)
    }

    /**
     * On pause
     * The fragment is responsible for deactivating the spinner while active.
     * Deactivate spinner.
     */
    override fun onPause() {
        super.onPause()
        closeKeyboard()
        // after resume
        val spinner = spinner!!
        releaseSpinner(spinner)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        exitSearch()

        toolbar.setSubtitle(R.string.app_subname)
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

    // T O O L B A R

    /**
     * Set up toolbar's custom view, its spinner, title, background
     */
    @SuppressLint("InflateParams")
    fun setupToolBar() {
        Log.d(TAG, "Toolbar: set up in $this")

        // title
        toolbar.setTitle(R.string.title_activity_browse)
        // toolbar.setSubtitle(R.string.app_subname)

        // search menu adds search icon to toolbar
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
                menu.clear()
                inflater.inflate(R.menu.search, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.search -> {
                        enterSearch()
                        true
                    }

                    else -> false
                }
            }
        }
        requireActivity().addMenuProvider(
            menuProvider,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    private fun Toolbar.show() {
        visibility = View.VISIBLE
    }

    private fun Toolbar.hide() {
        visibility = View.GONE
    }

    // S E A R C H   B A R

    /**
     * Set up searchbar
     */
    private fun setUpSearchBar() {
        Log.d(TAG, "SearchBar: set up in $this")
        searchBar.inflateMenu(R.menu.browse)
        searchBar.setOnMenuItemClickListener { menuItem: MenuItem? ->
            menuItem?.title?.let {
                Snackbar.make(requireActivity().findViewById(android.R.id.content), it, Snackbar.LENGTH_SHORT).show()
            }
            true
        }
        searchBar.setNavigationOnClickListener {
            exitSearch()
        }
    }

    private fun SearchBar.show() {
        visibility = View.VISIBLE
    }

    private fun SearchBar.hide() {
        visibility = View.GONE
    }

    // S E A R C H   V I E W

    /**
     * Set up search view
     */
    private fun setupSearchView() {
        Log.d(TAG, "SearchView: set up in $this")

        // m e n u
        searchView.inflateMenu(R.menu.browse)
        searchView.setOnMenuItemClickListener { menuItem: MenuItem? ->
            menuItem?.title?.let {
                Snackbar.make(requireActivity().findViewById(android.R.id.content), it, Snackbar.LENGTH_SHORT).show()
            }
            true
        }

        // b a c k   p r e s s e d
        val onBackPressedCallback: OnBackPressedCallback =
            object : OnBackPressedCallback( /* enabled= */false) {
                override fun handleOnBackPressed() {
                    searchView.hide()
                }
            }

        // 1. Access the dispatcher safely
        val dispatcher = requireActivity().onBackPressedDispatcher
        // 2. Add the callback using the Fragment's viewLifecycleOwner. This ensures the callback is removed when the fragment view is destroyed
        dispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
        // 3. Update the SearchView listener
        searchView.addTransitionListener { _, _, newState ->
            onBackPressedCallback.isEnabled = (newState == SearchView.TransitionState.SHOWN)
        }

        // search info
        val searchableInfo: SearchableInfo = getSearchInfo()

        // set the hint from your searchable.xml
        val hintRes = searchableInfo.hintId
        if (hintRes != 0) {
            searchView.hint = getString(hintRes)
        }

        // submission
        searchView.editText.setOnEditorActionListener { textView, actionId, event ->
            val query = textView.text.toString()

            // Close the search view after submission
            searchView.hide()

            // Update search bar with the submitted query
            searchBar.setText(searchView.text.toString())

            // Search view clear
            // clearSearchView(searchView)

            // Emulate what the legacy SearchView did automatically
            // Trigger the search intent manually for M3 SearchView
            performSearch(query, searchableInfo)
            true
        }

        // text change and edit action
        searchView.editText.addTextChangedListener {
            // activeSearchFragment?.onSearchQueryChanged(it.toString())
        }

        // s u g g e s t i o n
        // adapter to recyclerview
        val adapter = SuggestionAdapter { selectedText ->
            // Handle suggestion click
            searchView.setText(selectedText)
            searchView.hide()
            performSearch(selectedText, searchableInfo)
        }
        val suggestionContainer = requireActivity().findViewById<RecyclerView>(CoreR.id.search_view_suggestion_container)
        suggestionContainer.adapter = adapter
        // handle suggestion selection
        searchView.editText.addTextChangedListener { text ->
            val query = text.toString()
            if (query.isEmpty() || query.isBlank() || query.length < 3) {
                adapter.submitList(emptyList())
                return@addTextChangedListener
            }
            // provider queried on a background thread
            lifecycleScope.launch(Dispatchers.IO) {
                val results = getSuggestions(query, searchableInfo)
                Log.d(TAG, "Suggestions: $results")
                withContext(Dispatchers.Main) {
                    adapter.submitList(results)
                }
            }
        }

        // trigger focus
        if (triggerFocusSearch()) {
            Handler(Looper.getMainLooper()).postDelayed({
                                                            toolbar.hide()
                                                            searchBar.show()
                                                            //searchView.hide()
                                                        }, 1500)
        }
    }

    fun performSearch(query: String, searchableInfo: SearchableInfo) {
        val intent = Intent(Intent.ACTION_SEARCH).apply {

            // use the component name directly from SearchableInfo
            component = searchableInfo.searchActivity
            putExtra(SearchManager.QUERY, query)
        }
        startActivity(intent)
    }

    fun getSuggestions(query: String, searchableInfo: SearchableInfo): List<String> {

        val authority = searchableInfo.suggestAuthority
        val path = searchableInfo.suggestPath ?: ""
        return fetchSuggestions(query, authority, path)
    }

    fun fetchSuggestions(query: String, authority: String, path: String?): List<String> {

        // The standard URI format for search suggestions
        val uriBuilder = Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(authority)
            .query("")
            .fragment("")

        if (!path.isNullOrEmpty()) {
            uriBuilder.appendEncodedPath(path)
        }

        // Most providers expect the query appended at the end
        uriBuilder.appendPath(SearchManager.SUGGEST_URI_PATH_QUERY)
        uriBuilder.appendPath(query)

        val uri = uriBuilder.build()
        val suggestions = mutableListOf<String>()

        context?.contentResolver?.query(uri, null, null, null, null)?.use { cursor ->
            val text1Index = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)
            while (cursor.moveToNext()) {
                if (text1Index != -1) {
                    suggestions.add(cursor.getString(text1Index))
                }
            }
        }
        return suggestions
    }

    private fun getSearchInfo(): SearchableInfo {
        val componentName = requireActivity().componentName
        val searchManager = requireContext().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        return searchManager.getSearchableInfo(componentName)
    }

    protected open fun triggerFocusSearch(): Boolean {
        return true
    }

    fun clearQuery() {
        clearSearchView(searchView)
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

    // S E A R C H

    fun enterSearch() {
        Log.d(TAG, "SearchMode enter")
        //searchBar.hint =
        toolbar.hide()
        searchBar.show()
    }

    fun exitSearch() {
        Log.d(TAG, "SearchMode exit")
        //activeSearchFragment = null
        searchBar.hide()
        searchView.hide()
        toolbar.show()
    }

    // S P I N N E R

    /**
     * Toolbar's spinner
     */
    private val spinner: Spinner?
        get() {
            // must have
            return toolbar.findViewById(R.id.spinner) // must be non null after resume
        }

    /**
     * Ensure toolbar has a spinner
     *
     * @return spinner
     */
    private fun ensureSpinner(): Spinner {
        // must have
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
                    val drawable = getDrawable(context, resId)
                    textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
                    return view
                }

                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val rowItem = getItem(position)!!
                    val view = super.getDropDownView(position, convertView, parent)
                    val textView = view.findViewById<TextView>(android.R.id.text1)
                    textView.text = rowItem
                    val resId = modeIcons[position]
                    val drawable = getDrawable(context, resId)
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

    companion object {

        private const val TAG = "BaseSearchF"

        /**
         * Saved state of spinner
         */
        private const val STATE_SPINNER = "selected_mode"

        // S E A R C H

        private fun clearSearchView(searchView: SearchView) {
            searchView.clearFocus()
            searchView.isFocusable = false
            //searchView.editText.text = ""
            //searchView.isIconified = true
        }
    }
}
