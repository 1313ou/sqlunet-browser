/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.xn

import android.content.Context
import org.sqlunet.browser.AbstractApplication
import org.sqlunet.browser.AppContext
import org.sqlunet.browser.BuildConfig
import org.sqlunet.framenet.FnFlags
import org.sqlunet.style.Colors

class Application : AbstractApplication() {

    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)
        XnSettings.initializeSelectorPrefs(this)
        setAllColorsFromResources(this)
    }

    override fun setAllColorsFromResources(newContext: Context) {
        //Log.d(TAG, "DayNightMode: " + nightModeToString(this))
        Colors.setColorsFromResources(newContext)
        org.sqlunet.predicatematrix.style.Colors.setColorsFromResources(newContext)
        org.sqlunet.wordnet.style.Colors.setColorsFromResources(newContext)
        org.sqlunet.framenet.style.Colors.setColorsFromResources(newContext)
        org.sqlunet.verbnet.style.Colors.setColorsFromResources(newContext)
        org.sqlunet.propbank.style.Colors.setColorsFromResources(newContext)
        org.sqlunet.bnc.style.Colors.setColorsFromResources(newContext)
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
            FnFlags.standAlone = false
        }
    }
}
