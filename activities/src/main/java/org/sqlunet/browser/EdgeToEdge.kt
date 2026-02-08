/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.google.android.material.appbar.AppBarLayout
import org.sqlunet.activities.R

object EdgeToEdge {

    /**
     * Handle insets
     *
     * @param view view
     */
    fun handleInsets(view: View?) {
        view?.let {
            ViewCompat.setOnApplyWindowInsetsListener(view) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

                // Apply the insets as padding to the view
                view.updatePadding(top = systemBars.top, bottom = systemBars.bottom)

                // Return the insets so children don't get them doubled
                insets
            }
        }
    }

    /**
     * Handle top inset
     *
     * @param view view
     */
    fun handleInsetsTop(view: View?) {
        view?.let {
            ViewCompat.setOnApplyWindowInsetsListener(view) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

                // Apply the insets as padding to the view
                view.updatePadding(top = systemBars.top)

                // Return the insets so children don't get them doubled
                insets
            }
        }
    }

    /**
     * Handle bottom inset
     *
     * @param view view
     */
    fun handleInsetsBottom(view: View?) {
        view?.let {
            ViewCompat.setOnApplyWindowInsetsListener(view) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

                // Apply the insets as padding to the view
                view.updatePadding(bottom = systemBars.bottom)

                // Return the insets so children don't get them doubled
                insets
            }
        }
    }

    fun handleInsetsToolbarAndContent(rootView: View) {

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply top inset to toolbar/appbar
            view.findViewById<AppBarLayout>(R.id.appbar_layout)?.updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin = systemBars.top }

            // Apply bottom inset to your content
            view.findViewById<View>(android.R.id.content)?.updatePadding(bottom = systemBars.bottom)

            // Return the insets so children don't get them doubled
            insets
        }
    }
}