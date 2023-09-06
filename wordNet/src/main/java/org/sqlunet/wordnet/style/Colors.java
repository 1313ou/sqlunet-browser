/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.wordnet.style;

import android.content.Context;
import android.graphics.Color;

import org.sqlunet.wordnet.R;

import androidx.annotation.NonNull;

/**
 * Color values
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Colors
{
	static public int membersBackColor = Color.TRANSPARENT;
	static public int membersForeColor = Color.TRANSPARENT;

	static public int wordBackColor = Color.TRANSPARENT;
	static public int wordForeColor = Color.TRANSPARENT;

	static public void setColorsFromResources(@NonNull final Context context)
	{
		// do not reorder : dependent on resource array order

		int[] palette = context.getResources().getIntArray(R.array.palette_wn);
		int i = 0;
		membersBackColor = palette[i++];
		membersForeColor = palette[i++];

		wordBackColor = palette[i++];
		wordForeColor = palette[i];
	}
}
