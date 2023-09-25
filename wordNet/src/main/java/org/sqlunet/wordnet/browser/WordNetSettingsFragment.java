/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.wordnet.browser;

import android.os.Bundle;

import org.sqlunet.wordnet.R;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

public class WordNetSettingsFragment extends PreferenceFragmentCompat
{
	public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey)
	{
		this.setPreferencesFromResource(R.xml.pref_wordnet, rootKey);
	}
}
