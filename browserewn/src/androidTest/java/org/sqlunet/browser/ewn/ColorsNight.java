/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.browser.ewn;

import org.junit.runner.RunWith;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ColorsNight extends AbstractColors
{
	@Override
	int getMode()
	{
		return AppCompatDelegate.MODE_NIGHT_YES;
	}
}
