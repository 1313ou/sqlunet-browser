/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.loaders

import android.os.Parcelable
import org.sqlunet.HasWordId
import org.sqlunet.browser.TreeFragment
import org.sqlunet.model.TreeFactory.setNoResult
import org.sqlunet.treeview.model.TreeNode

/**
 * Module for PropBank role sets from word
 *
 * @param fragment fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class RoleSetFromWordModule(fragment: TreeFragment) : BaseModule(fragment) {

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
            roleSets(wordId!!, node)
        } else {
            setNoResult(node)
        }
    }
}
