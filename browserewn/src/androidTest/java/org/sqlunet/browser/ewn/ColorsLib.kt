/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.ewn

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.annotation.ArrayRes
import androidx.annotation.StyleableRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.graphics.ColorUtils
import androidx.test.platform.app.InstrumentationRegistry
import org.sqlunet.browser.ewn.test.R
import org.sqlunet.nightmode.NightMode.toConfigurationUiMode
import org.sqlunet.style.Colors.colorToString
import org.sqlunet.style.Colors.dumpColorAttrs
import org.sqlunet.style.Colors.getColorAttrs

object ColorsLib {

    internal class IllegalColorPair(
        private val res: String,
        private val resId: Int,
        private val index: Int,
        private val backColor: Int,
        private val foreColor: Int,
        private val defaultBackColor: Int,
        private val defaultForeColor: Int,
        private val contrast: Double,
    ) : Exception() {

        override val message: String
            get() = String.format(
                    "%d %s [%02d] %s on %s (default %s on %s) with contrast %f ",
                    resId,
                    res,
                    index,
                    colorToString(foreColor),
                    colorToString(backColor),
                    colorToString(defaultForeColor),
                    colorToString(defaultBackColor),
                    contrast
            )
    }

    @Throws(IllegalColorPair::class)
    fun testColorsFromResources(context: Context, @ArrayRes paletteId: Int, throwError: Boolean) {
        val res = context.resources.getResourceName(paletteId)
        Log.i(TAG, "Palette " + context.resources.getResourceEntryName(paletteId) + " " + res)
        val defaultColors = getDefaultColorAttrs(context)
        // Log.d(LOGTAG, String.format("Effective default colors #%x on #%x", defaultColors[1], defaultColors[0]))
        val palette = context.resources.getIntArray(paletteId)
        var i = 0
        while (i < palette.size) {
            val backColor0 = palette[i]
            var backColor = backColor0
            if (backColor == 0) {
                backColor = defaultColors[0]
            }
            if (i + 1 < palette.size) {
                val foreColor0 = palette[i + 1]
                var foreColor = foreColor0
                if (foreColor == 0) {
                    foreColor = defaultColors[1]
                }
                val contrast = ColorUtils.calculateContrast(foreColor, backColor)
                val info = String.format("[%02d] Contrast %s on %s is %f", i / 2, colorToString(foreColor), colorToString(backColor), contrast)
                if (contrast < MIN_CONTRAST) {
                    Log.e(TAG, info)
                    if (throwError) {
                        throw IllegalColorPair(res, paletteId, i / 2, backColor0, foreColor0, defaultColors[0], defaultColors[1], contrast)
                    }
                } else {
                    Log.d(TAG, info)
                }
            }
            i += 2
        }
    }

    fun getContext(mode: Int): Context {
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        val themedContext: Context = ContextThemeWrapper(targetContext, R.style.MyTheme)
        val newConfig = themedContext.resources.configuration
        newConfig.uiMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv() // clear
        newConfig.uiMode = newConfig.uiMode or (toConfigurationUiMode(mode) and Configuration.UI_MODE_NIGHT_MASK) // set
        val newContext = themedContext.createConfigurationContext(newConfig)
        return ContextThemeWrapper(newContext, R.style.MyTheme)
    }

     fun getDefaultColorAttrs(context: Context): IntArray {
        @StyleableRes val resIds = intArrayOf(
                android.R.attr.colorBackground,
                android.R.attr.colorForeground
        )
        return getColorAttrs(context, R.style.MyTheme, *resIds)
    }

    fun dumpDefaultColors(context: Context) {
        val resIds = intArrayOf(
                android.R.attr.colorForeground,
                android.R.attr.colorBackground,
                android.R.attr.windowBackground,
                android.R.attr.foreground,
                android.R.attr.textColor,
                android.R.attr.color,
                R.attr.color,
                R.attr.colorOnBackground,
                R.attr.colorSurface,
                R.attr.colorOnSurface,
                R.attr.backgroundColor
        )
        dumpColorAttrs(context, R.style.MyTheme, *resIds)
    }

    private const val TAG = "Colors"

    private const val MIN_CONTRAST = 3.0f
}