/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.sn.xselector

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import org.sqlunet.browser.AbstractBrowse1Activity
import org.sqlunet.browser.BaseBrowse1Fragment
import org.sqlunet.browser.sn.R
import org.sqlunet.browser.common.R as CommonR

/**
 * X selector activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class XBrowse1Activity : AbstractBrowse1Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // content
        setContentView(R.layout.activity_browse1)

        // toolbar
        val toolbar = findViewById<Toolbar>(CommonR.id.toolbar)
        setSupportActionBar(toolbar)

        // fragment
        // savedInstanceState is non-null when there is fragment state saved from previous configurations of this activity (e.g. when rotating the screen from
        // portrait to landscape). In this case, the fragment will automatically be re-added to its container so we don't need to manually addItem it.
        // @see http://developer.android.com/guide/components/fragments.html
        if (savedInstanceState == null) {
            val fragment: Fragment = XBrowse1Fragment()
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