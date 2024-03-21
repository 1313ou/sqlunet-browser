/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.loaders

import android.os.Parcelable
import org.sqlunet.HasWordId
import org.sqlunet.browser.TreeFragment
import org.sqlunet.model.TreeFactory.makeTextNode
import org.sqlunet.model.TreeFactory.setNoResult
import org.sqlunet.treeview.model.TreeNode

/**
 * Module for WordNet word
 *
 * @param fragment fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class WordModule(fragment: TreeFragment) : BaseModule(fragment) {
    private var wordId: Long? = null

    override fun unmarshal(pointer: Parcelable) {
        wordId = null
        if (pointer is HasWordId) {
            val wordPointer = pointer as HasWordId
            wordId = wordPointer.getWordId()
        }
    }

    override fun process(node: TreeNode) {
        if (wordId != null && wordId != 0L) {
            // sub nodes
            val wordNode = makeTextNode(wordLabel, false).addTo(node)

            // word
            word(wordId!!, wordNode, false)

            // senses
            senses(wordId!!, wordNode)
        } else {
            setNoResult(node)
        }
    }
}
