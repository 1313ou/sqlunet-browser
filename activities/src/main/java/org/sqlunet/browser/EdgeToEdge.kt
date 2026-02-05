/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser

import android.app.Activity
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

object EdgeToEdge {

    /**
     * Handle insets
     *
     * @param activity activity
     */
    fun handleInsets(activity: Activity, viewId : Int) {
        val rootView = activity.findViewById<View>(viewId)
        handleInsets(rootView)
    }

    /**
     * Handle insets
     *
     * @param mainView main view
     */
    fun handleInsets(mainView: View) {
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply the insets as padding to the view
            view.updatePadding(top = systemBars.top, bottom = systemBars.bottom)

            // Return the insets so children don't get them doubled
            insets
        }
    }
}