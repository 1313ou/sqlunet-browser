package org.sqlunet.browser;

import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.sqlunet.browser.vn.R;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

class Tests
{
	static public void searchRunFlat()
	{
		for (String word : TestUtils.getWordList())
		{
			Actions.do_typeSearch(R.id.search, word);

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

				// TestActions.do_swipeUp(R.id.container_browse2);
			}
		}
	}
}