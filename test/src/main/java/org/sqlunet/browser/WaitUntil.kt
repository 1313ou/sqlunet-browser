/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.view.View
import androidx.annotation.IdRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher

class WaitUntil  // private static final String TAG = WaitUntil.class.getSimpleName();
    (viewMatcher: Matcher<View?>?) : BaseWaitUntil(viewMatcher!!) {

    override fun isIdleNow(): Boolean {
        val view = getView(viewMatcher)
        val idle = view == null || view.isShown()
        if (idle && resourceCallback != null) {
            resourceCallback!!.onTransitionToIdle()
        }
        return idle
    }

    override fun getName(): String {
        return this.toString() + viewMatcher.toString()
    }

    companion object {

        private fun waitViewShown(matcher: Matcher<View?>) {
            val idlingResource: IdlingResource = WaitUntil(matcher)
            try {
                IdlingRegistry.getInstance().register(idlingResource)
                //onView(matcher).check(matches(isDisplayed()));
                Espresso.onView(matcher).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
            } finally {
                IdlingRegistry.getInstance().unregister(idlingResource)
            }
        }

        fun shown(@IdRes viewId: Int) {
            waitViewShown(ViewMatchers.withId(viewId))
        }
    }
}
