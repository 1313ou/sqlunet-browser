/*
 * Copyright (c) 2020. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import org.sqlunet.settings.Settings;

public class Application extends android.app.Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		Settings.initialize(this);
	}
}
