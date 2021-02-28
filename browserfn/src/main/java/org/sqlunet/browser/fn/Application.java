/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.fn;

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
		org.sqlunet.framenet.style.Colors.setColorsFromResources(context);
	}
}
