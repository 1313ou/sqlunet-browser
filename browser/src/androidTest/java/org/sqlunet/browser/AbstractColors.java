/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.content.Context;
import android.util.Log;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sqlunet.nightmode.NightMode;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static org.sqlunet.browser.ColorsTest.testColorsFromResources;

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
		this.context = ColorsTest.getContext(getMode());
		runOnUiThread(() -> {

			AppCompatDelegate.setDefaultNightMode(getMode());
			// Colors.dumpDefaultColors(this.context);
			@ColorInt int[] defaultColors = ColorsTest.getDefaultColorAttrs(this.context);
			Log.i(LOGTAG, String.format("Default color #%x on #%x", defaultColors[1], defaultColors[0]));
		});
	}

	@Test
	public void colorContrast() throws ColorsTest.IllegalColorPair
	{
		assertTrue(NightMode.checkDarkMode(getMode()));
		testColorsFromResources(this.context, R.array.palette_ui, false);
		testColorsFromResources(this.context, R.array.palette, false);
		testColorsFromResources(this.context, R.array.palette_wn, false);
		testColorsFromResources(this.context, R.array.palette_fn, false);
		testColorsFromResources(this.context, R.array.palette_vn, false);
		testColorsFromResources(this.context, R.array.palette_pb, false);
		testColorsFromResources(this.context, R.array.palette_bnc, false);
		testColorsFromResources(this.context, R.array.palette_pm, false);
	}

	@Test
	public void colorContrastXNet()
	{
		assertTrue(NightMode.checkDarkMode(getMode()));
		try
		{
			testColorsFromResources(this.context, R.array.palette_ui, true);
			testColorsFromResources(this.context, R.array.palette, true);
			testColorsFromResources(this.context, R.array.palette_wn, true);
			testColorsFromResources(this.context, R.array.palette_fn, true);
			testColorsFromResources(this.context, R.array.palette_vn, true);
			testColorsFromResources(this.context, R.array.palette_pb, true);
			testColorsFromResources(this.context, R.array.palette_bnc, true);
			testColorsFromResources(this.context, R.array.palette_pm, true);
		}
		catch (ColorsTest.IllegalColorPair ce)
		{
			Log.e(getName(), ce.getMessage());
			fail(ce.getMessage());
		}
	}
}
