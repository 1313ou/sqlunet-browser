/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;

abstract public class AbstractApplication extends Application
{
	static public AbstractApplication getApplication(@NonNull Activity thisApp )
	{
		return (AbstractApplication)thisApp.getApplication();
	}

	abstract public void setAllColorsFromResources();
}
