package org.sqlunet.browser;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.sqlunet.browser.wn.R;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.matcher.ViewMatchers;

class TestActions
{
	static public void spinner(final String targetText, final int spinnerId)
	{
		// expand spinner
		Espresso.onView(CoreMatchers.allOf(ViewMatchers.withId(spinnerId), CoreMatchers.instanceOf(Spinner.class))) //
				.perform(ViewActions.click());
		// click view matching text
		Espresso.onData(CoreMatchers.allOf(CoreMatchers.is(CoreMatchers.instanceOf(String.class)), CoreMatchers.is(targetText))) //
				.perform(ViewActions.click());
		// check
		Espresso.onView(ViewMatchers.withId(spinnerId)) //
				.check(ViewAssertions.matches(ViewMatchers.withSpinnerText(CoreMatchers.containsString(targetText))));
	}

	static void typeSearch(final String word, final int searchViewId)
	{
		final Matcher<View> searchView = ViewMatchers.withId(searchViewId);

		// open search view
		Espresso.onView(searchView) //
				.check(ViewAssertions.matches(ViewMatchers.isDisplayed())) //
				.perform(ViewActions.click());

		// type search
		Espresso.onView(CoreMatchers.allOf(ViewMatchers.isDescendantOfA(searchView), ViewMatchers.isAssignableFrom(EditText.class))) //
				.check(ViewAssertions.matches(ViewMatchers.isDisplayed())) //
				.perform( //
						ViewActions.typeText(word),  //
						ViewActions.pressImeActionButton() //
				);
	}

	static public void navigationDrawer(final String targetText)
	{
		Espresso.onView(ViewMatchers.withId(R.id.drawer_layout)) //
				.perform(DrawerActions.open());

		Espresso.onView(CoreMatchers.allOf( //
				ViewMatchers.withParent(ViewMatchers.withId(R.id.navigation_drawer)), //
				ViewMatchers.withText(targetText))) //
				.perform(ViewActions.click());

		Espresso.onView(ViewMatchers.withId(R.id.drawer_layout)) //
				.perform(DrawerActions.close());
	}

	static void swipeUp(final int viewId)
	{
		Espresso.onView(ViewMatchers.withId(viewId)) //
				.check(ViewAssertions.matches(ViewMatchers.isDisplayed())) //
				.perform( //
						TestUtils.withCustomConstraints(ViewActions.swipeUp(), ViewMatchers.isDisplayingAtLeast(1)) //
						// , ViewActions.swipeUp() //
				);
	}
}