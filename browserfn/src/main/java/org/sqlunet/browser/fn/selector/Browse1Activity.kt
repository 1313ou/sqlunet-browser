/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.fn.selector

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import org.sqlunet.browser.AbstractBrowse1Activity
import org.sqlunet.browser.BaseBrowse1Fragment
import org.sqlunet.browser.fn.R
import org.sqlunet.browser.common.R as CommonR

/**
 * Selector activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class Browse1Activity : AbstractBrowse1Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // content
        setContentView(R.layout.activity_browse1)

        // toolbar
        val toolbar = findViewById<Toolbar>(CommonR.id.toolbar)
        setSupportActionBar(toolbar)

        // set up the action bar
        val actionBar = supportActionBar!!
        actionBar.displayOptions = ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE

        // fragment
        // savedInstanceState is non-null when there is fragment state saved from previous configurations of this activity (e.g. when rotating the screen from
        // portrait to landscape). In this case, the fragment will automatically be re-added to its container so we don't need to manually addItem it.
        // @see http://developer.android.com/guide/components/fragments.html
        if (savedInstanceState == null) {
            val fragment: Fragment = Browse1Fragment()
            fragment.setArguments(intent.extras)
            supportFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.container_browse, fragment, BaseBrowse1Fragment.FRAGMENT_TAG)
                // .addToBackStack(BaseBrowse1Fragment.FRAGMENT_TAG) 
                .commit()
        }
    }
}
