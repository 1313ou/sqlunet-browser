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
public class RunXSelectTree extends TestCase
{
	@NonNull
	@Rule
	public ActivityScenarioRule<BrowseActivity> activityScenarioRule = new ActivityScenarioRule<>(BrowseActivity.class);

	@Before
	public void before()
	{
		//Actions.do_choose(R.id.spinner, "grouped by source");
		Seq.do_choose(R.id.spinner, 1);
	}

	@Test
	public void searchRun()
	{
		Do.xselectorsRunTree();
	}
}