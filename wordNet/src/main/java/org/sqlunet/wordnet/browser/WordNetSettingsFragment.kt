/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.browser

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import org.sqlunet.wordnet.R
import org.sqlunet.wordnet.settings.Settings

class WordNetSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_wordnet, rootKey)

        // does not respond to XML attr inputType
        val recursePreference = preferenceManager.findPreference<EditTextPreference>(Settings.PREF_RELATION_RECURSE)!!
        recursePreference.setOnBindEditTextListener { editText: EditText -> editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED }
    }
}
