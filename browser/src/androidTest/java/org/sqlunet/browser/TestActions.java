package org.sqlunet.browser;

import android.support.test.espresso.action.ViewActions;
import android.widget.EditText;
import android.widget.Spinner;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.sqlunet.browser.TestUtils.withCustomConstraints;

public class TestActions
{
	static public void spinner(final String targetText, final int spinnerId)
	{
		// expand spinner
		onView(allOf(withId(R.id.spinner), instanceOf(Spinner.class))) //
				.perform(click());
		// click view matching text
		onData(allOf(is(instanceOf(String.class)), is(targetText))) //
				.perform(click());
		// check
		onView(withId(R.id.spinner)) //
				.check(matches(withSpinnerText(containsString(targetText))));
	}

	static void typeSearch(final String word, final int searchViewId)
	{
		// open search view
		onView(withId(searchViewId)).check(matches(isDisplayed())).perform(click());

		// type search
		onView(allOf(isDisplayed(), isAssignableFrom(EditText.class))) //
				.check(matches(isDisplayed())) //
				.perform( //
						typeText(word),  //
						pressImeActionButton() //
				);
	}

	static void swipeUp(final int viewId)
	{
		onView(withId(R.id.container_browse2)) //
				.check(matches(isDisplayed())) //
				.perform( //
						withCustomConstraints(ViewActions.swipeUp(), isDisplayingAtLeast(1)) //
						// , ViewActions.swipeUp() //
				);
	}
}