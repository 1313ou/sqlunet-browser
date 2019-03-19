package org.sqlunet.browser;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.ExpandableListViewProtocol;
import androidx.test.espresso.matcher.ViewMatchers;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;

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

	static public void searchRunTree()
	{
		for (String word : TestUtils.getWordList())
		{
			TestActions.typeSearch(word, R.id.search);

			// progressMessage list
			final Matcher<View> list = CoreMatchers.allOf(ViewMatchers.withId(android.R.id.list), CoreMatchers.instanceOf(ListView.class));
			Espresso.onView(list).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

			// expand
			Espresso.onView(ViewMatchers.withChild(CoreMatchers.allOf(ViewMatchers.withId(R.id.xn), CoreMatchers.instanceOf(TextView.class), ViewMatchers.withText("wordnet")))) //
					.perform(ViewActions.click());
			Espresso.onView(ViewMatchers.withChild(CoreMatchers.allOf(ViewMatchers.withId(R.id.xn), CoreMatchers.instanceOf(TextView.class), ViewMatchers.withText("framenet")))) //
					.perform(ViewActions.click());
			Espresso.onView(ViewMatchers.withChild(CoreMatchers.allOf(ViewMatchers.withId(R.id.xn), CoreMatchers.instanceOf(TextView.class), ViewMatchers.withText("propbank")))) //
					.perform(ViewActions.click());
			Espresso.onView(ViewMatchers.withChild(CoreMatchers.allOf(ViewMatchers.withId(R.id.xn), CoreMatchers.instanceOf(TextView.class), ViewMatchers.withText("verbnet")))) //
					.perform(ViewActions.click());
			Espresso.onView(ViewMatchers.withChild(CoreMatchers.allOf(ViewMatchers.withId(R.id.xn), CoreMatchers.instanceOf(TextView.class), ViewMatchers.withText("wordnet")))) //
					.perform(ViewActions.click());

			// for all selectors
			int[] counts = TestUtils.getExpandableListViewItemCounts(list);

			Log.d("TEST", word + ' ' + TestUtils.arrayToString(counts));
			int k = 0;
			for (int g = 0; g < counts.length; g++)
			{
				k++; // for group header
				for (int c = 0; c < counts[g]; c++)
				{
					Log.d("xselector (", g + ' ' + c + ") = " + k);
					Espresso.onData(CoreMatchers.anything()) //
							.inAdapterView(list) //
							.atPosition(k++) //
							//.usingAdapterViewProtocol(android.support.test.espresso.action.AdapterViewProtocols.standardProtocol())
							.usingAdapterViewProtocol(ExpandableListViewProtocol.expandableListViewProtocol()).perform(  //
							ViewActions.click() //
					);

					// TestUtils.pause();

					// TestActions.swipeUp(R.id.container_browse2);
				}
			}
		}
	}
}