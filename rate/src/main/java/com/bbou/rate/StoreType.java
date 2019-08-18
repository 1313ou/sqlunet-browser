/*
 * Copyright (c) 2016. Shintaro Katafuchi hotchemi
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.rate;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public enum StoreType
{
	GOOGLE(context -> {
		final String packageName = context.getPackageName();
		final Intent intent = new Intent(Intent.ACTION_VIEW, getUri("https://play.google.com/store/apps/details?id=", packageName));
		setGooglePlayPackage(intent, context);
		return intent;
	}), //
	AMAZON(context -> {
		final String packageName = context.getPackageName();
		return new Intent(Intent.ACTION_VIEW, getUri("amzn://apps/android?p=", packageName));
	});

	@FunctionalInterface
	interface IntentBuilder
	{
		Intent build(@NonNull final Context context);
	}

	private final IntentBuilder builder;

	StoreType(final IntentBuilder builder)
	{
		this.builder = builder;
	}

	public Intent getIntent(@NonNull final Context context)
	{
		return builder.build(context);
	}

	private static Uri getUri(@NonNull final String uriPrefix, @Nullable final String packageName)
	{
		return packageName == null ? null : Uri.parse(uriPrefix + packageName);
	}

	// S P E C I F I C S

	static private final String GOOGLE_PLAY_PACKAGE_NAME = "com.android.vending";

	static private void setGooglePlayPackage(@NonNull final Intent intent, @NonNull final Context context)
	{
		if (isPackageExists(context, GOOGLE_PLAY_PACKAGE_NAME))
		{
			intent.setPackage(GOOGLE_PLAY_PACKAGE_NAME);
		}
	}

	static private boolean isPackageExists(@NonNull final Context context, @SuppressWarnings("SameParameterValue") @NonNull final String targetPackage)
	{
		final PackageManager pm = context.getPackageManager();
		final List<ApplicationInfo> packages = pm.getInstalledApplications(0);
		for (ApplicationInfo packageInfo : packages)
		{
			if (packageInfo.packageName.equals(targetPackage))
			{
				return true;
			}
		}
		return false;
	}
}