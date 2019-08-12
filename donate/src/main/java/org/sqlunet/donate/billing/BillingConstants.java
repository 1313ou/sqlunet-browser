/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sqlunet.donate.billing;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.SkuType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Static fields and methods useful for billing
 */
public final class BillingConstants
{
	// SKUs for our products: the donate (consumable)
	static public final String SKU_DONATE1 = "donate_1"; //1
	static public final String SKU_DONATE2 = "donate_2"; //5
	static public final String SKU_DONATE3 = "donate_3"; //10
	static public final String SKU_DONATE4 = "donate_5"; //20
	static public final String SKU_DONATE5 = "donate_4"; //50

	// SKU for our subscription (infinite gas)

	private static final String[] IN_APP_SKUS = {SKU_DONATE1, SKU_DONATE2, SKU_DONATE3, SKU_DONATE4, SKU_DONATE5};
	private static final String[] SUBSCRIPTIONS_SKUS = {};

	/**
	 * Returns the list of all SKUs for the billing type specified
	 */
	public static List<String> getSkuList(@SkuType String billingType)
	{
		return SkuType.INAPP.equals(billingType) ? Arrays.asList(IN_APP_SKUS) : Arrays.asList(SUBSCRIPTIONS_SKUS);
	}
}

