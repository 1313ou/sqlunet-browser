/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.propbank.style;

import android.content.Context;
import android.graphics.Color;

import org.sqlunet.propbank.R;

import androidx.annotation.NonNull;

/**
 * Color values
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Colors
{
	static public int thetaBackColor = Color.TRANSPARENT;
	static public int thetaForeColor = Color.TRANSPARENT;

	static public int relationBackColor = Color.TRANSPARENT;
	static public int relationForeColor = Color.TRANSPARENT;

	static public void setColorsFromResources(@NonNull final Context context)
	{
		// do not reorder : dependent on resource array order

		int[] palette = context.getResources().getIntArray(R.array.palette_pb);
		int i = 0;
		thetaBackColor = palette[i++];
		thetaForeColor = palette[i++];

		relationBackColor = palette[i++];
		//noinspection UnusedAssignment
		relationForeColor = palette[i++];
	}
}
