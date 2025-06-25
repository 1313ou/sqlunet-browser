/*
 * Copyright (c) 2025. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.wn.common

import android.content.Context
import org.sqlunet.browser.AbstractApplication
import org.sqlunet.style.Colors

abstract class CommonApplication : AbstractApplication() {

    override fun onCreate() {
        super.onCreate()
        setAllColorsFromResources(this)
    }

    override fun setAllColorsFromResources(newContext: Context) {
        // Log.d(TAG, "setColors " + NightMode.nightModeToString(this))
        Colors.setColorsFromResources(newContext)
        org.sqlunet.wordnet.style.Colors.setColorsFromResources(newContext)
        org.sqlunet.bnc.style.Colors.setColorsFromResources(newContext)
    }
}
