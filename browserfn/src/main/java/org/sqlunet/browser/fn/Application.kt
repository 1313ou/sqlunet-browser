/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.fn

import android.content.Context
import org.sqlunet.browser.AbstractApplication
import org.sqlunet.browser.AppContext
import org.sqlunet.framenet.FnFlags
import org.sqlunet.framenet.style.Colors
import org.sqlunet.style.Colors.setColorsFromResources

class Application : AbstractApplication() {

    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)
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


    /**
     * Build time
     *
     * @return build time
     */
    override fun buildTime(): String {
        return BuildConfig.BUILD_TIME
    }

    /**
     * Git hash
     *
     * @return git hasj
     */
    override fun gitHash(): String {
        return BuildConfig.GIT_HASH
    }

   companion object {
        init {
            FnFlags.standAlone = true
        }
    }
}
