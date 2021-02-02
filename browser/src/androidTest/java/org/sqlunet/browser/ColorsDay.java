package org.sqlunet.browser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ColorsDay extends AbstractColors
{
	@Override
	int getMode()
	{
		return AppCompatDelegate.MODE_NIGHT_NO;
	}

	@Before
	public void before() throws Throwable
	{
		super.before();
	}

	@Test
	public void colorContrast() throws Colors.IllegalColorPair
	{
		super.colorContrast();
	}

	@Test
	public void colorContrastXNet()
	{
		super.colorContrastXNet();
	}
}
