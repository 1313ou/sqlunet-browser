/*
 * Copyright (c) 2016. Shintaro Katafuchi hotchemi
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.rate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import static com.bbou.rate.PreferenceHelper.setAgreeShowDialog;
import static com.bbou.rate.PreferenceHelper.setRemindInterval;

final class DialogBuilder
{
	/*
	static private boolean underHoneyComb()
	{
		return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;
	}
	*/

	static private boolean isLollipop()
	{
		return Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1;
	}

	static private int getDialogTheme()
	{
		return isLollipop() ? R.style.CustomLollipopDialogStyle : 0;
	}

	@SuppressLint("NewApi")
	static private AlertDialog.Builder getDialogBuilder(Context context)
	{
		/*
		if (underHoneyComb())
		{
			return new AlertDialog.Builder(context);
		}
		else
		{
		 */
			return new AlertDialog.Builder(context, getDialogTheme());
		/*
		}
		 */
	}

	static Dialog build(@NonNull final Context context, final DialogOptions options)
	{
		final AlertDialog.Builder builder = getDialogBuilder(context);

		// message
		builder.setMessage(options.getMessageText(context));

		// title
		if (options.shouldShowTitle())
		{
			builder.setTitle(options.getTitleText(context));
		}

		// title
		final View view = options.getView();
		if (view != null)
		{
			builder.setView(view);
		}

		// cancelable
		builder.setCancelable(options.getCancelable());

		// positive button
		builder.setPositiveButton(options.getPositiveText(context), (dialog, which) -> {

			final Intent intentToAppstore = options.getStoreType().getIntent(context);
			try
			{
				context.startActivity(intentToAppstore);
			}
			catch (ActivityNotFoundException e)
			{
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
			}
			setAgreeShowDialog(context, false);
		});

		// neutral button
		if (options.shouldShowNeutralButton())
		{
			builder.setNeutralButton(options.getNeutralText(context), (dialog, which) -> setRemindInterval(context));
		}

		// negative button
		if (options.shouldShowNegativeButton())
		{
			builder.setNegativeButton(options.getNegativeText(context), (dialog, which) -> setAgreeShowDialog(context, false));
		}

		return builder.create();
	}
}