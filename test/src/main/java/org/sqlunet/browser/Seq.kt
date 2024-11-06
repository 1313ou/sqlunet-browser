/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.content.Context
import android.widget.EditText
import android.widget.Spinner
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers
import org.sqlunet.browser.Actions.onlyIf

object Seq {

    fun getResourceString(@StringRes id: Int): String {
        val targetContext = ApplicationProvider.getApplicationContext<Context>()
        return targetContext.resources.getString(id)
    }

    /**
     * Press back control
     */
    fun doPressBack() {
        Espresso.onView(ViewMatchers.isRoot()).perform(ViewActions.pressBack())
    }

    /**
     * Type in EditTextView
     *
     * @param editTextViewId EditTextView id
     * @param text           text
     */
    @Suppress("unused")
    fun doType(@IdRes editTextViewId: Int, text: String) {
        Espresso.onView(ViewMatchers.withId(editTextViewId))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(
                ViewActions.typeText(text)
            )
    }

    /**
     * Type in SearchView
     *
     * @param searchViewId SearchView id
     * @param text         text
     */
    fun doTypeSearch(@IdRes searchViewId: Int, text: String) {
        val searchView = ViewMatchers.withId(searchViewId)
        // open search view
        Espresso.onView(searchView)
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(
                ViewActions.click()
            )
        // type search
        Espresso.onView(CoreMatchers.allOf(ViewMatchers.isDescendantOfA(searchView), ViewMatchers.isAssignableFrom(EditText::class.java)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(
                ViewActions.typeText(text),
                ViewActions.pressImeActionButton()
            )
    }

    /**
     * Click button
     *
     * @param buttonId Button id
     */
    fun doClick(@IdRes buttonId: Int) {
        Espresso.onView(ViewMatchers.withId(buttonId))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(ViewActions.click())
    }

    /**
     * Click menu item (by text)
     *
     * @param menuId   Menu id
     * @param menuText Text in menu item to click
     */
    @Suppress("unused")
    fun doMenu(@IdRes menuId: Int, @StringRes menuText: Int) {
        Espresso.onView(Matchers.withMenuIdOrText(menuId, menuText)).perform(ViewActions.click())
    }

    /**
     * Click overflow menu item (by text)
     *
     * @param menuText Text in menu item to click
     */
    @Suppress("unused")
    fun doOptionsMenu(@StringRes menuText: Int) {
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        Espresso.onView(ViewMatchers.withText(menuText)).perform(ViewActions.click())
    }

    /**
     * Click spinner (by text)
     *
     * @param spinnerId  Spinner id
     * @param targetText Text in spinner item to click
     */
    fun doChoose(@IdRes spinnerId: Int, targetText: String) {
        // expand spinner
        Espresso.onView(CoreMatchers.allOf(ViewMatchers.withId(spinnerId), CoreMatchers.instanceOf(Spinner::class.java)))
            .perform(ViewActions.click())
        // do_click view matching text
        Espresso.onData(CoreMatchers.allOf(CoreMatchers.`is`(CoreMatchers.instanceOf<Any>(String::class.java)), CoreMatchers.`is`(targetText)))
            .perform(ViewActions.click())
        // check
        Espresso.onView(ViewMatchers.withId(spinnerId))
            .check(ViewAssertions.matches(ViewMatchers.withSpinnerText(CoreMatchers.containsString(targetText))))
    }

    /**
     * Click spinner (by text)
     *
     * @param spinnerId Spinner id
     * @param position  Text in spinner item to click
     */
    fun doChoose(@IdRes spinnerId: Int, position: Int) {
        // expand spinner
        Espresso.onView(CoreMatchers.allOf(ViewMatchers.withId(spinnerId), CoreMatchers.instanceOf(Spinner::class.java)))
            .perform(ViewActions.click())
        // do_click view matching position
        Espresso.onData(org.hamcrest.Matchers.anything()).atPosition(position)
            .perform(ViewActions.click())
    }

    /**
     * Click navigation drawer item (by text)
     *
     * @param drawerLayoutId DrawerLayout id
     * @param navViewId      NavigationView id
     * @param targetText     Text in nav item to click
     */
    fun doNavigate(@IdRes drawerLayoutId: Int, @IdRes navViewId: Int, targetText: String?) {
        Espresso.onView(ViewMatchers.withId(drawerLayoutId))
            .perform(DrawerActions.open())
        /*
		var t = !Utils.testAssertion(withId(drawer_layout), doesNotExist());
		t = Utils.test(withId(drawer_layout), isDisplayed());
		t = !Utils.testAssertion(allOf(isDescendantOfA(withId(drawer_layout)), withText(targetText)), doesNotExist());
		t = Utils.test(allOf(isDescendantOfA(withId(drawer_layout)), withText(targetText)), isDisplayed());
		*/
        Espresso.onView(
            CoreMatchers.allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(navViewId)),
                ViewMatchers.withText(targetText)
            )
        )
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(drawerLayoutId))
            .perform(DrawerActions.close())
    }

    /**
     * Click navigation drawer item (by text)
     *
     * @param drawerLayoutId DrawerLayout id
     * @param navViewId      NavigationView id
     * @param targetId       Menu item id in nav item to click
     */
    fun doNavigate(@IdRes drawerLayoutId: Int, @IdRes navViewId: Int, @MenuRes targetId: Int) {
        Espresso.onView(ViewMatchers.withId(drawerLayoutId))
            .perform(DrawerActions.open())
        /*
		var t = !Utils.testAssertion(withId(drawer_layout), doesNotExist())
		t = Utils.test(withId(drawer_layout), isDisplayed())
		t = !Utils.testAssertion(allOf(isDescendantOfA(withId(drawer_layout)), withText(targetText)), doesNotExist())
		t = Utils.test(allOf(isDescendantOfA(withId(drawer_layout)), withText(targetText)), isDisplayed())
		*/Espresso.onView(ViewMatchers.withId(navViewId))
            .perform(NavigationViewActions.navigateTo(targetId))
        Espresso.onView(ViewMatchers.withId(drawerLayoutId))
            .perform(DrawerActions.close())
    }

    /**
     * Swipe view
     *
     * @param viewId View id
     */
    @Suppress("unused")
    fun doSwipeUp(@IdRes viewId: Int) {
        Espresso.onView(ViewMatchers.withId(viewId))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(
                onlyIf(ViewActions.swipeUp(), ViewMatchers.isDisplayingAtLeast(1))
                //, swipeUp()
            )
    }
}