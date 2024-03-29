/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.predicatematrix.browser

import android.os.Bundle
import org.sqlunet.browser.Module
import org.sqlunet.browser.TreeFragment
import org.sqlunet.predicatematrix.PmRolePointer
import org.sqlunet.predicatematrix.R
import org.sqlunet.predicatematrix.loaders.PredicateRoleFromWordModule
import org.sqlunet.predicatematrix.loaders.PredicateRoleModule
import org.sqlunet.predicatematrix.settings.Settings.PMMode
import org.sqlunet.provider.ProviderArgs

/**
 * PredicateMatrix result fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class PredicateMatrixFragment : TreeFragment() {

    init {
        layoutId = R.layout.fragment_predicatematrix
        treeContainerId = R.id.data_contents
        headerId = R.string.predicatematrix_predicates
        iconId = R.drawable.predicatematrix
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // query
        val args = requireArguments()
        val type = args.getInt(ProviderArgs.ARG_QUERYTYPE)
        val pointer = getPointer(args)

        // view mode
        val mode = PMMode.getPref(requireContext())

        // root node
        val queryNode = treeRoot.children.iterator().next()

        // module
        val module: Module = if (pointer is PmRolePointer) PredicateRoleModule(this, mode) else PredicateRoleFromWordModule(this, mode)
        module.init(type, pointer)
        module.process(queryNode)
    }

    companion object {

        const val FRAGMENT_TAG = "predicatematrix"
    }
}
