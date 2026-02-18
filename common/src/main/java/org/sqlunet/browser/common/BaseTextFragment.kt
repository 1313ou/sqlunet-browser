/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.common

import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.RecyclerView
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.sql.Utils.join
import java.util.Collections

abstract class BaseTextFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    protected lateinit var query: String

    protected lateinit var adapter: TextAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = requireArguments()
        val queryArg = args.getString(ProviderArgs.ARG_QUERYARG)
        query = queryArg?.trim { it <= ' ' } ?: ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = requireArguments()
        val layoutId = args.getInt(ProviderArgs.ARG_QUERYLAYOUT)

        adapter = makeAdapter(layoutId)
        val recyclerView = view.findViewById<RecyclerView>(R.id.texts_list)
        recyclerView.adapter = adapter

        LoaderManager.getInstance(this).initLoader(0, null, this)
    }

    protected abstract fun makeAdapter(layoutId: Int?): TextAdapter

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {

        // args
        val args = requireArguments()

        // query params
        val uriString = args.getString(ProviderArgs.ARG_QUERYURI)
        val uri = uriString?.toUri()
        val id = args.getString(ProviderArgs.ARG_QUERYID)
        val items = args.getStringArray(ProviderArgs.ARG_QUERYITEMS)
        val hiddenItems = args.getStringArray(ProviderArgs.ARG_QUERYHIDDENITEMS)
        val sortOrder = args.getString(ProviderArgs.ARG_QUERYSORT)
        val selection = args.getString(ProviderArgs.ARG_QUERYFILTER)
        val queryArg = args.getString(ProviderArgs.ARG_QUERYARG)

        // adapter set up
        // from (database column names)
        val fromList: MutableList<String> = ArrayList()
        if (items != null) {
            for (item in items) {
                var col = item

                // remove alias
                val asIndex = col.lastIndexOf(" AS ")
                if (asIndex != -1) {
                    col = col.substring(asIndex + 4)
                }
                fromList.add(col)
            }
        }
        val from = fromList.toTypedArray<String>()
        Log.d("$TAG From", join(*from))

        // make projection
        val cols: MutableList<String> = ArrayList()

        // add _id alias for first column
        cols.add("$id AS _id")

        // add items
        if (items != null) {
            Collections.addAll(cols, *items)
        }

        // add hidden items
        if (hiddenItems != null) {
            Collections.addAll(cols, *hiddenItems)
        }
        val projection = cols.toTypedArray<String>()
        val selectionArgs = if (queryArg == null) null else arrayOf(queryArg)

        return CursorLoader(requireContext(), uri!!, projection, selection, selectionArgs, sortOrder)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        adapter.changeCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        adapter.changeCursor(null)
    }

    companion object {

        const val TAG = "BaseTextF"
    }
}