/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.preference

import android.content.Context
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.Preference.SummaryProvider
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.bbou.download.common.R

/**
 * This fragment shows download preferences only.
 */
class DownloadPreferenceFragment : PreferenceFragmentCompat() {

    /**
     * onCreatePreferences
     *
     * @param savedInstanceState saved instance state
     * @param rootKey root key
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        unrecorded = getString(R.string.pref_value_unrecorded)

        addPreferencesFromResource(R.xml.pref_download)

        val downloadModePreference = findPreference<Preference>(Settings.PREF_DOWNLOAD_MODE)!!
        downloadModePreference.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()

        val repoPreference = findPreference<Preference>(Settings.PREF_REPO)!!
        repoPreference.summaryProvider = STRING_SUMMARY_PROVIDER

        val cachePreference = findPreference<Preference>(Settings.PREF_CACHE)!!
        cachePreference.summaryProvider = DEVICE_STRING_SUMMARY_PROVIDER

        val datapackDirPreference = findPreference<Preference>(Settings.PREF_DATAPACK_DIR)!!
        datapackDirPreference.summaryProvider = DEVICE_STRING_SUMMARY_PROVIDER
    }

    companion object {
        private var unrecorded = ""

        private val STRING_SUMMARY_PROVIDER = SummaryProvider { preference: Preference -> PreferenceManager.getDefaultSharedPreferences(preference.context).getString(preference.key, unrecorded) }

        private val DEVICE_STRING_SUMMARY_PROVIDER = SummaryProvider { preference: Preference -> preference.context.getSharedPreferences(Settings.PREFERENCES_DEVICE, Context.MODE_PRIVATE).getString(preference.key, unrecorded) }
    }
}
