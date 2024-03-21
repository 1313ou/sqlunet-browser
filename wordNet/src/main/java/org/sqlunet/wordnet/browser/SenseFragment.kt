/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.browser

import org.sqlunet.browser.Module
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.wordnet.R
import org.sqlunet.wordnet.loaders.SenseModule

/**
 * A fragment representing a sense
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SenseFragment : SynsetFragment() {

    init {
        headerId = R.string.wordnet_senses
    }

    override fun makeModule(): Module {
        val module = SenseModule(this)
        module.setMaxRecursionLevel(maxRecursion)
        if (parameters != null) {
            module.setDisplayRelationNames(parameters!!.getBoolean(ProviderArgs.ARG_RENDER_DISPLAY_SEM_RELATION_NAME_KEY, true), parameters!!.getBoolean(ProviderArgs.ARG_RENDER_DISPLAY_LEX_RELATION_NAME_KEY, true))
        }
        return module
    }

    companion object {
        const val FRAGMENT_TAG = "wordnet_sense"
    }
}
