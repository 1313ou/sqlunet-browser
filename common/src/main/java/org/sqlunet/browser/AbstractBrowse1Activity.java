/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.content.res.Configuration;
import android.util.Log;

import org.sqlunet.provider.BaseProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public abstract class AbstractBrowse1Activity extends AppCompatActivity
{
	@Override
	protected void onNightModeChanged(final int mode)
	{
		super.onNightModeChanged(mode);
		final Configuration overrideConfig = AbstractApplication.createOverrideConfigurationForDayNight(this, mode);
		getApplication().onConfigurationChanged(overrideConfig);
	}
}
