/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.browser.ewn;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sqlunet.browser.MainActivity;
import org.sqlunet.browser.Seq;
import org.sqlunet.browser.ewn.test.R;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RunMainTest extends TestCase
{
	@NonNull
	@Rule
	public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

	@Before
	public void before()
	{
		if (Do.ensureDownloaded())
			ActivityScenario.launch(MainActivity.class);

		//Actions.do_navigate(R.id.drawer_layout, R.id.nav_view, "Browse");
		Seq.do_navigate(R.id.drawer_layout, R.id.nav_view, R.id.nav_search_browse);
	}

	@Test
	public void searchRun()
	{
		Do.searchRun();
	}
}