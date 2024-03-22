/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.xn

import android.content.Context
import android.util.Log
import org.sqlunet.browser.AbstractApplication
import org.sqlunet.nightmode.NightMode.nightModeToString
import org.sqlunet.style.Colors

class Application : AbstractApplication() {

    override fun onCreate() {
        super.onCreate()
        Settings.initializeSelectorPrefs(this)
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

    companion object {
        private const val TAG = "Application"
    }
}