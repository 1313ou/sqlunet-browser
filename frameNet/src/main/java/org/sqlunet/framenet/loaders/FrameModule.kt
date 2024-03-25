/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.loaders

import android.os.Parcelable
import org.sqlunet.HasXId
import org.sqlunet.browser.TreeFragment
import org.sqlunet.framenet.FnFramePointer
import org.sqlunet.model.TreeFactory.setNoResult
import org.sqlunet.treeview.model.TreeNode

/**
 * Frame module
 *
 * @param fragment containing fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class FrameModule(fragment: TreeFragment) : BaseModule(fragment) {

    /**
     * Frame id
     */
    private var frameId: Long? = null

    /**
     * Lex unit id
     */
    private var luId: Long? = null

    override fun unmarshal(pointer: Parcelable) {
        frameId = null
        luId = null
        if (pointer is FnFramePointer) {
            frameId = pointer.id
        }
        if (pointer is HasXId) {
            val xIdPointer = pointer as HasXId
            val xSources = xIdPointer.getXSources()
            if (xSources == null || xSources.contains("fn")) 
            {
                frameId = xIdPointer.getXClassId()
                luId = xIdPointer.getXMemberId()
            }
        }
    }

    override fun process(node: TreeNode) {
        if (luId != null) {
            lexUnit(luId!!, node, withFrame = true, withFes = false)
        } else if (frameId != null) {
            frame(frameId!!, node)
        } else {
            setNoResult(node)
        }
    }
}
