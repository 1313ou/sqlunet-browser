/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.treeview.settings;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class Settings
{
	public static final String PREF_TREE_INDENT = "pref_tree_indent";
	public static final String PREF_TREE_ROW_MIN_HEIGHT = "pref_tree_row_min_height";
	public static final String PREF_SCROLL_2D = "pref_scroll_2d";
	public static final String PREF_USE_ANIMATION = "pref_use_animation";

	public static float getTreeIndent(@NonNull final SharedPreferences prefs)
	{
		int value = prefs.getInt(Settings.PREF_TREE_INDENT, 0);
		if (value == 0)
		{
			return -1F;
		}
		return value / 100F;
	}

	public static float getTreeRowMinHeight(@NonNull final SharedPreferences prefs)
	{
		int value = prefs.getInt(Settings.PREF_TREE_ROW_MIN_HEIGHT, 0);
		if (value == 0)
		{
			return -1F;
		}
		return value / 100F;
	}
}
