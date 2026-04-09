/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.browser

import androidx.fragment.app.Fragment
import org.sqlunet.browser.AbstractDataActivity
import org.sqlunet.syntagnet.R

/**
 * SyntagNet collocation activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class CollocationActivity : AbstractDataActivity() {

    override val layoutId: Int
        get() = R.layout.activity_collocation

    override val containerId: Int
        get() = R.id.container_collocation

    override fun makeFragment(): Fragment {
        return CollocationFragment()
    }
}
