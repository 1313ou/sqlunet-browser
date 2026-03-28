/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser

import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding

object EdgeToEdge {

    // P A D D I N G

    /**
     * Update horizontal paddings
     *
     * @receiver view
     * @param systemBars system bars insets
     */
    fun View.updateHorizontalPadding(systemBars: Insets) = updatePadding(top = systemBars.left, bottom = systemBars.right)

    /**
     * Update vertical paddings
     *
     * @receiver view
     * @param systemBars system bars insets
     */
    fun View.updateVerticalPadding(systemBars: Insets) = updatePadding(top = systemBars.top, bottom = systemBars.bottom)

    /**
     * Update vertical padding
     *
     * @receiver view
     * @param systemBars system bars insets
     */
    fun View.updateTopPadding(systemBars: Insets) = updatePadding(top = systemBars.top)

    /**
     * Update vertical padding
     *
     * @receiver view
     * @param systemBars system bars insets
     */
    fun View.updateBottomPadding(systemBars: Insets) = updatePadding(bottom = systemBars.bottom)

    // M A R G I N

    /**
     * Update horizontal margins
     *
     * @receiver view
     * @param systemBars system bars insets
     */
    fun View.updateHorizontalMargin(systemBars: Insets) = updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin = systemBars.top; bottomMargin = systemBars.bottom }

    /**
     * Update vertical margins
     *
     * @receiver view
     * @param systemBars system bars insets
     */
    fun View.updateVerticalMargin(systemBars: Insets) = updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin = systemBars.top; bottomMargin = systemBars.bottom }

    /**
     * Update vertical margin
     *
     * @receiver view
     * @param systemBars system bars insets
     */
    fun View.updateTopMargin(systemBars: Insets) = updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin = systemBars.top }

    /**
     * Update vertical margin
     *
     * @receiver view
     * @param systemBars system bars insets
     */
    fun View.updateBottomMargin(systemBars: Insets) = updateLayoutParams<ViewGroup.MarginLayoutParams> { bottomMargin = systemBars.bottom }
}