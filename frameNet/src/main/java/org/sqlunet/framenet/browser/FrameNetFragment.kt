/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.browser

import android.os.Bundle
import org.sqlunet.HasXId
import org.sqlunet.browser.Module
import org.sqlunet.browser.TreeFragment
import org.sqlunet.framenet.R
import org.sqlunet.framenet.loaders.FrameModule
import org.sqlunet.framenet.loaders.LexUnitFromWordModule
import org.sqlunet.provider.ProviderArgs

/**
 * A fragment representing a framenet search from a (word, pos)
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FrameNetFragment : TreeFragment() {

    init {
        layoutId = R.layout.fragment_framenet
        treeContainerId = R.id.data_contents
        headerId = R.string.framenet_frames
        iconId = R.drawable.framenet
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
            val module: Module = if (pointer is HasXId) FrameModule(this) else LexUnitFromWordModule(this)
            module.init(type, pointer)
            module.process(queryNode)
        }
    }

    companion object {

        const val FRAGMENT_TAG = "framenet"
    }
}
