/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.wordnet.settings

import android.content.Context
import android.os.Bundle
import androidx.preference.PreferenceManager
import org.sqlunet.provider.ProviderArgs
import java.io.Serializable

object Settings {

    const val PREF_RELATION_RECURSE = "pref_relation_recurse"

    const val PREF_RELATION_FILTER = "pref_relation_filter"

    private const val PREF_DISPLAY_SEM_RELATION_NAME = "pref_display_sem_relation_name"

    private const val PREF_DISPLAY_LEX_RELATION_NAME = "pref_display_lex_relation_name"

    /**
     * Get preferred recurse max level
     *
     * @param context context
     * @return preferred recurse max level
     */
    fun getRecursePref(context: Context): Int {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val value = sharedPref.getString(PREF_RELATION_RECURSE, null) ?: return -1
        return try {
            value.toInt()
        } catch (_: NumberFormatException) {
            -1
        }
    }

    /**
     * Make render  parameters
     *
     * @param context context
     * @return bundle
     */
    fun makeParametersPref(context: Context): Bundle? {
        var b = marshalRenderParametersPref(context, null)
        b = marshalRelationFilterParametersPref(context, b)
        return b
    }

    /**
     * Marshal render parameters
     *
     * @param context context
     * @param bundle current bundle
     * @return bundle
     */
    fun marshalRenderParametersPref(context: Context, bundle: Bundle?): Bundle? {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val displaySemRelationName = sharedPref.getBoolean(PREF_DISPLAY_SEM_RELATION_NAME, true)
        val displayLexRelationName = sharedPref.getBoolean(PREF_DISPLAY_LEX_RELATION_NAME, true)
        if (displaySemRelationName && displayLexRelationName) {
            return bundle
        }
        val bundle2 = bundle ?: Bundle()
        if (!displaySemRelationName) {
            bundle2.putBoolean(ProviderArgs.ARG_RENDER_DISPLAY_SEM_RELATION_NAME_KEY, false)
        }
        if (!displayLexRelationName) {
            bundle2.putBoolean(ProviderArgs.ARG_RENDER_DISPLAY_LEX_RELATION_NAME_KEY, false)
        }
        return bundle2
    }

    /**
     * Marshal relation filter parameters
     *
     * @param context context
     * @param bundle current bundle
     * @return bundle
     */
    fun marshalRelationFilterParametersPref(context: Context, bundle: Bundle?): Bundle? {
        val relationFilter = getRelationFilter(context)
        if (relationFilter == null) {
            return bundle
        }
        val bundle2 = bundle ?: Bundle()
        bundle2.putSerializable(ProviderArgs.ARG_RELATION_FILTER_KEY, relationFilter as Serializable)
        return bundle2
    }

    /**
     * Retrieve map
     */
    private fun getRelationFilter(context: Context): Set<Int>? {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val filter = sharedPref.getLong(PREF_RELATION_FILTER, -1)
        if (filter == -1L) {
            return null
        }
        return RelationReference.entries
            .filter { !it.test(filter) }
            .map { it.id }
            .toSet()
    }
}
