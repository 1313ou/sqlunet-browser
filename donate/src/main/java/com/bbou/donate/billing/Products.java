/*
 * Copyright (c) 2022. Bernard Bou
 */
package com.bbou.donate.billing;

import android.content.Context;
import android.content.res.Resources;

import com.bbou.donate.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Static fields and methods useful for billing
 */
public final class Products
{
	// SKUs for products: the donate (consumable)
	@Nullable
	private static String[] inAppSkus = null;

	// Ids for products: the donate (consumable)
	@Nullable
	public static String[] getInappProducts()
	{
		return inAppSkus;
	}

	static public void init(@NonNull final Context context)
	{
		final Resources res = context.getResources();
		inAppSkus = res.getStringArray(R.array.skus);
	}
}
