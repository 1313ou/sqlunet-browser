/*
 * Copyright (c) 2020. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.sn;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sqlunet.browser.MainActivity;
import org.sqlunet.browser.R;
import org.sqlunet.browser.Seq;
import org.sqlunet.browser.Wait;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RunMainTree extends TestCase
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
		//Actions.do_choose(R.id.spinner, "grouped by source");
		Seq.do_choose(R.id.spinner, 1);
		Wait.pause(6);
	}

	@Test
	public void searchRun()
	{
		Do.searchRunTree();
	}
}