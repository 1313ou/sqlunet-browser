/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.model

import android.os.Handler
import android.os.Looper
import androidx.annotation.DrawableRes
import org.sqlunet.treeview.control.ColdQueryTreeController
import org.sqlunet.treeview.control.HotQueryController
import org.sqlunet.treeview.control.HotQueryLinkController
import org.sqlunet.treeview.control.HotQueryTreeController
import org.sqlunet.treeview.control.IconTextController
import org.sqlunet.treeview.control.LeafController
import org.sqlunet.treeview.control.Link
import org.sqlunet.treeview.control.LinkController
import org.sqlunet.treeview.control.LinkHotQueryTreeController
import org.sqlunet.treeview.control.LinkLeafController
import org.sqlunet.treeview.control.LinkQueryTreeController
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
    fun makeIconTextNode(text: CharSequence, @DrawableRes icon: Int, breakExpand: Boolean): TreeNode {
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
    fun makeLeafNode(text: CharSequence, @DrawableRes icon: Int, breakExpand: Boolean): TreeNode {
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
    fun makeMoreNode(text: CharSequence, @DrawableRes icon: Int, breakExpand: Boolean): TreeNode {
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
    fun makeLinkNode(text: CharSequence, @DrawableRes icon: Int, breakExpand: Boolean, link: Link?): TreeNode {
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
    fun makeLinkLeafNode(text: CharSequence, @DrawableRes icon: Int, breakExpand: Boolean, link: Link?): TreeNode {
        return TreeNode(text, icon, arrayOf(link), LinkLeafController(breakExpand), false)
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
    fun makeHotQueryNode(text: CharSequence, @DrawableRes icon: Int, breakExpand: Boolean, query: Query?): TreeNode {
        val controller = HotQueryController(breakExpand)
        val result = TreeNode(text, icon, arrayOf(query, null), controller, false)
        val handler = Handler(Looper.getMainLooper())
        handler.post { controller.processQuery() }
        return result
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
    fun makeHotQueryLinkNode(text: CharSequence, @DrawableRes icon: Int, breakExpand: Boolean, query: Query?): TreeNode {
        val controller = HotQueryLinkController(breakExpand)
        val result = TreeNode(text, icon, arrayOf(query, null), controller, false)
        val handler = Handler(Looper.getMainLooper())
        handler.post { controller.processQuery() }
        return result
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
    fun makeTreeNode(value: CharSequence, @DrawableRes icon: Int, breakExpand: Boolean): TreeNode {
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
    fun makeLinkTreeNode(text: CharSequence, @DrawableRes icon: Int, breakExpand: Boolean, link: Link?): TreeNode {
        return TreeNode(text, icon, arrayOf(link), LinkTreeController(breakExpand), true)
    }

    /**
     * Make query tree node
     *
     * @param text        label text
     * @param icon        icon
     * @param breakExpand break expand flag
     * @param query       query
     * @return created node
     */
    fun makeQueryTreeNode(text: CharSequence, @DrawableRes icon: Int, breakExpand: Boolean, query: Query?): TreeNode {
        return TreeNode(text, icon, arrayOf(query), ColdQueryTreeController(breakExpand), true)
    }

    /**
     * Make hot (self-triggered) tree query node
     *
     * @param text        label text
     * @param icon        icon
     * @param breakExpand break expand flag
     * @param query       query
     * @return created node
     */
    fun makeHotQueryTreeNode(text: CharSequence, @DrawableRes icon: Int, breakExpand: Boolean, query: Query?): TreeNode {
        val controller = HotQueryTreeController(breakExpand)
        val result = TreeNode(text, icon, arrayOf(query), controller, true)
        val handler = Handler(Looper.getMainLooper())
        handler.post { controller.processQuery() }
        return result
    }

    /**
     * Make hot (self-triggered) tree query node
     *
     * @param text           label text
     * @param icon           icon
     * @param breakExpand    break expand flag
     * @param query          query
     * @param link           link
     * @param buttonImageRes image drawable id for button, 0 for default
     * @return created node
     */
    fun makeLinkHotQueryTreeNode(text: CharSequence, @DrawableRes icon: Int, breakExpand: Boolean, query: Query?, link: Link?, @DrawableRes buttonImageRes: Int): TreeNode {
        val controller: HotQueryTreeController = LinkHotQueryTreeController(breakExpand, buttonImageRes)
        val result = TreeNode(text, icon, arrayOf(query, link), controller, true)
        val handler = Handler(Looper.getMainLooper())
        handler.post { controller.processQuery() }
        return result
    }

    /**
     * Make link query tree node
     *
     * @param text           label text
     * @param icon           icon
     * @param breakExpand    break expand flag
     * @param query          query
     * @param link           link
     * @param buttonImageRes image drawable id for button, 0 for default
     * @return created node
     */
    fun makeLinkQueryTreeNode(text: CharSequence, @DrawableRes icon: Int, breakExpand: Boolean, query: Query?, link: Link?, @DrawableRes buttonImageRes: Int): TreeNode {
        return TreeNode(text, icon, arrayOf(query, link), LinkQueryTreeController(breakExpand, buttonImageRes), true)
    }

    // H E L P E R S

    /**
     * No results have been attached to this node
     *
     * @param node node
     */
    fun setNoResult(node: TreeNode) {
        // Log.d(TAG, "Deadend " + node)
        node.isDeadend = true
        node.isEnabled = false
    }

    fun setTextNode(node: TreeNode, text: CharSequence?) {
        node.text = text
    }

    fun setTextNode(node: TreeNode, text: CharSequence, @DrawableRes icon: Int) {
        node.text = text
        node.icon = icon
    }
}
