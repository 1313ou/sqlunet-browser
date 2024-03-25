/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.assetpack

import android.content.Context
import androidx.preference.PreferenceManager

object Settings {
    const val PREF_DB_ASSET = "pref_db_asset"
    fun recordDbAsset(context: Context, assetPack: String?) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val edit = sharedPref.edit() 
        if (assetPack != null) {
            edit.putString(PREF_DB_ASSET, assetPack)
        } else {
            edit.remove(PREF_DB_ASSET)
        }
        edit.apply()
    }
}
