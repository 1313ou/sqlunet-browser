/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.ewn

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
import org.sqlunet.browser.ewn.Do.ensureDownloaded
import org.sqlunet.browser.ewn.Do.ensureTextSearchSetup
import org.sqlunet.browser.ewn.Do.textSearchRun
import org.sqlunet.browser.common.R as CommonR
import org.sqlunet.browser.wn.lib.R as BrowserR

@RunWith(AndroidJUnit4::class)
@LargeTest
class RunTextSearchTest : TestCase() {

    @Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun before() {
        if (ensureDownloaded()) ActivityScenario.launch(MainActivity::class.java)
        Seq.doNavigate(CommonR.id.drawer_layout, CommonR.id.nav_view, CommonR.id.nav_status)
        ensureTextSearchSetup(BrowserR.id.searchtextWnButton)
        Seq.doNavigate(CommonR.id.drawer_layout, CommonR.id.nav_view, CommonR.id.nav_search_text)
    }

    @Test
    fun searchWnDefinitionsRun() {
        textSearchRun(0)
    }

    @Test
    fun searchWnSamplesRun() {
        textSearchRun(1)
    }

    @Test
    fun searchWnWordsRun() {
        textSearchRun(1)
    }
}