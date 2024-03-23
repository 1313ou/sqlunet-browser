/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.loaders

import android.os.Parcelable
import org.sqlunet.HasWordId
import org.sqlunet.browser.TreeFragment
import org.sqlunet.model.TreeFactory.setNoResult
import org.sqlunet.treeview.model.TreeNode

/**
 * Module for SyntagNet collocation from word
 *
 * @param fragment fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class CollocationFromWordIdModule(fragment: TreeFragment) : BaseModule(fragment) {

    /**
     * Word id
     */
    private var wordId: Long? = null
    override fun isTargetSecond(word1Id: Long, word2Id: Long): Boolean {
        return false
    }

    override fun unmarshal(pointer: Parcelable) {
        wordId = null
        if (pointer is HasWordId) {
            val wordPointer = pointer as HasWordId
            wordId = wordPointer.getWordId()
        }
    }

    override fun process(node: TreeNode) {
        if (wordId != null && wordId != 0L) {
            // data
            collocations(wordId!!, node)
        } else {
            setNoResult(node)
        }
    }
}
