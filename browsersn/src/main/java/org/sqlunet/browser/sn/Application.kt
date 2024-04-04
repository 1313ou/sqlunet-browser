/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.sn

import android.content.Context
import org.sqlunet.browser.AbstractApplication
import org.sqlunet.style.Colors

class Application : AbstractApplication() {

    override fun onCreate() {
        super.onCreate()
        SnSettings.initializeSelectorPrefs(this)
        setAllColorsFromResources(this)
    }

    override fun setAllColorsFromResources(newContext: Context) {
        // Log.d(TAG, "setColors " + NightMode.nightModeToString(this))
        Colors.setColorsFromResources(newContext)
        org.sqlunet.syntagnet.style.Colors.setColorsFromResources(newContext)
        org.sqlunet.wordnet.style.Colors.setColorsFromResources(newContext)
        org.sqlunet.bnc.style.Colors.setColorsFromResources(newContext)
    }
}
