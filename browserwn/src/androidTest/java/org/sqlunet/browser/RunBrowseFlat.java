package org.sqlunet.browser;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sqlunet.browser.wn.R;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RunBrowseFlat extends TestCase
{
	@Rule
	public ActivityTestRule<BrowseActivity> testRule = new ActivityTestRule<>(BrowseActivity.class, true, true);

	@Before
	public void before()
	{
		TestActions.spinner("senses", R.id.spinner);
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