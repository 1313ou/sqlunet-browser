/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.browser

import android.os.Bundle
import org.sqlunet.browser.Module
import org.sqlunet.browser.TreeFragment
import org.sqlunet.framenet.R
import org.sqlunet.framenet.loaders.AnnoSetFromPatternModule
import org.sqlunet.framenet.loaders.AnnoSetFromValenceUnitModule
import org.sqlunet.framenet.loaders.AnnoSetModule
import org.sqlunet.provider.ProviderArgs

/**
 * A fragment representing an annoSet.
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FnAnnoSetFragment : TreeFragment() {

    init {
        layoutId = R.layout.fragment_fnannoset
        treeContainerId = R.id.data_contents
        iconId = R.drawable.annoset
        headerId = R.string.framenet_annosets

        // header
        val args = arguments
        if (args != null) {
            val type = args.getInt(ProviderArgs.ARG_QUERYTYPE)
            when (type) {
                ProviderArgs.ARG_QUERYTYPE_FNPATTERN -> headerId = R.string.framenet_annosets_for_pattern
                ProviderArgs.ARG_QUERYTYPE_FNVALENCEUNIT -> headerId = R.string.framenet_annosets_for_valenceunit
                else -> {}
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // query
        val args = requireArguments()
        if (args.containsKey(ProviderArgs.ARG_QUERYPOINTER)) {
            // pointer
            val pointer = getPointer(args)
            val type = args.getInt(ProviderArgs.ARG_QUERYTYPE)
            val standAlone = args.getBoolean(ProviderArgs.ARG_STANDALONE)

            // root node
            val queryNode = treeRoot.children.iterator().next()

            // module
            val module: Module = when (type) {
                ProviderArgs.ARG_QUERYTYPE_FNANNOSET -> AnnoSetModule(this, standAlone)
                ProviderArgs.ARG_QUERYTYPE_FNPATTERN -> AnnoSetFromPatternModule(this, standAlone)
                ProviderArgs.ARG_QUERYTYPE_FNVALENCEUNIT -> AnnoSetFromValenceUnitModule(this, standAlone)
                else -> return
            }
            module.init(type, pointer)
            module.process(queryNode)
        }
    }
}
