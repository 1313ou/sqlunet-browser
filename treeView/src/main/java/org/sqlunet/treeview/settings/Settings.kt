/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.treeview.settings

import android.content.SharedPreferences

object Settings {

    const val PREF_TREE_INDENT = "pref_tree_indent"
    private const val PREF_TREE_ROW_MIN_HEIGHT = "pref_tree_row_min_height"
    const val PREF_SCROLL_2D = "pref_scroll_2d"
    const val PREF_USE_ANIMATION = "pref_use_animation"

    fun getTreeIndent(prefs: SharedPreferences): Float {
        val value = prefs.getInt(PREF_TREE_INDENT, 0)
        return if (value == 0) {
            -1f
        } else value / 100f
    }

    fun getTreeRowMinHeight(prefs: SharedPreferences): Float {
        val value = prefs.getInt(PREF_TREE_ROW_MIN_HEIGHT, 0)
        return if (value == 0) {
            -1f
        } else value / 100f
    }
}
