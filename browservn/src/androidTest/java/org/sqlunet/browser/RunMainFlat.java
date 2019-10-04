package org.sqlunet.browser;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sqlunet.browser.vn.R;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RunMainFlat extends TestCase
{
	@Rule
	public ActivityTestRule<MainActivity> testRule = new ActivityTestRule<>(MainActivity.class, true, true);

	@Before
	public void before()
	{
		Actions.do_navigate("Browse");
		Actions.do_choose(R.id.spinner ,"senses");
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