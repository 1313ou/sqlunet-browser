/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.app.Application;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

abstract public class AbstractApplication extends Application
{
	@Override
	public void onConfigurationChanged(@NonNull final Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		setAllColorsFromResources();
	}

	abstract public void setAllColorsFromResources();
}
