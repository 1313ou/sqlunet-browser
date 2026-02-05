/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import org.sqlunet.browser.NightMode.createOverrideConfigurationForDayNight

abstract class AbstractBrowse2Activity : BaseActivity() {

    override fun onNightModeChanged(mode: Int) {
        super.onNightModeChanged(mode)
        val overrideConfig = createOverrideConfigurationForDayNight(this, mode)
        application.onConfigurationChanged(overrideConfig)
    }
}
