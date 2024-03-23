/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.content.Context
import android.util.Log
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import junit.framework.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.sqlunet.nightmode.NightMode.checkDarkMode

@RunWith(AndroidJUnit4::class)
@LargeTest
abstract class AbstractColors : TestCase() {
    abstract val mode: Int

    @Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    private var context: Context? = null

    @Before
    @Throws(Throwable::class)
    fun before() {
        context = ColorsTest.getContext(mode)
        UiThreadStatement.runOnUiThread {
            AppCompatDelegate.setDefaultNightMode(mode)
            // Colors.dumpDefaultColors(this.context);
            @ColorInt val defaultColors = ColorsTest.getDefaultColorAttrs(context!!)
            Log.i(LOGTAG, String.format("Default color #%x on #%x", defaultColors[1], defaultColors[0]))
        }
    }

    @Test
    @Throws(ColorsTest.IllegalColorPair::class)
    fun colorContrast() {
        assertTrue(checkDarkMode(mode))
        ColorsTest.testColorsFromResources(context!!, R.array.palette_ui, false)
        ColorsTest.testColorsFromResources(context!!, R.array.palette, false)
        ColorsTest.testColorsFromResources(context!!, R.array.palette_wn, false)
        ColorsTest.testColorsFromResources(context!!, R.array.palette_fn, false)
        ColorsTest.testColorsFromResources(context!!, R.array.palette_vn, false)
        ColorsTest.testColorsFromResources(context!!, R.array.palette_pb, false)
        ColorsTest.testColorsFromResources(context!!, R.array.palette_bnc, false)
        ColorsTest.testColorsFromResources(context!!, R.array.palette_pm, false)
    }

    @Test
    fun colorContrastXNet() {
        assertTrue(checkDarkMode(mode))
        try {
            ColorsTest.testColorsFromResources(context!!, R.array.palette_ui, true)
            ColorsTest.testColorsFromResources(context!!, R.array.palette, true)
            ColorsTest.testColorsFromResources(context!!, R.array.palette_wn, true)
            ColorsTest.testColorsFromResources(context!!, R.array.palette_fn, true)
            ColorsTest.testColorsFromResources(context!!, R.array.palette_vn, true)
            ColorsTest.testColorsFromResources(context!!, R.array.palette_pb, true)
            ColorsTest.testColorsFromResources(context!!, R.array.palette_bnc, true)
            ColorsTest.testColorsFromResources(context!!, R.array.palette_pm, true)
        } catch (ce: ColorsTest.IllegalColorPair) {
            Log.e(name, ce.message!!)
            fail(ce.message)
        }
    }

    companion object {
        private const val LOGTAG = "ColorsDay"
    }
}