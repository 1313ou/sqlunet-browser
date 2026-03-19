/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import org.sqlunet.browser.BaseActivity
import org.sqlunet.browser.EntryActivity.Companion.rerun
import org.sqlunet.browser.MenuHandler.menuDispatchWhenCantRun
import org.sqlunet.browser.common.R

/**
 * Load activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class LoadActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // content
        setContentView(R.layout.activity_load)

        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // set up the action bar
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.displayOptions = ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE
        }
    }

    // M E N U

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_initialize, menu)
        menuInflater.inflate(R.menu.activity_theme, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // handle home
            Log.d(TAG, "onHomePressed")
            rerun(this)
            return true
        }
        return menuDispatchWhenCantRun(this, item)
    }

    companion object {

        private const val TAG = "LoadA"
    }
}
