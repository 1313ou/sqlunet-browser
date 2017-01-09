package org.sqlunet.browser;

import android.support.test.espresso.contribs.ExpandableListViewProtocol;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import junit.framework.TestCase;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.sqlunet.browser.TestUtils.arrayToString;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestTreeRuns extends TestCase
{
	@Rule
	public ActivityTestRule<BrowseActivity> testRule = new ActivityTestRule<>(BrowseActivity.class, true, true);

	@Before
	public void set()
	{
		TestActions.spinner("per base", R.id.spinner);
	}

	@Test
	public void dummy()
	{
	}

	@Test
	public void searchRun()
	{
		for (String word : TestUtils.getWordList())
		{
			TestActions.typeSearch(word, R.id.search);

			// result list
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
			int[] counts = TestUtils.getExpandableListViewItemCounts(list);

			Log.d("TEST", word + ' ' + arrayToString(counts));
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

					// TestUtils.pause();

					// TestActions.swipeUp(R.id.container_browse2);
				}
			}
		}
	}
}