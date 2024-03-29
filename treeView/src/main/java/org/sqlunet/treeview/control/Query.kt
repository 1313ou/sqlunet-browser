/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.treeview.control

import org.sqlunet.treeview.model.TreeNode

/**
 * Query data
 *
 * @param id id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class Query protected constructor(
    /**
     * Id used in query
     */
    @JvmField val id: Long,
) {

    /**
     * Process query
     *
     * @param node node
     */
    abstract fun process(node: TreeNode)
}
