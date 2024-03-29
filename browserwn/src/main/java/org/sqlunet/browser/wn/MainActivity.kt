/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.wn

import org.sqlunet.browser.MainActivity
import org.sqlunet.browser.wn.Oewn.hook

class MainActivity : MainActivity() {

    override fun onStart() {
        super.onStart()
        hook(this)
    }
}
