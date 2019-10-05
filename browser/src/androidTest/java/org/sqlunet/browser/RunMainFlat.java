package org.sqlunet.browser;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RunMainFlat extends TestCase
{
	@NonNull
	@Rule
	public ActivityTestRule<MainActivity> testRule = new ActivityTestRule<>(MainActivity.class, true, true);

	@Before
	public void before()
	{
		Do.ensureDownloaded();
		Actions.do_navigate(R.id.drawer_layout, R.id.nav_view, "Browse");
		Actions.do_choose(R.id.spinner, "senses");
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