/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.xn

import android.annotation.SuppressLint
import android.content.Context
import androidx.preference.PreferenceManager
import org.sqlunet.settings.Settings

/**
 * Settings
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object XnSettings : Settings() {

    // preferences
    private const val PREF_ENABLE_WORDNET = "pref_enable_wordnet"
    private const val PREF_ENABLE_VERBNET = "pref_enable_verbnet"
    private const val PREF_ENABLE_PROPBANK = "pref_enable_propbank"
    private const val PREF_ENABLE_FRAMENET = "pref_enable_framenet"
    private const val PREF_ENABLE_BNC = "pref_enable_bnc"
    const val ENABLE_WORDNET = 0x1
    const val ENABLE_VERBNET = 0x10
    const val ENABLE_PROPBANK = 0x20
    const val ENABLE_FRAMENET = 0x40
    const val ENABLE_BNC = 0x100

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
        if (sharedPref.getBoolean(PREF_ENABLE_VERBNET, true)) {
            result = result or ENABLE_VERBNET
        }
        if (sharedPref.getBoolean(PREF_ENABLE_PROPBANK, true)) {
            result = result or ENABLE_PROPBANK
        }
        if (sharedPref.getBoolean(PREF_ENABLE_FRAMENET, true)) {
            result = result or ENABLE_FRAMENET
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
     * Get preferred enable VerbNet flag
     *
     * @param context context
     * @return preferred enable VerbNet flag
     */
    @JvmStatic
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
    @JvmStatic
    fun getPropBankPref(context: Context): Boolean {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPref.getBoolean(PREF_ENABLE_PROPBANK, true)
    }

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
        /**
         * Source mask
         */
        private val mask: Int,
    ) {

        WORDNET(0x1),
        BNC(0x2),
        VERBNET(0x10),
        PROPBANK(0x20),
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

    // S E L E C T O R   T Y P E

    /**
     * Selectors
     */
    enum class Selector {

        SELECTOR,
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
