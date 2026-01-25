/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.vn

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
import org.sqlunet.browser.vn.Do.ensureDownloaded
import org.sqlunet.browser.vn.Do.ensureTextSearchSetup
import org.sqlunet.browser.vn.Do.textSearchRun
import org.sqlunet.browser.vn.test.R
import org.sqlunet.browser.common.R as CommonR

@RunWith(AndroidJUnit4::class)
@LargeTest
class RunTextSearchTest : TestCase() {

    @Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun before() {
        if (ensureDownloaded()) {
            ActivityScenario.launch(MainActivity::class.java)
        }
        Seq.doNavigate(CommonR.id.drawer_layout, CommonR.id.nav_view, CommonR.id.nav_status)
        ensureTextSearchSetup(org.sqlunet.browser.vn.R.id.searchtextVnButton)
        ensureTextSearchSetup(org.sqlunet.browser.vn.R.id.searchtextPbButton)
        Seq.doNavigate(CommonR.id.drawer_layout, CommonR.id.nav_view, CommonR.id.nav_search_text)
    }

    @Test
    fun searchVnRun() {
        textSearchRun(0)
    }

    @Test
    fun searchPbRun() {
        textSearchRun(1)
    }
}