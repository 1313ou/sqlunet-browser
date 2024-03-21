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
 * Leaf controller with icon
 *
 * @param breakExpand whether this controller breaks expansion
 *
 * @author Bogdan Melnychuk on 2/12/15.
 */
open class LeafController(breakExpand: Boolean) : Controller<CompositeValue?>(breakExpand) {

    /**
     * Resource used (changed by derived classes)
     */
    @JvmField
    protected var layoutRes = R.layout.layout_leaf

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
            iconView.setImageResource(value.icon)
            val valueView = view.findViewById<TextView>(R.id.node_value)
            valueView.text = value.text
        }
        return view
    }
}
