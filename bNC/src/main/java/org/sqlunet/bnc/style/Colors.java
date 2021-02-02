/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.bnc.style;

import android.content.Context;
import android.graphics.Color;

import org.sqlunet.bnc.R;

import androidx.annotation.NonNull;

/**
 * Color values
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Colors
{
	// BNC
	static public int bncHeaderBackColor = Color.TRANSPARENT;
	static public int bncHeaderForeColor = Color.TRANSPARENT;

	static public void setColorsFromResources(@NonNull final Context context)
	{
		// do not reorder : dependent on resource array order

		int[] palette = context.getResources().getIntArray(R.array.palette_bnc);
		int i = 0;
		bncHeaderBackColor = palette[i++];
		bncHeaderForeColor = palette[i++];
	}
}