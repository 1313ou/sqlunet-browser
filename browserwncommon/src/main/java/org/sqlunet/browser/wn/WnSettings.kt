/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.wn

import android.content.Context
import androidx.preference.PreferenceManager
import org.sqlunet.settings.Settings

/**
 * Settings
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object WnSettings : Settings() {

    // preferences
    private const val PREF_ENABLE_WORDNET = "pref_enable_wordnet"
    private const val PREF_ENABLE_BNC = "pref_enable_bnc"
    const val ENABLE_WORDNET = 0x1
    const val ENABLE_BNC = 0x100

    // P R E F E R E N C E   S H O R T C U T S

    /**
     * Get preferred enable aggregated flag
     *
     * @param context context
     * @return preferred enable WordNet flag
     */
    @JvmStatic
    fun getAllPref(context: Context): Int {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        var result = 0
        if (sharedPref.getBoolean(PREF_ENABLE_WORDNET, true)) {
            result = result or ENABLE_WORDNET
        }
        if (sharedPref.getBoolean(PREF_ENABLE_BNC, true)) {
            result = result or ENABLE_BNC
        }
        return result
    }

    /**
     * Get preferred enable WordNet flag
     *
     * @param context context
     * @return preferred enable WordNet flag
     */
    @JvmStatic
    fun getWordNetPref(context: Context): Boolean {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPref.getBoolean(PREF_ENABLE_WORDNET, true)
    }

    /**
     * Get preferred enable BNC flag
     *
     * @param context context
     * @return preferred enable BNC flag
     */
    @JvmStatic
    fun getBncPref(context: Context): Boolean {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPref.getBoolean(PREF_ENABLE_BNC, true)
    }

    // D A T A

    /**
     * Source
     */
    enum class Source
    /**
     * Constructor
     *
     * @param mask mask
     */(
        /**
         * Source mask
         */
        private val mask: Int
    ) {

        WORDNET(0x1),
        BNC(0x2);

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
