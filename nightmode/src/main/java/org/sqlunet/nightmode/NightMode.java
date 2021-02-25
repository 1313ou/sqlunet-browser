package org.sqlunet.nightmode;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.ContextThemeWrapper;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

public class NightMode
{
	static private final String TAG = "NightMode";

	static public int toConfigurationUiMode(final int mode)
	{
		switch (mode)
		{
			case AppCompatDelegate.MODE_NIGHT_YES:
				return Configuration.UI_MODE_NIGHT_YES;

			case AppCompatDelegate.MODE_NIGHT_NO:
				return Configuration.UI_MODE_NIGHT_NO;

			default:
				throw new IllegalStateException("Unexpected value: " + mode);
		}
	}

	@NonNull
	static public Configuration createOverrideConfigurationForDayNight(@NonNull Context context, final int mode)
	{
		int newNightMode;
		switch (mode)
		{
			case MODE_NIGHT_YES:
				newNightMode = Configuration.UI_MODE_NIGHT_YES;
				break;
			case MODE_NIGHT_NO:
				newNightMode = Configuration.UI_MODE_NIGHT_NO;
				break;
			default:
			case MODE_NIGHT_FOLLOW_SYSTEM:
				// If we're following the system, we just use the system default from the application context
				final Configuration appConfig = context.getApplicationContext().getResources().getConfiguration();
				newNightMode = appConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
				break;
		}

		// If we're here then we can try and apply an override configuration on the Context.
		final Configuration overrideConf = new Configuration();
		overrideConf.fontScale = 0;
		overrideConf.uiMode = newNightMode | (overrideConf.uiMode & ~Configuration.UI_MODE_NIGHT_MASK);
		return overrideConf;
	}

	@NonNull
	public static Context wrapContext(@NonNull final Context context, final Configuration newConfig, @StyleRes int themeId)
	{
		//Context themedContext = new ContextThemeWrapper(context, R.style.MyTheme);
		//return themedContext;

		//Configuration newConfig = context.getResources().getConfiguration();
		//newConfig.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK; // clear
		//newConfig.uiMode |= toConfigurationUiMode(mode) & Configuration.UI_MODE_NIGHT_MASK; // set
		Context newContext = context.createConfigurationContext(newConfig);
		return new ContextThemeWrapper(newContext, themeId);
	}

	/**
	 * Test whether in night mode.
	 *
	 * @param context context
	 * @return -1 if in night mode, 1 in day mode
	 */
	static public int isNightMode(@NonNull final Context context)
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
	 * Get night mode.
	 *
	 * @param context context
	 * @return mode to string
	 */
	@NonNull
	static public String getNightMode(@NonNull final Context context)
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


	static public boolean checkDarkMode(int expected)
	{
		int mode = AppCompatDelegate.getDefaultNightMode();
		switch (mode)
		{
			case AppCompatDelegate.MODE_NIGHT_YES:
				Log.d(TAG, "Night mode");
				return expected == AppCompatDelegate.MODE_NIGHT_YES;

			case AppCompatDelegate.MODE_NIGHT_NO:
				Log.d(TAG, "Day mode");
				return expected == AppCompatDelegate.MODE_NIGHT_NO;

			case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
				Log.d(TAG, "Follow system");
				return expected == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;

			case AppCompatDelegate.MODE_NIGHT_UNSPECIFIED:
			default:
				throw new IllegalStateException("Unexpected value: " + mode);
		}
	}
}
