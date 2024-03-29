/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.database.Cursor
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import org.sqlunet.browser.BaseListFragment

/**
 * A list fragment representing a table.
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class TableFragment : BaseListFragment() {

    /**
     * Make view binder
     *
     * @return ViewBinder
     */
    override fun makeViewBinder(): SimpleCursorAdapter.ViewBinder {
        return SimpleCursorAdapter.ViewBinder { view: View, cursor: Cursor, columnIndex: Int ->
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
                    } catch (nfe: NumberFormatException) {
                        view.setImageURI(Uri.parse(value))
                        return@ViewBinder true
                    }
                }

                else -> {
                    throw IllegalStateException(view.javaClass.getName() + " is not a view that can be bound by this SimpleCursorAdapter")
                }
            }
        }
    }
}