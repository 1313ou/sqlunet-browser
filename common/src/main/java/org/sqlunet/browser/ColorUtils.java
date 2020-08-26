/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

/**
 * Color utils
 *
 * @author Bernard Bou
 */
public class ColorUtils
{
	static public void tint(int color, @NonNull final Drawable... drawables)
	{
		for (Drawable drawable : drawables)
		{
			if (drawable != null)
			{
				tint(drawable, color);
			}
		}
	}

	@SuppressWarnings("WeakerAccess")
	static public void tint(@NonNull final Drawable drawable, int color)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			drawable.setTint(color);
			//DrawableCompat.setTint(drawable, iconTint);
		}
		else
		{
			DrawableCompat.setTint(DrawableCompat.wrap(drawable), color);
		}
	}

	@NonNull
	static public int[] getColors(@NonNull final Context context, @NonNull int... colorRes)
	{
		int[] result = new int[colorRes.length];
		for (int i = 0; i < colorRes.length; i++)
		{
			result[i] = getColor(context, colorRes[i]);
		}
		return result;
	}

	static public int getColor(@NonNull final Context context, @ColorRes int colorRes)
	{
		final Resources res = context.getResources();
		final Resources.Theme theme = context.getTheme();
		return getColor(res, theme, colorRes);
	}

	@SuppressWarnings({"deprecation", "WeakerAccess"})
	static public int getColor(@NonNull final Resources res, final Resources.Theme theme, int colorRes)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			return res.getColor(colorRes, theme);
		}
		else
		{
			//noinspection deprecation
			return res.getColor(colorRes);
		}
	}

	static public Drawable getDrawable(@NonNull final Context context, @DrawableRes int drawableRes)
	{
		return ResourcesCompat.getDrawable(context.getResources(), drawableRes, context.getTheme());
	}

	static public int fetchColor(@NonNull final Context context, int attr)
	{
		final TypedValue typedValue = new TypedValue();
		final Resources.Theme theme = context.getTheme();
		theme.resolveAttribute(attr, typedValue, true);
		return typedValue.data;
	}
}
