/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.database.Cursor
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import org.sqlunet.browser.BaseRecyclerAdapter
import org.sqlunet.browser.BaseRecyclerFragment

/**
 * A list fragment representing a table.
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class TableFragment : BaseRecyclerFragment() {

    /**
     * Make view binder
     *
     * @return ViewBinder
     */
    override fun makeViewBinder(): BaseRecyclerAdapter.ViewBinder {

        return BaseRecyclerAdapter.ViewBinder { view: View, cursor: Cursor, columnIndex: Int ->
            var value = cursor.getString(columnIndex)
            if (value == null) {
                value = ""
            }
            when (view) {
                is TextView -> {
                    view.text = value
                    return@ViewBinder true
                }

                is ImageView -> {
                    try {
                        view.setImageResource(value.toInt())
                        return@ViewBinder true
                    } catch (_: NumberFormatException) {
                        view.setImageURI(value.toUri())
                        return@ViewBinder true
                    }
                }

                else -> {
                    throw IllegalStateException(view.javaClass.name + " is not a view that can be bound by this SimpleCursorAdapter")
                }
            }
        }
    }
}