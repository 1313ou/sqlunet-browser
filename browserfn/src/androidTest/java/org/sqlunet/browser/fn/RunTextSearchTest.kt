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
import org.sqlunet.browser.fn.Do.ensureTextSearchSetup
import org.sqlunet.browser.fn.Do.textSearchRun
import org.sqlunet.browser.fn.test.R

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
        Seq.doNavigate(R.id.drawer_layout, R.id.nav_view, R.id.nav_status)
        ensureTextSearchSetup(org.sqlunet.browser.fn.R.id.searchtextFnButton)
        Seq.doNavigate(R.id.drawer_layout, R.id.nav_view, R.id.nav_search_text)
    }

    @Test
    fun searchVnRun() {
        textSearchRun(-1)
    }
}