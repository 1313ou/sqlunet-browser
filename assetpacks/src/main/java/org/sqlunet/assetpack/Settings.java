/*
 * Copyright (c) 2020. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.assetpack;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

public class Settings
{
	public static final String PREF_DB_ASSET = "pref_db_asset";

	public static void recordDbAsset(final Context context, final String assetPack)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		final SharedPreferences.Editor edit = sharedPref.edit(); //
		if (assetPack != null)
		{
			edit.putString(PREF_DB_ASSET, assetPack);
		}
		else
		{
			edit.remove(PREF_DB_ASSET);
		}
		edit.apply();
	}
}
