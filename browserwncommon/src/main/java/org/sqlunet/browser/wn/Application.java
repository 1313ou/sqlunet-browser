/*
 * Copyright (c) 2020. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.wn;

import android.content.Context;
import android.util.Log;

import org.sqlunet.browser.AbstractApplication;
import org.sqlunet.nightmode.NightMode;
import org.sqlunet.settings.Settings;
import org.sqlunet.style.Colors;

import androidx.annotation.NonNull;

public class Application extends AbstractApplication
{
	static private final String TAG = "Application";

	@Override
	public void onCreate()
	{
		super.onCreate();
		setAllColorsFromResources(this);
		Settings.initialize(this);
	}

	@Override
	public void setAllColorsFromResources(@NonNull final Context context)
	{
		Log.d(TAG, "setColors " + NightMode.nightModeToString(this));
		Colors.setColorsFromResources(context);
		org.sqlunet.wordnet.style.Colors.setColorsFromResources(context);
		org.sqlunet.bnc.style.Colors.setColorsFromResources(context);
	}
}
