/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.browser

import android.os.Bundle
import org.sqlunet.browser.TreeFragment
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.wordnet.R
import org.sqlunet.wordnet.loaders.WordModule

/**
 * A fragment representing a synset.
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class WordFragment : TreeFragment() {

    init {
        layoutId = R.layout.fragment_word
        treeContainerId = R.id.data_contents
        headerId = R.string.wordnet_words
        iconId = R.drawable.wordnet
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
            val module = makeModule()
            module.init(type, pointer)
            module.process(queryNode)
        }
    }

    /**
     * Module factory
     *
     * @return module
     */
    private fun makeModule(): WordModule {
        return WordModule(this)
    }

    companion object {
        const val FRAGMENT_TAG = "word"
    }
}
