/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.browser.wn;

import android.content.Context;
import android.util.Log;

import org.sqlunet.browser.AbstractApplication;
import org.sqlunet.nightmode.NightMode;
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
