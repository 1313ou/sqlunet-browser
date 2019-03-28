package org.sqlunet.browser;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sqlunet.browser.fn.R;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RunBrowseTree extends TestCase
{
	@Rule
	public ActivityTestRule<BrowseActivity> testRule = new ActivityTestRule<>(BrowseActivity.class, true, true);

	@Before
	public void before()
	{
		TestActions.spinner("per base", R.id.spinner);
	}

	@Test
	public void dummy()
	{
	}

	@Test
	public void searchRun()
	{
		Tests.searchRunFlat();
	}
}