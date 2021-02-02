/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public abstract class AbstractBrowse1Activity extends AppCompatActivity
{
	@Override
	public void onConfigurationChanged(@NonNull final Configuration newConfig)
	{
		getApplication().onConfigurationChanged(newConfig);
		recreate();
	}
}
