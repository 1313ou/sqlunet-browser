/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.fn

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import junit.framework.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.sqlunet.browser.MainActivity
import org.sqlunet.browser.Seq
import org.sqlunet.browser.fn.Do.ensureDownloaded
import org.sqlunet.browser.fn.Do.searchRunFlat
import org.sqlunet.browser.fn.test.R
import org.sqlunet.browser.common.R as CommonR

@RunWith(AndroidJUnit4::class)
@LargeTest
class RunMainTest : TestCase() {

    @Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun before() {
        if (ensureDownloaded()) ActivityScenario.launch(MainActivity::class.java)
        //Actions.do_navigate(R.id.drawer_layout, R.id.nav_view, "Browse")
        Seq.doNavigate(CommonR.id.drawer_layout, CommonR.id.nav_view, CommonR.id.nav_search_browse)
    }

    @Test
    fun searchRun() {
        searchRunFlat()
    }
}