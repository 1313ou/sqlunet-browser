/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.view

import org.sqlunet.browser.TreeFragment
import org.sqlunet.treeview.control.CompositeValue
import org.sqlunet.treeview.model.TreeNode
import org.sqlunet.treeview.view.TreeViewer
import org.sqlunet.view.TreeOp.TreeOpCode

/**
 * TreeOp executor
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class TreeOpExecute(private val fragment: TreeFragment) {

    fun exec(ops: Array<TreeOp>) {
        execImpl(ops)
    }

    private fun noopImpl(ops: Array<TreeOp>) {}

    private fun execImpl(ops: Array<TreeOp>) {
        val treeView = fragment.treeViewer ?: return
        val n = ops.size
        if (n == 1) {
            val op = ops[0]
            execOp(op, treeView, 0)
        } else if (n > 1) {
            for (op in ops) {
                execOp(op, treeView, -1)
            }
        }
    }

    private fun execOp(op: TreeOp, treeViewer: TreeViewer, levels: Int) {
        val code = op.code
        val node = op.node
        when (code) {
            TreeOpCode.NEWTREE -> {

                // Log.d(TAG, "vvv " + op.getCode() + " " + node.toString());
                val view = treeViewer.expandNode(node, -1, false, false)
                if (node.controller.takeEnsureVisible()) {
                    treeViewer.ensureVisible(view)
                }
            }

            TreeOpCode.NEWCHILD -> {}
            TreeOpCode.NEWMAIN, TreeOpCode.NEWEXTRA, TreeOpCode.NEWUNIQUE -> {

                // Log.d(TAG, "... " + op.getCode() + " " + node.toString());
                val view = treeViewer.newNodeView(node, levels)
                if (node.controller.takeEnsureVisible()) {
                    treeViewer.ensureVisible(view)
                }
            }

            TreeOpCode.UPDATE -> {

                // Log.d(TAG, "!!! " + op.getCode() + " " + node.toString());
                val view = treeViewer.update(node)
                if (node.controller.takeEnsureVisible() && view != null) {
                    treeViewer.ensureVisible(view)
                }
            }

            TreeOpCode.COLLAPSE -> {
                val controller = node.controller
                val childrenView = controller.childrenView
                if (childrenView != null && TreeViewer.isExpanded(node)) {
                    // Log.d(TAG, "^^^ " + op.getCode() + " " + node.toString());
                    treeViewer.collapseNode(node, true)
                }
            }

            TreeOpCode.REMOVE -> {

                // Log.d(TAG, "--- " + op.getCode() + " " + node.toString());
                treeViewer.remove(node)
            }

            TreeOpCode.DEADEND -> {

                // Log.d(TAG, "xxx " + op.getCode() + " " + node.toString());
                treeViewer.deadend(node)
            }

            TreeOpCode.NOOP -> {}
            else -> {}
        }
    }

    private fun isNodeWithCompositeValueText(node: TreeNode, text: String): Boolean {
        val value = node.value
        return value is CompositeValue && text == value.text.toString()
    }

    companion object {
        // private const val TAG = "TreeOpExecute"
    }
}
