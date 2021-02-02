package org.sqlunet.browser;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.ContextThemeWrapper;

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
import androidx.test.platform.app.InstrumentationRegistry;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static org.sqlunet.browser.Colors.testColorsFromResources;

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
