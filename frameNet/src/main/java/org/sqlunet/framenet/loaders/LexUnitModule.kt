/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.loaders

import android.os.Parcelable
import org.sqlunet.browser.TreeFragment
import org.sqlunet.framenet.FnLexUnitPointer
import org.sqlunet.treeview.model.TreeNode

/**
 * Lex unit module
 *
 * @param fragment containing fragment
 * @param standAlone whether the database is FN-standalone or whether it is optimized and operates in conjunction with WN
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class LexUnitModule(fragment: TreeFragment, standAlone: Boolean) : FrameModule(fragment, standAlone) {

    /**
     * LuId
     */
    private var luId: Long? = null

    override fun unmarshal(pointer: Parcelable) {
        super.unmarshal(pointer)
        luId = null
        if (pointer is FnLexUnitPointer) {
            luId = pointer.id
        }
    }

    override fun process(node: TreeNode) {
        if (luId != null) {
            lexUnit(luId!!, node, withFrame = true, withFes = false)
        }
    }
}
