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
 * Tree controller with icon
 *
 * @param breakExpand whether this controller breaks expansion
 *
 * @author Bogdan Melnychuk on 2/12/15.
 */
open class TreeController(breakExpand: Boolean) : BaseResController(breakExpand) {
    @LayoutRes
    override val layoutResId = R.layout.layout_tree

    /**
     * Junction view
     */
    protected lateinit var junctionView: ImageView

    override fun createNodeView(context: Context, node: TreeNode, minHeight: Int): View {
        val view = super.createNodeView(context, node, minHeight)
        // junction icon (arrow)
        junctionView = view.findViewById(R.id.junction_icon)
        if (node.isDeadend) {
            markDeadend()
        } else {
            markCollapsed()
            // junctionView!!.setImageResource(R.drawable.ic_collapsed)
        }
        return view
    }

    override fun onExpandEvent() {
        if (node.isDeadend) {
            markDeadend()
        } else {
            markExpanded()
        }
    }

    override fun onCollapseEvent() {
        if (node.isDeadend) {
            markDeadend()
        } else {
            markCollapsed()
        }
    }

    override fun deadend() {
        markDeadend()
    }

    protected open fun markExpanded() {
        junctionView.setImageResource(R.drawable.ic_expanded)
    }

    protected open fun markCollapsed() {
        junctionView.setImageResource(R.drawable.ic_collapsed)
    }

    protected open fun markDeadend() {
        junctionView.setImageResource(R.drawable.ic_deadend)
    }
}
