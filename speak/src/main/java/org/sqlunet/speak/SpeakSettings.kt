/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.speak

import android.content.Context
import androidx.preference.PreferenceManager

object SpeakSettings {

    @JvmStatic
    fun findCountry(context: Context): String? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(SpeakSettingsFragment.COUNTRY_PREF, null)
    }

    @JvmStatic
    fun findVoiceFor(country: String?, context: Context): String? {
        if (country == null) {
            return null
        }
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val voices = prefs.getStringSet(SpeakSettingsFragment.VOICE_PREF, null)
        if (voices != null) {
            for (v in voices) {
                val c = v.substring(3, 5)
                if (c.equals(country, ignoreCase = true)) {
                    return v
                }
            }
        }
        return null
    }
}
