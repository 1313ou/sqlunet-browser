/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import junit.framework.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.sqlunet.browser.Do.searchRunFlat
import org.sqlunet.browser.xn.BrowseActivity
import org.sqlunet.browser.common.R as CommonR

@RunWith(AndroidJUnit4::class)
@LargeTest
class RunBrowseFlatTest : TestCase() {

    @Rule
    var activityScenarioRule = ActivityScenarioRule(BrowseActivity::class.java)

    @Before
    fun before() {
        //Actions.do_choose(R.id.spinner, "senses")
        Seq.doChoose(CommonR.id.spinner, 0)
    }

    @Test
    fun searchRun() {
        searchRunFlat()
    }
}