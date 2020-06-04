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
package com.bbou.donate.billing;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingClient.FeatureType;
import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.Purchase.PurchasesResult;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.bbou.donate.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Handles all the interactions with Play Store (via Billing library), maintains connection to it through BillingClient and caches temporary states/data if needed
 */
public class BillingManager implements PurchasesUpdatedListener
{
	private static final String TAG = "BillingManager";

	/**
	 * A reference to BillingClient
	 **/
	@Nullable
	private BillingClient client;

	/**
	 * Listener to fire updates to
	 */
	private final BillingUpdatesListener updatesListener;

	/**
	 * Activity
	 */
	private final Activity activity;

	/**
	 * List of verifiedPurchases
	 */
	private final List<Purchase> verifiedPurchases = new ArrayList<>();

	/**
	 * List of token to be consumed
	 */
	private Set<String> tokensToBeConsumed;

	// status

	/**
	 * True if billing service is connected now.
	 */
	private boolean isServiceConnected;

	/**
	 * Service connection response code
	 */
	@BillingResponseCode
	private int billingClientResponseCode = BillingResponseCode.SERVICE_DISCONNECTED;

	/* base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY
	 * (that you got from the Google Play developer console). This is not your
	 * developer public key, it's the *app-specific* public key.
	 *
	 * Instead of just storing the entire literal string here embedded in the
	 * program,  construct the key at runtime from pieces or
	 * use bit manipulation (for example, XOR with some other string) to hide
	 * the actual key.  The key itself is not secret information, but we don't
	 * want to make it easy for an attacker to replace the public key with one
	 * of their own and then fake messages from the server.
	 */
	@NonNull
	private final String base64EncodedPublicKey;

	/**
	 * Listener to the updates that happen when verifiedPurchases list was updated or consumption of the item was finished
	 */
	public interface BillingUpdatesListener
	{
		void onBillingClientSetupFinished();

		void onConsumeFinished(String token, @BillingResponseCode int response);

		void onPurchasesUpdated(@Nullable List<Purchase> purchases);
	}

	// C O N S T R U C T O R / D E S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param activity        activity
	 * @param updatesListener updates listener
	 */
	public BillingManager(final Activity activity, final BillingUpdatesListener updatesListener)
	{
		Log.d(TAG, "Creating billing client.");
		this.activity = activity;
		this.updatesListener = updatesListener;
		this.base64EncodedPublicKey = this.activity.getString(R.string.license_key);
		this.client = BillingClient.newBuilder(this.activity) //
				.enablePendingPurchases() //
				.setListener(this) //
				.build();

		// Start setup.
		// This is asynchronous and the specified listener will be called once setup completes.
		// It also starts to report all the new verifiedPurchases through onPurchasesUpdated() callback.
		Log.d(TAG, "Starting setup.");
		startServiceConnection(() -> {

			// Notifying the listener that billing client is ready
			BillingManager.this.updatesListener.onBillingClientSetupFinished();

			// IAB is fully set up. Now, let's get an inventory of stuff we own.
			Log.d(TAG, "Querying inventory.");
			queryPurchases();
		});
	}

	/**
	 * Start service connection
	 *
	 * @param executeOnSuccess runnable to be executed on success
	 */
	private void startServiceConnection(@Nullable final Runnable executeOnSuccess)
	{
		// guard against destroyed client
		if (this.client == null)
		{
			return;
		}
		Log.d(TAG, "Setting up client.");
		this.client.startConnection(new BillingClientStateListener()
		{
			@Override
			public void onBillingSetupFinished(@NonNull final BillingResult billingResult)
			{
				BillingManager.this.billingClientResponseCode = billingResult.getResponseCode();
				if (BillingResponseCode.OK == BillingManager.this.billingClientResponseCode)
				{
					Log.d(TAG, "Setup succeeded.");

					// Flag success
					BillingManager.this.isServiceConnected = true;

					//
					if (executeOnSuccess != null)
					{
						executeOnSuccess.run();
					}
				}
				else
				{
					Log.e(TAG, "Setup failed. Response: " + billingResult.getResponseCode());
				}
			}

			@Override
			public void onBillingServiceDisconnected()
			{
				BillingManager.this.isServiceConnected = false;
			}
		});
	}

	/**
	 * Clear the resources
	 */
	public void destroy()
	{
		Log.d(TAG, "Destroying the billing client.");
		if (this.client != null && this.client.isReady())
		{
			this.client.endConnection();
			this.client = null;
		}
	}

	// A C C E S S

	/**
	 * Get context
	 *
	 * @return context
	 */
	public Context getContext()
	{
		return this.activity;
	}

	/**
	 * Returns the value Billing client response code or BillingResponseCode.SERVICE_DISCONNECTED if the client connection response was not received yet.
	 *
	 * @return billing client response code
	 */
	public int getBillingClientResponseCode()
	{
		return billingClientResponseCode;
	}

	// I N I T I A T E   P U R C H A S E   F L O W

	/**
	 * Start a purchase flow
	 *
	 * @param sku         sku id
	 * @param billingType billing type
	 */
	public void initiatePurchaseFlow(final String sku, @SkuType final String billingType)
	{
		// guard against destroyed client
		if (this.client == null)
		{
			return;
		}
		Log.d(TAG, "Getting skuDetails for " + sku);

		// Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
		final List<String> skuList = new ArrayList<>();
		skuList.add(sku);
		final SkuDetailsParams.Builder builder = SkuDetailsParams.newBuilder() //
				.setSkusList(skuList) //
				.setType(billingType);
		this.client.querySkuDetailsAsync(builder.build(), (billingResult, skuDetailsList) -> {

			int response = billingResult.getResponseCode();
			if (BillingResponseCode.OK != response)
			{
				Log.e(TAG, "Getting skuDetails failed. " + response);
				return;
			}
			if (skuDetailsList == null)
			{
				Log.e(TAG, "Getting skuDetails failed. Null list.");
				return;
			}
			if (skuDetailsList.isEmpty())
			{
				Log.e(TAG, "Getting skuDetails yielded no details for sku " + sku);
				return;
			}
			final SkuDetails skuDetails = skuDetailsList.get(0);
			initiatePurchaseFlow(skuDetails);
		});
	}

	/**
	 * Start a purchase or subscription replace flow
	 *
	 * @param skuDetails sku details
	 */
	private void initiatePurchaseFlow(final SkuDetails skuDetails)
	{
		executeServiceRequest(() -> {

			// guard against destroyed client
			if (this.client == null)
			{
				return;
			}
			Log.d(TAG, "Launching inapp purchase flow.");
			final BillingFlowParams flowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetails) //
					.build();
			/* final BillingResult billingResult = */
			this.client.launchBillingFlow(this.activity, flowParams);
		});
	}

	// P U R C H A S E S   U P D A T E   L I S T E N E R

	/**
	 * Handle a callback that verifiedPurchases were updated from the Billing library
	 */
	@SuppressWarnings("WeakerAccess")
	@Override
	public void onPurchasesUpdated(@NonNull final BillingResult billingResult, @Nullable final List<Purchase> purchases)
	{
		int responseCode = billingResult.getResponseCode();
		if (BillingResponseCode.OK == responseCode)
		{
			if (purchases != null)
			{
				for (Purchase purchase : purchases)
				{
					handlePurchase(purchase);
				}
			}

			// fire update
			this.updatesListener.onPurchasesUpdated(this.verifiedPurchases);
		}
		else if (responseCode == BillingResponseCode.USER_CANCELED)
		{
			Log.i(TAG, "onPurchasesUpdated() user cancelled the purchase flow.");
		}
		else if (responseCode == BillingResponseCode.ITEM_ALREADY_OWNED)
		{
			Log.i(TAG, "onPurchasesUpdated() item already owned.");
		}
		else
		{
			Log.w(TAG, "onPurchasesUpdated() got unexpected response: " + responseCode);
		}
	}

	/**
	 * Handles the purchase
	 * <p>Note: Notice that for each purchase, we check if signature is valid on the client.
	 * It's recommended to move this check into your backend.
	 * See {@link Security#verifyPurchase(String, String, String)}
	 * </p>
	 *
	 * @param purchase Purchase to be handled
	 */
	private void handlePurchase(@NonNull final Purchase purchase)
	{
		if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature()))
		{
			Log.e(TAG, "Got a purchase: " + purchase + " but signature is bad.");
			return;
		}

		Log.d(TAG, "Got a verified purchase: " + purchase);
		this.verifiedPurchases.add(purchase);

		// Acknowledge the purchase if it hasn't already been acknowledged.
		acknowledgePurchase(purchase);
	}

	/**
	 * Acknowledge purchase
	 *
	 * @param purchase purchase
	 */
	@SuppressWarnings("WeakerAccess")
	public void acknowledgePurchase(@NonNull final Purchase purchase)
	{
		// guard against destroyed client
		if (this.client == null)
		{
			return;
		}

		// Acknowledge the purchase if it hasn't already been acknowledged.
		if (!purchase.isAcknowledged())
		{
			final AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder() //
					.setPurchaseToken(purchase.getPurchaseToken()) //
					.build();
			this.client.acknowledgePurchase(acknowledgePurchaseParams, (billingResult) -> Log.i(TAG, "Acknowledged purchase: " + purchase + " response: " + billingResult.getResponseCode()));
		}
	}

	// I N V E N T O R Y

	/**
	 * Query verifiedPurchases across various use cases and deliver the result in a formalized way through a listener
	 */
	public void queryPurchases()
	{
		executeServiceRequest(() -> {

			// guard against destroyed client
			if (this.client == null)
			{
				return;
			}

			long time = System.currentTimeMillis();
			final PurchasesResult inappResult = this.client.queryPurchases(SkuType.INAPP);
			Log.d(TAG, "Querying inapp purchases elapsed: " + (System.currentTimeMillis() - time) + "ms");
			Log.i(TAG, "Querying inapp purchases response: " + inappResult.getResponseCode() + " count: " + inappResult.getPurchasesList().size());

			// If there are subscriptions supported, we add subscription rows as well
			if (areSubscriptionsSupported())
			{
				time = System.currentTimeMillis();
				final PurchasesResult subscriptionResult = this.client.queryPurchases(SkuType.SUBS);
				Log.d(TAG, "Querying subscription purchases elapsed: " + (System.currentTimeMillis() - time) + "ms");
				Log.i(TAG, "Querying subscription purchases response: " + subscriptionResult.getResponseCode() + " count: " + subscriptionResult.getPurchasesList().size());

				if (BillingResponseCode.OK == subscriptionResult.getResponseCode())
				{
					inappResult.getPurchasesList().addAll(subscriptionResult.getPurchasesList());
				}
				else
				{
					Log.e(TAG, "Got an error response trying to query subscription purchases. " + subscriptionResult.getResponseCode());
				}
			}
			else if (BillingResponseCode.OK == inappResult.getResponseCode())
			{
				Log.d(TAG, "Skipped subscription purchases query since they are not supported.");
			}
			else
			{
				Log.e(TAG, "Got an error response trying to query trying to query inapp purchases: " + inappResult.getResponseCode());
			}

			onQueryPurchasesFinished(inappResult);
		});
	}

	/**
	 * Handle a result from querying of verifiedPurchases and report an updated list to the listener
	 *
	 * @param result purchase result
	 */
	private void onQueryPurchasesFinished(@NonNull final PurchasesResult result)
	{
		// Have we been disposed of in the meantime? If so, or bad response code, then quit
		if (this.client == null || BillingResponseCode.OK != result.getResponseCode())
		{
			Log.w(TAG, "Query inventory failed. Billing client was null or response: " + result.getResponseCode());
			return;
		}

		Log.d(TAG, "Query inventory was successful.");

		// Update the UI and verifiedPurchases inventory with new list of verifiedPurchases
		this.verifiedPurchases.clear();

		// fire
		onPurchasesUpdated(BillingResult.newBuilder().setResponseCode(BillingResponseCode.OK).build(), result.getPurchasesList());
	}

	// D E T A I L S

	/**
	 * Query details async op
	 *
	 * @param itemType sku type
	 * @param skuList  sku list
	 * @param listener response listener
	 */
	public void querySkuDetails(@SkuType final String itemType, @NonNull final List<String> skuList, @NonNull final SkuDetailsResponseListener listener)
	{
		// Creating a runnable from the request to use it inside our connection retry policy below
		executeServiceRequest(() -> {

			// guard against destroyed client
			if (this.client == null)
			{
				return;
			}

			// Query the purchase async
			SkuDetailsParams.Builder builder = SkuDetailsParams.newBuilder() //
					.setSkusList(skuList) //
					.setType(itemType);
			this.client.querySkuDetailsAsync(builder.build(), listener);
		});
	}

	// C O N S U M E

	/**
	 * Consume purchase
	 *
	 * @param purchase purchase
	 */
	public void consume(@NonNull final Purchase purchase)
	{
		final String purchaseToken = purchase.getPurchaseToken();

		// If we've already scheduled to consume this token - no action is needed
		// (this could happen if you received the token when querying verifiedPurchases inside onReceive() and later from onActivityResult()
		if (tokensToBeConsumed == null)
		{
			this.tokensToBeConsumed = new HashSet<>();
		}
		else if (this.tokensToBeConsumed.contains(purchaseToken))
		{
			Log.i(TAG, "Token was already scheduled to be consumed");
			return;
		}
		this.tokensToBeConsumed.add(purchaseToken);

		// Creating a runnable from the request to use it inside our connection retry policy below
		executeServiceRequest(() -> {

			// guard against destroyed client
			if (this.client == null)
			{
				return;
			}

			// Consume the purchase async
			final ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchaseToken).build();
			this.client.consumeAsync(consumeParams, (billingResult, consumedPurchaseToken) -> {

				// If billing service was disconnected, we try to reconnect 1 time (feel free to introduce your retry policy here).
				this.updatesListener.onConsumeFinished(consumedPurchaseToken, billingResult.getResponseCode());
			});
		});
	}

	// S E R V I C E   R E Q U E S T

	/**
	 * Execute service request
	 */
	private void executeServiceRequest(@NonNull final Runnable runnable)
	{
		if (this.isServiceConnected)
		{
			runnable.run();
		}
		else
		{
			// If billing service was disconnected, we try to reconnect 1 time.
			// (feel free to introduce your retry policy here).
			startServiceConnection(runnable);
		}
	}

	// S U P P O R T

	/**
	 * Checks if subscriptions are supported for current client
	 * <p>Note: This method does not automatically retry for RESULT_SERVICE_DISCONNECTED.
	 * It is only used in unit tests and after queryPurchases execution, which already has a retry-mechanism implemented.
	 * </p>
	 */
	private boolean areSubscriptionsSupported()
	{
		// guard against destroyed client
		if (this.client == null)
		{
			return false;
		}
		final BillingResult billingResult = this.client.isFeatureSupported(FeatureType.SUBSCRIPTIONS);
		int responseCode = billingResult.getResponseCode();
		if (BillingResponseCode.OK != responseCode)
		{
			Log.w(TAG, "areSubscriptionsSupported() got response: " + responseCode);
		}
		return BillingResponseCode.OK == responseCode;
	}

	// V A L I D I T Y

	/**
	 * Verifies that the purchase was signed correctly for this developer's public key.
	 * <p>Note: It's strongly recommended to perform such check on your backend since hackers can
	 * replace this method with "constant true" if they decompile/rebuild your app.
	 * </p>
	 */
	private boolean verifyValidSignature(@NonNull String signedData, String signature)
	{
		// Some sanity checks to see if the developer (that's you!) really followed the instructions to run this sample (don't put these checks on your app!)
		try
		{
			return Security.verifyPurchase(base64EncodedPublicKey, signedData, signature);
		}
		catch (IOException e)
		{
			Log.e(TAG, "Got an exception trying to validate a purchase: " + e);
			return false;
		}
	}
}

