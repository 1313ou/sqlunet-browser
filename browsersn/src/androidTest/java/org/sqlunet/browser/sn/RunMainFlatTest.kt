/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.sn

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
import org.sqlunet.browser.sn.Do.ensureDownloaded
import org.sqlunet.browser.sn.Do.searchRunFlat

@RunWith(AndroidJUnit4::class)
@LargeTest
class RunMainFlatTest : TestCase() {

    @Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun before() {
        if (ensureDownloaded()) ActivityScenario.launch(MainActivity::class.java)
        //Actions.do_navigate(R.id.drawer_layout, R.id.nav_view, "Browse")
        Seq.doNavigate(R.id.drawer_layout, R.id.nav_view, R.id.nav_search_browse)
        //Actions.do_choose(R.id.spinner, "senses")
        Seq.doChoose(R.id.spinner, 0)
    }

    @Test
    fun searchRun() {
        searchRunFlat()
    }
}