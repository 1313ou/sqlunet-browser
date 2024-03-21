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
 * More controller with icon
 *
 * @param breakExpand whether this controller breaks expansion
 *
 * @author Bogdan Melnychuk on 2/12/15.
 */
class MoreController(breakExpand: Boolean) : Controller<CompositeValue?>(breakExpand) {

    /**
     * Resource used (changed by derived classes)
     */
    private val layoutRes = R.layout.layout_more

    override fun createNodeView(context: Context, node: TreeNode, value: CompositeValue?, minHeight: Int): View {

        val inflater = LayoutInflater.from(context)
        @SuppressLint("InflateParams") val view = inflater.inflate(layoutRes, null, false)!!
        if (minHeight > 0) {
            view.setMinimumHeight(minHeight)
        }

        // junction
        // val junctionView: ImageView = view.findViewById(R.id.junction_icon)

        // icon + text
        value?.let {
            val iconView = view.findViewById<ImageView>(R.id.node_icon)
            iconView.setImageResource(it.icon)
            val valueView = view.findViewById<TextView>(R.id.node_value)
            valueView.text = it.text
        }
        return view
    }
}
