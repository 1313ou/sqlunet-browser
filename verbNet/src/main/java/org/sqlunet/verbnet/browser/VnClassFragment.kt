/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.browser

import android.os.Bundle
import org.sqlunet.browser.Module
import org.sqlunet.browser.TreeFragment
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.verbnet.R
import org.sqlunet.verbnet.loaders.ClassModule

/**
 * A fragment representing a VerbNet class
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class VnClassFragment : TreeFragment() {

    init {
        layoutId = R.layout.fragment_vnclass
        treeContainerId = R.id.data_contents
        headerId = R.string.verbnet_classes
        iconId = R.drawable.verbnet
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
            val module: Module = ClassModule(this)
            module.init(type, pointer)
            module.process(queryNode)
        }
    }
}
