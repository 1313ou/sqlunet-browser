/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.fn;

import android.content.Context;

import org.sqlunet.browser.AbstractApplication;
import org.sqlunet.settings.Settings;
import org.sqlunet.style.Colors;

import androidx.annotation.NonNull;

public class Application extends AbstractApplication
{
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
		Colors.setColorsFromResources(context);
		org.sqlunet.framenet.style.Colors.setColorsFromResources(context);
	}
}
