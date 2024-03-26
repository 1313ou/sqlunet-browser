/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.fn

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import junit.framework.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.sqlunet.browser.MainActivity
import org.sqlunet.browser.fn.Do.ensureDownloaded

@RunWith(AndroidJUnit4::class)
@LargeTest
class RunDownloadTest : TestCase() {

    @Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)
    @Test
    fun download() {
        ensureDownloaded()
    }
}