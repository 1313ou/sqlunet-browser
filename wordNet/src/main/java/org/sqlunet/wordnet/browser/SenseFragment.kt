/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.browser

import org.sqlunet.browser.Module
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
        baseModuleSettings(module)
        return module
    }

    companion object {

        const val FRAGMENT_TAG = "wordnet_sense"
    }
}
