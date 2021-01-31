/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.content.res.Configuration;

import org.sqlunet.style.Colors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public abstract class AbstractBrowse2Activity extends AppCompatActivity
{
	@Override
	public void onConfigurationChanged(@NonNull final Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		AbstractApplication.getApplication(this).setAllColorsFromResources();
	}
}
