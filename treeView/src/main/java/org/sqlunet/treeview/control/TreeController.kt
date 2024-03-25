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
import org.sqlunet.treeview.R
import org.sqlunet.treeview.model.TreeNode

/**
 * Tree controller with icon
 *
 * @param breakExpand whether this controller breaks expansion
 *
 * @author Bogdan Melnychuk on 2/12/15.
 */
open class TreeController(breakExpand: Boolean) : Controller<CompositeValue?>(breakExpand) {

    /**
     * Junction view
     */
    @JvmField
    protected var junctionView: ImageView? = null

    /**
     * Resource used (changed by derived classes)
     */
    @JvmField
    var layoutRes = R.layout.layout_tree

    override fun createNodeView(context: Context, node: TreeNode, value: CompositeValue?, minHeight: Int): View {
        val inflater = LayoutInflater.from(context)
        @SuppressLint("InflateParams") val view = inflater.inflate(layoutRes, null, false)!!
        if (minHeight > 0) {
            view.setMinimumHeight(minHeight)
        }

        // junction icon (arrow)
        junctionView = view.findViewById(R.id.junction_icon)
        if (node.isDeadend) {
            markDeadend()
        } else {
            markCollapsed()
            // junctionView!!.setImageResource(R.drawable.ic_collapsed)
        }

        // icon + text
        value?.let {
            if (value.icon != 0) {
                val iconView = view.findViewById<ImageView>(R.id.node_icon)
                iconView.setImageResource(it.icon)
            }
            val valueView = view.findViewById<TextView>(R.id.node_value)
            valueView.text = it.text
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
        junctionView!!.setImageResource(R.drawable.ic_expanded)
    }

    protected open fun markCollapsed() {
        junctionView!!.setImageResource(R.drawable.ic_collapsed)
    }

    protected open fun markDeadend() {
        junctionView!!.setImageResource(R.drawable.ic_deadend)
    }
}
