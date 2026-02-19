/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

        adapter = SourcesAdapter()
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

    inner class SourcesAdapter : RecyclerView.Adapter<SourcesAdapter.ViewHolder>() {

        private var cursor: Cursor? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_source, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (cursor?.moveToPosition(position) == true) {
                holder.bind(cursor!!)
            }
        }

        override fun getItemCount(): Int {
            return cursor?.count ?: 0
        }

        @SuppressLint("NotifyDataSetChanged")
        fun changeCursor(newCursor: Cursor?) {
            if (cursor != newCursor) {
                val oldCursor = cursor
                cursor = newCursor
                notifyDataSetChanged()
                oldCursor?.close()
            }
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val nameView: TextView = itemView.findViewById(R.id.name)
            private val versionView: TextView = itemView.findViewById(R.id.version)
            private val urlView: TextView = itemView.findViewById(R.id.url)
            private val providerView: TextView = itemView.findViewById(R.id.provider)
            private val referenceView: TextView = itemView.findViewById(R.id.reference)

            fun bind(cursor: Cursor) {
                val nameIdx = cursor.getColumnIndex(XNetContract.Sources.NAME)
                val versionIdx = cursor.getColumnIndex(XNetContract.Sources.VERSION)
                val urlIdx = cursor.getColumnIndex(XNetContract.Sources.URL)
                val providerIdx = cursor.getColumnIndex(XNetContract.Sources.PROVIDER)
                val referenceIdx = cursor.getColumnIndex(XNetContract.Sources.REFERENCE)

                if (nameIdx != -1) {
                    nameView.text = cursor.getString(nameIdx)
                }
                if (versionIdx != -1) {
                    versionView.text = cursor.getString(versionIdx)
                }
                if (urlIdx != -1) {
                    urlView.text = cursor.getString(urlIdx)
                }
                if (providerIdx != -1) {
                    providerView.text = cursor.getString(providerIdx)
                }
                if (referenceIdx != -1) {
                    referenceView.text = cursor.getString(referenceIdx)
                }
            }
        }
    }
}
