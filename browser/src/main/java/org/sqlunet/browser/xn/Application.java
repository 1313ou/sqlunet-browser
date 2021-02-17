/*
 * Copyright (c) 2020. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.xn;

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
		org.sqlunet.predicatematrix.style.Colors.setColorsFromResources(context);
		org.sqlunet.wordnet.style.Colors.setColorsFromResources(context);
		org.sqlunet.framenet.style.Colors.setColorsFromResources(context);
		org.sqlunet.verbnet.style.Colors.setColorsFromResources(context);
		org.sqlunet.propbank.style.Colors.setColorsFromResources(context);
		org.sqlunet.bnc.style.Colors.setColorsFromResources(context);
	}
}
