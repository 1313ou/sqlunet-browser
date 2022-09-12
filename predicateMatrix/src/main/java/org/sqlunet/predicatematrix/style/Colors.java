/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.predicatematrix.style;

import android.content.Context;
import android.graphics.Color;

import org.sqlunet.predicatematrix.R;

import androidx.annotation.NonNull;

/**
 * Color values
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Colors
{
	static public int predicateNameBackColor = Color.TRANSPARENT;
	static public int predicateNameForeColor = Color.TRANSPARENT;

	static public int groupBackColor = Color.TRANSPARENT;
	static public int groupForeColor = Color.TRANSPARENT;

	static public int roleAliasBackColor = Color.TRANSPARENT;
	static public int roleAliasForeColor = Color.TRANSPARENT;

	static public void setColorsFromResources(@NonNull final Context context)
	{
		// do not reorder : dependent on resource array order

		int[] palette = context.getResources().getIntArray(R.array.palette_pm);
		int i = 0;
		predicateNameBackColor = palette[i++];
		predicateNameForeColor = palette[i++];

		groupBackColor = palette[i++];
		groupForeColor = palette[i++];

		roleAliasBackColor = palette[i++];
		//noinspection UnusedAssignment
		roleAliasForeColor = palette[i++];
	}
}
