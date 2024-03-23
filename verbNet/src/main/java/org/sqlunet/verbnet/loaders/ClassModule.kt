/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.loaders

import android.os.Parcelable
import org.sqlunet.HasXId
import org.sqlunet.browser.TreeFragment
import org.sqlunet.model.TreeFactory.setNoResult
import org.sqlunet.treeview.model.TreeNode
import org.sqlunet.verbnet.VnClassPointer

/**
 * VerbNet class module
 *
 * @param fragment fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class ClassModule(fragment: TreeFragment) : BaseModule(fragment) {

    /**
     * Query
     */
    private var classId: Long? = null

    override fun unmarshal(pointer: Parcelable) {
        classId = null
        if (pointer is VnClassPointer) {
            classId = pointer.id
        }
        if (pointer is HasXId) {
            val xIdPointer = pointer as HasXId
            val xSources = xIdPointer.getXSources()
            if (xSources == null || xSources.contains("vn")) //
            {
                classId = xIdPointer.getXClassId()
                // var xMemberId = pointer.getXMemberId()
                // var xSources = pointer.getXSources()
            }
        }
    }

    override fun process(node: TreeNode) {
        if (classId != null) {
            vnClass(classId!!, node)
        } else {
            setNoResult(node)
        }
    }
}
