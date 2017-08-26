package org.sqlunet.browser;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.TypedValue;

/**
 * Color utils
 *
 * @author Bernard Bou
 */
public class ColorUtils
{
	static public void tint(int color, final Drawable... drawables)
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
	static public void tint(final Drawable drawable, int color)
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

	static public int[] getColors(final Context context, int... colorRes)
	{
		int result[] = new int[colorRes.length];
		for (int i = 0; i < colorRes.length; i++)
		{
			result[i] = getColor(context, colorRes[i]);
		}
		return result;
	}

	static public int getColor(final Context context, int colorRes)
	{
		final Resources res = context.getResources();
		final Resources.Theme theme = context.getTheme();
		return getColor(res, theme, colorRes);
	}

	@SuppressWarnings("WeakerAccess")
	static public int getColor(final Resources res, final Resources.Theme theme, int colorRes)
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

	static public Drawable getDrawable(final Context context, int drawableRes)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			return context.getResources().getDrawable(drawableRes, context.getTheme());
		}
		else
		{
			//noinspection deprecation
			return context.getResources().getDrawable(drawableRes);
		}
	}

	static public int fetchColor(final Context context, int attr)
	{
		final TypedValue typedValue = new TypedValue();
		final Resources.Theme theme = context.getTheme();
		theme.resolveAttribute(attr, typedValue, true);
		return typedValue.data;
	}
}
