/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import junit.framework.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.sqlunet.browser.Do.ensureDownloaded
import org.sqlunet.browser.Do.ensureTextSearchSetup
import org.sqlunet.browser.Do.textSearchRun
import org.sqlunet.browser.test.R

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
        Seq.do_navigate(R.id.drawer_layout, R.id.nav_view, R.id.nav_status)
        ensureTextSearchSetup(org.sqlunet.browser.R.id.searchtextWnButton)
        ensureTextSearchSetup(org.sqlunet.browser.R.id.searchtextFnButton)
        ensureTextSearchSetup(org.sqlunet.browser.R.id.searchtextVnButton)
        ensureTextSearchSetup(org.sqlunet.browser.R.id.searchtextPbButton)
        Seq.do_navigate(R.id.drawer_layout, R.id.nav_view, R.id.nav_search_text)
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
        textSearchRun(2)
    }

    @Test
    fun searchFnRun() {
        textSearchRun(3)
    }

    @Test
    fun searchVnRun() {
        textSearchRun(4)
    }

    @Test
    fun searchPbRun() {
        textSearchRun(5)
    }
}