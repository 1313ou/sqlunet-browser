/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.vn;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.hamcrest.Matcher;
import org.sqlunet.browser.Actions;
import org.sqlunet.browser.ContainerUtils;
import org.sqlunet.browser.DataUtils;
import org.sqlunet.browser.ToBoolean;
import org.sqlunet.browser.Wait;

import androidx.annotation.IdRes;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;

class Do
{
	static void ensureDownloaded()
	{
		boolean notMain = ToBoolean.testAssertion(withId(R.id.drawer_layout), doesNotExist()) || !ToBoolean.test(withId(R.id.drawer_layout), isDisplayed());
		if (notMain)
		{
			download();
			Actions.do_pressBack();
		}
	}

	static private void download()
	{
		Actions.do_click(R.id.databaseButton);
		Actions.do_click(R.id.downloadButton);
		Wait.until_not_text(R.id.status, Actions.getResourceString(R.string.status_task_running), 100);
	}

	static void ensureTextSearchSetup(@IdRes int buttonId)
	{
		boolean notSet = ToBoolean.testAssertion(withId(buttonId), doesNotExist()) || ToBoolean.test(withId(buttonId), isDisplayed());
		if (notSet)
		{
			textSearchSetup(buttonId);
			Actions.do_pressBack();
		}
	}

	static private void textSearchSetup(@IdRes int buttonId)
	{
		Actions.do_click(buttonId);
		Actions.do_click(R.id.task_run);
		Wait.until_not_text(R.id.task_status, Actions.getResourceString(R.string.status_task_running), 100);
	}

	static void searchRunTree()
	{
		for (String word : DataUtils.getWordList())
		{
			Actions.do_typeSearch(R.id.search, word);

			// selector list
			Wait.until(android.R.id.list, 5);
			final Matcher<View> list = allOf(withId(android.R.id.list), instanceOf(ListView.class));
			onView(list).check(matches(isDisplayed()));

			// close first
			onView(withChild(allOf(withId(R.id.xn), instanceOf(TextView.class), withText("verbnet")))) //
					.perform(click());
			// expand all
			onView(withChild(allOf(withId(R.id.xn), instanceOf(TextView.class), withText("propbank")))) //
					.perform(click());
			onView(withChild(allOf(withId(R.id.xn), instanceOf(TextView.class), withText("verbnet")))) //
					.perform(click());

			// for all selectors
			int[] counts = ContainerUtils.getExpandableListViewItemCounts(list);

			Log.d("Searching ", word + ' ' + DataUtils.arrayToString(counts));
			int k = 0;
			for (int g = 0; g < counts.length; g++)
			{
				k++; // for group header
				for (int c = 0; c < counts[g]; c++)
				{
					Log.d("xselector (", g + ' ' + c + ") = " + k);
					onData(anything()) //
							.inAdapterView(list) //
							.atPosition(k++) //
							.usingAdapterViewProtocol(androidx.test.espresso.action.AdapterViewProtocols.standardProtocol()).perform(click() //
					);
					Actions.do_pressBack();
				}
			}
		}
	}

	static void textSearchRun(int position)
	{
		Actions.do_choose(R.id.spinner, position);

		for (String word : DataUtils.getWordList())
		{
			Actions.do_typeSearch(R.id.search, word);
		}
	}
}