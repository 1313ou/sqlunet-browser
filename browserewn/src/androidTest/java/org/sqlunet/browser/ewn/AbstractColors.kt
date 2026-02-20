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
            // Colors.dumpDefaultColors(this.context)
            @ColorInt val defaultColors = ColorsLib.getDefaultColorAttrs(context)
            Log.i(LOGTAG, String.format("Default color #%x on #%x", defaultColors[1], defaultColors[0]))
        }
    }

    @Test
    @Throws(ColorsLib.IllegalColorPair::class)
    fun colorContrast() {
        assertTrue(checkDarkMode(mode))
        ColorsLib.testColorsFromResources(context, CommonR.array.palette_ui, false)
        ColorsLib.testColorsFromResources(context, XNetR.array.palette, false)
        ColorsLib.testColorsFromResources(context, WordNetR.array.palette_wn, false)
        ColorsLib.testColorsFromResources(context, BNCR.array.palette_bnc, false)
    }

    @Test
    fun colorContrastXNet() {
        assertTrue(checkDarkMode(mode))
        try {
            ColorsLib.testColorsFromResources(context, CommonR.array.palette_ui, true)
            ColorsLib.testColorsFromResources(context, XNetR.array.palette, true)
            ColorsLib.testColorsFromResources(context, WordNetR.array.palette_wn, true)
            ColorsLib.testColorsFromResources(context, BNCR.array.palette_bnc, true)
        } catch (ce: ColorsLib.IllegalColorPair) {
            Log.e(LOGTAG, ce.message)
            fail(ce.message)
        }
    }

    companion object {

        private const val LOGTAG = "ColorsDay"
    }
}