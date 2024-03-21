/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.browser

import androidx.fragment.app.Fragment
import org.sqlunet.browser.AbstractActivity
import org.sqlunet.wordnet.R

/**
 * Synset activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class SynsetActivity : AbstractActivity() {

    override val layoutId: Int
        get() = R.layout.activity_synset

    override val containerId: Int
        get() = R.id.container_synset

    override fun makeFragment(): Fragment {
        return SynsetFragment()
    }
}
