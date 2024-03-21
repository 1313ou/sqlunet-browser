/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.view

import org.sqlunet.treeview.model.TreeNode

class TreeOp private constructor(@JvmField val code: TreeOpCode, @JvmField val node: TreeNode) {

    enum class TreeOpCode {
        NOOP,
        NEWTREE,
        NEWCHILD,
        NEWUNIQUE,
        NEWMAIN,
        NEWEXTRA,
        UPDATE,
        REMOVE,
        COLLAPSE,
        DEADEND
    }

    class TreeOps(vararg items: Any) : ArrayList<TreeOp>() {

        init {
            add(*items)
        }

        fun add(vararg items: Any) {
            prepend(*items)
        }

        private fun prepend(vararg items: Any) {
            var i = 0
            while (i < items.size - 1) {
                super.add(0, TreeOp(items[i] as TreeOpCode, items[i + 1] as TreeNode))
                i += 2
            }
        }

        override fun toArray(): Array<TreeOp> {
            return toTypedArray<TreeOp>()
        }
    }

    companion object {

        @JvmStatic
        fun seq(vararg items: Any): Array<TreeOp> {
            val result = Array(items.size / 2) {
                val j = 2 * it
                TreeOp(items[j] as TreeOpCode, items[j + 1] as TreeNode)
            }
            return result
        }
    }
}
