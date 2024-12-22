/*
 * Copyright (c) 2019-2023. Bernard Bou
 */
package org.sqlunet.wordnet.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.sqlunet.settings.BitAggregatePreference
import org.sqlunet.settings.BitAggregatePreference.Companion.apply

import org.sqlunet.wordnet.R
import org.sqlunet.wordnet.settings.Settings.PREF_RELATION_FILTER

/**
 * RelationFilterPreferenceFragment activity
 *
 * @author Bernard Bou
 */

class RelationFilterPreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // inflate
        addPreferencesFromResource(R.xml.pref_relation_filter)

        // init
        val filter = initRelationFilter()
        val relationFilterPrefs: Array<Preference> = relationFilterPreferences()
        apply(filter, relationFilterPrefs)

        // set button
        val setButton = checkNotNull(findPreference(PREF_SET_BUTTON))
        setButton.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            onRelationPreferenceClick(relationFilterPrefs, true)
            true
        }

        // unset button
        val unsetButton = checkNotNull(findPreference(PREF_UNSET_BUTTON))
        unsetButton.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            onRelationPreferenceClick(relationFilterPrefs, false)
            true
        }
    }

    /**
     * Init relation filter
     */
    private fun initRelationFilter(): Long {
        // if no preference is persisted, persist the default
        val sharedPrefs = checkNotNull(preferenceManager.sharedPreferences)
        var filter = sharedPrefs.getLong(PREF_RELATION_FILTER, -1)
        if (filter == -1L) {
            filter = RelationReference.FILTERDEFAULT
            sharedPrefs.edit().putLong(PREF_RELATION_FILTER, filter).apply()
        }
        return filter
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun relationFilterPreferences(): Array<Preference> {
        return Array(RelationReference.entries.size) {
            val key = RelationReference.entries[it].pref
            val pref: Preference? = findPreference(key)
            pref!!
        }
    }

    companion object {

        const val PREF_SET_BUTTON = "pref_set"

        const val PREF_UNSET_BUTTON = "pref_unset"

        /**
         * Factored handler
         *
         * @param relationFilterPrefs relation preferences
         * @param value               set/unset
         */
        private fun onRelationPreferenceClick(relationFilterPrefs: Array<Preference>, value: Boolean) {
            for (preference in relationFilterPrefs) {
                val bitAggregatePreference = preference as BitAggregatePreference
                bitAggregatePreference.isChecked = value
            }
        }
    }
}
