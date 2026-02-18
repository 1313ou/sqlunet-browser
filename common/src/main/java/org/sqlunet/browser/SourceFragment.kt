/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import org.sqlunet.browser.common.R
import org.sqlunet.provider.XNetContract
import org.sqlunet.provider.XSqlUNetProvider.Companion.makeUri

/**
 * A list fragment representing sources.
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SourceFragment : Fragment() {

    private lateinit var adapter: SourcesAdapter

    /**
     * View model
     */
    private var model: SqlunetViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SourcesAdapter(null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()

        // models
        makeModels() // sets cursor

        // load the contents
        val uri = makeUri(XNetContract.Sources.URI).toUri()
        val projection = arrayOf(XNetContract.Sources.ID + " AS _id", XNetContract.Sources.NAME, XNetContract.Sources.VERSION, XNetContract.Sources.URL, XNetContract.Sources.PROVIDER, XNetContract.Sources.REFERENCE)
        val sortOrder = XNetContract.Sources.ID
        model!!.loadData(uri, projection, null, null, sortOrder, null)
    }

    override fun onStop() {
        super.onStop()
        adapter.changeCursor(null)
    }

    /**
     * Make view models
     */
    private fun makeModels() {
        model = ViewModelProvider(this)["sources", SqlunetViewModel::class.java]
        model!!.getData().observe(viewLifecycleOwner) { cursor: Cursor? ->
            adapter.changeCursor(cursor)
        }
    }
}
