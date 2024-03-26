/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.bnc.browser

import android.os.Bundle
import org.sqlunet.bnc.R
import org.sqlunet.bnc.loaders.BaseModule
import org.sqlunet.browser.Module
import org.sqlunet.browser.TreeFragment
import org.sqlunet.provider.ProviderArgs

/**
 * A fragment representing a lexunit.
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class BNCFragment : TreeFragment() {

   init {
        layoutId = R.layout.fragment_bnc
        treeContainerId = R.id.data_contents
        headerId = R.string.bnc_frequencies
        iconId = R.drawable.bnc
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
            val module: Module = BaseModule(this)
            module.init(type, pointer)
            module.process(queryNode)
        }
    }

    companion object {
        // private const val TAG = "BncF"
        const val FRAGMENT_TAG = "bnc"
    }
}
