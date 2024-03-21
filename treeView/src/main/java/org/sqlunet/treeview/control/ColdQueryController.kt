/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.treeview.control

/**
 * Query controller (expanding this controller will trigger query)
 *
 * @param breakExpand whether this controller breaks expansion
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class ColdQueryController(breakExpand: Boolean) : QueryController(breakExpand) {

    override fun fire() {
        if (node.isLeaf) {
            processQuery()
        }
    }
}
