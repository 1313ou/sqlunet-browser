/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.browser

import androidx.fragment.app.Fragment
import org.sqlunet.browser.AbstractDataActivity
import org.sqlunet.framenet.R

/**
 * AnnoSet activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FnAnnoSetActivity : AbstractDataActivity() {

    override val layoutId: Int
        get() = R.layout.activity_fnannoset

    override val containerId: Int
        get() = R.id.container_fnannoset

    override fun makeFragment(): Fragment {
        return FnAnnoSetFragment()
    }
}
