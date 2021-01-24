/*
 * Copyright (c) 2020. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.sn;

import org.sqlunet.style.Colors;

public class Application extends android.app.Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		Colors.setColorsFromResources(this);
		Settings.initialize(this);
	}
}
