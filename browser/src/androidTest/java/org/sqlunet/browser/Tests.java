package org.sqlunet.browser;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.hamcrest.Matcher;

import androidx.test.espresso.contrib.ExpandableListViewProtocol;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;

class Tests
{
	static public void searchRunFlat()
	{
		for (String word : DataUtils.getWordList())
		{
			Actions.do_typeSearch(R.id.search, word);

			// list
			final Matcher<View> list = allOf(withId(android.R.id.list), instanceOf(ListView.class));
			onView(list).check(matches(isDisplayed()));

			// for all selectors
			int n = ContainerUtils.getItemCount(list);

			Log.d("Test", word + " has " + n + " results");
			for (int i = 0; i < n; i++)
			{
				onData(anything()) //
						.inAdapterView(list) //
						.atPosition(i) //
						.perform(  //
								click() //
						);

				// Wait.pause(2);

				// Actions.do_swipeUp(R.id.container_browse2);
			}
		}
	}

	static public void searchRunTree()
	{
		for (String word : DataUtils.getWordList())
		{
			Actions.do_typeSearch(R.id.search, word);

			// progressMessage list
			final Matcher<View> list = allOf(withId(android.R.id.list), instanceOf(ListView.class));
			onView(list).check(matches(isDisplayed()));

			// expand
			onView(withChild(allOf(withId(R.id.xn), instanceOf(TextView.class), withText("wordnet")))) //
					.perform(click());
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

			Log.d("Test", word + ' ' + DataUtils.arrayToString(counts));
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
							//.usingAdapterViewProtocol(android.support.test.espresso.action.AdapterViewProtocols.standardProtocol())
							.usingAdapterViewProtocol(ExpandableListViewProtocol.expandableListViewProtocol()).perform(  //
							click() //
					);

					// Wait.pause(2);

					// Actions.do_swipeUp(R.id.container_browse2);

					Actions.do_pressBack();
				}
			}
		}
	}
}