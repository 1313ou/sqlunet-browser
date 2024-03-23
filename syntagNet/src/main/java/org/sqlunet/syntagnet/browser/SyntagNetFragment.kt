/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.browser

import android.os.Bundle
import org.sqlunet.browser.Module
import org.sqlunet.browser.TreeFragment
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.syntagnet.R
import org.sqlunet.syntagnet.loaders.CollocationModule
import org.sqlunet.syntagnet.loaders.CollocationsModule

/**
 * A fragment representing a SyntagNet search
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SyntagNetFragment : TreeFragment() {

    init {
        layoutId = R.layout.fragment_syntagnet
        treeContainerId = R.id.data_contents
        headerId = R.string.syntagnet_collocations
        iconId = R.drawable.syntagnet
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // query
        val args = requireArguments()
        val type = args.getInt(ProviderArgs.ARG_QUERYTYPE)
        if (args.containsKey(ProviderArgs.ARG_QUERYPOINTER)) {
            // pointer
            val pointer = getPointer(args)

            // root node
            val queryNode = treeRoot.children.iterator().next()

            // module
            val module: Module = if (type == ProviderArgs.ARG_QUERYTYPE_COLLOCATION) CollocationModule(this) else CollocationsModule(this)
            module.init(type, pointer)
            module.process(queryNode)
        }
    }

    companion object {
        // static private final String TAG = "SyntagNetF";
        const val FRAGMENT_TAG = "syntagnet"
    }
}
