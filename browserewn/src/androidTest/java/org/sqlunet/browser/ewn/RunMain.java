package org.sqlunet.browser.ewn;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sqlunet.browser.Actions;
import org.sqlunet.browser.MainActivity;
import org.sqlunet.browser.ewn.R;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RunMain extends TestCase
{
	@Rule
	public ActivityTestRule<MainActivity> testRule = new ActivityTestRule<>(MainActivity.class, true, true);

	@Before
	public void before()
	{
		Do.ensureDownloaded();
		Actions.do_navigate(R.id.drawer_layout, R.id.nav_view, "Browse");
	}

	@Test
	public void dummy()
	{
	}

	@Test
	public void searchRun()
	{
		Do.searchRun();
	}
}