/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.predicatematrix.loaders

import android.os.Parcelable
import org.sqlunet.Word
import org.sqlunet.browser.TreeFragment
import org.sqlunet.predicatematrix.settings.Settings.PMMode
import org.sqlunet.treeview.model.TreeNode

/**
 * Module for predicate roles obtained from word
 *
 * @param fragment fragment
 * @param mode view mode
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class PredicateRoleFromWordModule(
    fragment: TreeFragment,
    private val mode: PMMode,
) : BaseModule(fragment) {

    /**
     * Query
     */
    private var word: String? = null
    override fun unmarshal(pointer: Parcelable) {
        word = null
        if (pointer is Word) {
            this.word = pointer.word
        }
    }

    override fun process(node: TreeNode) {
        if (word != null) {
            when (mode) {
                PMMode.ROLES -> fromWordGrouped(word, node)
                PMMode.ROWS_GROUPED_BY_ROLE -> fromWord(word, node, DisplayerByPmRole())
                PMMode.ROWS_GROUPED_BY_SYNSET -> fromWord(word, node, DisplayerBySynset())
                PMMode.ROWS -> fromWord(word, node, DisplayerUngrouped())
            }
        }
    }
}
