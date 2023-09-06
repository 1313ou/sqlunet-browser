/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import junit.framework.TestCase;

import org.sqlunet.browser.test.R;
import org.sqlunet.nightmode.NightMode;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleableRes;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.graphics.ColorUtils;
import androidx.test.platform.app.InstrumentationRegistry;

public class ColorsTest extends TestCase
{
	static private final String TAG = "Colors";

	static class IllegalColorPair extends Exception
	{
		final String res;
		final int resId;
		final int index;
		final int backColor;
		final int foreColor;
		final int defaultBackColor;
		final int defaultForeColor;
		final double contrast;

		public IllegalColorPair(final String res, final int resId, final int index, final int backColor, final int foreColor, final int defaultBackColor, final int defaultForeColor, double contrast)
		{
			super();
			this.res = res;
			this.resId = resId;
			this.index = index;
			this.backColor = backColor;
			this.foreColor = foreColor;
			this.defaultBackColor = defaultBackColor;
			this.defaultForeColor = defaultForeColor;
			this.contrast = contrast;
		}

		@Nullable
		@Override
		public String getMessage()
		{
			return String.format("%d %s [%02d] %s on %s (default %s on %s) with contrast %f ", resId, res, index, org.sqlunet.style.Colors.colorToString(foreColor), org.sqlunet.style.Colors.colorToString(backColor), org.sqlunet.style.Colors.colorToString(defaultForeColor), org.sqlunet.style.Colors.colorToString(defaultBackColor), contrast);
		}
	}

	static private final float MIN_CONTRAST = 3.0F;

	@NonNull
	static public Context getContext(int mode)
	{
		Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
		Context themedContext = new ContextThemeWrapper(targetContext, R.style.MyTheme);
		Configuration newConfig = themedContext.getResources().getConfiguration();
		newConfig.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK; // clear
		newConfig.uiMode |= NightMode.toConfigurationUiMode(mode) & Configuration.UI_MODE_NIGHT_MASK; // set
		Context newContext = themedContext.createConfigurationContext(newConfig);
		return new ContextThemeWrapper(newContext, R.style.MyTheme);
	}

	static void testColorsFromResources(@NonNull final Context context, @ArrayRes int paletteId, final boolean throwError) throws IllegalColorPair
	{
		String res = context.getResources().getResourceName(paletteId);
		Log.i(TAG, "Palette " + context.getResources().getResourceEntryName(paletteId) + " " + res);

		int[] defaultColors = getDefaultColorAttrs(context);
		// Log.d(LOGTAG, String.format("Effective default colors #%x on #%x", defaultColors[1], defaultColors[0]));

		int[] palette = context.getResources().getIntArray(paletteId);
		for (int i = 0; i < palette.length; i += 2)
		{
			int backColor0 = palette[i];
			int backColor = backColor0;
			if (backColor == 0)
			{
				backColor = defaultColors[0];
			}

			if (i + 1 < palette.length)
			{
				int foreColor0 = palette[i + 1];
				int foreColor = foreColor0;
				if (foreColor == 0)
				{
					foreColor = defaultColors[1];
				}
				double contrast = ColorUtils.calculateContrast(foreColor, backColor);
				final String info = String.format("[%02d] Contrast %s on %s is %f", i / 2, org.sqlunet.style.Colors.colorToString(foreColor), org.sqlunet.style.Colors.colorToString(backColor), contrast);
				if (contrast < MIN_CONTRAST)
				{
					Log.e(TAG, info);
					if (throwError)
					{
						throw new IllegalColorPair(res, paletteId, i / 2, backColor0, foreColor0, defaultColors[0], defaultColors[1], contrast);
					}
				}
				else
				{
					Log.d(TAG, info);
				}
			}
		}
	}

	static void dumpDefaultColors(@NonNull final Context context)
	{
		int[] resIds = new int[]{ //
				android.R.attr.colorForeground, //
				android.R.attr.colorBackground, //
				android.R.attr.windowBackground, //
				android.R.attr.foreground, //
				android.R.attr.textColor, //
				android.R.attr.color, //

				R.attr.color, //
				R.attr.colorOnBackground, //
				R.attr.colorSurface, //
				R.attr.colorOnSurface, //
				R.attr.backgroundColor, //
		};
		org.sqlunet.style.Colors.dumpColorAttrs(context, R.style.MyTheme, resIds);
	}

	@NonNull
	static int[] getDefaultColorAttrs(@NonNull final Context context)
	{
		@StyleableRes int[] resIds = new int[]{ //
				android.R.attr.colorBackground, //
				android.R.attr.colorForeground, //
		};
		return org.sqlunet.style.Colors.getColorAttrs(context, R.style.MyTheme, resIds);
	}
}