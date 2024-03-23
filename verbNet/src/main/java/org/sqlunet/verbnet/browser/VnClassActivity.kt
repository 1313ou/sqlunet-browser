/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.browser

import androidx.fragment.app.Fragment
import org.sqlunet.browser.AbstractActivity
import org.sqlunet.verbnet.R

/**
 * VerbNet class activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class VnClassActivity : AbstractActivity() {

    override val layoutId: Int
        get() = R.layout.activity_vnclass

    override val containerId: Int
        get() = R.id.container_vnclass

    override fun makeFragment(): Fragment {
        return VnClassFragment()
    }
}
