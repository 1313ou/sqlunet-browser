/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.predicatematrix.settings

import android.content.Context
import androidx.preference.PreferenceManager

/**
 * Settings
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object Settings {

    private const val PREF_PM_MODE = "pref_pm_mode"

    /**
     * Modes
     */
    enum class PMMode {

        ROLES,
        ROWS_GROUPED_BY_ROLE,
        ROWS_GROUPED_BY_SYNSET,
        ROWS;

        /**
         * Set preferred mode to this mode
         *
         * @param context context
         * @return true if value has changed
         */
        fun setPref(context: Context): Boolean {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val prev = sharedPref.getString(PREF_PM_MODE, null)
            if (name == prev) {
                return false
            }
            sharedPref.edit().putString(PREF_PM_MODE, name).apply()
            return true
        }

        companion object {

            /**
             * Get mode preference
             *
             * @param context context
             * @return mode preference
             */
            fun getPref(context: Context): PMMode {
                val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
                val modeString = sharedPref.getString(PREF_PM_MODE, ROLES.name)
                var mode: PMMode
                try {
                    mode = valueOf(modeString!!)
                } catch (e: Exception) {
                    mode = ROLES
                    sharedPref.edit().putString(PREF_PM_MODE, mode.name).apply()
                }
                return mode
            }
        }
    }
}
