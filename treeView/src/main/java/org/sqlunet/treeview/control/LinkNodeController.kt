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
import org.sqlunet.treeview.R
import org.sqlunet.treeview.model.TreeNode

/**
 * Link leaf controller
 *
 * @param breakExpand whether this controller breaks expansion
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class LinkNodeController(breakExpand: Boolean) : Controller<CompositeValue?>(breakExpand) {
    override fun createNodeView(context: Context, node: TreeNode, value: CompositeValue?, minHeight: Int): View {

        val inflater = LayoutInflater.from(context)
        @SuppressLint("InflateParams") val view = inflater.inflate(R.layout.layout_node_link, null, false)!!
        if (minHeight > 0) {
            view.setMinimumHeight(minHeight)
        }

        // icon + text
        value?.let {
            val iconView = view.findViewById<ImageView>(R.id.node_icon)
            iconView.setImageResource(value.icon)
            val valueView = view.findViewById<TextView>(R.id.node_value)
            valueView.text = it.text
        }

        // link listener
        val hotLink = view.findViewById<View>(R.id.node_link)
        hotLink?.setOnClickListener { followLink() }
        return view
    }

    /**
     * Follow link
     */
    private fun followLink() {
        val value = node.value as CompositeValue?
        if (value != null) {
            assert(value.payload != null)
            val link = value.payload!![0] as Link
            link.process()
        }
    }
}
