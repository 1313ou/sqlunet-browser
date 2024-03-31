/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.loaders

import android.os.Parcelable
import org.sqlunet.browser.TreeFragment
import org.sqlunet.framenet.FnValenceUnitPointer
import org.sqlunet.treeview.model.TreeNode

/**
 * AnnoSet from valence unit module
 *
 * @param fragment containing fragment
 * @param standAlone whether the database is FN-standalone or whether it is optimized and operates in conjunction with WN
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class AnnoSetFromValenceUnitModule(fragment: TreeFragment, standAlone: Boolean) : BaseModule(fragment, standAlone) {

    /**
     * Valence unit id
     */
    private var vuId: Long? = null

    override fun unmarshal(pointer: Parcelable) {
        vuId = null
        if (pointer is FnValenceUnitPointer) {
            vuId = pointer.id
        }
    }

    override fun process(node: TreeNode) {
        if (vuId != null) {
            // data
            annoSetsForValenceUnit(vuId!!, node)
        }
    }
}
