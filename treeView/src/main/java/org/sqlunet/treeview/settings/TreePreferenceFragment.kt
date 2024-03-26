/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.treeview.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import org.sqlunet.treeview.R

class TreePreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_tree)
    }
}
