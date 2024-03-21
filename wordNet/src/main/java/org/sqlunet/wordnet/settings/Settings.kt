/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.wordnet.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import org.sqlunet.provider.ProviderArgs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

public class Settings
{
	static public final String PREF_RELATION_RECURSE = "pref_relation_recurse";
	static public final String PREF_DISPLAY_SEM_RELATION_NAME = "pref_display_sem_relation_name";
	static public final String PREF_DISPLAY_LEX_RELATION_NAME = "pref_display_lex_relation_name";

	/**
	 * Get preferred recurse max level
	 *
	 * @param context context
	 * @return preferred recurse max level
	 */
	static public int getRecursePref(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		final String value = sharedPref.getString(PREF_RELATION_RECURSE, null);
		if (value == null)
		{
			return -1;
		}
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			return -1;
		}
	}

	/**
	 * Get render  parameters
	 *
	 * @param context context
	 * @return bundle
	 */
	@Nullable
	public static Bundle getRenderParametersPref(@NonNull final Context context)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		boolean displaySemRelationName = sharedPref.getBoolean(PREF_DISPLAY_SEM_RELATION_NAME, true);
		boolean displayLexRelationName = sharedPref.getBoolean(PREF_DISPLAY_LEX_RELATION_NAME, true);
		if (displaySemRelationName && displayLexRelationName)
		{
			return null;
		}
		Bundle bundle = new Bundle();
		if (!displaySemRelationName)
		{
			bundle.putBoolean(ProviderArgs.ARG_RENDER_DISPLAY_SEM_RELATION_NAME_KEY, false);
		}
		if (!displayLexRelationName)
		{
			bundle.putBoolean(ProviderArgs.ARG_RENDER_DISPLAY_LEX_RELATION_NAME_KEY, false);
		}
		return bundle;
	}
}
