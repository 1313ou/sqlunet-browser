/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.browser

import androidx.fragment.app.Fragment

/**
 * Sense activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SenseKeyActivity : SynsetActivity() {

    override fun makeFragment(): Fragment {
        return SenseKeyFragment()
    }
}
