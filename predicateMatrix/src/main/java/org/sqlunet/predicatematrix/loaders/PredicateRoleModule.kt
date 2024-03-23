/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.predicatematrix.loaders

import android.os.Parcelable
import org.sqlunet.browser.TreeFragment
import org.sqlunet.predicatematrix.PmRolePointer
import org.sqlunet.predicatematrix.settings.Settings.PMMode
import org.sqlunet.treeview.model.TreeNode

/**
 * Module for predicate roles obtained from pm role id
 *
 * @param fragment fragment
 * @param mode view mode
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class PredicateRoleModule(
    fragment: TreeFragment,
    private val mode: PMMode,
) : BaseModule(fragment) {

    /**
     * Query id
     */
    private var pmRoleId: Long? = null

    override fun unmarshal(pointer: Parcelable) {
        pmRoleId = null
        if (pointer is PmRolePointer) {
            pmRoleId = pointer.id
        }
    }

    override fun process(node: TreeNode) {
        if (pmRoleId != null) {
            val displayer: Displayer = when (mode) {
                PMMode.ROWS -> DisplayerUngrouped()
                PMMode.ROWS_GROUPED_BY_SYNSET -> DisplayerBySynset()
                PMMode.ROLES, PMMode.ROWS_GROUPED_BY_ROLE -> DisplayerByPmRole()
            }
            fromRoleId(pmRoleId!!, node, displayer)
        }
    }
}
