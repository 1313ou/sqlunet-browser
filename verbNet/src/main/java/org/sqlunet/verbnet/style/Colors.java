/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.verbnet.style;

import android.content.Context;
import android.graphics.Color;

import org.sqlunet.verbnet.R;

import androidx.annotation.NonNull;

/**
 * Color values
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Colors
{
	static public int vnPredicateBackColor = Color.TRANSPARENT;
	static public int vnPredicateForeColor = Color.TRANSPARENT;

	static public int groupingBackColor = Color.TRANSPARENT;
	static public int groupingForeColor = Color.TRANSPARENT;

	static public int vnFrameBackColor = Color.TRANSPARENT;
	static public int vnFrameForeColor = Color.TRANSPARENT;

	static public int vnFrameSubnameBackColor = Color.TRANSPARENT;
	static public int vnFrameSubnameForeColor = Color.TRANSPARENT;

	static public int themroleBackColor = Color.TRANSPARENT;
	static public int themroleForeColor = Color.TRANSPARENT;

	static public int catBackColor = Color.TRANSPARENT;
	static public int catForeColor = Color.TRANSPARENT;

	static public int catValueBackColor = Color.TRANSPARENT;
	static public int catValueForeColor = Color.TRANSPARENT;

	static public int restrBackColor = Color.TRANSPARENT;
	static public int restrForeColor = Color.TRANSPARENT;

	static public int constantBackColor = Color.TRANSPARENT;
	static public int constantForeColor = Color.TRANSPARENT;

	static public int eventBackColor = Color.TRANSPARENT;
	static public int eventForeColor = Color.TRANSPARENT;

	static public void setColorsFromResources(@NonNull final Context context)
	{
		// do not reorder : dependent on resource array order

		int[] palette = context.getResources().getIntArray(R.array.palette_vn);
		int i = 0;
		vnPredicateBackColor = palette[i++];
		vnPredicateForeColor = palette[i++];

		groupingBackColor = palette[i++];
		groupingForeColor = palette[i++];

		vnFrameBackColor = palette[i++];
		vnFrameForeColor = palette[i++];

		vnFrameSubnameBackColor = palette[i++];
		vnFrameSubnameForeColor = palette[i++];

		themroleBackColor = palette[i++];
		themroleForeColor = palette[i++];

		catBackColor = palette[i++];
		catForeColor = palette[i++];

		catValueBackColor = palette[i++];
		catValueForeColor = palette[i++];

		restrBackColor = palette[i++];
		restrForeColor = palette[i++];

		constantBackColor = palette[i++];
		constantForeColor = palette[i++];

		eventBackColor = palette[i++];
		//noinspection UnusedAssignment
		eventForeColor = palette[i++];
	}
}
