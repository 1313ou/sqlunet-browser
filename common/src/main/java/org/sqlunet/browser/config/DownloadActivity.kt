/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import com.bbou.download.workers.DownloadActivity
import org.sqlunet.browser.EntryActivity.Companion.rerun
import org.sqlunet.browser.MenuHandler
import org.sqlunet.browser.common.R

/**
 * Download activity
 * Handles completion by re-entering application through entry activity.
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class DownloadActivity : DownloadActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set up the action bar
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.displayOptions = ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE
        }
    }

    override fun onComplete(success: Boolean) {
        Log.d(TAG, "OnComplete $success $this")
        if (success) {
            rerun(this)
        }
    }

    // M E N U

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // inflate the menu; this adds items to the type bar if it is present.
        menuInflater.inflate(R.menu.initialize, menu)
        // MenuCompat.setGroupDividerEnabled(menu, true);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle home
        if (item.itemId == android.R.id.home) {
            Log.d(TAG, "onHomePressed")
            rerun(this)
            return true
        }
        return MenuHandler.menuDispatchWhenCantRun(this, item)
    }

    companion object {
        private const val TAG = "DownloadA"
    }
}
