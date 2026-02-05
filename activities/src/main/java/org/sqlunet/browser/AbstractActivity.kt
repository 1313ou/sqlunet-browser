/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import org.sqlunet.activities.R
import org.sqlunet.nightmode.NightMode.createOverrideConfigurationForDayNight

/**
 * Abstract activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class AbstractActivity : AppCompatCommonActivity() {

    protected abstract val layoutId: Int
    protected abstract val containerId: Int
    protected abstract fun makeFragment(): Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // content
        setContentView(layoutId)

        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // set up the action bar
        val actionBar = supportActionBar!!
        actionBar.displayOptions = ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE

        // fragment
        // savedInstanceState is non-null when there is fragment state saved from previous configurations of this activity (e.g. when rotating the screen from
        // portrait to landscape). In this case, the fragment will automatically be re-added to its container so we don't need to manually addItem it.
        // @see http://developer.android.com/guide/components/fragments.html
        if (savedInstanceState == null) {
            // create the sense fragment, transmit intent's extras as parameters and addItem it to the activity using a fragment transaction
            val fragment = makeFragment()
            fragment.setArguments(intent.extras)
            supportFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(containerId, fragment)
                // .addToBackStack(fragment.getTag() == null ? "tagless" : fragment.getTag()) 
                .commit()
        }
    }

    override fun onNightModeChanged(mode: Int) {
        super.onNightModeChanged(mode)
        val overrideConfig = createOverrideConfigurationForDayNight(this, mode)
        application.onConfigurationChanged(overrideConfig)
    }
}
