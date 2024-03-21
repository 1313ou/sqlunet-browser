/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.treeview.control

import android.content.Context
import android.view.View
import android.widget.TextView
import org.sqlunet.treeview.model.TreeNode

/**
 * Simple controller
 *
 * @author Bogdan Melnychuk on 2/11/15.
 */
class SimpleController : Controller<Any?>(false) {

    override fun createNodeView(context: Context, node: TreeNode, value: Any?, minHeight: Int): View {
        val textView = TextView(context)
        if (minHeight > 0) {
            textView.setMinimumHeight(minHeight)
        }
        textView.text = value.toString()
        return textView
    }
}
