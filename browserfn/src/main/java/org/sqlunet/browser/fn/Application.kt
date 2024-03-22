/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.fn

import android.content.Context
import org.sqlunet.browser.AbstractApplication
import org.sqlunet.framenet.style.Colors
import org.sqlunet.style.Colors.setColorsFromResources

class Application : AbstractApplication() {

    override fun onCreate() {
        super.onCreate()
        setAllColorsFromResources(this)
    }

    override fun setAllColorsFromResources(newContext: Context) {
        // Log.d(TAG, "setColors " + NightMode.nightModeToString(this))
        setColorsFromResources(newContext)
        Colors.setColorsFromResources(newContext)
    }

    companion object {
        private const val TAG = "Application"
    }
}