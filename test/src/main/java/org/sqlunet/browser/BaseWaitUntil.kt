/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingResource.ResourceCallback
import androidx.test.espresso.ViewFinder
import org.hamcrest.Matcher

abstract class BaseWaitUntil(@JvmField protected val viewMatcher: Matcher<View?>) : IdlingResource {

    @JvmField
    protected var resourceCallback: ResourceCallback? = null

    override fun registerIdleTransitionCallback(resourceCallback: ResourceCallback) {
        this.resourceCallback = resourceCallback
    }

    companion object {

        @JvmStatic
        protected fun getView(viewMatcher: Matcher<View?>): View? {
            return try {
                val viewInteraction = Espresso.onView(viewMatcher)
                // private in ViewInteraction
                val finderField = viewInteraction.javaClass.getDeclaredField("viewFinder")
                finderField.isAccessible = true
                val finder = (finderField[viewInteraction] as ViewFinder)
                finder.view
            } catch (e: Exception) {
                null
            }
        }
    }
}
