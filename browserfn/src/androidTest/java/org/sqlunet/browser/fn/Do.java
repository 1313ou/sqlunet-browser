/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.fn;

import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.sqlunet.browser.Seq;
import org.sqlunet.browser.ContainerUtils;
import org.sqlunet.browser.DataUtils;
import org.sqlunet.browser.ToBoolean;
import org.sqlunet.browser.Wait;

import java.util.Objects;

import androidx.annotation.IdRes;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

class Do
{
	static boolean ensureDownloaded()
	{
		boolean notMain = ToBoolean.testAssertion(withId(R.id.drawer_layout), doesNotExist()) || !ToBoolean.test(withId(R.id.drawer_layout), isDisplayed());
		if (notMain)
		{
			download();
			return true;
		}
		return false;
	}

	static private void download()
	{
		Seq.do_click(R.id.databaseButton);
		// download activity
		Seq.do_click(R.id.downloadButton);
		Wait.until_not_text(R.id.status, Seq.getResourceString(R.string.status_task_running), 500);
	}

	static void ensureTextSearchSetup(@SuppressWarnings("SameParameterValue") @IdRes int buttonId)
	{
		boolean notSet = ToBoolean.testAssertion(withId(buttonId), doesNotExist()) || ToBoolean.test(withId(buttonId), isDisplayed());
		if (notSet)
		{
			textSearchSetup(buttonId);
			Seq.do_pressBack();
		}
	}

	static private void textSearchSetup(@IdRes int buttonId)
	{
		Seq.do_click(buttonId);
		Seq.do_click(R.id.task_run);
		Wait.until_not_text(R.id.task_status, Seq.getResourceString(R.string.status_task_running), 100);
	}

	static void searchRunFlat()
	{
		for (String word : Objects.requireNonNull(DataUtils.getWordList()))
		{
			Seq.do_typeSearch(R.id.search, word);

			// selector list
			Wait.until(android.R.id.list, 5);
			final Matcher<View> list = CoreMatchers.allOf(ViewMatchers.withId(android.R.id.list), CoreMatchers.instanceOf(ListView.class));
			onView(list).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

			// for all selectors
			int n = ContainerUtils.getItemCount(list);

			Log.d("Searching ", word + " has " + n + " results");
			for (int i = 0; i < n; i++)
			{
				Espresso.onData(CoreMatchers.anything()) //
						.inAdapterView(list) //
						.atPosition(i) //
						.perform(  //
								click() //
						);
			}
		}
	}

	static void textSearchRun(@SuppressWarnings("SameParameterValue") int position)
	{
		if (position != -1)
		{
			Seq.do_choose(R.id.spinner, position);
		}

		for (String word : Objects.requireNonNull(DataUtils.getWordList()))
		{
			Seq.do_typeSearch(R.id.search, word);
		}
	}
}