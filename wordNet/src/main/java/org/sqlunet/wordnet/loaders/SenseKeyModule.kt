/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.loaders

import android.os.Parcelable
import org.sqlunet.HasSenseKey
import org.sqlunet.browser.TreeFragment
import org.sqlunet.model.TreeFactory.setNoResult
import org.sqlunet.treeview.model.TreeNode

/**
 * Module for WordNet sense (from sensekey)
 *
 * @param fragment fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SenseKeyModule(fragment: TreeFragment) : BaseModule(fragment) {
    /**
     * Sense key
     */
    private var senseKey: String? = null

    override fun unmarshal(pointer: Parcelable) {
        senseKey = null
        if (pointer is HasSenseKey) {
            val sensePointer = pointer as HasSenseKey
            senseKey = sensePointer.getSenseKey()
        }
    }

    override fun process(node: TreeNode) {
        if (senseKey != null && senseKey!!.isNotEmpty()) {
            // synset
            sense(senseKey, node)
        } else {
            setNoResult(node)
        }
    }
}
