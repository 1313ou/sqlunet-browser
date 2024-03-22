/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.wn;

public class MainActivity extends org.sqlunet.browser.MainActivity
{
	@Override
	protected void onStart()
	{
		super.onStart();
		Oewn.hook(this);
	}
}
