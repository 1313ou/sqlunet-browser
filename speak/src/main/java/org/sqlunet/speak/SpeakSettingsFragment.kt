/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.speak

import android.os.Build
import android.os.Bundle
import android.speech.tts.Voice
import android.view.View
import androidx.annotation.RequiresApi
import androidx.preference.Preference
import androidx.preference.Preference.SummaryProvider
import androidx.preference.PreferenceFragmentCompat
import java.util.Locale
import java.util.stream.Collectors

class SpeakSettingsFragment : PreferenceFragmentCompat() {

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.speak_preferences, rootKey)
        val noneString = requireContext().getString(R.string.none)
        val voicePref = findPreference<ResettableMultiSelectListPreference>(VOICE_PREF)
        if (voicePref != null) {
            voicePref.entries = arrayOf(noneString)
            voicePref.entryValues = arrayOf("")
            voicePref.setSummaryProvider(SummaryProvider { pref2: Preference -> prepareSummary(pref2 as MultiSelectListPreference) })
            voicePref.setClickListener(View.OnClickListener { voicePref.setValues(HashSet()) })
            Discover().discoverVoices(requireContext()) { voices ->
                val voicesValues = prepareVoiceValues(voices)
                voicePref.entryValues = voicesValues.toTypedArray<String>()
                val voicesLabels = prepareVoiceLabels(voices)
                voicePref.entries = voicesLabels.toTypedArray<String>()
                voicePref.notifyEntriesChanged()
            }
        }
        val countryPref = findPreference<ListPreference>(COUNTRY_PREF)
        if (countryPref != null) {
            countryPref.entries = arrayOf(noneString)
            countryPref.entryValues = arrayOf("")
            Discover().discoverLanguages(requireContext()) { locales ->
                val localeValues = prepareLocaleValues(locales)
                localeValues.add("")
                countryPref.entryValues = localeValues.toTypedArray<String>()
                val localeLabels = prepareLocaleLabels(locales)
                localeLabels.add(noneString)
                countryPref.entries = localeLabels.toTypedArray<String>()
                countryPref.notifyEntriesChanged()
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun prepareVoiceValues(voices: List<Voice>): List<String> {
        return voices.stream().map { obj: Voice -> obj.name }.collect(Collectors.toList())
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun prepareVoiceLabels(voices: List<Voice>): List<String> {
        return voices.stream().map { voice: Voice -> voice.name + " " + if (voice.isNetworkConnectionRequired) "N" else "L" }.collect(Collectors.toList())
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun prepareLocaleValues(locales: List<Locale>): MutableList<String> {
        return locales.stream().map { obj: Locale -> obj.country }.collect(Collectors.toList())
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun prepareLocaleLabels(locales: List<Locale>): MutableList<String> {
        return locales.stream().map { locale: Locale -> locale.country + " " + locale.language }.collect(Collectors.toList())
    }

    private fun prepareSummary(pref: MultiSelectListPreference): CharSequence {
        val noneString = requireContext().getString(R.string.none)
        val titles: MutableList<String> = ArrayList()
        val entryValues = pref.entryValues
        val persisted = pref.getPersistedStringSet(null)
        if (persisted != null) {
            if (persisted.isEmpty()) {
                return noneString
            } else {
                if (entryValues != null) {
                    for ((i, value) in entryValues.withIndex()) {
                        if (persisted.contains(value.toString())) {
                            titles.add(entryValues[i].toString().substring(3))
                        }
                    }
                }
            }
            return java.lang.String.join("\n", titles)
        }
        return noneString
    }

    companion object {

        const val VOICE_PREF = "voice"
        const val COUNTRY_PREF = "country"
    }
}
