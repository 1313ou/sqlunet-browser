/*
 * Copyright (c) 2023. Bernard Bou
 */
package com.bbou.rate;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public enum StoreType
{
	GOOGLE(context -> {
		// if GP not present on device, open web browser
		Intent intent = getGooglePlayIntent(context);
		if (intent != null)
		{
			return intent;
		}
		final String packageName = context.getPackageName();
		intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
		return intent;

	}), //
	AMAZON(context -> {
		final String packageName = context.getPackageName();
		return new Intent(Intent.ACTION_VIEW, getUri("amzn://apps/android?p=", packageName));
	});

	@FunctionalInterface
	interface IntentBuilder
	{
		@NonNull
		Intent build(@NonNull final Context context);
	}

	private final IntentBuilder builder;

	StoreType(final IntentBuilder builder)
	{
		this.builder = builder;
	}

	@NonNull
	public Intent getIntent(@NonNull final Context context)
	{
		return builder.build(context);
	}

	@Nullable
	private static Uri getUri(@SuppressWarnings("SameParameterValue") @NonNull final String uriPrefix, @Nullable final String packageName)
	{
		return packageName == null ? null : Uri.parse(uriPrefix + packageName);
	}

	// S P E C I F I C S

	@Nullable
	private static Intent getGooglePlayIntent(@NonNull final Context context)
	{
		final String packageName = context.getPackageName();
		final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));

		// find all applications able to handle our rateIntent
		final List<ResolveInfo> handlingApps = context.getPackageManager().queryIntentActivities(intent, 0);
		for (ResolveInfo app : handlingApps)
		{
			// look for Google Play application
			if (app.activityInfo.applicationInfo.packageName.equals("com.android.vending"))
			{
				final ActivityInfo activity = app.activityInfo;
				final ComponentName componentName = new ComponentName(activity.applicationInfo.packageName, activity.name);
				intent.setComponent(componentName);
				return intent;
			}
		}
		return null;
	}
}