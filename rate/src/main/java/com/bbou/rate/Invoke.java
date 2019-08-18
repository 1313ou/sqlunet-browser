/*
 * Copyright (c) 2016. Shintaro Katafuchi hotchemi
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.rate;

import android.app.Activity;

import androidx.annotation.NonNull;

public class Invoke
{
	// Called from activity's onCreate()
	public void invoke(@NonNull final Activity context)
	{
		AppRate.with(context).setStoreType(StoreType.GOOGLE) //default is Google, other option is Amazon
				.setInstallDays(3) // default 10, 0 means install day.
				.setLaunchTimes(10) // default 10 times.
				.setRemindInterval(2) // default 1 day.
				.setShowLaterButton(true) // default true.
				.setDebug(true) // default false.
				.setCancelable(false) // default false.
				.setTitle(R.string.rate_dialog_title) //
				.setTextLater(R.string.rate_dialog_later) //
				.setTextNever(R.string.rate_dialog_never) //
				.setTextRateNow(R.string.rate_dialog_ok) //
				.monitor();

		AppRate.showRateDialogIfMeetsConditions(context);
	}
}