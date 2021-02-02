/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import org.sqlunet.browser.common.R;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;

abstract public class AbstractApplication extends Application
{
	static private final String LOG = "AApplication";

	// doesn't get called
	@Override
	public void onConfigurationChanged(@NonNull final Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		Context newContext = wrapContext(this, newConfig);
		Log.d(LOG, "onConfigurationChanged: "  + getNightMode(this) + " -> " + getNightMode(newContext));
		setAllColorsFromResources(newContext);
	}

	static private Context wrapContext(@NonNull final Context context, final Configuration newConfig)
	{
		//Context themedContext = new ContextThemeWrapper(context, R.style.MyTheme);
		//return themedContext;

		//Configuration newConfig = context.getResources().getConfiguration();
		//newConfig.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK; // clear
		//newConfig.uiMode |= toConfigurationUiMode(mode) & Configuration.UI_MODE_NIGHT_MASK; // set
		Context newContext = context.createConfigurationContext(newConfig);
		return new ContextThemeWrapper(newContext, R.style.MyTheme);
	}

	/**
	 * Test whether in night mode.
	 *
	 * @param context context
	 * @return -1 if in night mode, 1 in day mode
	 */
	public static int isNightMode(@NonNull final Context context)
	{
		int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
		switch (nightModeFlags)
		{
			case Configuration.UI_MODE_NIGHT_YES:
				return +1;

			case Configuration.UI_MODE_NIGHT_NO:
				return 1;

			default:
				return 0;
		}
	}

	/**
	 * Test whether in night mode.
	 *
	 * @param context context
	 * @return mode to string
	 */
	public static String getNightMode(@NonNull final Context context)
	{
		int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
		switch (nightModeFlags)
		{
			case Configuration.UI_MODE_NIGHT_YES:
				return "night";

			case Configuration.UI_MODE_NIGHT_NO:
				return "day";

			default:
				return "unknown";
		}
	}

	abstract public void setAllColorsFromResources(@NonNull final Context newContext);
}
