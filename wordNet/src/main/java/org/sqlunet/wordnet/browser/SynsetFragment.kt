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
import org.sqlunet.wordnet.loaders.SynsetModule

/**
 * A fragment representing a synset.
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class SynsetFragment : TreeFragment() {

    /**
     * Whether to expandContainer
     */
    private var expand = true

    /**
     * Max recursion level
     */
    var maxRecursion = 0

    /**
     * Parameters
     */
    var parameters: Bundle? = null

    init {
        layoutId = R.layout.fragment_sense
        treeContainerId = R.id.data_contents
        headerId = R.string.wordnet_synsets
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
    protected open fun makeModule(): Module {
        val module = SynsetModule(this)
        module.setMaxRecursionLevel(maxRecursion)
        if (parameters != null) {
            module.setDisplayRelationNames(parameters!!.getBoolean(ARG_RENDER_DISPLAY_SEM_RELATION_NAME_KEY, true), parameters!!.getBoolean(ARG_RENDER_DISPLAY_LEX_RELATION_NAME_KEY, true))
        }
        module.expand = this.expand
        return module
    }

    /**
     * Set expand container
     */
    fun setExpand(expand: Boolean) {
        this.expand = expand
    }

    companion object {

        private const val TAG = "SynsetF"

        /**
         * State of tree
         */
        private const val STATE_EXPAND = "state_expand"
    }
}
