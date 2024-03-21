/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.browser

import android.os.Bundle
import android.util.Log
import org.sqlunet.browser.Module
import org.sqlunet.browser.TreeFragment
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.provider.ProviderArgs.ARG_RENDER_DISPLAY_LEX_RELATION_NAME_KEY
import org.sqlunet.provider.ProviderArgs.ARG_RENDER_DISPLAY_SEM_RELATION_NAME_KEY
import org.sqlunet.wordnet.R
import org.sqlunet.wordnet.loaders.RelationModule

/**
 * A fragment representing a relation tree.
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class RelationFragment : TreeFragment() {
    /**
     * Whether to expandContainer
     */
    private var expand = true

    /**
     * Max recursion level
     */
    private var maxRecursion = 0

    /**
     * Parameters
     */
    var parameters: Bundle? = null

    /**
     * Constructor
     */
    init {
        layoutId = R.layout.fragment_relation
        treeContainerId = R.id.data_contents
        headerId = R.string.wordnet_tree
        iconId = R.drawable.wordnet
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // query
        val args = requireArguments()
        val type = args.getInt(ProviderArgs.ARG_QUERYTYPE)
        maxRecursion = if (args.containsKey(ProviderArgs.ARG_QUERYRECURSE)) args.getInt(ProviderArgs.ARG_QUERYRECURSE) else -1
        parameters = if (args.containsKey(ProviderArgs.ARG_RENDERPARAMETERS)) args.getBundle(ProviderArgs.ARG_RENDERPARAMETERS) else null

        // saved state
        if (savedInstanceState != null) {
            Log.d(TAG, "restore instance state $this")
            expand = savedInstanceState.getBoolean(STATE_EXPAND)
        }

        // load
        if (args.containsKey(ProviderArgs.ARG_QUERYPOINTER)) {
            // pointer
            val pointer = getPointer(args)

            // root node
            val queryNode = treeRoot.children.iterator().next()

            // module
            val module = makeModule()
            module.init(type, pointer)
            module.process(queryNode)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_EXPAND, expand)
    }

    /**
     * Module factory
     *
     * @return module
     */
    private fun makeModule(): Module {
        val module = RelationModule(this)
        module.setMaxRecursionLevel(maxRecursion)
        if (parameters != null) {
            module.setDisplayRelationNames(parameters!!.getBoolean(ARG_RENDER_DISPLAY_SEM_RELATION_NAME_KEY, true), parameters!!.getBoolean(ARG_RENDER_DISPLAY_LEX_RELATION_NAME_KEY, true))
        }
        return module
    }

    override val scroll2D: Boolean
        get() = true

    companion object {
        private const val TAG = "RelationF"

        /**
         * State of tree
         */
        private const val STATE_EXPAND = "state_expand"
    }
}
