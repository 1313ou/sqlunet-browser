package org.sqlunet.browser;

import android.content.Context;
import android.util.Log;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static org.sqlunet.browser.Colors.testColorsFromResources;

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
		assertTrue(Colors.checkDarkMode(this.context, getMode()));
		testColorsFromResources(this.context, org.sqlunet.xnet.R.array.palette, false);
		testColorsFromResources(this.context, org.sqlunet.wordnet.R.array.palette_wn, false);
		testColorsFromResources(this.context, org.sqlunet.framenet.R.array.palette_fn, false);
		testColorsFromResources(this.context, org.sqlunet.verbnet.R.array.palette_vn, false);
		testColorsFromResources(this.context, org.sqlunet.propbank.R.array.palette_pb, false);
		testColorsFromResources(this.context, org.sqlunet.bnc.R.array.palette_bnc, false);
		testColorsFromResources(this.context, org.sqlunet.predicatematrix.R.array.palette_pm, false);
		//testColorsFromResources(this.context, org.sqlunet.syntagnet.R.array.palette_sn);
	}

	@Test
	public void colorContrastXNet()
	{
		assertTrue(Colors.checkDarkMode(this.context, getMode()));
		try
		{
			testColorsFromResources(this.context, org.sqlunet.xnet.R.array.palette, true);
			testColorsFromResources(this.context, org.sqlunet.wordnet.R.array.palette_wn, true);
			testColorsFromResources(this.context, org.sqlunet.framenet.R.array.palette_fn, true);
			testColorsFromResources(this.context, org.sqlunet.verbnet.R.array.palette_vn, true);
			testColorsFromResources(this.context, org.sqlunet.propbank.R.array.palette_pb, true);
			testColorsFromResources(this.context, org.sqlunet.bnc.R.array.palette_bnc, true);
			testColorsFromResources(this.context, org.sqlunet.predicatematrix.R.array.palette_pm, true);
			//testColorsFromResources(this.context, org.sqlunet.syntagnet.R.array.palette_sn, false);
		}
		catch (Colors.IllegalColorPair ce)
		{
			Log.e(getName(), ce.getMessage());
			fail(ce.getMessage());
		}
	}
}
