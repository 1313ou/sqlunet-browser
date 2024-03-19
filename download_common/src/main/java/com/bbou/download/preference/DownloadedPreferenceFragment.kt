/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.preference

import android.content.Context
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.Preference.SummaryProvider
import androidx.preference.PreferenceFragmentCompat
import com.bbou.download.common.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * This fragment shows download preferences
 */
class DownloadedPreferenceFragment : PreferenceFragmentCompat() {

    /**
     * onCreatePreferences
     *
     * @param savedInstanceState saved instance state
     * @param rootKey root key
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        unrecorded = getString(R.string.pref_value_unrecorded)

        val manager = this.preferenceManager
        manager.sharedPreferencesName = Settings.PREFERENCES_DATAPACK
        manager.sharedPreferencesMode = Context.MODE_PRIVATE
        addPreferencesFromResource(R.xml.pref_downloaded)
        val namePreference = findPreference<Preference>(Settings.PREF_DATAPACK_NAME)!!
        namePreference.summary = DATAPACK_STRING_SUMMARY_PROVIDER.provideSummary(namePreference)
        val datePreference = findPreference<Preference>(Settings.PREF_DATAPACK_DATE)!!
        datePreference.summary = DATAPACK_DATE_SUMMARY_PROVIDER.provideSummary(datePreference)
        val sizePreference = findPreference<Preference>(Settings.PREF_DATAPACK_SIZE)!!
        sizePreference.summary = DATAPACK_LONG_SUMMARY_PROVIDER.provideSummary(sizePreference)
        val sourcePreference = findPreference<Preference>(Settings.PREF_DATAPACK_SOURCE)!!
        sourcePreference.summary = DATAPACK_STRING_SUMMARY_PROVIDER.provideSummary(sourcePreference)
        val sourceTypePreference = findPreference<Preference>(Settings.PREF_DATAPACK_SOURCE_TYPE)!!
        sourceTypePreference.summary = DATAPACK_STRING_SUMMARY_PROVIDER.provideSummary(sourceTypePreference)
        val sourceDatePreference = findPreference<Preference>(Settings.PREF_DATAPACK_SOURCE_DATE)!!
        sourceDatePreference.summary = DATAPACK_DATE_SUMMARY_PROVIDER.provideSummary(sourceDatePreference)
        val sourceSizePreference = findPreference<Preference>(Settings.PREF_DATAPACK_SOURCE_SIZE)!!
        sourceSizePreference.summary = DATAPACK_LONG_SUMMARY_PROVIDER.provideSummary(sourceSizePreference)
        val sourceEtagPreference = findPreference<Preference>(Settings.PREF_DATAPACK_SOURCE_ETAG)!!
        sourceEtagPreference.summary = DATAPACK_STRING_SUMMARY_PROVIDER.provideSummary(sourceEtagPreference)
        val sourceVersionPreference = findPreference<Preference>(Settings.PREF_DATAPACK_SOURCE_VERSION)!!
        sourceVersionPreference.summary = DATAPACK_STRING_SUMMARY_PROVIDER.provideSummary(sourceVersionPreference)
        val sourceStaticVersionPreference = findPreference<Preference>(Settings.PREF_DATAPACK_SOURCE_STATIC_VERSION)!!
        sourceStaticVersionPreference.summary = DATAPACK_STRING_SUMMARY_PROVIDER.provideSummary(sourceStaticVersionPreference)

        // unset button
        val unsetButton = findPreference<Preference>(Settings.PREF_DATAPACK_CLEAR_BUTTON)!!
        unsetButton.onPreferenceClickListener = Preference.OnPreferenceClickListener { preference: Preference ->
            preference.context.getSharedPreferences(Settings.PREFERENCES_DATAPACK, Context.MODE_PRIVATE).edit()
                //.remove(Settings.PREF_DATAPACK_NAME)
                .remove(Settings.PREF_DATAPACK_DATE)
                .remove(Settings.PREF_DATAPACK_SIZE)
                .remove(Settings.PREF_DATAPACK_SOURCE)
                .remove(Settings.PREF_DATAPACK_SOURCE_DATE)
                .remove(Settings.PREF_DATAPACK_SOURCE_SIZE)
                .remove(Settings.PREF_DATAPACK_SOURCE_ETAG)
                .remove(Settings.PREF_DATAPACK_SOURCE_VERSION)
                .remove(Settings.PREF_DATAPACK_SOURCE_STATIC_VERSION)
                .apply()
            namePreference.summary = DATAPACK_STRING_SUMMARY_PROVIDER.provideSummary(namePreference)
            datePreference.summary = DATAPACK_DATE_SUMMARY_PROVIDER.provideSummary(datePreference)
            sizePreference.summary = DATAPACK_LONG_SUMMARY_PROVIDER.provideSummary(sizePreference)
            sourcePreference.summary = DATAPACK_STRING_SUMMARY_PROVIDER.provideSummary(sourcePreference)
            sourceDatePreference.summary = DATAPACK_DATE_SUMMARY_PROVIDER.provideSummary(sourceDatePreference)
            sourceSizePreference.summary = DATAPACK_LONG_SUMMARY_PROVIDER.provideSummary(sourceSizePreference)
            sourceEtagPreference.summary = DATAPACK_STRING_SUMMARY_PROVIDER.provideSummary(sourceEtagPreference)
            sourceVersionPreference.summary = DATAPACK_STRING_SUMMARY_PROVIDER.provideSummary(sourceVersionPreference)
            sourceStaticVersionPreference.summary = DATAPACK_STRING_SUMMARY_PROVIDER.provideSummary(sourceStaticVersionPreference)
            true
        }
    }

    companion object {
        private var unrecorded = ""

        private val DATAPACK_STRING_SUMMARY_PROVIDER = SummaryProvider { preference: Preference? ->
            preference!!.context.getSharedPreferences(Settings.PREFERENCES_DATAPACK, Context.MODE_PRIVATE).getString(
                preference.key, unrecorded
            )
        }

        private val DATAPACK_LONG_SUMMARY_PROVIDER = SummaryProvider { preference: Preference? ->
            val value = preference!!.context.getSharedPreferences(Settings.PREFERENCES_DATAPACK, Context.MODE_PRIVATE).getLong(preference.key, -1)
            if (value == -1L) unrecorded else value.toString()
        }

        private val DATAPACK_DATE_SUMMARY_PROVIDER = SummaryProvider { preference: Preference? ->
            val value = preference!!.context.getSharedPreferences(Settings.PREFERENCES_DATAPACK, Context.MODE_PRIVATE).getLong(preference.key, -1)
            if (value == -1L) unrecorded else SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(Date(value))
        }
    }
}
