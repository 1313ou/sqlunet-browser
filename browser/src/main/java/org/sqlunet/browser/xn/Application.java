/*
 * Copyright (c) 2020. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.xn;

public class Application extends android.app.Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		Settings.initialize(this);
	}
}
