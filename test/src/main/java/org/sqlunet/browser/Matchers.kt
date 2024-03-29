/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.view.View
import android.widget.CheckBox
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher

object Matchers {

    fun withMenuIdOrText(@IdRes id: Int, @StringRes menuText: Int): Matcher<View> {
        val matcher = ViewMatchers.withId(id)
        return try {
            Espresso.onView(matcher).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            matcher
        } catch (noMatchingViewException: Exception) {
            Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
            ViewMatchers.withText(menuText)
        }
    }

    fun checkboxWithMenuItem(@StringRes titleId: Int): Matcher<View> {
        /*
		menuitem
			title+shortcut
				title
				shortcut
			arrow
			checkbox

		allOf(
			instanceOf(AppCompatCheckBox.class),
			isCompletelyDisplayed(),
			hasSibling(withChild(withText(titleId)))
		)

		withText->title
		withChild(title)->title+shortcut
		hasSibling(title+shortcut)->arrow,checkbox
		isCompletelyDisplayed()+instanceOf(AppCompatCheckBox.class) eliminates arrow
		*/
        return CoreMatchers.allOf(CoreMatchers.instanceOf(CheckBox::class.java), ViewMatchers.hasSibling(ViewMatchers.withChild(ViewMatchers.withText(titleId))), ViewMatchers.isCompletelyDisplayed())
    }
}