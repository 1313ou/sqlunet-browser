/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.treeview.control

import androidx.annotation.LayoutRes
import org.sqlunet.treeview.R

/**
 * Query controller
 *
 * @param breakExpand whether this controller breaks expansion
 */
open class QueryController(breakExpand: Boolean) : TreeController(breakExpand) {

    @LayoutRes
    override val layoutResId = R.layout.layout_query

    private var processed = false

    override fun markExpanded() {
        junctionView.setImageResource(R.drawable.ic_query_expanded)
    }

    override fun markCollapsed() {
        junctionView.setImageResource(R.drawable.ic_query_collapsed)
    }

    override fun markDeadend() {
        junctionView.setImageResource(R.drawable.ic_query_deadend)
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
}
