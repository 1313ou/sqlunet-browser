/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.treeview.control

import android.content.Context
import android.view.View
import android.view.ViewGroup
import org.sqlunet.treeview.model.TreeNode
import org.sqlunet.treeview.view.SubtreeView

class RootController : Controller<Void?>(false) {

    override var childrenView: ViewGroup? = null
        set(childrenView) {
            super.childrenView = childrenView
            field = childrenView
        }

    override fun createNodeView(context: Context, node: TreeNode, value: Void?, minHeight: Int): View? {
        return null
    }

    override var subtreeView: SubtreeView?
        get() = if (childrenView != null && childrenView!!.childCount > 0) {
            childrenView!!.getChildAt(0) as SubtreeView
        } else null
        set(subtreeView) {
            super.subtreeView = subtreeView
        }

    fun setContentView(contentView: ViewGroup?) {
        childrenView = contentView
    }
}
