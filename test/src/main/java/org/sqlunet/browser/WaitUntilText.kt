/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.PerformException
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.util.HumanReadables
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher

class WaitUntilText(
    viewMatcher: Matcher<in View?>,
    private val target: String, private val not: Boolean
) : BaseWaitUntil(
    CoreMatchers.allOf(
        viewMatcher, CoreMatchers.instanceOf(
            TextView::class.java
        )
    )
) {

    override fun isIdleNow(): Boolean {
        val view = getView(viewMatcher)
        val idle: Boolean = if (view == null) {
            true
        } else {
            if (view !is TextView) {
                throw PerformException.Builder().withActionDescription(this.name).withViewDescription(HumanReadables.describe(view)).withCause(ClassCastException()).build()
            }
            val content = view.text
            val text = content.toString()
            val condition = not != (text == target)
            view.isShown && condition
        }
        if (idle && resourceCallback != null) {
            resourceCallback!!.onTransitionToIdle()
        }
        return idle
    }

    override fun getName(): String {
        return this.toString() + viewMatcher.toString()
    }

    companion object {

        private fun waitTextView(matcher: Matcher<View?>, target: String, not: Boolean) {
            val idlingResource: IdlingResource = WaitUntilText(matcher, target, not)
            try {
                IdlingRegistry.getInstance().register(idlingResource)
                Espresso.onView(matcher).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            } finally {
                IdlingRegistry.getInstance().unregister(idlingResource)
            }
        }

        fun changesTo(@IdRes viewId: Int, target: String) {
            waitTextView(ViewMatchers.withId(viewId), target, false)
        }

        fun changesFrom(@IdRes viewId: Int, target: String) {
            waitTextView(ViewMatchers.withId(viewId), target, true)
        }
    }
}
