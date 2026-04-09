/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.browser

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import org.sqlunet.browser.AbstractDataActivity
import org.sqlunet.browser.BaseActivity
import org.sqlunet.wordnet.R
import org.sqlunet.core.R as CoreR

/**
 * Synset activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SensesActivity : AbstractDataActivity() {

    override val layoutId: Int
        get() = R.layout.activity_senses

    override val containerId: Int
        get() = R.id.container_senses

    override fun makeFragment(): Fragment {
        return SensesFragment()
    }
}
