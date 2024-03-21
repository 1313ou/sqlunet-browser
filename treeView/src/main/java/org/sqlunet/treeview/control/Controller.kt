/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.treeview.control

import android.content.Context
import android.view.View
import android.view.ViewGroup
import org.sqlunet.treeview.model.TreeNode
import org.sqlunet.treeview.view.SubtreeView

/**
 * Base controller
 *
 * * @param breakExpand whether this controller breaks expansion
 *
 * @author Bogdan Melnychuk on 2/10/15.
 */
abstract class Controller<E> protected constructor(
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
    @JvmField
    var nodeView: View? = null

    /**
     * Child nodes' container view
     */
    open var childrenView: ViewGroup? = null

    // V I E W

    fun createView(context: Context, containerStyle: Int, treeIndent: Int, treeRowMinHeight: Int): SubtreeView? {

        // node view
        nodeView = createNodeView(context, node, node.value as E?, treeRowMinHeight)
        assert(nodeView != null)

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
     * @param value     value
     * @param minHeight min height
     * @return node view
     */
    abstract fun createNodeView(context: Context, node: TreeNode, value: E?, minHeight: Int): View?

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
