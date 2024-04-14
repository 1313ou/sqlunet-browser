/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.CursorAdapter
import android.widget.ListView
import androidx.annotation.LayoutRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import org.sqlunet.browser.common.R

abstract class BaseSelectorsListFragment : LoggingFragment(), OnItemClickListener {

    /**
     * Search query
     */
    protected lateinit var word: String

    /**
     * List view
     */
    protected var listView: ListView? = null

    /**
     * Cursor adapter
     */
    protected var adapter: CursorAdapter? = null

    /**
     * Cursor loader id
     */
    @LayoutRes
    protected var layoutId = 0

    /**
     * Activate on click flag
     */
    var activateOnItemClick = true

    // L I F E C Y C L E

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // list view
        listView = view.findViewById(android.R.id.list)

        // bind to adapter
        listView!!.setChoiceMode(if (activateOnItemClick) AbsListView.CHOICE_MODE_SINGLE else AbsListView.CHOICE_MODE_NONE)
        listView!!.onItemClickListener = this

        // data view models
        Log.d(TAG, "Make models. Lifecycle: onViewCreated()")
        makeModels()
    }

    override fun onStart() {
        super.onStart()

        // list adapter bound to view
        Log.d(TAG, "Make adapter. Lifecycle: onStart()")
        adapter = makeAdapter()
        Log.d(TAG, "Set listview adapter. Lifecycle: onStart()")
        listView!!.setAdapter(adapter)

        // load data
        Log.d(TAG, "Load data. Lifecycle: onStart()")
        load()
    }

    override fun onStop() {
        super.onStop()

        Log.d(TAG, "Nullify listview adapter. Lifecycle: onStop()")
        listView!!.setAdapter(null)
        // the cursor will be saved along with fragment state if any
        Log.d(TAG, "Nullify adapter cursor but do not close cursor. Lifecycle: onStop()")
        adapter!!.swapCursor(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (adapter != null) {
            Log.d(TAG, "Close cursor.")
            adapter!!.changeCursor(null)
            Log.d(TAG, "Nullify adapter.")
            adapter = null
        }
    }

    // A D A P T E R

    /**
     * Load data from word
     */
    protected abstract fun load()

    protected abstract fun makeAdapter(): CursorAdapter?

    // V I E W M O D E L S

    /**
     * Data view model
     */
    protected var dataModel: SqlunetViewModel? = null

    /**
     * Position view model
     */
    protected var positionModel: PositionViewModel? = null

    /**
     * View Model key
     */
    protected var viewModelKey: String? = null

    /**
     * Make view models
     */
    protected fun makeModels() {

        // data model
        dataModel = ViewModelProvider(this)[viewModelKey!!, SqlunetViewModel::class.java]
        dataModel!!.getData().observe(getViewLifecycleOwner(), cursorObserver)

        // position model
        positionModel = ViewModelProvider(this)[PositionViewModel::class.java]
        positionModel!!.positionLiveData.observe(getViewLifecycleOwner(), positionObserver)
        positionModel!!.setPosition(AdapterView.INVALID_POSITION)
    }

    protected open fun augmentCursor(cursor: Cursor): Cursor {
        return cursor
    }

    // O B S E R V E R S

    private val cursorObserver: Observer<in Cursor?>
        get() = Observer { cursor: Cursor? ->
            if (cursor == null || cursor.count <= 0) {
                val html = getString(R.string.error_entry_not_found, "<b>$word</b>")
                val message: CharSequence = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY) else @Suppress("DEPRECATION") Html.fromHtml(html)
                // Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                val view = requireView()
                Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
            } else {
                // pass on to list adapter
                adapter!!.changeCursor(augmentCursor(cursor))
            }
        }

    private val positionObserver: Observer<Int>
        get() = Observer { position: Int ->
            Log.d(TAG, "Observed position change $position")
            listView!!.setItemChecked(position, position != AdapterView.INVALID_POSITION)
        }

    // C L I C K   L I S T E N E R

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        Log.d(TAG, "Select $position")
        activate(position)
    }

    protected abstract fun activate(position: Int)

    companion object {

        private const val TAG = "SelectorsListF"
    }
}
