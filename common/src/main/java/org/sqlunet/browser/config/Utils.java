/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.config;

import android.content.Context;

import org.sqlunet.browser.common.R;

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
}
