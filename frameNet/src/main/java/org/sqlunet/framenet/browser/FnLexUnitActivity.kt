/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.browser

import androidx.fragment.app.Fragment
import org.sqlunet.browser.AbstractDataActivity
import org.sqlunet.framenet.R

/**
 * Lex unit activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FnLexUnitActivity : AbstractDataActivity() {

    override val layoutId: Int
        get() = R.layout.activity_fnlexunit

    override val containerId: Int
        get() = R.id.container_fnlexunit

    override fun makeFragment(): Fragment {
        return FnLexUnitFragment()
    }
}
