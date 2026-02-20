/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.ewn

import android.content.Context
import android.util.Log
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.sqlunet.browser.MainActivity
import org.sqlunet.browser.NightMode.checkDarkMode
import org.sqlunet.browser.ewn.ColorsLib.dumpDefaultColors
import org.sqlunet.browser.ewn.ColorsLib.testColorsFromResources
import org.sqlunet.bnc.R as BNCR
import org.sqlunet.browser.common.R as CommonR
import org.sqlunet.wordnet.R as WordNetR
import org.sqlunet.xnet.R as XNetR

@LargeTest
abstract class AbstractColors {

    abstract val mode: Int

    @Rule
    @JvmField
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var context: Context

    @Before
    @Throws(Throwable::class)
    fun before() {
        context = ColorsLib.getContext(mode)
        UiThreadStatement.runOnUiThread {
            AppCompatDelegate.setDefaultNightMode(mode)
            dumpDefaultColors(this.context)
            @ColorInt val defaultColors = ColorsLib.getDefaultColorAttrs(context)
            Log.i(LOGTAG, String.format("Default color #%x on #%x", defaultColors[1], defaultColors[0]))
        }
    }

    @Test
    @Throws(ColorsLib.IllegalColorPair::class)
    fun colorContrast() {
        assertTrue(checkDarkMode(mode))
        testColorsFromResources(context, CommonR.array.palette_ui, false)
        testColorsFromResources(context, XNetR.array.palette, false)
        testColorsFromResources(context, WordNetR.array.palette_wn, false)
        testColorsFromResources(context, BNCR.array.palette_bnc, false)
    }

    @Test
    fun colorContrastFail() {
        assertTrue(checkDarkMode(mode))
        try {
            testColorsFromResources(context, CommonR.array.palette_ui, true)
            testColorsFromResources(context, XNetR.array.palette, true)
            testColorsFromResources(context, WordNetR.array.palette_wn, true)
            testColorsFromResources(context, BNCR.array.palette_bnc, true)
        } catch (ce: ColorsLib.IllegalColorPair) {
            Log.e(LOGTAG, ce.message)
            fail(ce.message)
        }
    }

    companion object {

        private const val LOGTAG = "Colors"
    }
}