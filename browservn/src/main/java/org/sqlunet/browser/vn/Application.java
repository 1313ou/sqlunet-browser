/*
 * Copyright (c) 2020. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.vn;

import android.content.Context;

import org.sqlunet.settings.Settings;
import org.sqlunet.style.Colors;

import androidx.annotation.NonNull;

public class Application extends android.app.Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		setAllColorsFromResources(this);
		Settings.initialize(this);
	}

	public void setAllColorsFromResources(@NonNull Context context)
	{
		Colors.setColorsFromResources(context);
		org.sqlunet.verbnet.style.Colors.setColorsFromResources(context);
		org.sqlunet.propbank.style.Colors.setColorsFromResources(context);
		org.sqlunet.wordnet.style.Colors.setColorsFromResources(context);
	}
}
