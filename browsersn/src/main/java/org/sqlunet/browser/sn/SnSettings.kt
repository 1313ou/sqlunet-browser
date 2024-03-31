/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.sn

import android.annotation.SuppressLint
import android.content.Context
import androidx.preference.PreferenceManager
import org.sqlunet.settings.Settings

/**
 * Settings
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object SnSettings : Settings() {

    // preferences

    private const val PREF_ENABLE_WORDNET = "pref_enable_wordnet"
    private const val PREF_ENABLE_BNC = "pref_enable_bnc"
    private const val PREF_ENABLE_SYNTAGNET = "pref_enable_syntagnet"
    const val ENABLE_WORDNET = 0x1
    const val ENABLE_BNC = 0x100
    const val ENABLE_SYNTAGNET = 0x1000

    // P R E F E R E N C E   S H O R T C U T S

    /**
     * Get preferred selector type
     *
     * @param context context
     * @return preferred selector type
     */
    @JvmStatic
    fun getXSelectorPref(context: Context): Selector {
        return Selector.getPref(context)
    }

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
        if (sharedPref.getBoolean(PREF_ENABLE_SYNTAGNET, true)) {
            result = result or ENABLE_SYNTAGNET
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

    /**
     * Get preferred enable SyntagNet flag
     *
     * @param context context
     * @return preferred enable SyntagNet flag
     */
    @JvmStatic
    fun getSyntagNetPref(context: Context): Boolean {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPref.getBoolean(PREF_ENABLE_SYNTAGNET, true)
    }

    /**
     * Initialize selector preferences
     *
     * @param context context
     */
    @SuppressLint("CommitPrefEdits", "ApplySharedPref")
    fun initializeSelectorPrefs(context: Context) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPref.edit()
        val selectorString = sharedPref.getString(PREF_SELECTOR, null)
        if (selectorString == null) {
            editor.putString(PREF_SELECTOR, Selector.XSELECTOR.name)
        }
        editor.commit()
    }

    // D A T A

    /**
     * Source
     *
     * @param mask mask
     */
    enum class Source(
        private val mask: Int,
    ) {

        WORDNET(0x1),
        BNC(0x2),
        SYNTAGNET(0x1000);

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

    // S E L E C T O R   T Y P E

    /**
     * Selectors
     */
    enum class Selector {

        SELECTOR,
        XSELECTOR,
        SELECTOR_ALT;

        /**
         * Set this selector as preferred selector
         *
         * @param context context
         */
        fun setPref(context: Context) {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPref.edit().putString(PREF_SELECTOR, name).apply()
        }

        companion object {

            /**
             * Get preferred mode
             *
             * @param context context
             * @return preferred selector mode
             */
            @JvmStatic
            fun getPref(context: Context): Selector {
                val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
                val name = sharedPref.getString(PREF_SELECTOR, XSELECTOR.name)
                var mode: Selector
                try {
                    mode = valueOf(name!!)
                } catch (e: Exception) {
                    mode = XSELECTOR
                    sharedPref.edit().putString(PREF_SELECTOR, mode.name).apply()
                }
                return mode
            }
        }
    }
}
