/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import org.sqlunet.nightmode.NightMode.createOverrideConfigurationForDayNight

abstract class AbstractBrowse1Activity : AppCompatCommonActivity() {

    override fun onNightModeChanged(mode: Int) {
        super.onNightModeChanged(mode)
        val overrideConfig = createOverrideConfigurationForDayNight(this, mode)
        application.onConfigurationChanged(overrideConfig)
    }
}
