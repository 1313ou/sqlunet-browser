/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.wn;

import android.os.Bundle;

import androidx.annotation.Nullable;

public class MainActivity extends org.sqlunet.browser.MainActivity
{
	@Override
	protected void onPostCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		Oewn.hook(this);
	}
}
