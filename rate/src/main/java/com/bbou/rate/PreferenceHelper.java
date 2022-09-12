/*
 * Copyright (c) 2022. Bernard Bou
 */
package com.bbou.rate;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Date;

import androidx.annotation.NonNull;

final class PreferenceHelper
{
	private static final String PREF_FILE_NAME = "android_rate_pref_file";

	private static final String PREF_KEY_INSTALL_DATE = "android_rate_install_date";

	private static final String PREF_KEY_LAUNCH_TIMES = "android_rate_launch_times";

	private static final String PREF_KEY_AGREE_SHOW_DIALOG = "android_rate_agree_show_dialog";

	private static final String PREF_KEY_REMIND_INTERVAL = "android_rate_remind_interval";

	private PreferenceHelper()
	{
	}

	private static SharedPreferences getPreferences(@NonNull Context context)
	{
		return context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
	}

	private static Editor getPreferencesEditor(@NonNull Context context)
	{
		return getPreferences(context).edit();
	}

	/**
	 * Clear data in shared preferences.<br/>
	 *
	 * @param context context
	 */
	static void clearSharedPreferences(@NonNull Context context)
	{
		SharedPreferences.Editor editor = getPreferencesEditor(context);
		editor.remove(PREF_KEY_INSTALL_DATE);
		editor.remove(PREF_KEY_LAUNCH_TIMES);
		editor.apply();
	}

	/**
	 * Set agree flag about show dialog.<br/>
	 * If it is false, rate dialog will never shown unless data is cleared.
	 *
	 * @param context context
	 * @param agree   agree with showing rate dialog
	 */
	static void setAgreeShowDialog(@NonNull Context context, boolean agree)
	{
		SharedPreferences.Editor editor = getPreferencesEditor(context);
		editor.putBoolean(PREF_KEY_AGREE_SHOW_DIALOG, agree);
		editor.apply();
	}

	static boolean getIsAgreeShowDialog(@NonNull Context context)
	{
		return getPreferences(context).getBoolean(PREF_KEY_AGREE_SHOW_DIALOG, true);
	}

	static void setRemindInterval(@NonNull Context context)
	{
		SharedPreferences.Editor editor = getPreferencesEditor(context);
		editor.remove(PREF_KEY_REMIND_INTERVAL);
		editor.putLong(PREF_KEY_REMIND_INTERVAL, new Date().getTime());
		editor.apply();
	}

	static long getRemindInterval(@NonNull Context context)
	{
		return getPreferences(context).getLong(PREF_KEY_REMIND_INTERVAL, 0);
	}

	static void setInstallDate(@NonNull Context context)
	{
		SharedPreferences.Editor editor = getPreferencesEditor(context);
		editor.putLong(PREF_KEY_INSTALL_DATE, new Date().getTime());
		editor.apply();
	}

	static long getInstallDate(@NonNull Context context)
	{
		return getPreferences(context).getLong(PREF_KEY_INSTALL_DATE, 0);
	}

	static void setLaunchTimes(@NonNull Context context, int launchTimes)
	{
		SharedPreferences.Editor editor = getPreferencesEditor(context);
		editor.putInt(PREF_KEY_LAUNCH_TIMES, launchTimes);
		editor.apply();
	}

	static int getLaunchTimes(@NonNull Context context)
	{
		return getPreferences(context).getInt(PREF_KEY_LAUNCH_TIMES, 0);
	}

	static boolean isFirstLaunch(@NonNull Context context)
	{
		return getPreferences(context).getLong(PREF_KEY_INSTALL_DATE, 0) == 0L;
	}
}