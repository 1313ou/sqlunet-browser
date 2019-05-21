/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.support.util;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;

/**
 * Represents an in-app product's listing details.
 */
public class SkuDetails
{
	@SuppressWarnings({"FieldCanBeLocal", "unused"})
	private final String mItemType;
	private final String mSku;
	private final String mType;
	private final String mPrice;
	private final long mPriceAmountMicros;
	private final String mPriceCurrencyCode;
	private final String mTitle;
	private final String mDescription;
	private final String mJson;

	@SuppressWarnings("unused")
	public SkuDetails(String jsonSkuDetails) throws JSONException
	{
		this(IabHelper.ITEM_TYPE_INAPP, jsonSkuDetails);
	}

	public SkuDetails(String itemType, String jsonSkuDetails) throws JSONException
	{
		mItemType = itemType;
		mJson = jsonSkuDetails;
		JSONObject o = new JSONObject(mJson);
		mSku = o.optString("productId");
		mType = o.optString("type");
		mPrice = o.optString("price");
		mPriceAmountMicros = o.optLong("price_amount_micros");
		mPriceCurrencyCode = o.optString("price_currency_code");
		mTitle = o.optString("title");
		mDescription = o.optString("description");
	}

	public String getSku()
	{
		return mSku;
	}

	@SuppressWarnings("unused")
	public String getType()
	{
		return mType;
	}

	@SuppressWarnings("unused")
	public String getPrice()
	{
		return mPrice;
	}

	@SuppressWarnings("unused")
	public long getPriceAmountMicros()
	{
		return mPriceAmountMicros;
	}

	@SuppressWarnings("unused")
	public String getPriceCurrencyCode()
	{
		return mPriceCurrencyCode;
	}

	@SuppressWarnings("unused")
	public String getTitle()
	{
		return mTitle;
	}

	@SuppressWarnings("unused")
	public String getDescription()
	{
		return mDescription;
	}

	@NonNull
	@Override
	public String toString()
	{
		return "SkuDetails:" + mJson;
	}
}
