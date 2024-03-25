/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.loaders

import android.os.Parcelable
import org.sqlunet.HasXId
import org.sqlunet.browser.TreeFragment
import org.sqlunet.model.TreeFactory.setNoResult
import org.sqlunet.propbank.PbRoleSetPointer
import org.sqlunet.treeview.model.TreeNode

/**
 * Module for PropBank role sets from id
 *
 * @param fragment fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class RoleSetModule(fragment: TreeFragment) : BaseModule(fragment) {

    /**
     * Role set id
     */
    private var roleSetId: Long? = null

    override fun unmarshal(pointer: Parcelable) {
        roleSetId = null
        if (pointer is PbRoleSetPointer) {
            roleSetId = pointer.id
        }
        if (pointer is HasXId) {
            val xIdPointer = pointer as HasXId
            val xSources = xIdPointer.getXSources()
            if (xSources == null || xSources.contains("pb")) 
            {
                roleSetId = xIdPointer.getXClassId()
            }
        }
    }

    override fun process(node: TreeNode) {
        if (roleSetId != null) {
            // data
            roleSet(roleSetId!!, node)
        } else {
            setNoResult(node)
        }
    }
}
