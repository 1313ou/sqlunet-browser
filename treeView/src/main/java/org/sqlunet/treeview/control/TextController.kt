/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.treeview.control

import android.content.Context
import android.view.View
import android.widget.TextView
import org.sqlunet.treeview.model.TreeNode

/**
 * Text controller
 *
 * @param breakExpand whether this controller breaks expansion

 * @author Bogdan Melnychuk on 2/11/15.
 */
class TextController(breakExpand: Boolean) : Controller<Any?>(breakExpand) {

    override fun createNodeView(context: Context, node: TreeNode, value: Any?, minHeight: Int): View {
        val textView = TextView(context)
        if (minHeight > 0) {
            textView.setMinimumHeight(minHeight)
        }
        when (value) {

            is CompositeValue -> {
                textView.text = value.text
                textView.setCompoundDrawablePadding(10)
                textView.setCompoundDrawablesWithIntrinsicBounds(value.icon, 0, 0, 0)
            }

            is CharSequence -> {
                textView.text = value
            }

            else -> {
                throw IllegalArgumentException(value.toString())
            }
        }
        return textView
    }
}
