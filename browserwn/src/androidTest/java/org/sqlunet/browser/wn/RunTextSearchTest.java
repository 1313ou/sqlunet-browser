/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.browser.wn;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sqlunet.browser.Seq;
import org.sqlunet.browser.MainActivity;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RunTextSearchTest extends TestCase
{
	@NonNull
	@Rule
	public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

	@Before
	public void before()
	{
		if (Do.ensureDownloaded())
			ActivityScenario.launch(MainActivity.class);

		Seq.do_navigate(R.id.drawer_layout, R.id.nav_view, R.id.nav_status);
		Do.ensureTextSearchSetup(R.id.searchtextWnButton);

		Seq.do_navigate(R.id.drawer_layout, R.id.nav_view, R.id.nav_search_text);
	}

	@Test
	public void searchWnDefinitionsRun()
	{
		Do.textSearchRun(0);
	}

	@Test
	public void searchWnSamplesRun()
	{
		Do.textSearchRun(1);
	}

	@Test
	public void searchWnWordsRun()
	{
		Do.textSearchRun(1);
	}
}