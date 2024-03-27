/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.treeview.control

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import org.sqlunet.treeview.R
import org.sqlunet.treeview.model.TreeNode

/**
 * More controller with icon
 *
 * @param breakExpand whether this controller breaks expansion
 *
 * @author Bogdan Melnychuk on 2/12/15.
 */
class MoreController(breakExpand: Boolean) : BaseResController(breakExpand) {
    @LayoutRes override val layoutResId = R.layout.layout_node_link
}
