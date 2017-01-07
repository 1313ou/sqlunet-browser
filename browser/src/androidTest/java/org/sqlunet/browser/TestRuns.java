package org.sqlunet.browser;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

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
import static android.support.test.espresso.matcher.ViewMatchers.withId;
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

			// for all selectors
			int n = getAdapterViewChildCount(list);
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

	public int getAdapterViewChildCount(final Matcher<View> matcher)
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
				return "getting child count";
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
}