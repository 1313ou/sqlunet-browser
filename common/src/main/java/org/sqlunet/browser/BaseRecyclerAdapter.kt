/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Recycler adapter
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class BaseRecyclerAdapter(
    private val context: Context,
    private val layout: Int,
    var cursor: Cursor?,
    private val fromColumns: Array<String>,
    private val viewIds: IntArray
) : RecyclerView.Adapter<BaseRecyclerAdapter.ViewHolder>() {

    private var viewBinder: ViewBinder? = null

    /**
     * View binder
     */
    fun interface ViewBinder {

        /**
         * Binds the Cursor to the view.
         *
         * @param view    view to bind to
         * @param cursor  the cursor to get the data from
         * @param from    the column index to get the data from
         * @return true if the data was bound to the view, false otherwise
         */
        fun setViewValue(view: View, cursor: Cursor, from: Int): Boolean
    }

    /**
     * Set view binder
     *
     * @param viewBinder view binder
     */
    fun setViewBinder(viewBinder: ViewBinder?) {
        this.viewBinder = viewBinder
    }

    /**
     * Swap cursor
     *
     * @param newCursor new cursor
     * @return old cursor
     */
    fun swapCursor(newCursor: Cursor?): Cursor? {
        if (newCursor === cursor) {
            return null
        }
        val oldCursor = cursor
        cursor = newCursor
        if (newCursor != null) {
            notifyDataSetChanged()
        }
        return oldCursor
    }

    /**
     * Change cursor
     *
     * @param newCursor new cursor
     */
    fun changeCursor(newCursor: Cursor?) {
        swapCursor(newCursor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (cursor == null || cursor!!.moveToPosition(position)) {
            throw IllegalStateException("couldn't move cursor to position $position")
        }
        bindView(holder.itemView, cursor!!)
    }

    /**
     * Bind view
     *
     * @param view    view
     * @param cursor  cursor
     */
    private fun bindView(view: View, cursor: Cursor) {
        val count = viewIds.size
        val colIndexes: IntArray = columnsToColumnIndexes(fromColumns, cursor)
        for (i in 0 until count) {
            val v = view.findViewById<View>(viewIds[i])
            if (v != null) {
                var bound = false
                if (viewBinder != null) {
                    bound = viewBinder!!.setViewValue(v, cursor, colIndexes[i])
                }
                if (!bound) {
                    var text = cursor.getString(colIndexes[i])
                    if (text == null) {
                        text = ""
                    }
                    if (v is TextView) {
                        v.text = text
                    } else {
                        throw IllegalStateException(v.javaClass.name + " is not a view that can be bounds by this SimpleCursorAdapter")
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return cursor?.count ?: 0
    }

    /**
     * Get from-columns
     *
     * @param fromColumns from columns (names)
     * @param cursor cursor
     * @return from columns (indexes)
     */
    private fun columnsToColumnIndexes(fromColumns: Array<String>, cursor: Cursor): IntArray {
        val colIndexes = IntArray(fromColumns.size)
        for (i in fromColumns.indices) {
            colIndexes[i] = cursor.getColumnIndexOrThrow(fromColumns[i])
        }
        return colIndexes
    }

    /**
     * View holder
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

