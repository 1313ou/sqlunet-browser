/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.CursorAdapter
import android.widget.ListAdapter
import android.widget.SimpleCursorAdapter
import androidx.fragment.app.ListFragment
import androidx.lifecycle.ViewModelProvider
import org.sqlunet.browser.common.R
import org.sqlunet.provider.XNetContract
import org.sqlunet.provider.XSqlUNetProvider.Companion.makeUri

/**
 * A list fragment representing sources.
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SourceFragment : ListFragment() {

    /**
     * View model
     */
    private var model: SqlunetViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // make cursor adapter
        val from = arrayOf(XNetContract.Sources.NAME, XNetContract.Sources.VERSION, XNetContract.Sources.URL, XNetContract.Sources.PROVIDER, XNetContract.Sources.REFERENCE)
        val to = intArrayOf(R.id.name, R.id.version, R.id.url, R.id.provider, R.id.reference)
        val adapter: ListAdapter = SimpleCursorAdapter(
            requireContext(), R.layout.item_source, null,
            from,
            to, 0
        )
        setListAdapter(adapter)
    }

    override fun onStart() {
        super.onStart()

        // models
        makeModels() // sets cursor

        // load the contents
        val uri = Uri.parse(makeUri(XNetContract.Sources.URI))
        val projection = arrayOf(XNetContract.Sources.ID + " AS _id", XNetContract.Sources.NAME, XNetContract.Sources.VERSION, XNetContract.Sources.URL, XNetContract.Sources.PROVIDER, XNetContract.Sources.REFERENCE)
        val sortOrder = XNetContract.Sources.ID
        model!!.loadData(uri, projection, null, null, sortOrder, null)
    }

    override fun onStop() {
        super.onStop()
        val listView = getListView()
        val adapter = listAdapter as CursorAdapter?
        listView.adapter = null
        adapter!!.swapCursor(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        val adapter = listAdapter as CursorAdapter?
        adapter?.changeCursor(null)
    }

    /**
     * Make view models
     */
    private fun makeModels() {
        model = ViewModelProvider(this)["sources", SqlunetViewModel::class.java]
        model!!.getData().observe(getViewLifecycleOwner()) { cursor: Cursor? ->
            val adapter = (listAdapter as CursorAdapter?)!!
            adapter.changeCursor(cursor)
        }
    }
}
