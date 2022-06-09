/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.wn;

import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.hamcrest.Matcher;
import org.sqlunet.browser.ContainerUtils;
import org.sqlunet.browser.DataUtils;
import org.sqlunet.browser.Seq;
import org.sqlunet.browser.ToBoolean;
import org.sqlunet.browser.Wait;
import org.sqlunet.browser.WaitUntil;
import org.sqlunet.browser.WaitUntilText;
import org.sqlunet.browser.wn.lib.R;

import java.util.Objects;

import androidx.annotation.IdRes;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;

class Do
{
	static boolean ensureDownloaded()
	{
		boolean notMain = ToBoolean.testAssertion(withId(R.id.drawer_layout), doesNotExist()) || !ToBoolean.test(withId(R.id.drawer_layout), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE));
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
		//Wait.until_not_text(R.id.status, Seq.getResourceString(R.string.status_task_running), 10);
		WaitUntilText.changesFrom(R.id.status, Seq.getResourceString(R.string.status_task_running));
	}

	static void ensureTextSearchSetup(@SuppressWarnings("SameParameterValue") @IdRes int buttonId)
	{
		boolean notSet = ToBoolean.testAssertion(withId(buttonId), doesNotExist()) || ToBoolean.test(withId(buttonId), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE));
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
		//Wait.until_not_text(R.id.task_status, Seq.getResourceString(R.string.status_task_running), 10);
		WaitUntilText.changesFrom(R.id.task_status, Seq.getResourceString(R.string.status_task_running));
	}

	static void searchRun()
	{
		for (String word : Objects.requireNonNull(DataUtils.getWordList()))
		{
			Seq.do_typeSearch(R.id.search, word);

			// selector list
			Wait.until(android.R.id.list, 5);
			WaitUntil.shown(android.R.id.list);

			final Matcher<View> list = allOf(withId(android.R.id.list), instanceOf(ListView.class));
			onView(list).check(ViewAssertions.matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

			// for all selectors
			int n = ContainerUtils.getItemCount(list);

			Log.d("Searching ", word + " has " + n + " results");
			for (int i = 0; i < n; i++)
			{
				onData(anything()) //
						.inAdapterView(list) //
						.atPosition(i) //
						.perform(  //
								ViewActions.click() //
						);
				Seq.do_pressBack();
			}
		}
	}

	static void textSearchRun(int position)
	{
		Seq.do_choose(R.id.spinner, position);

		for (String word : Objects.requireNonNull(DataUtils.getWordList()))
		{
			Seq.do_typeSearch(R.id.search, word);
		}
	}
}