/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.treeview.control

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.content.res.AppCompatResources
import org.sqlunet.treeview.R
import org.sqlunet.treeview.model.TreeNode

/**
 * Link and hot query controller
 *
 * @param breakExpand    whether this controller breaks expansion
 * @param buttonImageRes image drawable id for button, 0 for default
 */
class LinkHotQueryTreeController(breakExpand: Boolean, @param:DrawableRes private val buttonImageRes: Int) : HotQueryTreeController(breakExpand) {

    @LayoutRes
    override val layoutResId = R.layout.layout_query_link

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

    override fun fire() {
        followLink()
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
