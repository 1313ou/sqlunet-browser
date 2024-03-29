/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.treeview.control

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.sqlunet.treeview.model.TreeNode
import org.sqlunet.treeview.view.SubtreeView

/**
 * Base controller
 *
 * @property isBreakExpand whether this controller breaks expansion
 *
 * @author Bogdan Melnychuk on 2/10/15.
 */
abstract class Controller protected constructor(
    var isBreakExpand: Boolean,
) {

    // M O D E L

    /**
     * Node
     */
    protected lateinit var node: TreeNode

    // B E H A V I O U R

    /**
     * Ensure visible
     */
    private var ensureVisible = false

    // V I E W

    /**
     * View (wrapper subtreeView that includes label and children)
     */
    open var subtreeView: SubtreeView? = null

    /**
     * Node view (node label)
     */
    var nodeView: View? = null

    /**
     * Child nodes' container view
     */
    open var childrenView: ViewGroup? = null

    fun createView(context: Context, containerStyle: Int, treeIndent: Int, treeRowMinHeight: Int): SubtreeView? {

        // node view
        nodeView = createNodeView(context, node, treeRowMinHeight)

        // wrapper
        subtreeView = SubtreeView(context, containerStyle, treeIndent, nodeView!!)
        subtreeView!!.tag = node

        // children view
        childrenView = subtreeView!!.childrenContainer
        return subtreeView
    }

    // N O D E V I E W

    /**
     * Create node view
     *
     * @param context   context
     * @param node      node
     * @param minHeight min height
     * @return node view
     */
    open fun createNodeView(context: Context, node: TreeNode, minHeight: Int): View? {
        val textView = TextView(context)
        if (minHeight > 0) {
            textView.setMinimumHeight(minHeight)
        }
        // text
        textView.text = node.text

        // icon
        if (node.icon != null) {
            textView.setCompoundDrawablePadding(10)
            textView.setCompoundDrawablesWithIntrinsicBounds(node.icon!!, 0, 0, 0)
        }
        return textView
    }

    // I N I T

    /**
     * Whether subtreeView is initialized
     */
    val isInitialized: Boolean
        get() = subtreeView != null

    fun takeEnsureVisible(): Boolean {
        val result = ensureVisible
        ensureVisible = false
        return result
    }

    fun flagEnsureVisible() {
        ensureVisible = true
    }

    // M O D E L

    /**
     * Attach/Set node
     *
     * @param node node
     */
    fun attachNode(node: TreeNode) {
        this.node = node
    }

    // S T A T E

    /**
     * Disable
     */
    open fun deadend() {
        // empty
    }

    // F I R E

    /**
     * Fire
     */
    open fun fire() {
        // empty
    }

    // E V E N T   L I S T E N E R

    /**
     * Expand event notification
     */
    open fun onExpandEvent() {
        // empty
    }

    /**
     * Collapse event notification
     */
    open fun onCollapseEvent() {
        // empty
    }

    /**
     * Selection event
     *
     * @param selected selected flag
     */
    fun onSelectedEvent(selected: Boolean) {
        // empty
    }
}
