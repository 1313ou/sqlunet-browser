package org.sqlunet.browser;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.DrawerActions;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
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
		final Matcher<View> searchView = withId(searchViewId);

		// open search view
		onView(searchView) //
				.check(matches(isDisplayed())) //
				.perform(click());

		// type search
		onView(allOf(isDescendantOfA(searchView), isAssignableFrom(EditText.class))) //
				.check(matches(isDisplayed())) //
				.perform( //
						typeText(word),  //
						pressImeActionButton() //
				);
	}

	static public void navigationDrawer(final String targetText)
	{
		int navigationDrawerId = R.id.navigation_drawer;

		onView(withId(R.id.drawer_layout)) //
				.perform(DrawerActions.open());

		onView(allOf( //
				withParent(withId(navigationDrawerId)), //
				withText(targetText))) //
				.perform(ViewActions.click());

		onView(withId(R.id.drawer_layout)) //
				.perform(DrawerActions.close());
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