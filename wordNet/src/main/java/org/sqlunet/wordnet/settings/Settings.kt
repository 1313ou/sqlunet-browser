/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.wordnet.settings

import android.content.Context
import android.os.Bundle
import androidx.preference.PreferenceManager
import org.sqlunet.provider.ProviderArgs

object Settings {

    const val PREF_RELATION_RECURSE = "pref_relation_recurse"
    private const val PREF_DISPLAY_SEM_RELATION_NAME = "pref_display_sem_relation_name"
    private const val PREF_DISPLAY_LEX_RELATION_NAME = "pref_display_lex_relation_name"

    /**
     * Get preferred recurse max level
     *
     * @param context context
     * @return preferred recurse max level
     */
    @JvmStatic
    fun getRecursePref(context: Context): Int {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val value = sharedPref.getString(PREF_RELATION_RECURSE, null) ?: return -1
        return try {
            value.toInt()
        } catch (e: NumberFormatException) {
            -1
        }
    }

    /**
     * Get render  parameters
     *
     * @param context context
     * @return bundle
     */
    @JvmStatic
    fun getRenderParametersPref(context: Context): Bundle? {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val displaySemRelationName = sharedPref.getBoolean(PREF_DISPLAY_SEM_RELATION_NAME, true)
        val displayLexRelationName = sharedPref.getBoolean(PREF_DISPLAY_LEX_RELATION_NAME, true)
        if (displaySemRelationName && displayLexRelationName) {
            return null
        }
        val bundle = Bundle()
        if (!displaySemRelationName) {
            bundle.putBoolean(ProviderArgs.ARG_RENDER_DISPLAY_SEM_RELATION_NAME_KEY, false)
        }
        if (!displayLexRelationName) {
            bundle.putBoolean(ProviderArgs.ARG_RENDER_DISPLAY_LEX_RELATION_NAME_KEY, false)
        }
        return bundle
    }
}
