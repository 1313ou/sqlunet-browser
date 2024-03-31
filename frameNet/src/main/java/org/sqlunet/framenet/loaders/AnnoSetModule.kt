/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.loaders

import android.os.Parcelable
import org.sqlunet.browser.TreeFragment
import org.sqlunet.framenet.FnAnnoSetPointer
import org.sqlunet.treeview.model.TreeNode

/**
 * AnnoSet module
 *
 * @param fragment containing fragment
 * @param standAlone whether the database is FN-standalone or whether it is optimized and operates in conjunction with WN
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class AnnoSetModule(fragment: TreeFragment, standAlone: Boolean) : BaseModule(fragment, standAlone) {

    /**
     * AnnoSet id
     */
    private var annoSetId: Long? = null

    override fun unmarshal(pointer: Parcelable) {
        annoSetId = null
        if (pointer is FnAnnoSetPointer) {
            annoSetId = pointer.id
        }
    }

    override fun process(node: TreeNode) {
        if (annoSetId != null) {
            // data
            annoSet(annoSetId!!, node, true)
        }
    }
}
