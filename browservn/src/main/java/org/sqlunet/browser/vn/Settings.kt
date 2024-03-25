/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.vn

import android.content.Context
import androidx.preference.PreferenceManager
import org.sqlunet.settings.Settings

/**
 * Settings
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object Settings : Settings() {

    // preferences

    private const val PREF_ENABLE_WORDNET = "pref_enable_wordnet"
    private const val PREF_ENABLE_VERBNET = "pref_enable_verbnet"
    private const val PREF_ENABLE_PROPBANK = "pref_enable_propbank"
    const val ENABLE_WORDNET = 0x1
    const val ENABLE_VERBNET = 0x10
    const val ENABLE_PROPBANK = 0x20

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

    // P R E F E R E N C E S H O R T C U T S

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
        if (sharedPref.getBoolean(PREF_ENABLE_VERBNET, true)) {
            result = result or ENABLE_VERBNET
        }
        if (sharedPref.getBoolean(PREF_ENABLE_PROPBANK, true)) {
            result = result or ENABLE_PROPBANK
        }
        if (sharedPref.getBoolean(PREF_ENABLE_WORDNET, true)) {
            result = result or ENABLE_WORDNET
        }
        return result
    }

    /**
     * Get preferred enable VerbNet flag
     *
     * @param context context
     * @return preferred enable VerbNet flag
     */
    fun getVerbNetPref(context: Context): Boolean {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPref.getBoolean(PREF_ENABLE_VERBNET, true)
    }

    /**
     * Get preferred enable PropBank flag
     *
     * @param context context
     * @return preferred enable PropBank flag
     */
    fun getPropBankPref(context: Context): Boolean {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPref.getBoolean(PREF_ENABLE_PROPBANK, true)
    }

    /**
     * Get preferred enable WordNet flag
     *
     * @param context context
     * @return preferred enable WordNet flag
     */
    fun getWordNetPref(context: Context): Boolean {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPref.getBoolean(PREF_ENABLE_WORDNET, true)
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
        WORDNET(ENABLE_WORDNET),
        VERBNET(ENABLE_VERBNET),
        PROPBANK(ENABLE_PROPBANK);

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
        XSELECTOR;

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
