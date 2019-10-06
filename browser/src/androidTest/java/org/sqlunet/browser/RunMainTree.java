package org.sqlunet.browser;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RunMainTree extends TestCase
{
	@Rule
	public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

	@Before
	public void before()
	{
		Do.ensureDownloaded();

		//Actions.do_navigate(R.id.drawer_layout, R.id.nav_view, "Browse");
		Actions.do_navigate(R.id.drawer_layout, R.id.nav_view, R.id.nav_search_browse);
		//Actions.do_choose(R.id.spinner, "grouped by source");
		Actions.do_choose(R.id.spinner, 1);
		Wait.pause(6);
	}

	@Test
	public void dummy()
	{
	}

	@Test
	public void searchRun()
	{
		Do.searchRunTree();
	}
}