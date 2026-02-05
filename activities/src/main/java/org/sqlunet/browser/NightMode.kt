/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.ContextThemeWrapper

object NightMode {

    private const val TAG = "NightMode"

    fun toConfigurationUiMode(mode: Int): Int {
        return when (mode) {
            AppCompatDelegate.MODE_NIGHT_YES -> Configuration.UI_MODE_NIGHT_YES
            AppCompatDelegate.MODE_NIGHT_NO -> Configuration.UI_MODE_NIGHT_NO
            else -> throw IllegalStateException("Unexpected value: $mode")
        }
    }

    fun createOverrideConfigurationForDayNight(context: Context, mode: Int): Configuration {
        val newNightMode: Int = when (mode) {
            AppCompatDelegate.MODE_NIGHT_YES -> Configuration.UI_MODE_NIGHT_YES
            AppCompatDelegate.MODE_NIGHT_NO -> Configuration.UI_MODE_NIGHT_NO
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {
                // If we're following the system, we just use the system default from the application context
                val appConfig = context.applicationContext.resources.configuration
                appConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
            }

            else -> {
                val appConfig = context.applicationContext.resources.configuration
                appConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
            }
        }

        // If we're here then we can try and apply an override configuration on the Context.
        val overrideConf = Configuration()
        overrideConf.fontScale = 0f
        overrideConf.uiMode = newNightMode or (overrideConf.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv())
        return overrideConf
    }

    fun wrapContext(context: Context, newConfig: Configuration?, @StyleRes themeId: Int): Context {
        val newContext = context.createConfigurationContext(newConfig!!)
        return ContextThemeWrapper(newContext, themeId)
    }

    /**
     * Test whether in night mode.
     *
     * @param context context
     * @return true if in night mode
     */
    fun isNightMode(context: Context): Boolean {
        val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }

    /**
     * Get night mode.
     *
     * @param context context
     * @return mode to string
     */
    fun nightModeToString(context: Context): String {
        val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> "night"
            Configuration.UI_MODE_NIGHT_NO -> "day"
            Configuration.UI_MODE_NIGHT_UNDEFINED -> "undefined"
            else -> "undefined"
        }
    }

    fun checkDarkMode(expected: Int): Boolean {
        return when (val mode = AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> {
                Log.d(TAG, "Night mode")
                expected == AppCompatDelegate.MODE_NIGHT_YES
            }

            AppCompatDelegate.MODE_NIGHT_NO -> {
                Log.d(TAG, "Day mode")
                expected == AppCompatDelegate.MODE_NIGHT_NO
            }

            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {
                Log.d(TAG, "Follow system")
                expected == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }

            AppCompatDelegate.MODE_NIGHT_UNSPECIFIED,
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY,
            @Suppress("DEPRECATION")
            AppCompatDelegate.MODE_NIGHT_AUTO_TIME -> throw IllegalStateException("Unexpected value: $mode")

            else -> throw IllegalStateException("Unexpected value: $mode")
        }
    }
}