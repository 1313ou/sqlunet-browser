/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.syntagnet.style;

import android.content.Context;
import android.graphics.Color;

import org.sqlunet.syntagnet.R;

import androidx.annotation.NonNull;

/**
 * Color values
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Colors
{
	static public int collocationBackColor = Color.TRANSPARENT;
	static public int collocationForeColor = Color.TRANSPARENT;

	static public int word1BackColor = Color.TRANSPARENT;
	static public int word1ForeColor = Color.TRANSPARENT;

	static public int word2BackColor = Color.TRANSPARENT;
	static public int word2ForeColor = Color.TRANSPARENT;

	static public int definition1BackColor = Color.TRANSPARENT;
	static public int definition1ForeColor = Color.TRANSPARENT;

	static public int definition2BackColor = Color.TRANSPARENT;
	static public int definition2ForeColor = Color.TRANSPARENT;

	static public int idsBackColor = Color.TRANSPARENT;
	static public int idsForeColor = Color.TRANSPARENT;

	static public void setColorsFromResources(@NonNull final Context context)
	{
		// do not reorder : dependent on resource array order

		int[] palette = context.getResources().getIntArray(R.array.palette_sn);
		int i = 0;
		collocationBackColor = palette[i++];
		collocationForeColor = palette[i++];

		word1BackColor = palette[i++];
		word1ForeColor = palette[i++];

		word2BackColor = palette[i++];
		word2ForeColor = palette[i++];

		definition1BackColor = palette[i++];
		definition1ForeColor = palette[i++];

		definition2BackColor = palette[i++];
		definition2ForeColor = palette[i++];

		idsBackColor = palette[i++];
		//noinspection UnusedAssignment
		idsForeColor = palette[i++];
	}
}
