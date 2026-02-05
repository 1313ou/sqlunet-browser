/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import org.sqlunet.browser.common.R
import org.sqlunet.browser.NightMode.createOverrideConfigurationForDayNight

/**
 * Abstract search text activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class AbstractSearchTextActivity<F : BaseSearchFragment?> : BaseActivity() {

    /**
     * Fragment
     */
    private var fragment: F? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // content
        setContentView(R.layout.activity_searchtext)

        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // fragment
        if (savedInstanceState == null) {
            fragment = safeCast(supportFragmentManager.findFragmentById(R.id.fragment_searchtext))
        }
    }

    override fun onResume() {
        super.onResume()

        // check hook
        EntryActivity.branchOffToLoadIfCantRun(this)

        // handle sent intent
        handleSearchIntent(intent)
    }

    @SuppressLint("MissingSuperCall") // BUG
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleSearchIntent(intent)
    }

    override fun onNightModeChanged(mode: Int) {
        super.onNightModeChanged(mode)
        val overrideConfig = createOverrideConfigurationForDayNight(this, mode)
        application.onConfigurationChanged(overrideConfig)
    }

    // M E N U

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // inflate the menu; this adds items to the type bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        // MenuCompat.setGroupDividerEnabled(menu, true)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return MenuHandler.menuDispatch(this, item)
    }

    // S E A R C H

    /**
     * Handle intent dispatched by search view (either onCreate or onNewIntent if activity is single top)
     *
     * @param intent intent
     */
    private fun handleSearchIntent(intent: Intent) {
        val action = intent.action
        val isActionView = Intent.ACTION_VIEW == action
        if (isActionView || Intent.ACTION_SEARCH == action) {
            // search query submit (SEARCH) or suggestion selection (when a suggested item is selected) (VIEW)
            val query = intent.getStringExtra(SearchManager.QUERY)
            if (query != null && fragment != null) {
                if (isActionView) {
                    fragment!!.clearQuery()
                }
                fragment!!.search(query)
            }
        } else if (Intent.ACTION_SEND == action) {
            val type = intent.type
            if ("text/plain" == type) {
                val query = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (query != null && fragment != null) {
                    fragment!!.search(query)
                }
            }
        }
    }
}
