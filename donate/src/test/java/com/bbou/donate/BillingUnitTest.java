/*
 * Copyright 2017 Google Inc. All rights reserved.
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
package com.bbou.donate;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import com.bbou.donate.billing.Skus;

import static com.bbou.donate.billing.Skus.SKU_DONATE1;
import static com.bbou.donate.billing.Skus.SKU_DONATE2;
import static com.bbou.donate.billing.Skus.SKU_DONATE3;
import static com.bbou.donate.billing.Skus.SKU_DONATE4;
import static com.bbou.donate.billing.Skus.SKU_DONATE5;

import org.junit.Test;

/**
 * Unit tests for billing model.
 * <p>
 * Note:
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 * <p>
 * Testing Fundamentals:
 * http://d.android.com/tools/testing/testing_android.html
 * <p>
 * Local Unit tests:
 * https://d.android.com/training/testing/unit-testing/local-unit-tests.html
 */
public class BillingUnitTest
{
	@Test
	public void billingHelperData_isConsistent() throws Exception
	{
		List<String> inAppList = Arrays.asList(Skus.INAPP_SKUS);
		assertTrue(inAppList.contains(SKU_DONATE1));
		assertTrue(inAppList.contains(SKU_DONATE2));
		assertTrue(inAppList.contains(SKU_DONATE3));
		assertTrue(inAppList.contains(SKU_DONATE4));
		assertTrue(inAppList.contains(SKU_DONATE5));
		List<String> subscriptionsList = Arrays.asList(Skus.SUBSCRIPTIONS_SKUS);
		assertTrue(subscriptionsList.isEmpty());
	}
}
