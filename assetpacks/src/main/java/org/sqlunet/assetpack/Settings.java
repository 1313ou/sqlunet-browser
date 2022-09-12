/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.assetpack;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

public class Settings
{
	public static final String PREF_DB_ASSET = "pref_db_asset";

	public static void recordDbAsset(@NonNull final Context context, @Nullable final String assetPack)
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
