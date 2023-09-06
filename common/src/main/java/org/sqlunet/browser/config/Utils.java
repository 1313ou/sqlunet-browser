/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser.config;

import android.annotation.SuppressLint;
import android.content.Context;

import org.sqlunet.browser.common.R;

import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

/**
 * Utils
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("WeakerAccess")
public class Utils
{
	/**
	 * Confirm
	 *
	 * @param context  context
	 * @param titleId  title resource id
	 * @param askId    ask resource id
	 * @param runnable run if confirmed
	 */
	static public void confirm(@NonNull final Context context, @StringRes final int titleId, @StringRes final int askId, @NonNull final Runnable runnable)
	{
		new AlertDialog.Builder(context) //
				.setIcon(android.R.drawable.ic_dialog_alert) //
				.setTitle(titleId) //
				.setMessage(askId) //
				.setPositiveButton(R.string.yes, (dialog, which) -> runnable.run()).setNegativeButton(R.string.no, null).show();
	}

	// Human-readable sizes

	static private final String f = "%d %s";

	static private final String[] units = {"B", "KB", "MB", "GB"};

	@SuppressLint("DefaultLocale")
	static private String hrSize(long x0)
	{
		float x = x0;
		for (String unit : units)
		{
			if (x > -1024.0 && x < 1024.0)
			{
				return String.format(f, Math.round(x), unit);
			}
			x /= 1024.0;
		}
		return String.format(f, Math.round(x), "TB");
	}

	static public String hrSize(@IntegerRes int id, final Context context)
	{
		long x = context.getResources().getInteger(id);
		return hrSize(x);
	}
}
