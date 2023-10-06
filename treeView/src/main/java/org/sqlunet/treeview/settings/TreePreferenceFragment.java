/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.treeview.settings;

import android.os.Bundle;

import org.sqlunet.treeview.R;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

public class TreePreferenceFragment extends PreferenceFragmentCompat
{
	@Override
	public void onCreatePreferences(@Nullable final Bundle savedInstanceState, @Nullable final String rootKey)
	{
		// inflate
		addPreferencesFromResource(R.xml.pref_tree);
	}
}
