/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.SimpleCursorAdapter
import androidx.fragment.app.ListFragment
import androidx.lifecycle.ViewModelProvider
import org.sqlunet.browser.common.R
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.sql.Utils.join
import java.util.Collections

/**
 * A list fragment representing a table.
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class BaseListFragment : ListFragment() {

    /**
     * View model
     */
    private var model: SqlunetViewModel? = null

    /**
     * View binder factory
     *
     * @return view binder
     */
    protected abstract fun makeViewBinder(): SimpleCursorAdapter.ViewBinder?

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
        return inflater.inflate(R.layout.fragment_table, container, false)
    }

    override fun onStart() {
        super.onStart()

        // models
        makeModels() // sets cursor

        // args
        val args = requireArguments()

        // query params
        val uriString = args.getString(ProviderArgs.ARG_QUERYURI)
        val uri = Uri.parse(uriString)
        val id = args.getString(ProviderArgs.ARG_QUERYID)
        val items = args.getStringArray(ProviderArgs.ARG_QUERYITEMS)
        val hiddenItems = args.getStringArray(ProviderArgs.ARG_QUERYHIDDENITEMS)
        val sortOrder = args.getString(ProviderArgs.ARG_QUERYSORT)
        val selection = args.getString(ProviderArgs.ARG_QUERYFILTER)
        val queryArg = args.getString(ProviderArgs.ARG_QUERYARG)
        val layoutId = args.getInt(ProviderArgs.ARG_QUERYLAYOUT)
        // var database = args.getString(ProviderArgs.ARG_QUERYDATABASE)

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

        // make cursor adapter
        val adapter = SimpleCursorAdapter(
            requireContext(), layoutId, null,
            from,
            to, 0
        )
        adapter.viewBinder = makeViewBinder()
        setListAdapter(adapter)

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
        model!!.loadData(uri, projection, selection, selectionArgs, sortOrder, null)
    }

    override fun onStop() {
        super.onStop()
        val listView = getListView()
        val adapter = listAdapter as CursorAdapter?
        Log.d(TAG, "Nullify listview adapter. Lifecycle: onStop()")
        listView.setAdapter(null)
        // the cursor will be saved along with fragment state if any
        Log.d(TAG, "Nullify adapter cursor but do not close cursor. Lifecycle: onStop()")
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
        model = ViewModelProvider(this)["elements", SqlunetViewModel::class.java]
        model!!.getData().observe(getViewLifecycleOwner()) { cursor: Cursor? ->
            val adapter = (listAdapter as CursorAdapter?)!!
            adapter.changeCursor(cursor)
        }
    }

    /**
     * Dump cursor
     *
     * @param cursor cursor
     */
    fun dump(cursor: Cursor?) {
        if (cursor == null) {
            Log.d(TAG, "null cursor")
            return
        }

        // column names
        val n = cursor.columnCount
        val cols = cursor.columnNames
        val position = cursor.position
        if (cursor.moveToFirst()) {
            do {
                // all columns in row
                for (i in 0 until n) {
                    val value: String = when (cursor.getType(i)) {
                        Cursor.FIELD_TYPE_NULL -> "null"
                        Cursor.FIELD_TYPE_INTEGER -> cursor.getInt(i).toString()
                        Cursor.FIELD_TYPE_FLOAT -> cursor.getFloat(i).toString()
                        Cursor.FIELD_TYPE_STRING -> cursor.getString(i)
                        Cursor.FIELD_TYPE_BLOB -> "blob"
                        else -> "NA"
                    }
                    Log.d(TAG, "column " + i + " " + cols[i] + "=" + value)
                }
            } while (cursor.moveToNext())

            // reset
            cursor.moveToPosition(position)
        }
    }

    companion object {

        private const val TAG = "BaseListF"
        private const val VERBOSE = true
    }
}