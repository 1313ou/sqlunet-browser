/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.loaders

import android.os.Parcelable
import org.sqlunet.browser.TreeFragment
import org.sqlunet.model.TreeFactory.setNoResult
import org.sqlunet.syntagnet.SnCollocationPointer
import org.sqlunet.treeview.model.TreeNode

/**
 * Module for SyntagNet collocation from id
 *
 * @param fragment fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class CollocationModule(fragment: TreeFragment) : BaseModule(fragment) {

    /**
     * Collocation id
     */
    private var collocationId: Long? = null

    override fun isTargetSecond(word1Id: Long, word2Id: Long): Boolean {
        return false
    }

    override fun unmarshal(pointer: Parcelable) {
        collocationId = null
        if (pointer is SnCollocationPointer) {
            collocationId = pointer.id
        }
    }

    override fun process(node: TreeNode) {
        if (collocationId != null) {
            collocation(collocationId!!, node)
        } else {
            setNoResult(node)
        }
    }
}
