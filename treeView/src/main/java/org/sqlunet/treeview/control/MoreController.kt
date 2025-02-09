/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.treeview.control

import androidx.annotation.LayoutRes
import org.sqlunet.treeview.R

/**
 * More controller with icon
 *
 * @param breakExpand whether this controller breaks expansion
 *
 * @author Bogdan Melnychuk on 2/12/15.
 */
class MoreController(breakExpand: Boolean) : BaseController(breakExpand) {

    @LayoutRes
    override val layoutResId = R.layout.layout_node_link
}
