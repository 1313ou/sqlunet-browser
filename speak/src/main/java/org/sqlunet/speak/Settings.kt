/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.speak;

import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import androidx.preference.PreferenceManager;

public class Settings
{
	@Nullable
	public static String findCountry(@NotNull final Context context)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(SpeakSettingsFragment.COUNTRY_PREF, null);
	}

	@Nullable
	public static String findVoiceFor(@Nullable final String country, @NotNull final Context context)
	{
		if (country == null)
		{
			return null;
		}
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Set<String> voices = prefs.getStringSet(SpeakSettingsFragment.VOICE_PREF, null);
		if (voices != null)
		{
			for (String v : voices)
			{
				String c = v.substring(3, 5);
				if (c.equalsIgnoreCase(country))
				{
					return v;
				}
			}
		}
		return null;
	}
}
