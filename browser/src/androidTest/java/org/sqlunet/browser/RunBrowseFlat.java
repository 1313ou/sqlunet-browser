package org.sqlunet.browser;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RunBrowseFlat extends TestCase
{
	@NonNull
	@Rule
	public ActivityScenarioRule<BrowseActivity> activityScenarioRule = new ActivityScenarioRule<>(BrowseActivity.class);

	@Before
	public void before()
	{
		if (Do.ensureDownloaded())
			activityScenarioRule.getScenario().launch(MainActivity.class);

		//Actions.do_choose(R.id.spinner, "senses");
		Actions.do_choose(R.id.spinner, 0);
	}

	@Test
	public void dummy()
	{
	}

	@Test
	public void searchRun()
	{
		Do.searchRunFlat();
	}
}