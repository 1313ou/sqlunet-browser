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
import androidx.annotation.LayoutRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import org.sqlunet.browser.common.R

abstract class BaseSelectorsRecyclerFragment : LoggingFragment() {

    /**
     * Search query
     */
    protected lateinit var word: String

    /**
     * Recycler view
     */
    protected lateinit var recyclerView: RecyclerView

    /**
     * Cursor adapter
     */
    protected abstract val adapter: RecyclerView.Adapter<*>

    /**
     * Layout resource id
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

        // recycler view
        recyclerView = view.findViewById(R.id.selectors_list)

        // data view models
        Log.d(TAG, "Make models. Lifecycle: onViewCreated()")
        makeModels()
    }

    override fun onStart() {
        super.onStart()

        // list adapter bound to view
        Log.d(TAG, "Make adapter. Lifecycle: onStart()")
        Log.d(TAG, "Set recyclerview adapter. Lifecycle: onStart()")
        recyclerView.adapter = adapter

        // load data
        Log.d(TAG, "Load data. Lifecycle: onStart()")
        load()
    }

    override fun onStop() {
        super.onStop()

        Log.d(TAG, "Nullify recyclerview adapter. Lifecycle: onStop()")
        recyclerView.adapter = null
    }

    // A D A P T E R

    /**
     * Load data from word
     */
    protected abstract fun load()

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
        positionModel!!.setPosition(-1)
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
                val view = requireView()
                Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
            } else {
                // pass on to list adapter
                (adapter as? CursorRecyclerViewAdapter)?.changeCursor(augmentCursor(cursor))
            }
        }

    private val positionObserver: Observer<Int>
        get() = Observer { position: Int ->
            Log.d(TAG, "Observed position change $position")
            adapter.notifyDataSetChanged()
        }

    // A C T I V A T I O N

    protected abstract fun select(position: Int)

    companion object {

        private const val TAG = "SelectorsRecyclerF"
    }
}
