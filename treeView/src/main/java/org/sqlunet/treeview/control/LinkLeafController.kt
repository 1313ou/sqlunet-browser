/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.treeview.control

import android.content.Context
import android.view.View
import androidx.annotation.LayoutRes
import org.sqlunet.treeview.R
import org.sqlunet.treeview.model.TreeNode

/**
 * Link leaf controller
 *
 * @param breakExpand whether this controller breaks expansion
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class LinkLeafController(breakExpand: Boolean) : LeafController(breakExpand) {

    @LayoutRes
    override var layoutResId = R.layout.layout_leaf_link

    override fun createNodeView(context: Context, node: TreeNode, minHeight: Int): View {
        val view = super.createNodeView(context, node, minHeight)
        // link listener
        val hotLink = view.findViewById<View>(R.id.node_link)
        hotLink.setOnClickListener { followLink() }
        return view
    }

    override fun fire() {
        followLink()
    }

    /**
     * Follow link
     */
    private fun followLink() {
        if (node.payload != null) {
            val link = node.payload!![0] as Link
            link.process()
        }
    }
}
