package org.sqlunet.browser;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.contribs.ExpandableListViewProtocol;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import junit.framework.TestCase;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestRuns extends TestCase
{
	@Rule
	public ActivityTestRule<BrowseActivity> testRule = new ActivityTestRule<>(BrowseActivity.class, true, true);

	@Test
	public void searchRun()
	{
		for (String word : new String[]{"abandon", "leave", "inveigle", "foist", "flounder", "flout"})
		{
			// open search view
			onView(withId(R.id.search)).check(matches(isDisplayed())).perform(click());

			// type search
			onView(allOf(isDisplayed(), isAssignableFrom(EditText.class))) //
					.check(matches(isDisplayed())) //
					.perform( //
							typeText(word),  //
							pressImeActionButton() //
					);

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
			int[] counts = getGroupCounts(list);

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
							.usingAdapterViewProtocol(ExpandableListViewProtocol.expandableListViewProtocol())
							.perform(  //
									click() //
							);
					//sleep();
					/*
					onView(withId(R.id.container_browse2)).check(matches(isDisplayed())) //
							.perform( //
									withCustomConstraints(ViewActions.swipeUp(), isDisplayingAtLeast(1)) //
									// , ViewActions.swipeUp() //
							);
					*/
				}
			}
		}
	}

	//@Test
	public void searchRun2()
	{
		for (String word : new String[]{"abandon", "leave", "inveigle", "foist", "flounder", "flout"})
		{
			// open search view
			onView(withId(R.id.search)).check(matches(isDisplayed())).perform(click());

			// type search
			onView(allOf(isDisplayed(), isAssignableFrom(EditText.class))) //
					.check(matches(isDisplayed())) //
					.perform( //
							typeText(word),  //
							pressImeActionButton() //
					);

			// result list
			final Matcher<View> list = allOf(withId(android.R.id.list), instanceOf(ListView.class));
			onView(list).check(matches(isDisplayed()));

			// for all selectors
			int n = getItemCount(list);

			Log.d("TEST", word + ' ' + n);
			for (int i = 0; i < n; i++)
			{
				onData(anything()) //
						.inAdapterView(list) //
						.atPosition(i) //
						.perform(  //
								click() //
						);
				/*
				onView(withId(R.id.container_browse2)).check(matches(isDisplayed())) //
						.perform( //
								withCustomConstraints(ViewActions.swipeUp(), isDisplayingAtLeast(1)) //
								// , ViewActions.swipeUp() //
						);
				*/
			}
		}
	}


	private void sleep()
	{
		try
		{
			Thread.sleep(5000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public int getViewCount(final Matcher<View> matcher)
	{
		final int[] count = {0}; // has to be final
		onView(matcher).perform(new ViewAction()
		{
			@Override
			public Matcher<View> getConstraints()
			{
				return matcher;
			}

			@Override
			public String getDescription()
			{
				return "getting child view count";
			}

			@Override
			public void perform(UiController uiController, View view)
			{
				AdapterView av = (AdapterView) view;
				count[0] = av.getChildCount();
			}
		});
		return count[0];
	}

	public int getItemCount(final Matcher<View> matcher)
	{
		final int[] count = {0}; // has to be final
		onView(matcher).perform(new ViewAction()
		{
			@Override
			public Matcher<View> getConstraints()
			{
				return matcher;
			}

			@Override
			public String getDescription()
			{
				return "getting item count";
			}

			@Override
			public void perform(UiController uiController, View view)
			{
				AdapterView av = (AdapterView) view;
				count[0] = av.getAdapter().getCount();
			}
		});
		return count[0];
	}

	public int[] getGroupCounts(final Matcher<View> matcher)
	{
		final int[][] result = {new int[0]}; // has to be final
		onView(matcher).perform(new ViewAction()
		{
			@Override
			public Matcher<View> getConstraints()
			{
				return matcher;
			}

			@Override
			public String getDescription()
			{
				return "getting group item counts";
			}

			@Override
			public void perform(UiController uiController, View view)
			{
				ExpandableListView av = (ExpandableListView) view;
				ExpandableListAdapter aav = av.getExpandableListAdapter();
				int g = aav.getGroupCount();
				int[] counts = new int[g];
				for (int i = 0; i < g; i++)
				{
					counts[i] = aav.getChildrenCount(i);
				}
				result[0] = counts;
			}

		});
		return result[0];
	}

	public static ViewAction withCustomConstraints(final ViewAction action, final Matcher<View> constraints)
	{
		return new ViewAction()
		{
			@Override
			public Matcher<View> getConstraints()
			{
				return constraints;
			}

			@Override
			public String getDescription()
			{
				return action.getDescription();
			}

			@Override
			public void perform(UiController uiController, View view)
			{
				action.perform(uiController, view);
			}
		};
	}

	private static String arrayToString(int[] a)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append('{');
		boolean first = true;
		for (int i : a)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				sb.append(',');
			}
			sb.append(i);
		}
		sb.append('}');
		return sb.toString();
	}
}