/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.treeview.control

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.CallSuper
import org.sqlunet.treeview.R
import org.sqlunet.treeview.model.TreeNode

/**
 * Link leaf controller
 *
 * @param breakExpand whether this controller breaks expansion
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class BaseResController(breakExpand: Boolean) : Controller(breakExpand) {
    abstract val layoutResId: Int

    @CallSuper
    override fun createNodeView(context: Context, node: TreeNode, minHeight: Int): View {
        val inflater = LayoutInflater.from(context)
        @SuppressLint("InflateParams") val view = inflater.inflate(layoutResId, null, false)!!
        if (minHeight > 0) {
            view.setMinimumHeight(minHeight)
        }
        // text
        val valueView = view.findViewById<TextView>(R.id.node_value)
        valueView.text = node.text
        // icon
        if (node.icon != null) {
            val iconView = view.findViewById<ImageView>(R.id.node_icon)
            iconView.setImageResource(node.icon!!)
        }
        return view
    }
}
