/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.assetpack

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.core.content.edit

object Settings {

    const val PREF_DB_ASSET = "pref_db_asset"
    fun recordDbAsset(context: Context, assetPack: String?) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPref.edit {
            if (assetPack != null) {
                putString(PREF_DB_ASSET, assetPack)
            } else {
                remove(PREF_DB_ASSET)
            }
        }
    }
}
