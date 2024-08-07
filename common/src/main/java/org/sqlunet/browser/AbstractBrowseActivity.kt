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
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import org.sqlunet.browser.common.R
import org.sqlunet.nightmode.NightMode.createOverrideConfigurationForDayNight

/**
 * Browse activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class AbstractBrowseActivity<F : BaseSearchFragment?> : AppCompatActivity() {

    /**
     * Fragment
     */
    protected var fragment: F? = null

    @get:LayoutRes
    protected open val layoutId: Int
        get() = R.layout.activity_browse

    @get:IdRes
    protected open val fragmentId: Int
        get() = R.id.fragment_browse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // content
        setContentView(layoutId)

        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // fragment
        if (savedInstanceState == null) {
            fragment = safeCast(supportFragmentManager.findFragmentById(fragmentId))
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
    protected open fun handleSearchIntent(intent: Intent) {
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
                    // this.fragment.clearQuery()
                    fragment!!.search(query)
                }
            }
        }
    }
}
