/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.fn

import android.content.Context
import org.sqlunet.browser.AbstractApplication
import org.sqlunet.framenet.FnFlags
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

    /**
     * Drop data
     *
     * @return true if flagged in build config
     */
    override fun dropData(): Boolean {
        return BuildConfig.DROP_DATA
    }

    companion object {
        init {
            FnFlags.standAlone = true
        }
    }
}
