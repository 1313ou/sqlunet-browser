/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.support.util;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;

/**
 * Represents an in-app billing purchase.
 */
public class Purchase
{
	final String mItemType;  // ITEM_TYPE_INAPP or ITEM_TYPE_SUBS
	private String mOrderId;
	private String mPackageName;
	private String mSku;
	private long mPurchaseTime;
	private int mPurchaseState;
	private String mDeveloperPayload;
	private String mToken;
	private final String mOriginalJson;
	private String mSignature;
	private boolean mIsAutoRenewing;

	public Purchase(String itemType, String jsonPurchaseInfo, String signature) throws JSONException
	{
		mItemType = itemType;
		mOriginalJson = jsonPurchaseInfo;
		JSONObject o = new JSONObject(mOriginalJson);
		mOrderId = o.optString("orderId");
		mPackageName = o.optString("packageName");
		mSku = o.optString("productId");
		mPurchaseTime = o.optLong("purchaseTime");
		mPurchaseState = o.optInt("purchaseState");
		mDeveloperPayload = o.optString("developerPayload");
		mToken = o.optString("token", o.optString("purchaseToken"));
		mIsAutoRenewing = o.optBoolean("autoRenewing");
		mSignature = signature;
	}

	public String getItemType()
	{
		return mItemType;
	}

	@SuppressWarnings("unused")
	public String getOrderId()
	{
		return mOrderId;
	}

	@SuppressWarnings("unused")
	public String getPackageName()
	{
		return mPackageName;
	}

	public String getSku()
	{
		return mSku;
	}

	public long getPurchaseTime()
	{
		return mPurchaseTime;
	}

	@SuppressWarnings("unused")
	public int getPurchaseState()
	{
		return mPurchaseState;
	}

	public String getDeveloperPayload()
	{
		return mDeveloperPayload;
	}

	public String getToken()
	{
		return mToken;
	}

	@SuppressWarnings("unused")
	public String getOriginalJson()
	{
		return mOriginalJson;
	}

	@SuppressWarnings("unused")
	public String getSignature()
	{
		return mSignature;
	}

	@SuppressWarnings("unused")
	public boolean isAutoRenewing()
	{
		return mIsAutoRenewing;
	}

	@NonNull
	@Override
	public String toString()
	{
		return "PurchaseInfo(type:" + mItemType + "):" + mOriginalJson;
	}
}
