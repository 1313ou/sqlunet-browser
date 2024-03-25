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
class LinkQueryController(breakExpand: Boolean, @DrawableRes buttonImageRes: Int) : ColdQueryController(breakExpand) {

    /**
     * Link button image resource id, may be 0
     */
    private val buttonImageRes: Int

    init {
        layoutRes = R.layout.layout_tree_link
        this.buttonImageRes = buttonImageRes
    }

    override fun createNodeView(context: Context, node: TreeNode, value: CompositeValue?, minHeight: Int): View {
        val view = super.createNodeView(context, node, value, minHeight)

        // link button and listener
        val hotLink = view.findViewById<ImageView>(R.id.node_link)
        if (hotLink != null) {
            if (buttonImageRes != 0) {
                hotLink.setImageDrawable(AppCompatResources.getDrawable(context, buttonImageRes))
            }
            hotLink.setOnClickListener { followLink() }
        }
        return view
    }

    /**
     * Follow link
     */
    private fun followLink() {
        val value = node.value as CompositeValue?
        if (value != null) {
            val link = value.payload!![1] as Link
            link.process()
        }
    }
}
