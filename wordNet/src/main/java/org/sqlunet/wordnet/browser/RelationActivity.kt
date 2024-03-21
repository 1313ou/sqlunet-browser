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
class RelationActivity : AbstractActivity() {

    override val layoutId: Int
        get() = R.layout.activity_relation

    override val containerId: Int
        get() = R.id.container_relation

    override fun makeFragment(): Fragment {
        return RelationFragment()
    }
}
