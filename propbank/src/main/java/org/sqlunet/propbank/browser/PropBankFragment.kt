/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.browser

import android.os.Bundle
import org.sqlunet.HasXId
import org.sqlunet.browser.Module
import org.sqlunet.browser.TreeFragment
import org.sqlunet.propbank.R
import org.sqlunet.propbank.loaders.RoleSetFromWordModule
import org.sqlunet.propbank.loaders.RoleSetModule
import org.sqlunet.provider.ProviderArgs

/**
 * A fragment representing a PropBank search
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class PropBankFragment : TreeFragment() {

    init {
        layoutId = R.layout.fragment_propbank
        treeContainerId = R.id.data_contents
        headerId = R.string.propbank_rolesets
        iconId = R.drawable.propbank
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
            val module: Module = if (pointer is HasXId) RoleSetModule(this) else RoleSetFromWordModule(this)
            module.init(type, pointer)
            module.process(queryNode)
        }
    }

    companion object {
        const val FRAGMENT_TAG = "propbank"
    }
}
