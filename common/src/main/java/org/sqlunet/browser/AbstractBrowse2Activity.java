/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.content.res.Configuration;

import org.sqlunet.nightmode.NightMode;

import androidx.appcompat.app.AppCompatActivity;

public abstract class AbstractBrowse2Activity extends AppCompatActivity
{
	@Override
	protected void onNightModeChanged(final int mode)
	{
		super.onNightModeChanged(mode);
		final Configuration overrideConfig = NightMode.createOverrideConfigurationForDayNight(this, mode);
		getApplication().onConfigurationChanged(overrideConfig);
	}
}
