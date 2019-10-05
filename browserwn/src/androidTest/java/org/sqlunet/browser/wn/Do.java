/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.wn;

import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.sqlunet.browser.Actions;
import org.sqlunet.browser.ContainerUtils;
import org.sqlunet.browser.DataUtils;
import org.sqlunet.browser.ToBoolean;
import org.sqlunet.browser.Wait;
import org.sqlunet.browser.wn.lib.R;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

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
		Wait.until_not_text(R.id.status, "running", 100);
	}

	static void searchRun()
	{
		for (String word : DataUtils.getWordList())
		{
			Actions.do_typeSearch(R.id.search, word);

			// selector list
			Wait.until(android.R.id.list, 5);
			final Matcher<View> list = CoreMatchers.allOf(ViewMatchers.withId(android.R.id.list), CoreMatchers.instanceOf(ListView.class));
			Espresso.onView(list).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

			// for all selectors
			int n = ContainerUtils.getItemCount(list);

			Log.d("Searching ", word + " has " + n + " results");
			for (int i = 0; i < n; i++)
			{
				Espresso.onData(CoreMatchers.anything()) //
						.inAdapterView(list) //
						.atPosition(i) //
						.perform(  //
								ViewActions.click() //
						);
			}
		}
	}
}