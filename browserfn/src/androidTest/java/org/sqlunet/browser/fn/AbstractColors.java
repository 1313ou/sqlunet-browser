/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.browser.fn;

import android.content.Context;
import android.util.Log;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sqlunet.browser.MainActivity;
import org.sqlunet.nightmode.NightMode;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static org.sqlunet.browser.fn.Colors.testColorsFromResources;

@RunWith(AndroidJUnit4.class)
@LargeTest
abstract class AbstractColors extends TestCase
{
	static private final String LOGTAG = "ColorsDay";

	abstract int getMode();

	@NonNull
	@Rule
	public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

	protected Context context;

	@Before
	public void before() throws Throwable
	{
		this.context = Colors.getContext(getMode());
		runOnUiThread(() -> {

			AppCompatDelegate.setDefaultNightMode(getMode());
			// Colors.dumpDefaultColors(this.context);
			@ColorInt int[] defaultColors = Colors.getDefaultColorAttrs(this.context);
			Log.i(LOGTAG, String.format("Default color #%x on #%x", defaultColors[1], defaultColors[0]));
		});
	}

	@Test
	public void colorContrast() throws Colors.IllegalColorPair
	{
		assertTrue(NightMode.checkDarkMode(getMode()));
		testColorsFromResources(this.context, org.sqlunet.browser.common.R.array.palette_ui, false);
		testColorsFromResources(this.context, org.sqlunet.xnet.R.array.palette, false);
		testColorsFromResources(this.context, org.sqlunet.framenet.R.array.palette_fn, false);
	}

	@Test
	public void colorContrastXNet()
	{
		assertTrue(NightMode.checkDarkMode(getMode()));
		try
		{
			testColorsFromResources(this.context, org.sqlunet.browser.common.R.array.palette_ui, true);
			testColorsFromResources(this.context, org.sqlunet.xnet.R.array.palette, true);
			testColorsFromResources(this.context, org.sqlunet.framenet.R.array.palette_fn, true);
		}
		catch (Colors.IllegalColorPair ce)
		{
			Log.e(getName(), ce.getMessage());
			fail(ce.getMessage());
		}
	}
}
