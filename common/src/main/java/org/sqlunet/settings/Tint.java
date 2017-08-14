package org.sqlunet.settings;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.TypedValue;
import android.view.MenuItem;

import org.sqlunet.browser.common.R;

/**
 * Tint
 *
 * @author Bernard Bou
 */
public class Tint
{
	static private int fetchColor(final Context context, int attr)
	{
		final TypedValue typedValue = new TypedValue();
		final Resources.Theme theme = context.getTheme();
		theme.resolveAttribute(attr, typedValue, true);
		return typedValue.data;
	}

	static public void tint(final Context context, final MenuItem... menuItems)
	{
		int iconTint = fetchColor(context, R.attr.actionbar_icon_color); //
		// iconTint = Color.WHITE;
		for (MenuItem menuItem : menuItems)
		{
			final Drawable drawable = menuItem.getIcon();
			if (drawable != null)
			{
				tint(drawable, iconTint);
			}
		}
	}

	static public void tint(final Drawable drawable, int iconTint)
	{
		// final int iconTint = fetchColor(R.attr.treebolic_actionbar_icon_color);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			drawable.setTint(iconTint);
			//DrawableCompat.setTint(drawable, iconTint);
		}
		else
		{
			DrawableCompat.setTint(DrawableCompat.wrap(drawable), iconTint);
		}
	}
}
