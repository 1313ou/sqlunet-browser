/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.treeview.control

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import org.sqlunet.treeview.R
import org.sqlunet.treeview.model.TreeNode

/**
 * Query controller (expanding this controller will trigger query)
 *
 * @param breakExpand whether this controller breaks expansion
 * @param buttonImageRes image drawable id for button, 0 for default
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class LinkQueryTreeController(breakExpand: Boolean, @param:DrawableRes private val buttonImageRes: Int) : ColdQueryTreeController(breakExpand) {

    override val layoutResId = R.layout.layout_tree_link

    override fun createNodeView(context: Context, node: TreeNode, minHeight: Int): View {
        val view = super.createNodeView(context, node, minHeight)
        // link button and listener
        val hotLink = view.findViewById<ImageView>(R.id.node_link)
        if (buttonImageRes != 0) {
            hotLink.setImageDrawable(AppCompatResources.getDrawable(context, buttonImageRes))
        }
        hotLink.setOnClickListener { followLink() }
        return view
    }

    /**
     * Follow link
     */
    private fun followLink() {
        if (node.payload != null) {
            val link = node.payload!![1] as Link
            link.process()
        }
    }
}
