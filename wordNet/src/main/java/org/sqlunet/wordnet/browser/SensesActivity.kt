/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.browser

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import org.sqlunet.browser.BaseActivity
import org.sqlunet.wordnet.R
import org.sqlunet.activities.R as ActivitiesR

/**
 * Synset activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SensesActivity : BaseActivity() {

    private var fromSavedInstance = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // content
        setContentView(R.layout.activity_senses)

        // toolbar
        val toolbar = findViewById<Toolbar>(ActivitiesR.id.toolbar)
        setSupportActionBar(toolbar)

        // set up the action bar
        val actionBar = supportActionBar!!
        actionBar.displayOptions = ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE

        // fragment
        // savedInstanceState is non-null when there is fragment state saved from previous configurations of this activity (e.g. when rotating the screen from
        // portrait to landscape). In this case, the fragment will automatically be re-added to its container so we don't need to manually addItem it.
        // @see http://developer.android.com/guide/components/fragments.html
        if (savedInstanceState != null) {
            fromSavedInstance = true
        }
    }

    public override fun onStart() {
        super.onStart()
        if (!fromSavedInstance) {
            // create the senses fragment, transmit intent's extras as parameters and addItem it to the activity using a fragment transaction
            val manager = supportFragmentManager
            var fragment = manager.findFragmentByTag(SensesFragment.FRAGMENT_TAG)
            if (fragment == null) {
                fragment = SensesFragment()
                val args = intent.extras
                fragment.setArguments(args)
            }
            manager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.container_senses, fragment, SensesFragment.FRAGMENT_TAG)
                // .addToBackStack(SensesFragment.FRAGMENT_TAG)
                .commit()
        }
    }
}
