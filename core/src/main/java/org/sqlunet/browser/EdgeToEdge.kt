/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser

import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import kotlin.Int

object EdgeToEdge {

    // P A D D I N G

    /**
     * Update horizontal paddings
     *
     * @receiver view
     * @param systemBars system bars insets
     * @param initialLeftPadding initial (built-in) left padding
     * @param initialRightPadding initial (built-in) right padding
     */
    fun View.updateHorizontalPadding(systemBars: Insets, initialLeftPadding: Int = 0, initialRightPadding: Int = 0) =
        updatePadding(left = systemBars.left + initialLeftPadding, right = systemBars.right + initialRightPadding)

    /**
     * Update vertical paddings
     *
     * @receiver view
     * @param systemBars system bars insets
     * @param initialTopPadding initial (built-in) top padding
     * @param initialBottomPadding initial (built-in) bottom padding
     */
    fun View.updateVerticalPadding(systemBars: Insets, initialTopPadding: Int = 0, initialBottomPadding: Int = 0) =
        updatePadding(top = systemBars.top + initialTopPadding, bottom = systemBars.bottom + initialBottomPadding)

    /**
     * Update vertical padding
     *
     * @receiver view
     * @param systemBars system bars insets
     * @param initialPadding initial (built-in) padding
     */
    fun View.updateTopPadding(systemBars: Insets, initialPadding: Int = 0) =
        updatePadding(top = systemBars.top + initialPadding)

    /**
     * Update vertical padding
     *
     * @receiver view
     * @param systemBars system bars insets
     * @param initialPadding initial (built-in) padding
     */
    fun View.updateBottomPadding(systemBars: Insets, initialPadding: Int = 0) =
        updatePadding(bottom = systemBars.bottom + initialPadding)

    // M A R G I N

    /**
     * Update horizontal margins
     *
     * @receiver view
     * @param systemBars system bars insets
     * @param initialLeftMargin initial (built-in) left margin
     * @param initialRightMargin initial (built-in) right margin
     */
    fun View.updateHorizontalMargin(systemBars: Insets, initialLeftMargin: Int = 0, initialRightMargin: Int = 0) =
        updateLayoutParams<ViewGroup.MarginLayoutParams> { leftMargin = systemBars.left + initialLeftMargin; rightMargin = systemBars.right + initialRightMargin }

    /**
     * Update vertical margins
     *
     * @receiver view
     * @param systemBars system bars insets
     * @param initialTopMargin initial (built-in) top margin
     * @param initialBottomMargin initial (built-in) bottom margin
     */
    fun View.updateVerticalMargin(systemBars: Insets, initialTopMargin: Int = 0, initialBottomMargin: Int = 0) =
        updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin = systemBars.top + initialTopMargin; bottomMargin = systemBars.bottom + initialBottomMargin }

    /**
     * Update vertical margin
     *
     * @receiver view
     * @param systemBars system bars insets
     * @param initialMargin initial (built-in) margin
     */
    fun View.updateTopMargin(systemBars: Insets, initialMargin: Int = 0) =
        updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin = systemBars.top + initialMargin }

    /**
     * Update vertical margin
     *
     * @receiver view
     * @param systemBars system bars insets
     * @param initialMargin initial (built-in) margin
     */
    fun View.updateBottomMargin(systemBars: Insets, initialMargin: Int = 0) =
        updateLayoutParams<ViewGroup.MarginLayoutParams> { bottomMargin = systemBars.bottom + initialMargin }
}