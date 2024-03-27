/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.model

import android.os.Handler
import android.os.Looper
import androidx.annotation.DrawableRes
import org.sqlunet.treeview.control.ColdQueryController
import org.sqlunet.treeview.control.HotQueryController
import org.sqlunet.treeview.control.IconTextController
import org.sqlunet.treeview.control.LeafController
import org.sqlunet.treeview.control.Link
import org.sqlunet.treeview.control.LinkHotQueryController
import org.sqlunet.treeview.control.LinkLeafController
import org.sqlunet.treeview.control.LinkController
import org.sqlunet.treeview.control.LinkQueryController
import org.sqlunet.treeview.control.LinkTreeController
import org.sqlunet.treeview.control.MoreController
import org.sqlunet.treeview.control.Query
import org.sqlunet.treeview.control.TextController
import org.sqlunet.treeview.control.TreeController
import org.sqlunet.treeview.model.TreeNode

/**
 * Tree factory
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object TreeFactory {
    // NON-TREE (without tree junction icon)
    /**
     * Make text node
     *
     * @param value       character sequence
     * @param breakExpand break expand flag
     * @return created node
     */
    @JvmStatic
    fun makeTextNode(value: CharSequence, breakExpand: Boolean): TreeNode {
        return TreeNode(value, null, null, TextController(breakExpand), false)
    }

    /**
     * Make icon-text node
     *
     * @param text        text
     * @param icon        icon
     * @param breakExpand break expand flag
     * @return created node
     */
    @JvmStatic
    fun makeIconTextNode(text: CharSequence, icon: Int, breakExpand: Boolean): TreeNode {
        return TreeNode(text, icon, null, IconTextController(breakExpand), false)
    }

    /**
     * Make leaf node
     *
     * @param text        text
     * @param icon        icon (extra icon after tree icon)
     * @param breakExpand break expand flag
     * @return created node
     */
    @JvmStatic
    fun makeLeafNode(text: CharSequence, icon: Int, breakExpand: Boolean): TreeNode {
        return TreeNode(text, icon, null, LeafController(breakExpand), false)
    }

    /**
     * Make more (leaf) node
     *
     * @param text        text
     * @param icon        icon (extra icon after tree icon)
     * @param breakExpand break expand flag
     * @return created node
     */
    @JvmStatic
    fun makeMoreNode(text: CharSequence, icon: Int, breakExpand: Boolean): TreeNode {
        return TreeNode(text, icon, null, MoreController(breakExpand), false)
    }

    /**
     * Make icon-text-link node
     *
     * @param text        text
     * @param icon        icon
     * @param link        link
     * @param breakExpand break expand flag
     * @return created node
     */
    @JvmStatic
    fun makeLinkNode(text: CharSequence, icon: Int, breakExpand: Boolean, link: Link?): TreeNode {
        return TreeNode(text, icon, arrayOf(link), LinkController(breakExpand), false)
    }

    /**
     * Make link leaf node
     *
     * @param text        text
     * @param icon        icon
     * @param breakExpand break expand flag
     * @param link        link
     * @return created node
     */
    @JvmStatic
    fun makeLinkLeafNode(text: CharSequence, icon: Int, breakExpand: Boolean, link: Link?): TreeNode {
        return TreeNode(text, icon, arrayOf(link), LinkLeafController(breakExpand), false)
    }
    // TREE
    /**
     * Add tree node
     *
     * @param value       character sequence
     * @param icon        icon resource id
     * @param breakExpand break expand flag
     * @return created node
     */
    @JvmStatic
    fun makeTreeNode(value: CharSequence, icon: Int, breakExpand: Boolean): TreeNode {
        return TreeNode(value, icon, null, TreeController(breakExpand), true)
    }

    /**
     * Make link tree node
     *
     * @param text        text
     * @param icon        icon
     * @param breakExpand break expand flag
     * @param link        link
     * @return created node
     */
    @JvmStatic
    fun makeLinkTreeNode(text: CharSequence, icon: Int, breakExpand: Boolean, link: Link?): TreeNode {
        return TreeNode(text, icon, arrayOf(link), LinkTreeController(breakExpand), true)
    }

    /**
     * Make query node
     *
     * @param text        label text
     * @param icon        icon
     * @param breakExpand break expand flag
     * @param query       query
     * @return created node
     */
    @JvmStatic
    fun makeQueryNode(text: CharSequence, icon: Int, breakExpand: Boolean, query: Query?): TreeNode {
        return TreeNode(text, icon, arrayOf(query), ColdQueryController(breakExpand), true)
    }

    /**
     * Make hot (self-triggered) query node
     *
     * @param text        label text
     * @param icon        icon
     * @param breakExpand break expand flag
     * @param query       query
     * @return created node
     */
    @JvmStatic
    fun makeHotQueryNode(text: CharSequence, icon: Int, breakExpand: Boolean, query: Query?): TreeNode {
        val controller = HotQueryController(breakExpand)
        val result = TreeNode(text, icon, arrayOf(query), controller, true)
        val handler = Handler(Looper.getMainLooper())
        handler.post { controller.processQuery() }
        return result
    }

    /**
     * Make hot (self-triggered) query node
     *
     * @param text           label text
     * @param icon           icon
     * @param breakExpand    break expand flag
     * @param query          query
     * @param link           link
     * @param buttonImageRes image drawable id for button, 0 for default
     * @return created node
     */
    @JvmStatic
    fun makeLinkHotQueryNode(text: CharSequence, @DrawableRes icon: Int, breakExpand: Boolean, query: Query?, link: Link?, @DrawableRes buttonImageRes: Int): TreeNode {
        val controller: HotQueryController = LinkHotQueryController(breakExpand, buttonImageRes)
        val result = TreeNode(text, icon, arrayOf(query, link), controller, true)
        val handler = Handler(Looper.getMainLooper())
        handler.post { controller.processQuery() }
        return result
    }

    /**
     * Make link query node
     *
     * @param text           label text
     * @param icon           icon
     * @param breakExpand    break expand flag
     * @param query          query
     * @param link           link
     * @param buttonImageRes image drawable id for button, 0 for default
     * @return created node
     */
    @JvmStatic
    fun makeLinkQueryNode(text: CharSequence, @DrawableRes icon: Int, breakExpand: Boolean, query: Query?, link: Link?, @DrawableRes buttonImageRes: Int): TreeNode {
        return TreeNode(text, icon, arrayOf(query, link), LinkQueryController(breakExpand, buttonImageRes), true)
    }
    // H E L P E R S
    /**
     * No results have been attached to this node
     *
     * @param node node
     */
    @JvmStatic
    fun setNoResult(node: TreeNode) {
        // Log.d(TAG, "Deadend " + node)
        node.isDeadend = true
        node.isEnabled = false
    }

    @JvmStatic
    fun setTextNode(node: TreeNode, text: CharSequence?) {
        node.text = text
    }

    @JvmStatic
    fun setTextNode(node: TreeNode, text: CharSequence, @DrawableRes icon: Int) {
        node.text = text
        node.icon = icon
    }
}
