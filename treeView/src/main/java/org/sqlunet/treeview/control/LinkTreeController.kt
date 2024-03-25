/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.treeview.control

import android.content.Context
import android.view.View
import org.sqlunet.treeview.R
import org.sqlunet.treeview.model.TreeNode

/**
 * Link tree controller
 *
 * @param breakExpand whether this controller breaks expansion
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class LinkTreeController(breakExpand: Boolean) : TreeController(breakExpand) {

    init {
        layoutRes = R.layout.layout_tree_link
    }

    override fun createNodeView(context: Context, node: TreeNode, value: CompositeValue?, minHeight: Int): View {
        val view = super.createNodeView(context, node, value, minHeight)

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
            val link = value.payload!![0] as Link
            link.process()
        }
    }
}
