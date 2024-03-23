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
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class LexUnitModule(fragment: TreeFragment) : FrameModule(fragment) {

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
            lexUnit(luId!!, node, true, false)
        }
    }
}
