/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.style;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.Log;

import org.sqlunet.xnet.R;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.annotation.StyleableRes;
import androidx.core.content.res.ResourcesCompat;

/**
 * Color values
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Colors
{
	static private final String DUMP = "Contrast";

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

	static public int relationBackColor = Color.TRANSPARENT;
	static public int relationForeColor = Color.TRANSPARENT;

	static public int dataBackColor = Color.TRANSPARENT;
	static public int dataForeColor = Color.TRANSPARENT;

	static public int wordBackColor = Color.TRANSPARENT;
	static public int wordForeColor = Color.TRANSPARENT;

	static public int casedBackColor = Color.TRANSPARENT;
	static public int casedForeColor = Color.TRANSPARENT;

	static public int pronunciationBackColor = Color.TRANSPARENT;
	static public int pronunciationForeColor = Color.TRANSPARENT;

	static public int posBackColor = Color.TRANSPARENT;
	static public int posForeColor = Color.TRANSPARENT;

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

		relationBackColor = palette[i++];
		relationForeColor = palette[i++];

		dataBackColor = palette[i++];
		dataForeColor = palette[i++];

		wordBackColor = palette[i++];
		wordForeColor = palette[i++];

		casedBackColor = palette[i++];
		casedForeColor = palette[i++];

		pronunciationBackColor = palette[i++];
		pronunciationForeColor = palette[i++];

		posBackColor = palette[i++];
		posForeColor = palette[i++];

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

	static public final int NOT_DEFINED = 0xAAAAAAAA;

	@NonNull
	static public int[] getColorAttrs(@NonNull final Context context, @StyleRes int themeId, @NonNull @StyleableRes int... resIds)
	{
		TypedArray a = context.getTheme().obtainStyledAttributes(themeId, resIds);

		int[] result = new int[resIds.length];
		for (int i = 0; i < resIds.length; i++)
		{
			result[i] = a.getColor(i, NOT_DEFINED);
		}

		a.recycle();
		return result;
	}

	static public void dumpColorAttrs(@NonNull final Context context, @StyleRes int themeId, @StyleableRes int... resIds)
	{
		TypedArray a = context.getTheme().obtainStyledAttributes(themeId, resIds);
		for (int i = 0; i < a.length(); i++)
		{
			String name = context.getResources().getResourceName(resIds[i]);
			int value = a.getColor(i, NOT_DEFINED);
			Log.i(DUMP, String.format("Attr %s = %s", name, colorToString(value)));
		}
		a.recycle();
	}

	@NonNull
	static public String colorToString(final int color)
	{
		switch (color)
		{
			case 0:
				return "transparent";
			case 0xFF000000:
				return "black";
			case 0xFFffffff:
				return "white";
			case 0xFF808080:
				return "gray";
			default:
				return '#' + Integer.toHexString(color);
		}
	}
}
