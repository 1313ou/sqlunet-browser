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
    private var cursor: Cursor?,
    from: Array<String>,
    private val to: IntArray
) : RecyclerView.Adapter<BaseRecyclerAdapter.ViewHolder>() {

    private var viewBinder: ViewBinder? = null
    private val cursorHelper: CursorHelper = CursorHelper(cursor, from)

    /**
     * View binder
     */
    interface ViewBinder {
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
        cursorHelper.changeCursor(newCursor)
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
        if (!cursorHelper.isValid(position)) {
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
        val count = to.size
        val from = cursorHelper.from
        val to = to
        for (i in 0 until count) {
            val v = view.findViewById<View>(to[i])
            if (v != null) {
                var bound = false
                if (viewBinder != null) {
                    bound = viewBinder!!.setViewValue(v, cursor, from[i])
                }
                if (!bound) {
                    var text = cursor.getString(from[i])
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
     * View holder
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    /**
     * Cursor helper
     *
     * @author [Bernard Bou](mailto:1313ou@gmail.com)
     */
    class CursorHelper(private var cursor: Cursor?, private val fromColumns: Array<String>) {

        /**
         * Column indexes
         */
        val from: IntArray

        /**
         * Constructor
         */
        init {
            from = fromColumns(fromColumns)
        }

        /**
         * Get from-columns
         *
         * @param fromColumns from columns (names)
         * @return from columns (indexes)
         */
        private fun fromColumns(fromColumns: Array<String>): IntArray {
            val from = IntArray(fromColumns.size)
            if (cursor != null) {
                for (i in fromColumns.indices) {
                    from[i] = cursor!!.getColumnIndexOrThrow(fromColumns[i])
                }
            }
            return from
        }

        /**
         * Is cursor valid at this position
         *
         * @param position position
         * @return true if cursor is valid at this position
         */
        fun isValid(position: Int): Boolean {
            return cursor != null && cursor!!.moveToPosition(position)
        }

        /**
         * Change cursor
         *
         * @param cursor new cursor
         */
        fun changeCursor(cursor: Cursor?) {
            this.cursor = cursor
        }
    }
}

