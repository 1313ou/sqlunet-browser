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
 * Hot Query controller (expanding this controller will trigger query)
 *
 * @param breakExpand whether this controller breaks expansion
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class HotQueryLinkController(breakExpand: Boolean) : HotQueryController(breakExpand) {

    @LayoutRes
    override val layoutResId = R.layout.layout_node_query_link

    override fun createNodeView(context: Context, node: TreeNode, minHeight: Int): View {
        val view = super.createNodeView(context, node, minHeight)
        // link listener
        val hotLink = view.findViewById<View>(R.id.node_link)
        hotLink.setOnClickListener { followLink() }
        return view
    }

    /**
     * Follow link
     */
    private fun followLink() {
        if (node.payload != null && node.payload!![1] != null) {
            // if cast is successful, call the function
            @Suppress("UNCHECKED_CAST")
            (node.payload!![1] as? () -> Unit)?.let {
                it()
            }
        }
    }
}
