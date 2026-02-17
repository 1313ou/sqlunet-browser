/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser

import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.sqlunet.browser.common.R
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.sql.Utils.join
import java.util.Collections

/**
 * A recycler fragment representing a table.
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class BaseRecyclerFragment : Fragment() {

    /**
     * View model
     */
    private var model: SqlunetViewModel? = null

    /**
     * Recycler view
     */
    private var recyclerView: RecyclerView? = null

    /**
     * Recycler adapter
     */
    private var recyclerAdapter: BaseRecyclerAdapter? = null

    /**
     * View binder factory
     *
     * @return view binder
     */
    protected abstract fun makeViewBinder(): BaseRecyclerAdapter.ViewBinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // query
        if (VERBOSE) {
            // args
            val args = requireArguments()
            val queryArg = args.getString(ProviderArgs.ARG_QUERYARG)
            val uriString = args.getString(ProviderArgs.ARG_QUERYURI)
            val selection = args.getString(ProviderArgs.ARG_QUERYFILTER)
            Log.d(TAG, String.format("%s (filter: %s)(arg=%s)", uriString, selection, queryArg))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // recycler view
        recyclerView = view.findViewById(R.id.list)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
    }

    override fun onStart() {
        super.onStart()

        // models
        makeModels() // sets cursor

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
        val layoutId = args.getInt(ProviderArgs.ARG_QUERYLAYOUT)

        // make cursor adapter
        val (items2, viewIds) = compute(items, hiddenItems)
        recyclerAdapter = BaseRecyclerAdapter(requireContext(), layoutId, null, items2, viewIds)
        recyclerAdapter!!.setViewBinder(makeViewBinder())
        recyclerView!!.adapter = recyclerAdapter

        // load the contents

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
        model!!.loadData(uri!!, projection, selection, selectionArgs, sortOrder, null)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "Nullify adapter. Lifecycle: onStop()")
        recyclerView!!.adapter = null
        // the cursor will be saved along with fragment state if any
        Log.d(TAG, "Nullify adapter cursor but do not close cursor. Lifecycle: onStop()")
        recyclerAdapter!!.changeCursor(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        recyclerAdapter?.cursor = null
    }

    /**
     * Make view models
     */
    private fun makeModels() {
        model = ViewModelProvider(this)["elements", SqlunetViewModel::class.java]
        model!!.getData().observe(getViewLifecycleOwner()) { cursor: Cursor? ->
            recyclerAdapter!!.changeCursor(cursor)
        }
    }

    companion object {

        private fun compute(items: Array<String>?, hiddenItems: Array<String>?): Pair<Array<String>, IntArray> {
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

            // to (view ids)
            val toList: MutableCollection<Int> = ArrayList()
            val nItems = items?.size ?: 0
            val nXItems = hiddenItems?.size ?: 0
            val resIds = intArrayOf(R.id.item0, R.id.item1, R.id.item2)
            run {
                var i = 0
                while (i < nItems + nXItems && i < resIds.size) {
                    toList.add(resIds[i])
                    i++
                }
            }
            val to = IntArray(toList.size)
            var i = 0
            for (n in toList) {
                to[i++] = n
            }
            Log.d("$TAG To", "" + join(*to))

            return from to to
        }

        private const val TAG = "BaseRecyclerF"

        private const val VERBOSE = true
    }
}
