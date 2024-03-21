/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.browser

import org.sqlunet.browser.Module
import org.sqlunet.provider.ProviderArgs.ARG_RENDER_DISPLAY_LEX_RELATION_NAME_KEY
import org.sqlunet.provider.ProviderArgs.ARG_RENDER_DISPLAY_SEM_RELATION_NAME_KEY
import org.sqlunet.wordnet.R
import org.sqlunet.wordnet.loaders.SenseKeyModule

/**
 * A fragment representing a sense (from sensekey)
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SenseKeyFragment : SynsetFragment() {

    init {
        headerId = R.string.wordnet_sensekeys
    }

    override fun makeModule(): Module {
        val module = SenseKeyModule(this)
        module.setMaxRecursionLevel(maxRecursion)
        if (parameters != null) {
            module.setDisplayRelationNames(parameters!!.getBoolean(ARG_RENDER_DISPLAY_SEM_RELATION_NAME_KEY, true), parameters!!.getBoolean(ARG_RENDER_DISPLAY_LEX_RELATION_NAME_KEY, true))
        }
        return module
    }
}
