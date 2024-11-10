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
open class QueryController(breakExpand: Boolean) : BaseResController(breakExpand) {

    @LayoutRes
    override val layoutResId = R.layout.layout_node_query

    private var processed = false

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
