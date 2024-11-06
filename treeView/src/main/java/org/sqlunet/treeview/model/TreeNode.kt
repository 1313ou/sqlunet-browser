/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.treeview.model

import androidx.annotation.DrawableRes
import org.sqlunet.treeview.control.Controller

/**
 * Tree node
 *
 * @author Bogdan Melnychuk on 2/10/15.
 */
class TreeNode(
    var text: CharSequence?,
    @DrawableRes var icon: Int? = null,
    val payload: Array<out Any?>? = null,
    var controller: Controller,
    var isCollapsible: Boolean,
) {

    /**
     * Id
     */
    private var id = 0

    /**
     * Parent node
     */
    var parent: TreeNode? = null
        private set

    /**
     * Children nodes
     */
    val children: MutableList<TreeNode> = ArrayList()

    /**
     * Click listener
     */
    var clickListener: TreeNodeClickListener? = null
        private set

    /**
     * Whether this node is enabled
     */
    var isEnabled = true

    /**
     * Selected
     */
    private var selected = false

    /**
     * Whether this node is selectable
     */
    var isSelectable = false

    /**
     * Whether this node is deadend
     */
    var isDeadend = false

    // C O N S T R U C T O R S

    constructor(controller: Controller) : this(null, null, null, controller, true) // root

    constructor(text: CharSequence, controller: Controller, collapsible: Boolean) : this(text, null, null, controller, collapsible)

    init {
        controller.attachNode(this)
    }

    // T R E E

    /**
     * Add this node to parent node
     *
     * @param parentNode parent node
     * @return this node
     */
    fun addTo(parentNode: TreeNode): TreeNode {
        parentNode.addChild(this)
        return this
    }

    /**
     * Add child node
     *
     * @param childNode child node
     * @return this node
     */
    fun addChild(childNode: TreeNode): TreeNode {
        childNode.parent = this
        childNode.id = size()
        children.add(childNode)
        return this
    }

    /**
     * Add children nodes
     *
     * @param nodes children nodes
     * @return this node
     */
    fun addChildren(vararg nodes: TreeNode): TreeNode {
        for (node in nodes) {
            addChild(node)
        }
        return this
    }

    /**
     * Add children
     *
     * @param nodes children node iteration
     * @return this node
     */
    fun addChildren(nodes: Iterable<TreeNode>): TreeNode {
        for (node in nodes) {
            addChild(node)
        }
        return this
    }

    /**
     * Prepend this node to parent node
     *
     * @param parentNode parent node
     * @return this node
     */
    fun prependTo(parentNode: TreeNode): TreeNode {
        parentNode.prependChild(this)
        return this
    }

    /**
     * Prepend child node
     *
     * @param childNode child node
     * @return this node
     */
    private fun prependChild(childNode: TreeNode): TreeNode {
        childNode.parent = this
        childNode.id = size()
        children.add(0, childNode)
        return this
    }

    /**
     * Prepend children nodes
     *
     * @param nodes children nodes
     * @return this node
     */
    fun prependChildren(vararg nodes: TreeNode): TreeNode {
        for (i in nodes.indices.reversed()) {
            val node = nodes[i]
            prependChild(node)
        }
        return this
    }

    /**
     * Self-delete
     *
     * @return true if deleted
     */
    fun delete(): Boolean {
        val parent = parent
        return if (parent != null) {
            parent.deleteChild(this) != -1
        } else false
    }

    /**
     * Delete child
     *
     * @param child child to delete
     * @return child indexOf
     */
    fun deleteChild(child: TreeNode): Int {
        for (i in children.indices) {
            if (child.id == children[i].id) {
                children.removeAt(i)
                child.parent = null
                return i
            }
        }
        return -1
    }

    /**
     * Number of children
     *
     * @return Number of children
     */
    private fun size(): Int {
        return children.size
    }

    /**
     * Index of child
     *
     * @param child child
     * @return child indexOf
     */
    fun indexOf(child: TreeNode): Int {
        for (i in children.indices) {
            if (child.id == children[i].id) {
                return i
            }
        }
        return -1
    }

    /**
     * Root
     */
    val root: TreeNode
        get() {
            var root: TreeNode? = this
            while (root!!.parent != null) {
                root = root.parent
            }
            return root
        }

    /**
     * Path
     */
    val path: String
        get() {
            val path = StringBuilder()
            var node: TreeNode? = this
            while (node!!.parent != null) {
                path.append(node.id)
                node = node.parent
                // peek loop condition
                if (node!!.parent != null) {
                    path.append(NODES_ID_SEPARATOR)
                }
            }
            return path.toString()
        }

    /**
     * Level of node
     */
    val level: Int
        get() {
            var level = 0
            var root: TreeNode? = this
            while (root!!.parent != null) {
                root = root.parent
                level++
            }
            return level
        }

    /**
     * Whether node is leaf
     */
    val isLeaf: Boolean
        get() = size() == 0

    /**
     * Whether node is root
     */
    private val isNotRoot: Boolean
        get() = parent != null

    /**
     * Whether node is first child
     */
    val isFirstChild: Boolean
        get() = if (isNotRoot) {
            parent!!.children[0].id == id
        } else false

    /**
     * Whether node is last child
     */
    val isLastChild: Boolean
        get() {
            if (isNotRoot) {
                val parentSize = parent!!.children.size
                if (parentSize > 0) {
                    return parent!!.children[parentSize - 1].id == id
                }
            }
            return false
        }

    // A T T R I B U T E S

    /**
     * Get whether this node is selected
     *
     * @return whether this node is selected
     */
    fun isSelected(): Boolean {
        return isSelectable && selected
    }

    /**
     * Set this node selected
     *
     * @param selected selected flag
     */
    fun setSelected(selected: Boolean) {
        this.selected = selected
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb
            .append("#")
            .append(id)
            .append(' ')
            .append('"')
            .append(text)
            .append('"')
            .append(' ')
        if (payload != null) {
            sb
                .append("payload=")
                .append('{')
            var first = true
            for (obj in payload) {
                if (first) {
                    first = false
                } else {
                    sb.append(',')
                }
                sb.append(obj)
            }
            sb.append('}')
        }
        sb
            .append("controller=")
            .append(controller.javaClass.simpleName)
            .append(' ')
            .append("parent=")
            .append(parent?.id ?: "none")
            .append(' ')
        sb
            .append("num children=")
            .append(children.size)
        return sb.toString()
    }

    fun toStringWithChildren(): String {
        val sb = StringBuilder()
        toStringWithChildren(sb, 0)
        return sb.toString()
    }

    private fun toStringWithChildren(sb: StringBuilder, level: Int) {
        if (level > 0) {
            for (i in 0 until level - 1) {
                sb.append("     ")
            }
            sb.append("└")
            //sb.append("├")
            sb.append("────")
        }
        sb.append(this)
        sb.append('\n')
        for (child in children) {
            child.toStringWithChildren(sb, level + 1)
        }
    }

    // C L I C K  L I S T E N E R

    /**
     * Set click listener
     *
     * @param listener click listener
     * @return this node
     */
    fun setClickListener(listener: TreeNodeClickListener?): TreeNode {
        clickListener = listener
        return this
    }

    /**
     * Click listener interface
     */
    fun interface TreeNodeClickListener {

        /**
         * Click event
         *
         * @param node  node
         */
        fun onClick(node: TreeNode)
    }

    companion object {

        private const val NODES_ID_SEPARATOR = ":"
    }
}
