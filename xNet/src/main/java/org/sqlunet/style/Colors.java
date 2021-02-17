/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.style;

import android.content.Context;
import android.graphics.Color;

import org.sqlunet.xnet.R;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

/**
 * Color values
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Colors
{
	// COMMON

	static public int classBackColor = Color.TRANSPARENT;
	static public int classForeColor = Color.TRANSPARENT;

	static public int roleBackColor = Color.TRANSPARENT;
	static public int roleForeColor = Color.TRANSPARENT;

	static public int memberBackColor = Color.TRANSPARENT;
	static public int memberForeColor = Color.TRANSPARENT;

	static public int definitionBackColor = Color.TRANSPARENT;
	static public int definitionForeColor = Color.TRANSPARENT;

	static public int exampleBackColor = Color.TRANSPARENT;
	static public int exampleForeColor = Color.TRANSPARENT;

	static public int dataBackColor = Color.TRANSPARENT;
	static public int dataForeColor = Color.TRANSPARENT;

	// TEXT
	static public int textBackColor = Color.TRANSPARENT;
	static public int textForeColor = Color.TRANSPARENT;

	static public int textNormalBackColor = Color.TRANSPARENT;
	static public int textNormalForeColor = Color.TRANSPARENT;

	static public int textHighlightBackColor = Color.TRANSPARENT;
	static public int textHighlightForeColor = Color.TRANSPARENT;

	static public int textDimmedBackColor = Color.TRANSPARENT;
	static public int textDimmedForeColor = Color.TRANSPARENT;

	static public int textMatchBackColor = Color.TRANSPARENT;
	static public int textMatchForeColor = Color.TRANSPARENT;

	// SQL
	static public int sqlKeywordBackColor = Color.TRANSPARENT;
	static public int sqlKeywordForeColor = Color.TRANSPARENT;

	static public int sqlSlashBackColor = Color.TRANSPARENT;
	static public int sqlSlashForeColor = Color.TRANSPARENT;

	static public int sqlQuestionMarkBackColor = Color.TRANSPARENT;
	static public int sqlQuestionMarkForeColor = Color.TRANSPARENT;

	// REPORT

	static public int storageTypeBackColor = Color.TRANSPARENT;
	static public int storageTypeForeColor = Color.TRANSPARENT;

	static public int storageValueBackColor = Color.TRANSPARENT;
	static public int storageValueForeColor = Color.TRANSPARENT;

	static public int dirTypeBackColor = Color.TRANSPARENT;
	static public int dirTypeForeColor = Color.TRANSPARENT;

	static public int dirValueBackColor = Color.TRANSPARENT;
	static public int dirValueForeColor = Color.TRANSPARENT;

	static public int dirOkBackColor = Color.TRANSPARENT;
	static public int dirOkForeColor = Color.TRANSPARENT;

	static public int dirFailBackColor = Color.TRANSPARENT;
	static public int dirFailForeColor = Color.TRANSPARENT;

	static public void setColorsFromResources(@NonNull final Context context)
	{
		// do not reorder : dependent on resource array order

		int[] palette = context.getResources().getIntArray(R.array.palette);
		int i = 0;

		// COMMON
		classBackColor = palette[i++];
		classForeColor = palette[i++];

		roleBackColor = palette[i++];
		roleForeColor = palette[i++];

		memberBackColor = palette[i++];
		memberForeColor = palette[i++];

		definitionBackColor = palette[i++];
		definitionForeColor = palette[i++];

		exampleBackColor = palette[i++];
		exampleForeColor = palette[i++];

		dataBackColor = palette[i++];
		dataForeColor = palette[i++];

		// TEXT
		textBackColor = palette[i++];
		textForeColor = palette[i++];

		textNormalBackColor = palette[i++];
		textNormalForeColor = palette[i++];

		textHighlightBackColor = palette[i++];
		textHighlightForeColor = palette[i++];

		textDimmedBackColor = palette[i++];
		textDimmedForeColor = palette[i++];

		textMatchBackColor = palette[i++];
		textMatchForeColor = palette[i++];

		// SQL
		sqlKeywordBackColor = palette[i++];
		sqlKeywordForeColor = palette[i++];

		sqlSlashBackColor = palette[i++];
		sqlSlashForeColor = palette[i++];

		sqlQuestionMarkBackColor = palette[i++];
		sqlQuestionMarkForeColor = palette[i++];

		// REPORT
		storageTypeBackColor = palette[i++];
		storageTypeForeColor = palette[i++];

		storageValueBackColor = palette[i++];
		storageValueForeColor = palette[i++];

		dirTypeBackColor = palette[i++];
		dirTypeForeColor = palette[i++];

		dirValueBackColor = palette[i++];
		dirValueForeColor = palette[i++];

		dirOkBackColor = palette[i++];
		dirOkForeColor = palette[i++];

		dirFailBackColor = palette[i++];
		//noinspection UnusedAssignment
		dirFailForeColor = palette[i++];
	}

	@NonNull
	public static int[] getColors(@NonNull final Context context, @NonNull @ColorRes final int... colorIds)
	{
		int[] result = new int[colorIds.length];
		for (int i = 0; i < colorIds.length; i++)
		{
			result[i] = ResourcesCompat.getColor(context.getResources(), colorIds[i], null);
		}
		return result;
	}
}
