/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.browser

import androidx.fragment.app.Fragment
import org.sqlunet.browser.AbstractActivity
import org.sqlunet.propbank.R

/**
 * PropBank role set activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class PbRoleSetActivity : AbstractActivity() {

    override val layoutId: Int
        get() = R.layout.activity_pbroleset

    override val containerId: Int
        get() = R.id.container_pbroleset

    override fun makeFragment(): Fragment {
        return PbRoleSetFragment()
    }
}
