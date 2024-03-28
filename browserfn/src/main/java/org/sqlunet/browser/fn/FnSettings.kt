/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.fn

import android.content.Context
import androidx.preference.PreferenceManager
import org.sqlunet.settings.Settings

/**
 * Settings
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object FnSettings : Settings() {

    // preferences
    private const val PREF_ENABLE_FRAMENET = "pref_enable_framenet"

    // P R E F E R E N C E S H O R T C U T S

    /**
     * Get preferred enable FrameNet flag
     *
     * @param context context
     * @return preferred enable FrameNet flag
     */
    @JvmStatic
    fun getFrameNetPref(context: Context): Boolean {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPref.getBoolean(PREF_ENABLE_FRAMENET, true)
    }

    // D A T A

    /**
     * Source
     *
     * @param mask mask
     */
    enum class Source(
        /**
         * Source mask
         */
        private val mask: Int,
    ) {
        FRAMENET(0x40);

        /**
         * Set this source in sources
         *
         * @param sources sources to set
         * @return result
         */
        fun set(sources: Int): Int {
            return sources or mask
        }

        /**
         * Test
         *
         * @param sources sources to test
         * @return true if this source is set
         */
        fun test(sources: Int): Boolean {
            return sources and mask != 0
        }
    }
}
