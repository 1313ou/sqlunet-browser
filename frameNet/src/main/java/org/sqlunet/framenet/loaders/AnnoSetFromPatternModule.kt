/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.loaders

import android.os.Parcelable
import org.sqlunet.browser.TreeFragment
import org.sqlunet.framenet.FnPatternPointer
import org.sqlunet.treeview.model.TreeNode

/**
 * AnnoSet from pattern module
 *
 * @param fragment containing fragment
 * @param standAlone whether the database is FN-standalone or whether it is optimized and operates in conjunction with WN
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class AnnoSetFromPatternModule(fragment: TreeFragment, standAlone: Boolean) : BaseModule(fragment, standAlone) {

    /**
     * Pattern id
     */
    private var patternId: Long? = null

    override fun unmarshal(pointer: Parcelable) {
        patternId = null
        if (pointer is FnPatternPointer) {
            patternId = pointer.id
        }
    }

    override fun process(node: TreeNode) {
        if (patternId != null) {
            annoSetsForPattern(patternId!!, node)
        }
    }
}
