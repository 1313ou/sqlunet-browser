/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.hamcrest.Matcher;

import java.util.Objects;

import androidx.annotation.IdRes;
import androidx.test.espresso.matcher.ViewMatchers;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
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

	static void searchRunFlat()
	{
		for (String word : Objects.requireNonNull(DataUtils.getWordList()))
		{
			Seq.do_typeSearch(R.id.search, word);

			// selector list
			//Wait.until(android.R.id.list, 5);
			WaitUntil.shown(android.R.id.list);

			final Matcher<View> list = allOf(withId(android.R.id.list), instanceOf(ListView.class));
			onView(list).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

			// for all selectors
			int n = ContainerUtils.getItemCount(list);

			Log.d("Searching ", word + " has " + n + " results");
			for (int i = 0; i < n; i++)
			{
				onData(anything()) //
						.inAdapterView(list) //
						.atPosition(i) //
						.perform(  //
								click() //
						);
				Seq.do_pressBack();
			}
		}
	}

	static void searchRunTree()
	{
		for (String word : Objects.requireNonNull(DataUtils.getWordList()))
		{
			Seq.do_typeSearch(R.id.search, word);

			// selector list
			//Wait.until(android.R.id.list, 5);
			WaitUntil.shown(android.R.id.list);

			final Matcher<View> list = allOf(withId(android.R.id.list), instanceOf(ListView.class));
			onView(list).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

			// close first
			onView(withChild(allOf(withId(R.id.xn), instanceOf(TextView.class), withText("wordnet")))) //
					.perform(click());
			// expand all
			onView(withChild(allOf(withId(R.id.xn), instanceOf(TextView.class), withText("framenet")))) //
					.perform(click());
			onView(withChild(allOf(withId(R.id.xn), instanceOf(TextView.class), withText("propbank")))) //
					.perform(click());
			onView(withChild(allOf(withId(R.id.xn), instanceOf(TextView.class), withText("verbnet")))) //
					.perform(click());
			onView(withChild(allOf(withId(R.id.xn), instanceOf(TextView.class), withText("wordnet")))) //
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
					Seq.do_pressBack();
				}
			}
		}
	}

	static void xselectorsRunTree()
	{
		for (String word : Objects.requireNonNull(DataUtils.getWordList()))
		{
			Seq.do_typeSearch(R.id.search, word);

			// selector list
			//Wait.until(android.R.id.list, 5);
			WaitUntil.shown(android.R.id.list);

			final Matcher<View> list = allOf(withId(android.R.id.list), instanceOf(ListView.class));
			onView(list).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
			onView(withChild(allOf(withId(R.id.xn), instanceOf(TextView.class), withText("wordnet")))) //
					.perform(click());

			for (int i = 0; i < 50; i++)
			{
				for (String section : new String[]{"framenet", "propbank", "verbnet", "wordnet"})
				{
					onView(withChild(allOf(withId(R.id.xn), instanceOf(TextView.class), withText(section)))) //
							.perform(click());
					onView(withChild(allOf(withId(R.id.xn), instanceOf(TextView.class), withText(section)))) //
							.perform(click());
				}
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