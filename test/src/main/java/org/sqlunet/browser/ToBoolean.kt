/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.assertion.ViewAssertions
import org.hamcrest.Matcher

object ToBoolean {

    fun test(view: Matcher<View?>, state: Matcher<View?>): Boolean {
        return try {
            Espresso.onView(view).check(ViewAssertions.matches(state))
            true
        }
        catch (e: Throwable) {
            false
        }
    }

    fun testAssertion(view: Matcher<View?>, assertion: ViewAssertion): Boolean {
        return try {
            Espresso.onView(view).check(assertion)
            true
        }
        catch (e: Throwable) {
            false
        }
    }
}