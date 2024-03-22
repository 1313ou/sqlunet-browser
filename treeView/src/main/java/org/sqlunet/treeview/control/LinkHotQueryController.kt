/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
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
 * Link and hot query controller
 *
 * @param breakExpand    whether this controller breaks expansion
 * @param buttonImageRes image drawable id for button, 0 for default
 */
class LinkHotQueryController(breakExpand: Boolean, @DrawableRes buttonImageRes: Int) : HotQueryController(breakExpand) {

    /**
     * Link button image resource id, may be 0
     */
    private val buttonImageRes: Int

    init {
        layoutRes = R.layout.layout_query_link
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

    override fun fire() {
        followLink()
    }

    /**
     * Follow link
     */
    private fun followLink() {
        val value = node.value as CompositeValue?
        if (value != null) {
            assert(value.payload != null)
            val link = value.payload!![1] as Link
            link.process()
        }
    }
}