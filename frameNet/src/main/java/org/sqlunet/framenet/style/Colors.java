/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.framenet.style;

import android.content.Context;
import android.graphics.Color;

import org.sqlunet.framenet.R;

import androidx.annotation.NonNull;

/**
 * Color values
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Colors
{
	static public int feBackColor = Color.TRANSPARENT;
	static public int feForeColor = Color.TRANSPARENT;

	static public int feAbbrevBackColor = Color.TRANSPARENT;
	static public int feAbbrevForeColor = Color.TRANSPARENT;

	static public int fe2BackColor = Color.TRANSPARENT;
	static public int fe2ForeColor = Color.TRANSPARENT;

	static public int fenBackColor = Color.TRANSPARENT;
	static public int fenForeColor = Color.TRANSPARENT;

	static public int fenWithinDefBackColor = Color.TRANSPARENT;
	static public int fenWithinDefForeColor = Color.TRANSPARENT;

	static public int xfenBackColor = Color.TRANSPARENT;
	static public int xfenForeColor = Color.TRANSPARENT;

	static public int fexBackColor = Color.TRANSPARENT;
	static public int fexForeColor = Color.TRANSPARENT;

	static public int fexWithinDefBackColor = Color.TRANSPARENT;
	static public int fexWithinDefForeColor = Color.TRANSPARENT;

	static public int metaFrameDefinitionBackColor = Color.TRANSPARENT;
	static public int metaFrameDefinitionForeColor = Color.TRANSPARENT;

	static public int metaFeDefinitionBackColor = Color.TRANSPARENT;
	static public int metaFeDefinitionForeColor = Color.TRANSPARENT;

	static public int tag1BackColor = Color.TRANSPARENT;
	static public int tag1ForeColor = Color.TRANSPARENT;

	static public int tag2BackColor = Color.TRANSPARENT;
	static public int tag2ForeColor = Color.TRANSPARENT;

	static public int exBackColor = Color.TRANSPARENT;
	static public int exForeColor = Color.TRANSPARENT;

	static public int xBackColor = Color.TRANSPARENT;
	static public int xForeColor = Color.TRANSPARENT;

	static public int tBackColor = Color.TRANSPARENT;
	static public int tForeColor = Color.TRANSPARENT;

	static public int governorTypeBackColor = Color.TRANSPARENT;
	static public int governorTypeForeColor = Color.TRANSPARENT;

	static public int governorBackColor = Color.TRANSPARENT;
	static public int governorForeColor = Color.TRANSPARENT;

	static public int annoSetBackColor = Color.TRANSPARENT;
	static public int annoSetForeColor = Color.TRANSPARENT;

	static public int layerTypeBackColor = Color.TRANSPARENT;
	static public int layerTypeForeColor = Color.TRANSPARENT;

	static public int labelBackColor = Color.TRANSPARENT;
	static public int labelForeColor = Color.TRANSPARENT;

	static public int subtextBackColor = Color.TRANSPARENT;
	static public int subtextForeColor = Color.TRANSPARENT;

	static public int groupBackColor = Color.TRANSPARENT;
	static public int groupForeColor = Color.TRANSPARENT;

	static public int targetBackColor = Color.TRANSPARENT;
	static public int targetForeColor = Color.TRANSPARENT;

	static public int targetHighlightTextBackColor = Color.TRANSPARENT;
	static public int targetHighlightTextForeColor = Color.TRANSPARENT;

	static public int ptBackColor = Color.TRANSPARENT;
	static public int ptForeColor = Color.TRANSPARENT;

	static public int gfBackColor = Color.TRANSPARENT;
	static public int gfForeColor = Color.TRANSPARENT;

	static public void setColorsFromResources(@NonNull final Context context)
	{
		// do not reorder : dependent on resource array order

		int[] palette = context.getResources().getIntArray(R.array.palette_fn);
		int i = 0;
		feBackColor = palette[i++];
		feForeColor = palette[i++];

		feAbbrevBackColor = palette[i++];
		feAbbrevForeColor = palette[i++];

		fe2BackColor = palette[i++];
		fe2ForeColor = palette[i++];

		fenBackColor = palette[i++];
		fenForeColor = palette[i++];

		fenWithinDefBackColor = palette[i++];
		fenWithinDefForeColor = palette[i++];

		xfenBackColor = palette[i++];
		xfenForeColor = palette[i++];

		fexBackColor = palette[i++];
		fexForeColor = palette[i++];

		fexWithinDefBackColor = palette[i++];
		fexWithinDefForeColor = palette[i++];

		metaFrameDefinitionBackColor = palette[i++];
		metaFrameDefinitionForeColor = palette[i++];

		metaFeDefinitionBackColor = palette[i++];
		metaFeDefinitionForeColor = palette[i++];

		tag1BackColor = palette[i++];
		tag1ForeColor = palette[i++];

		tag2BackColor = palette[i++];
		tag2ForeColor = palette[i++];

		exBackColor = palette[i++];
		exForeColor = palette[i++];

		xBackColor = palette[i++];
		xForeColor = palette[i++];

		tBackColor = palette[i++];
		tForeColor = palette[i++];

		governorTypeBackColor = palette[i++];
		governorTypeForeColor = palette[i++];

		governorBackColor = palette[i++];
		governorForeColor = palette[i++];

		annoSetBackColor = palette[i++];
		annoSetForeColor = palette[i++];

		layerTypeBackColor = palette[i++];
		layerTypeForeColor = palette[i++];

		labelBackColor = palette[i++];
		labelForeColor = palette[i++];

		subtextBackColor = palette[i++];
		subtextForeColor = palette[i++];

		groupBackColor = palette[i++];
		groupForeColor = palette[i++];

		targetBackColor = palette[i++];
		targetForeColor = palette[i++];

		targetHighlightTextBackColor = palette[i++];
		targetHighlightTextForeColor = palette[i++];

		ptBackColor = palette[i++];
		ptForeColor = palette[i++];

		gfBackColor = palette[i++];
		gfForeColor = palette[i++];
	}
}
