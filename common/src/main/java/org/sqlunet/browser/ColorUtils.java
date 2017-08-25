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

	static public int getColor(final Context context, int colorRes)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			return context.getResources().getColor(colorRes, context.getTheme());
		}
		else
		{
			//noinspection deprecation
			return context.getResources().getColor(colorRes);
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
