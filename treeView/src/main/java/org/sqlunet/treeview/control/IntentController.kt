/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.treeview.control

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import org.sqlunet.treeview.R
import org.sqlunet.treeview.model.TreeNode

/**
 * Link leaf controller
 *
 * @param breakExpand whether this controller breaks expansion
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class IntentController(breakExpand: Boolean) : BaseController(breakExpand) {

    @LayoutRes
    override val layoutResId = R.layout.layout_node_link

    override fun createNodeView(context: Context, node: TreeNode, minHeight: Int): View {
        val view = super.createNodeView(context, node, minHeight)
        // link listener
        val link = view.findViewById<View>(R.id.node_link)
        link.setOnClickListener { followLink(context) }
        return view
    }

    /**
     * Follow link
     */
    private fun followLink(context: Context) {
        if (node.payload != null) {
            val intent = node.payload!![0] as Intent
            val data = intent.getStringExtra("data")
            Toast.makeText(context, "$data clicked!", Toast.LENGTH_SHORT).show()
            context.startActivity(intent)
        }
    }
}
