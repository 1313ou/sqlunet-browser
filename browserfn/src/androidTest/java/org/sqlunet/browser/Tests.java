package org.sqlunet.browser;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.sqlunet.browser.fn.R;

class Tests
{
	static public void searchRunFlat()
	{
		for (String word : TestUtils.getWordList())
		{
			TestActions.typeSearch(word, R.id.search);

			// progressMessage list
			final Matcher<View> list = CoreMatchers.allOf(ViewMatchers.withId(android.R.id.list), CoreMatchers.instanceOf(ListView.class));
			Espresso.onView(list).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

			// for all selectors
			int n = TestUtils.getItemCount(list);

			Log.d("TEST", word + " has " + n + " results");
			for (int i = 0; i < n; i++)
			{
				Espresso.onData(CoreMatchers.anything()) //
						.inAdapterView(list) //
						.atPosition(i) //
						.perform(  //
								ViewActions.click() //
						);

				// TestUtils.pause();

				// TestActions.swipeUp(R.id.container_browse2);
			}
		}
	}
}