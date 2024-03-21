/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.wordnet.browser;

import android.os.Bundle;
import android.text.InputType;

import org.sqlunet.wordnet.R;
import org.sqlunet.wordnet.settings.Settings;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

public class WordNetSettingsFragment extends PreferenceFragmentCompat
{
	public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey)
	{
		this.setPreferencesFromResource(R.xml.pref_wordnet, rootKey);

		// does not respond to XML attr inputType
		final EditTextPreference recursePreference = getPreferenceManager().findPreference(Settings.PREF_RELATION_RECURSE);
		assert recursePreference != null;
		recursePreference.setOnBindEditTextListener((editText) -> editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED));
	}
}
