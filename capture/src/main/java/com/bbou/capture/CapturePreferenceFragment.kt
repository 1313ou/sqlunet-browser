/*
 * Copyright (c) 2025. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.capture

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class CapturePreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_capture)
    }
}