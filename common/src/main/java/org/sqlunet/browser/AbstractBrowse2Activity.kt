/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.view.Menu
import android.view.MenuItem
import org.sqlunet.browser.MenuHandler.menuDispatch
import org.sqlunet.browser.NightMode.createOverrideConfigurationForDayNight
import org.sqlunet.browser.common.R

abstract class AbstractBrowse2Activity : BaseActivity() {

    override fun onNightModeChanged(mode: Int) {
        super.onNightModeChanged(mode)
        val overrideConfig = createOverrideConfigurationForDayNight(this, mode)
        application.onConfigurationChanged(overrideConfig)
    }

    // M E N U

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        menuInflater.inflate(R.menu.activity_theme, menu)
        menuInflater.inflate(R.menu.activity_capture, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return menuDispatch(this, item)
    }
}
