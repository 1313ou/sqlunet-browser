/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.fn;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sqlunet.browser.Seq;
import org.sqlunet.browser.MainActivity;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RunTextSearch extends TestCase
{
	@Rule
	public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

	@Before
	public void before()
	{
		if (Do.ensureDownloaded())
			ActivityScenario.launch(MainActivity.class);

		Seq.do_navigate(R.id.drawer_layout, R.id.nav_view, R.id.nav_status);
		Do.ensureTextSearchSetup(R.id.searchtextFnButton);

		Seq.do_navigate(R.id.drawer_layout, R.id.nav_view, R.id.nav_search_text);
	}

	@Test
	public void searchVnRun()
	{
		Do.textSearchRun(-1);
	}
}