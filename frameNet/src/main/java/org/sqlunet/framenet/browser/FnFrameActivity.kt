/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.browser

import androidx.fragment.app.Fragment
import org.sqlunet.browser.AbstractActivity
import org.sqlunet.framenet.R

/**
 * Frame activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FnFrameActivity : AbstractActivity() {

    override val layoutId: Int
        get() = R.layout.activity_fnframe

    override val containerId: Int
        get() = R.id.container_fnframe

    override fun makeFragment(): Fragment {
        return FnFrameFragment()
    }
}
