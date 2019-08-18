/*
 * Copyright (c) 2016. Shintaro Katafuchi hotchemi
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.rate;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import java.util.Date;

import androidx.annotation.NonNull;

import static com.bbou.rate.BuildConfig.DEBUG;
import static com.bbou.rate.DialogBuilder.build;
import static com.bbou.rate.PreferenceHelper.getInstallDate;
import static com.bbou.rate.PreferenceHelper.getIsAgreeShowDialog;
import static com.bbou.rate.PreferenceHelper.getLaunchTimes;
import static com.bbou.rate.PreferenceHelper.getRemindInterval;
import static com.bbou.rate.PreferenceHelper.isFirstLaunch;
import static com.bbou.rate.PreferenceHelper.setInstallDate;

public final class AppRate
{
	private static AppRate singleton;

	private final Context context;

	private final DialogOptions options = new DialogOptions();

	private int installDate = 10;

	private int launchTimes = 10;

	private int remindInterval = 1;

	private boolean isDebug = false;

	private AppRate(@NonNull final Context context)
	{
		this.context = context.getApplicationContext();
	}

	public static AppRate with(@NonNull Context context)
	{
		if (singleton == null)
		{
			synchronized (AppRate.class)
			{
				if (singleton == null)
				{
					singleton = new AppRate(context);
				}
			}
		}
		return singleton;
	}

	@SuppressWarnings("UnusedReturnValue")
	public static boolean showRateDialogIfMeetsConditions(@NonNull final Activity activity)
	{
		boolean isMeetsConditions = singleton.isDebug || singleton.shouldShowRateDialog();
		if (isMeetsConditions)
		{
			singleton.showRateDialog(activity);
		}
		return isMeetsConditions;
	}

	private static boolean isOverDate(long targetDate, int threshold)
	{
		return new Date().getTime() - targetDate >= threshold * 24 * 60 * 60 * 1000;
	}

	@NonNull
	public AppRate setLaunchTimes(int launchTimes)
	{
		this.launchTimes = launchTimes;
		return this;
	}

	@NonNull
	public AppRate setInstallDays(int installDate)
	{
		this.installDate = installDate;
		return this;
	}

	@NonNull
	public AppRate setRemindInterval(int remindInterval)
	{
		this.remindInterval = remindInterval;
		return this;
	}

	@NonNull
	public AppRate setShowLaterButton(boolean isShowNeutralButton)
	{
		options.setShowNeutralButton(isShowNeutralButton);
		return this;
	}

	@NonNull
	public AppRate setShowNeverButton(boolean isShowNeverButton)
	{
		options.setShowNegativeButton(isShowNeverButton);
		return this;
	}

	@NonNull
	public AppRate setShowTitle(boolean isShowTitle)
	{
		options.setShowTitle(isShowTitle);
		return this;
	}

	@NonNull
	public AppRate clearAgreeShowDialog()
	{
		PreferenceHelper.setAgreeShowDialog(context, true);
		return this;
	}

	@NonNull
	public AppRate clearSettingsParam()
	{
		PreferenceHelper.setAgreeShowDialog(context, true);
		PreferenceHelper.clearSharedPreferences(context);
		return this;
	}

	@NonNull
	public AppRate setAgreeShowDialog(boolean clear)
	{
		PreferenceHelper.setAgreeShowDialog(context, clear);
		return this;
	}

	@NonNull
	public AppRate setView(final View view)
	{
		options.setView(view);
		return this;
	}

	@NonNull
	public AppRate setTitle(int resourceId)
	{
		options.setTitleResId(resourceId);
		return this;
	}

	@NonNull
	public AppRate setTitle(final String title)
	{
		options.setTitleText(title);
		return this;
	}

	@NonNull
	public AppRate setMessage(int resourceId)
	{
		options.setMessageResId(resourceId);
		return this;
	}

	@NonNull
	public AppRate setMessage(final String message)
	{
		options.setMessageText(message);
		return this;
	}

	@NonNull
	public AppRate setTextRateNow(int resourceId)
	{
		options.setTextPositiveResId(resourceId);
		return this;
	}

	@NonNull
	public AppRate setTextRateNow(final String positiveText)
	{
		options.setPositiveText(positiveText);
		return this;
	}

	@NonNull
	public AppRate setTextLater(int resourceId)
	{
		options.setTextNeutralResId(resourceId);
		return this;
	}

	@NonNull
	public AppRate setTextLater(final String neutralText)
	{
		options.setNeutralText(neutralText);
		return this;
	}

	@NonNull
	public AppRate setTextNever(int resourceId)
	{
		options.setTextNegativeResId(resourceId);
		return this;
	}

	@NonNull
	public AppRate setTextNever(final String negativeText)
	{
		options.setNegativeText(negativeText);
		return this;
	}

	@NonNull
	public AppRate setCancelable(boolean cancelable)
	{
		options.setCancelable(cancelable);
		return this;
	}

	@NonNull
	public AppRate setStoreType(final StoreType appstore)
	{
		options.setStoreType(appstore);
		return this;
	}

	public void monitor()
	{
		if (isFirstLaunch(context))
		{
			setInstallDate(context);
		}
		PreferenceHelper.setLaunchTimes(context, getLaunchTimes(context) + 1);
	}

	private void showRateDialog(final Activity activity)
	{
		if (!activity.isFinishing())
		{
			build(activity, options).show();
		}
	}

	private boolean shouldShowRateDialog()
	{
		return getIsAgreeShowDialog(context) && isOverLaunchTimes() && isOverInstallDate() && isOverRemindDate();
	}

	private boolean isOverLaunchTimes()
	{
		return getLaunchTimes(context) >= launchTimes;
	}

	private boolean isOverInstallDate()
	{
		return isOverDate(getInstallDate(context), installDate);
	}

	private boolean isOverRemindDate()
	{
		return isOverDate(getRemindInterval(context), remindInterval);
	}

	public boolean isDebug()
	{
		return isDebug;
	}

	@NonNull
	public AppRate setDebug(boolean isDebug)
	{
		this.isDebug = isDebug;
		return this;
	}

	// Called from activity's onCreate()
	static public void invoke(@NonNull final Activity activity)
	{
		AppRate.with(activity).setStoreType(StoreType.GOOGLE) //default is Google, other option is Amazon
				.setInstallDays(3) // default 10, 0 means install day.
				.setLaunchTimes(10) // default 10 times.
				.setRemindInterval(2) // default 1 day.
				.setShowLaterButton(true) // default true.
				.setCancelable(false) // default false.
				.setTitle(R.string.rate_dialog_title) //
				.setTextLater(R.string.rate_dialog_later) //
				.setTextNever(R.string.rate_dialog_never) //
				.setTextRateNow(R.string.rate_dialog_ok) //
				.setDebug(DEBUG) // default false.
				.monitor();

		AppRate.showRateDialogIfMeetsConditions(activity);
	}

	// Called from menu
	@SuppressWarnings("UnusedReturnValue")
	static public void rate(@NonNull final Activity activity)
	{
		AppRate.with(activity)
				.showRateDialog(activity);
	}


}
