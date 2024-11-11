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
 * Query controller
 *
 * @param breakExpand whether this controller breaks expansion
 */
open class QueryController(breakExpand: Boolean) : BaseResController(breakExpand) {

    @LayoutRes
    override val layoutResId = R.layout.layout_node_query

    private var processed = false

    override fun createNodeView(context: Context, node: TreeNode, minHeight: Int): View {
        val view = super.createNodeView(context, node, minHeight)
        // link listener
        val hotLink = view.findViewById<View>(R.id.node_link)
        hotLink.setOnClickListener { followLink() }
        return view
    }

    /**
     * Add data to tree by launching the query
     */
    @Synchronized
    fun processQuery() {
        if (!processed) {
            processed = true
            val query = query!!
            query.process(node)
        }
    }

    private val query: Query?
        get() {
            if (node.payload != null) {
                return node.payload!![0] as Query
            }
            return null
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
