/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

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
import org.sqlunet.browser.ColorsLib.dumpDefaultColors
import org.sqlunet.browser.ColorsLib.testColorsFromResources
import org.sqlunet.browser.NightMode.checkDarkMode
import org.sqlunet.bnc.R as BNCR
import org.sqlunet.browser.common.R as CommonR
import org.sqlunet.framenet.R as FrameNetR
import org.sqlunet.predicatematrix.R as PredicateMatrixR
import org.sqlunet.propbank.R as PropbankR
import org.sqlunet.verbnet.R as VerbNetR
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
        testColorsFromResources(context, FrameNetR.array.palette_fn, false)
        testColorsFromResources(context, VerbNetR.array.palette_vn, false)
        testColorsFromResources(context, PropbankR.array.palette_pb, false)
        testColorsFromResources(context, BNCR.array.palette_bnc, false)
        testColorsFromResources(context, PredicateMatrixR.array.palette_pm, false)
    }

    @Test
    fun colorContrastFail() {
        assertTrue(checkDarkMode(mode))
        try {
            testColorsFromResources(context, CommonR.array.palette_ui, true)
            testColorsFromResources(context, XNetR.array.palette, true)
            testColorsFromResources(context, WordNetR.array.palette_wn, true)
            testColorsFromResources(context, FrameNetR.array.palette_fn, true)
            testColorsFromResources(context, VerbNetR.array.palette_vn, true)
            testColorsFromResources(context, PropbankR.array.palette_pb, true)
            testColorsFromResources(context, BNCR.array.palette_bnc, true)
            testColorsFromResources(context, PredicateMatrixR.array.palette_pm, true)
        } catch (ce: ColorsLib.IllegalColorPair) {
            Log.e(LOGTAG, ce.message)
            fail(ce.message)
        }
    }

    companion object {

        private const val LOGTAG = "Colors"
    }
}