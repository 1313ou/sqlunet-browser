/*
 * Copyright (c) 2020. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.sn;

import android.content.Context;

import org.sqlunet.browser.AbstractApplication;
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
		org.sqlunet.syntagnet.style.Colors.setColorsFromResources(context);
		org.sqlunet.wordnet.style.Colors.setColorsFromResources(context);
		org.sqlunet.bnc.style.Colors.setColorsFromResources(context);
	}
}
