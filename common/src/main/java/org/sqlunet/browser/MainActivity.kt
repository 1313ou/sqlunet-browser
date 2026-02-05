/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.bbou.rate.AppRate.Companion.invoke
import com.google.android.material.navigation.NavigationView
import org.sqlunet.browser.common.R
import org.sqlunet.nightmode.NightMode.createOverrideConfigurationForDayNight
import org.sqlunet.settings.StorageSettings

/**
 * Main activity
 */
open class MainActivity : BaseActivityWithDrawer() {

    private var appBarConfiguration: AppBarConfiguration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // rate
        invoke(this)

        // info
        Log.d(TAG, "Database:" + StorageSettings.getDatabasePath(baseContext))

        // content
        setContentView(R.layout.activity_main)

        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // navigation top destinations
        val topDests: IntArray
        resources.obtainTypedArray(R.array.drawer_top_dest).let {
            val len = it.length()
            topDests = IntArray(len)
            for (i in 0 until len) {
                topDests[i] = it.getResourceId(i, 0)
            }
            it.recycle()
        }

        // navigation
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val navController = findNavController(this, R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration.Builder(*topDests).setOpenableLayout(drawer).build()
        setupActionBarWithNavController(this, navController, appBarConfiguration!!)
        setupWithNavController(navView, navController)
        navController.addOnDestinationChangedListener { _: NavController?, destination: NavDestination, _: Bundle? -> Log.d(TAG, "Nav: to $destination") }
    }

    override fun onResume() {
        super.onResume()
        // check hook
        EntryActivity.branchOffToLoadIfCantRun(this)
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

    // S E A R C H

    /**
     * Handle intent dispatched by search view (either onCreate or onNewIntent if activity is single top)
     *
     * @param intent intent
     */
    private fun handleSearchIntent(intent: Intent) {
        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?)!!
        if (!navHostFragment.isAdded) {
            return
        }
        val manager = navHostFragment.getChildFragmentManager()
        val fragments = manager.fragments
        val fragment = fragments[0]
        if (fragment is BaseSearchFragment) {
            val action = intent.action
            val isActionView = Intent.ACTION_VIEW == action
            if (isActionView || Intent.ACTION_SEARCH == action) {
                // search query submit (SEARCH) or suggestion selection (when a suggested item is selected) (VIEW)
                val query = intent.getStringExtra(SearchManager.QUERY)!!
                if (isActionView) {
                    fragment.clearQuery()
                }
                // search query submit or suggestion selection (when a suggested item is selected)
                Log.d(TAG, "Search intent having query '$query'")
                fragment.search(query)
            }
        }
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

    // N A V

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(this, R.id.nav_host_fragment)
        return navigateUp(navController, appBarConfiguration!!) || super.onSupportNavigateUp()
    }

    companion object {

        private const val TAG = "MainA"
    }
}
