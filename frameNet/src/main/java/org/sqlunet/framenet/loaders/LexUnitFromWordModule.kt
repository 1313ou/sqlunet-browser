/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.loaders

import android.os.Parcelable
import org.sqlunet.HasPos
import org.sqlunet.HasWordId
import org.sqlunet.browser.TreeFragment
import org.sqlunet.model.TreeFactory.setNoResult
import org.sqlunet.treeview.model.TreeNode

/**
 * Lex unit from word module
 *
 * @param fragment containing fragment
 * @param standAlone whether the database is FN-standalone or whether it is optimized and operates in conjunction with WN
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class LexUnitFromWordModule(fragment: TreeFragment, standAlone: Boolean) : LexUnitModule(fragment, standAlone) {

    /**
     * Word id
     */
    private var wordId: Long? = null

    /**
     * Pos
     */
    private var pos: Char? = null

    override fun unmarshal(pointer: Parcelable) {
        super.unmarshal(pointer)

        wordId = null
        pos = null
        if (pointer is HasWordId) {
            val wordPointer = pointer as HasWordId
            wordId = wordPointer.getWordId()
        }
        if (pointer is HasPos) {
            val posPointer = pointer as HasPos
            pos = posPointer.getPos()
        }
    }

    override fun process(node: TreeNode) {
        if (wordId != null /*&& this.pos != null*/) {
            lexUnitsForWordAndPos(wordId!!, pos, node)
        } else {
            setNoResult(node)
        }
    }
}
