/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.wn;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View;

import com.bbou.others.OthersActivity;
import com.google.android.material.behavior.SwipeDismissBehavior;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

public class Oewn
{
	private static final String PREF_FILE_NAME = "android_launches_pref_file";
	private static final String PREF_KEY_LAUNCH_TIMES = "android_launch_times";
	private static final String PREF_KEY_OEWN_NO_NOTICE = "pref_oewn_no_notice";

	private static final int HOW_OFTEN = 10;

	public static void hook(@NonNull final Activity activity)
	{
		final SharedPreferences prefs = activity.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
		int launchTimes = prefs.getInt(PREF_KEY_LAUNCH_TIMES, 0);
		if (launchTimes % HOW_OFTEN == 1)
		{
			final SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(activity);
			boolean noNotice = prefs2.getBoolean(PREF_KEY_OEWN_NO_NOTICE, false);
			if (!noNotice)
			{
				suggestOewn(activity);
			}
		}
		prefs.edit().putInt(PREF_KEY_LAUNCH_TIMES, launchTimes + 1).apply();
	}

	private static void suggestOewn(@NonNull final Activity activity)
	{
		final BaseTransientBottomBar.Behavior behavior = new BaseTransientBottomBar.Behavior();
		behavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_ANY);
		final View parentLayout = activity.findViewById(R.id.activity_main_sub); // view to find a parent from
		if (parentLayout != null)
		{
			String message1 = activity.getString(R.string.obsolete_app);
			String message2 = activity.getString(R.string.new_app);
			SpannableStringBuilder sb = new SpannableStringBuilder();
			sb.append(message1).append('\n');
			sb.append('@').setSpan(new ImageSpan(activity, R.drawable.logo_semantikos_ewn), sb.length() - 1, sb.length(), 0);
			sb.append('\n').append(message2);

			final Snackbar snackbar = Snackbar.make(parentLayout, sb, Snackbar.LENGTH_INDEFINITE);
			snackbar.setTextMaxLines(10) //
					.setBackgroundTint(ContextCompat.getColor(activity, R.color.snackbar_oewn)) //
					.setAction(R.string.obsolete_get_oewn, view -> OthersActivity.install(activity.getString(R.string.semantikos_ewn_uri), activity)) //
					.setActionTextColor(ContextCompat.getColor(activity, android.R.color.white)) //
					.setBehavior(behavior).show();
		}
	}
}
