/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.annotation.SuppressLint
import android.app.SearchManager
import android.app.SearchableInfo
import android.content.ContentResolver
import android.content.Intent
import android.content.SearchRecentSuggestionsProvider
import android.net.Uri
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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
import androidx.core.content.getSystemService
import androidx.core.content.res.use
import androidx.core.view.MenuProvider
import androidx.core.view.size
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.sqlunet.browser.ColorUtils.fetchColor
import org.sqlunet.browser.ColorUtils.getDrawable
import org.sqlunet.browser.MenuHandler.menuDispatch
import org.sqlunet.browser.common.R
import org.sqlunet.browser.history.SearchRecentSuggestions
import java.io.IOException
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
     * Toolbar
     */
    private lateinit var toolbar: Toolbar

    /**
     * Search bar group (searchbar and spinner)
     */
    private lateinit var searchBarGroup: View

    /**
     * Search bar
     */
    private lateinit var searchBar: SearchBar

    /**
     * Search bar spinner
     */
    protected lateinit var searchSpinner: Spinner

    /**
     * Search view
     */
    private lateinit var searchView: SearchView

    /**
     * Suggestion container
     */
    private lateinit var suggestionContainer: RecyclerView

    // S T A T E   A N D   C A L L B A C K S

    /**
     * Stored between onViewStateRestored and onResume
     */
    private var spinnerPosition = 0

    /**
     * Search view text watcher
     */
    private var searchTextWatcher: TextWatcher? = null

    /**
     * Search view transition listener
     */
    private var searchTransitionListener: SearchView.TransitionListener? = null

    /**
     * Back press callback to close search view
     * Managed as a member to avoid redundant registrations
     */
    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (::searchView.isInitialized) {
                searchView.hide()
            }
        }
    }

    /**
     * Fragment menu provider
     */
    private val fragmentMenuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            // inflate
            menu.clear()
            menuInflater.inflate(R.menu.search, menu)
            menuInflater.inflate(R.menu.main_safedata, menu)
            menuIds.forEach { menuInflater.inflate(it, menu) }
            Log.d(TAG, "MenuProvider: onCreateMenu() size=${menu.size}")
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.search -> {
                    enterSearch()
                    true
                }

                else -> {
                    @Suppress("DEPRECATION")
                    val handled = onOptionsItemSelected(menuItem)
                    handled || menuDispatch(requireActivity() as AppCompatActivity, menuItem)
                }
            }
        }
    }

    // R E S O U R C E S

    @LayoutRes
    protected var layoutId = 0

    @MenuRes
    protected var menuIds: List<Int> = emptyList()

    @ArrayRes
    protected var spinnerLabels = 0

    @ArrayRes
    protected var spinnerIcons = 0

    // V I E W

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // backstack
        childFragmentManager.addOnBackStackChangedListener {
            if (isResumed) {
                val count = childFragmentManager.backStackEntryCount
                Log.d(TAG, "BackStack: $count")
                toolbar.subtitle = if (count > 0 && !query.isNullOrEmpty())
                    query
                else
                    getString(R.string.app_subname)
            }
        }

        // menu provider for fragment
        requireActivity().addMenuProvider(fragmentMenuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // back press callback registration (once per view lifecycle)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)

        // toolbar, searchBar and searchView
        // searchBar and searchView are declared as properties (members) of your fragment instance:
        // however, they are being retrieved from the Activity's view hierarchy, not the Fragment's own view.
        // as such they are shared across fragments
        // the variables (searchBar and searchView) are members of the fragment instance, but the objects they point to are not.
        // When you navigate from Fragment A to Fragment B:
        // 1. fragment A's instance is destroyed (or put in backstack), but the Activity and its views (the SearchBar/View) stay alive.
        // 2. fragment B is created. It calls findViewById on the Activity and gets a reference to the exact same view objects that fragment A was using.
        // 3. when fragment B calls inflateMenu(), it adds items to the menu that was already populated by fragment A.

        // toolbar
        // searchBar and searchView
        // spinner
        toolbar = requireActivity().findViewById(R.id.toolbar)
        searchBarGroup = requireActivity().findViewById(R.id.search_bar_group)
        searchBar = requireActivity().findViewById(R.id.search_bar)
        searchView = requireActivity().findViewById(R.id.search_view)
        suggestionContainer = requireActivity().findViewById(R.id.search_view_suggestion_container)
        searchSpinner = requireActivity().findViewById(R.id.search_bar_spinner)

        // connect searchbar and searchview
        searchView.setupWithSearchBar(searchBar)
    }

    override fun onStart() {
        super.onStart()

        // trigger focus in 2.0s
        if (!triggeredFocusSearch && triggerFocusSearch()) {
            viewLifecycleOwner.lifecycleScope.launch {
                triggeredFocusSearch = true
                delay(2000)
                // ensure fragment is still resumed before touching shared components
                if (isResumed && ::searchView.isInitialized) {
                    searchView.show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        takeToolBar()
        takeSearchBar()
        takeSpinner()
        takeSearchView()

        enterSearch()
    }

    override fun onPause() {
        super.onPause()

        releaseSearchView()
        releaseSpinner()
        releaseSearchBar()
        releaseToolbar()

        exitSearch()
    }

    // S A V E / R E S T O R E

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::searchSpinner.isInitialized)
            searchSpinner.apply {
                outState.putInt(STATE_SPINNER, selectedItemPosition)
            }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            spinnerPosition = it.getInt(STATE_SPINNER)
        }
    }

    // T O O L B A R

    /**
     * Set up toolbar
     */
    fun takeToolBar() {
        Log.d(TAG, "Toolbar: set up in $this")
        toolbar.setTitle(R.string.title_activity_browse)
        toolbar.setSubtitle(R.string.app_subname)
    }

    private fun releaseToolbar() {
        toolbar.setTitle(R.string.app_name)
        toolbar.setSubtitle(R.string.app_subname)
        Log.d(TAG, "Toolbar: released $this")
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
    private fun takeSearchBar() {
        Log.d(TAG, "SearchBar: controlled by $this")
        searchBar.apply {
            menu.clear()
            inflateMenu(R.menu.browse)
            setOnMenuItemClickListener { menuItem ->
                @Suppress("DEPRECATION")
                val handled = onOptionsItemSelected(menuItem) // use it a normal function
                handled || menuDispatch((requireActivity() as AppCompatActivity), menuItem)
            }
            setNavigationOnClickListener {
                exitSearch()
            }
        }
    }

    private fun releaseSearchBar() {
        searchBar.apply {
            menu.clear()
            setOnMenuItemClickListener(null)
            setNavigationOnClickListener(null)
        }
        Log.d(TAG, "SearchBar: released by $this")
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
    private fun takeSearchView() {
        Log.d(TAG, "SearchView: controlled by $this")

        searchView.apply {

            // e d i t t e x t   t w e a k
            editText.apply {
                // set the white pill background
                setBackgroundResource(R.drawable.searchview_edittext_pill)
                setTextColor(fetchColor(context, MaterialR.attr.colorOnSurface))
                // match the height
                layoutParams.height = resources.getDimensionPixelSize(R.dimen.search_pill_height)
                // center text vertically
                val horizontalPadding = resources.getDimensionPixelSize(R.dimen.search_pill_horizontal_padding)
                setPadding(horizontalPadding, 0, horizontalPadding, 0)
                // optional: adjust gravity to center the text perfectly
                gravity = Gravity.CENTER_VERTICAL
            }

            // m e n u
            searchView.toolbar.menu.clear()
            inflateMenu(R.menu.searchview)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.searchview_history -> {
                        historyToSuggestions()
                        true
                    }

                    else -> {
                        @Suppress("DEPRECATION")
                        val handled = onOptionsItemSelected(menuItem) // use it a normal function
                        handled || menuDispatch((requireActivity() as AppCompatActivity), menuItem)
                    }
                }
            }

            // b a c k   h a n d l e r
            // update the SearchView listener to toggle the existing member callback
            searchTransitionListener = SearchView.TransitionListener { _, _, newState ->
                onBackPressedCallback.isEnabled = (newState == SearchView.TransitionState.SHOWN)
            }
            searchTransitionListener?.let { addTransitionListener(it) }

            // s e a r c h   i n f o
            val searchableInfo = getSearchInfo() ?: return@apply // safety check
            searchableInfo.hintId.takeIf { it != 0 }?.let {
                hint = getString(it)
            }

            // s u b m i s s i o n
            editText.setOnEditorActionListener { textView, _, _ ->
                val query = textView.text.toString()

                // update search bar with the submitted query
                searchBar.setText(searchView.text.toString())

                // close the search view after submission
                searchView.hide()

                // emulate what the legacy SearchView did automatically
                // trigger the search intent manually for M3 SearchView
                performSearch(query, searchableInfo)
                true
            }

            // s u g g e s t i o n s
            // adapter to recyclerview
            val adapter = SuggestionAdapter { selectedText ->
                // update search view with the submitted query
                setText(selectedText)

                // update search bar with the submitted query
                searchBar.setText(selectedText)

                // close the search view after submission
                hide()

                // Handle suggestion click: perform search
                performSearch(selectedText, searchableInfo)
            }
            suggestionContainer.adapter = adapter

            // handle suggestion selection
            searchTextWatcher = editText.addTextChangedListener { text ->

                val queryText = text.toString()
                if (queryText.isBlank() || queryText.length < 3) {
                    adapter.submitList(emptyList())
                } else {
                    // provider queried on a background thread
                    viewLifecycleOwner.lifecycleScope.launch {
                        val results = withContext(Dispatchers.IO) {
                            getSuggestions(queryText, searchableInfo)
                        }
                        adapter.submitList(results)
                    }
                }
            }
        }
    }

    private fun releaseSearchView() {
        // disable the back press callback when releasing control
        onBackPressedCallback.isEnabled = false

        // remove listeners from shared searchView/editText
        searchTextWatcher?.let { searchView.editText.removeTextChangedListener(it) }
        searchTextWatcher = null
        searchTransitionListener?.let { searchView.removeTransitionListener(it) }
        searchTransitionListener = null

        searchView.setOnMenuItemClickListener(null)
        searchView.editText.setOnEditorActionListener(null)
        searchView.toolbar.menu.clear()
        searchView.hint = null

        suggestionContainer.adapter = null
        Log.d(TAG, "SearchView: released by $this")
    }

    private fun getSuggestions(query: String, searchableInfo: SearchableInfo): List<Pair<String, Int>> {
        val authority = searchableInfo.suggestAuthority
        val path = searchableInfo.suggestPath ?: ""
        val uri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(authority)
            .apply {
                if (path.isNotEmpty()) {
                    appendEncodedPath(path)
                }
            }
            .appendPath(SearchManager.SUGGEST_URI_PATH_QUERY)
            .appendPath(query)
            .build()
        context?.contentResolver?.query(uri, null, null, null, null)?.use { cursor ->
            val text1Idx = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)
            return generateSequence { if (cursor.moveToNext()) cursor else null }
                .map { it.getString(text1Idx) to R.drawable.ic_item }
                .toList()
        }
        return emptyList()
    }

    private fun historyToSuggestions() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val history = withContext(Dispatchers.IO) {
                    val suggestions = SearchRecentSuggestions(AppContext.context, SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES)
                    suggestions.cursor()?.use { cursor ->
                        val dataIdx = cursor.getColumnIndex(SearchRecentSuggestions.SuggestionColumns.DISPLAY1)
                        generateSequence { if (cursor.moveToNext()) cursor else null }
                            .map { it.getString(dataIdx) to R.drawable.ic_history }
                            .toList()
                    } ?: emptyList()
                }
                (suggestionContainer.adapter as? SuggestionAdapter)?.submitAddedList(history)
            } catch (e: IOException) {
                Log.e(TAG, "While getting history", e)
            }
        }
    }

    private fun getSearchInfo(): SearchableInfo? {
        val searchManager = context?.getSystemService<SearchManager>()
        return searchManager?.getSearchableInfo(activity?.componentName ?: return null)
    }

    fun performSearch(query: String, searchableInfo: SearchableInfo) {
        val intent = Intent(Intent.ACTION_SEARCH).apply {
            component = searchableInfo.searchActivity
            putExtra(SearchManager.QUERY, query)
        }
        startActivity(intent)
    }

    // S E A R C H   E X E C U T O R

    override fun search(query: String) {
        this.query = query
    }

    protected open fun triggerFocusSearch(): Boolean = true

    protected var triggeredFocusSearch: Boolean = false

    fun clearQuery() {
    }

    // S E A R C H

    fun enterSearch() {
        Log.d(TAG, "SearchMode enter")
        toolbar.hide()
        searchBar.show()
        searchBarGroup.visibility = View.VISIBLE
    }

    fun exitSearch() {
        Log.d(TAG, "SearchMode exit")
        searchBarGroup.visibility = View.GONE
        searchBar.hide()
        searchView.hide()
        toolbar.show()
    }

    // S P I N N E R

    open fun onSelection(position: Int) {
    }

    open val initialSelection: Int = spinnerPosition

    /**
     * Set up  spinner
     */
    protected open fun takeSpinner() {
        if (spinnerLabels != 0 && !resources.getTextArray(spinnerLabels).isEmpty()) {
            searchSpinner.apply {
                adapter = spinnerAdapter
                setSelection(initialSelection)
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        spinnerPosition = position
                        onSelection(position)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
                visibility = View.VISIBLE
            }
        } else {
            searchSpinner.visibility = View.GONE
        }
    }

    /**
     * Release spinner
     */
    private fun releaseSpinner() {
        searchSpinner.apply {
            setSelection(0)
            adapter = null
            onItemSelectedListener = null
            visibility = View.GONE
        }
    }

    /**
     * Spinner adapter
     */
    protected val spinnerAdapter: BaseAdapter
        get() {

            // adapter values and icons
            val modeLabels = resources.getTextArray(spinnerLabels)
            val modeIcons = resources.obtainTypedArray(spinnerIcons).use { typedArray ->
                IntArray(typedArray.length()) { i -> typedArray.getResourceId(i, -1) }
            }

            return object : ArrayAdapter<CharSequence?>(requireContext(), R.layout.spinner_item_actionbar, android.R.id.text1, modeLabels) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    return super.getView(position, convertView, parent).apply {
                        findViewById<TextView>(android.R.id.text1).apply {
                            text = ""
                            val drawable = getDrawable(context, modeIcons[position])
                            setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
                        }
                    }
                }

                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val rowItem = getItem(position)
                    return super.getDropDownView(position, convertView, parent).apply {
                        findViewById<TextView>(android.R.id.text1).apply {
                            text = rowItem
                            val drawable = getDrawable(context, modeIcons[position])
                            setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
                        }
                    }
                }
            }.apply {
                setDropDownViewResource(R.layout.spinner_item_actionbar_dropdown)
            }
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
    }
}
