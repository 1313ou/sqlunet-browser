/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.treeview.control

import androidx.annotation.LayoutRes
import org.sqlunet.treeview.R

/**
 * Hot Query controller (expanding this controller will trigger query)
 *
 * @param breakExpand whether this controller breaks expansion
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class HotQueryController(breakExpand: Boolean) : QueryController(breakExpand) {

    @LayoutRes
    override val layoutResId = R.layout.layout_node_query
}
