/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.util.TreeIterables
import org.hamcrest.Matcher
import java.util.concurrent.TimeoutException

object Wait {

    private const val TIME_UNIT_IN_MS = 1000
    private fun waitId(@IdRes viewId: Int, millis: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isRoot()
            }

            override fun getDescription(): String {
                return "wait for a specific view with id <$viewId> during $millis millis."
            }

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadUntilIdle()
                val startTime = System.currentTimeMillis()
                val endTime = startTime + millis
                val viewMatcher = ViewMatchers.withId(viewId)
                do {
                    for (child in TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            return
                        }
                    }
                    uiController.loopMainThreadForAtLeast(50)
                } while (System.currentTimeMillis() < endTime)
                // timeout happens
                throw PerformException.Builder().withActionDescription(this.description).withViewDescription(HumanReadables.describe(view)).withCause(TimeoutException()).build()
            }
        }
    }

    private fun waitIdText(viewId: Int, target: String, not: Boolean, millis: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isRoot()
            }

            override fun getDescription(): String {
                return "wait for a specific view with id <$viewId> during $millis millis."
            }

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadUntilIdle()
                val startTime = System.currentTimeMillis()
                val endTime = startTime + millis
                val viewMatcher = ViewMatchers.withId(viewId)
                do {
                    for (child in TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            if (child !is TextView) {
                                throw PerformException.Builder().withActionDescription(this.description).withViewDescription(HumanReadables.describe(view)).withCause(ClassCastException()).build()
                            }
                            val text = child.text.toString()
                            if (not && text != target) {
                                return
                            } else if (!not && text == target) {
                                return
                            }
                        }
                    }
                    uiController.loopMainThreadForAtLeast(50)
                } while (System.currentTimeMillis() < endTime)
                // timeout happens
                throw PerformException.Builder().withActionDescription(this.description).withViewDescription(HumanReadables.describe(view)).withCause(TimeoutException()).build()
            }
        }
    }

    fun until(@IdRes resId: Int, sec: Int) {
        Espresso.onView(ViewMatchers.isRoot()).perform(waitId(resId, sec.toLong() * TIME_UNIT_IN_MS))
    }

    @Suppress("unused")
    fun untilNotText(@IdRes resId: Int, target: String, sec: Int) {
        Espresso.onView(ViewMatchers.isRoot()).perform(waitIdText(resId, target, true, sec.toLong() * TIME_UNIT_IN_MS))
    }

    @Suppress("unused")
    fun untilText(@IdRes resId: Int, target: String, sec: Int) {
        Espresso.onView(ViewMatchers.isRoot()).perform(waitIdText(resId, target, false, sec.toLong() * TIME_UNIT_IN_MS))
    }

    fun pause(sec: Int) {
        try {
            Thread.sleep(sec.toLong() * TIME_UNIT_IN_MS)
        } catch (e: InterruptedException) {
            //
        }
    }
}
