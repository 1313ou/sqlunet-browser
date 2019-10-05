/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import org.hamcrest.Matcher;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.platform.app.InstrumentationRegistry;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

public class Actions
{
	/**
	 * Press back control
	 */
	@SuppressWarnings("unused")
	static public void do_pressBack()
	{
		onView(isRoot()).perform(pressBack());
	}

	/**
	 * Type in EditTextView
	 *
	 * @param editTextViewId EditTextView id
	 * @param text           text
	 */
	@SuppressWarnings("unused")
	static public void do_type(@SuppressWarnings("SameParameterValue") @IdRes final int editTextViewId, @NonNull final String text)
	{
		onView(withId(editTextViewId)) //
				.check(matches(isDisplayed())) //
				.perform(typeText(text) //
				);
	}

	/**
	 * Type in SearchView
	 *
	 * @param searchViewId SearchView id
	 * @param text         text
	 */
	@SuppressWarnings("unused")
	static public void do_typeSearch(@IdRes final int searchViewId, final String text)
	{
		final Matcher<View> searchView = withId(searchViewId);

		// open search view
		onView(searchView) //
				.check(matches(isDisplayed())) //
				.perform(click() //
				);

		// type search
		onView(allOf(isDescendantOfA(searchView), isAssignableFrom(EditText.class))) //
				.check(matches(isDisplayed())) //
				.perform( //
						typeText(text),  //
						pressImeActionButton() //
				);
	}

	/**
	 * Click button
	 *
	 * @param buttonId Button id
	 */
	@SuppressWarnings("unused")
	static public void do_click(@IdRes final int buttonId)
	{
		onView(withId(buttonId)) //
				.check(matches(isDisplayed())) //
				.perform(click())  //
		;
	}

	/**
	 * Click menu item (by text)
	 *
	 * @param menuId   Menu id
	 * @param menuText Text in menu item to click
	 */
	@SuppressWarnings("unused")
	static public void do_menu(final @IdRes int menuId, @StringRes int menuText)
	{
		onView(Matchers.withMenuIdOrText(menuId, menuText)).perform(click());
	}

	/**
	 * Click overflow menu item (by text)
	 *
	 * @param menuText Text in menu item to click
	 */
	@SuppressWarnings("unused")
	static public void do_options_menu(final @StringRes int menuText)
	{
		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
		onView(withText(menuText)).perform(click());
	}

	/**
	 * Click spinner (by text)
	 *
	 * @param spinnerId  Spinner id
	 * @param targetText Text in spinner item to click
	 */
	@SuppressWarnings("Unused")
	static public void do_choose(@IdRes int spinnerId, final String targetText)
	{
		// expand spinner
		onView(allOf(withId(spinnerId), instanceOf(Spinner.class))) //
				.perform(click());

		// do_click view matching text
		onData(allOf(is(instanceOf(String.class)), is(targetText))) //
				.perform(click());

		// check
		onView(withId(spinnerId)) //
				.check(matches(withSpinnerText(containsString(targetText))));
	}

	/**
	 * Click navigation drawer item (by text)
	 *
	 * @param drawerLayoutId DrawerLayout id
	 * @param navViewId      NavigationView id
	 * @param targetText     Text in nav item to click
	 */
	@SuppressWarnings("unused")
	static public void do_navigate(@IdRes int drawerLayoutId, @IdRes int navViewId, final String targetText)
	{
		onView(withId(drawerLayoutId)) //
				.perform(DrawerActions.open());

		/*
		boolean t = !Utils.testAssertion(withId(R.id.navigation_drawer), doesNotExist());
		t = Utils.test(withId(R.id.navigation_drawer), isDisplayed());
		t = !Utils.testAssertion(allOf(isDescendantOfA(withId(R.id.navigation_drawer)), withText(targetText)), doesNotExist());
		t = Utils.test(allOf(isDescendantOfA(withId(R.id.navigation_drawer)), withText(targetText)), isDisplayed());
		*/

		onView(allOf( //
				isDescendantOfA(withId(navViewId)), //
				withText(targetText))) //
				.perform(click());

		onView(withId(drawerLayoutId)) //
				.perform(DrawerActions.close());
	}

	/**
	 * Swipe view
	 *
	 * @param viewId View id
	 */
	@SuppressWarnings("unused")
	static public void do_swipeUp(@IdRes final int viewId)
	{
		onView(withId(viewId)) //
				.check(matches(isDisplayed())) //
				.perform( //
						Matchers.withCustomConstraints(swipeUp(), isDisplayingAtLeast(1)) //
						//, swipeUp() //
				);
	}
}