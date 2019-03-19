/* Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sqlunet.support;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.sqlunet.support.util.IabBroadcastReceiver;
import org.sqlunet.support.util.IabHelper;
import org.sqlunet.support.util.Purchase;

/**
 * Uses in-app billing version 3. The general-purpose boilerplate that can be reused in any app is provided in the classes in the util/ subdirectory. When
 * implementing your own application, you can copy over util/*.java to make use of those utility classes.
 *
 * @author Bruno Oliveira (Google)
 * @author Bernard Bou
 */
public class IABAdapter implements IabBroadcastReceiver.IabBroadcastListener
{
	static private final String TAG = "SqlUNetIAB";

	/**
	 * SKUs for our products
	 */
	// static private final String SKU_TEST = "android.test.purchased";
	static public final String SKU_DONATE1 = "donate_1"; //1
	static public final String SKU_DONATE2 = "donate_2"; //5
	static public final String SKU_DONATE3 = "donate_3"; //10
	static public final String SKU_DONATE4 = "donate_5"; //20
	static public final String SKU_DONATE5 = "donate_4"; //50
	static private final String[] SKU_DONATES = {SKU_DONATE1, SKU_DONATE2, SKU_DONATE3, SKU_DONATE4, SKU_DONATE5, /*SKU_TEST*/};

	/**
	 * The (arbitrary) payload for the purchase flow
	 */
	static private final String PAYLOAD = "sqlunet donation";

	/**
	 * The (arbitrary) request code for the purchase flow
	 */
	static private final int RC_REQUEST = 0xBBBB;

	/**
	 * Operation Listener
	 */
	public interface IABListener
	{
		enum Op
		{
			SETUP, INVENTORY, BUY
		}

		@SuppressWarnings("EmptyMethod")
		void onStart(@SuppressWarnings("UnusedParameters") Op op);

		void onFinish(@SuppressWarnings("UnusedParameters") boolean result, Op op);
	}

	/**
	 * Operations listener
	 */
	private IABListener iabListener;

	/**
	 * The helper object
	 */
	@Nullable
	private IabHelper iabHelper;

	/**
	 * Provides purchase notification while this app is running
	 */
	private IabBroadcastReceiver broadcastReceiver;

	/**
	 * The context activity
	 */
	private Activity activity;

	/**
	 * Has the user donated ?
	 */
	private boolean hasDonated = false;

	// L I F E C Y C L E

	/**
	 * OnCreated
	 *
	 * @param activity0    activity
	 * @param iabListener0 listener
	 */
	public void onCreate(final Activity activity0, final IABListener iabListener0)
	{
		this.activity = activity0;
		this.iabListener = iabListener0;

		/*
		 * base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY (that you got from the Google Play developer console). This is not your developer
		 * public key, it's the *app-specific* public key.
		 *
		 * Instead of just storing the entire literal string here embedded in the program, construct the key at runtime from pieces or use bit manipulation (for
		 * example, XOR with some other string) to hide the actual key. The key itself is not secret information, but we don't want to make it easy for an
		 * attacker to replace the public key with one of their own and then fake messages from the server.
		 */
		final String base64EncodedPublicKey = this.activity.getString(R.string.license_key);

		// Create the helper, passing it our context and the public key to verify signatures with
		Log.d(TAG, "Creating IAB helper");
		this.iabHelper = new IabHelper(this.activity, base64EncodedPublicKey);
		if (BuildConfig.DEBUG)
		{
			this.iabHelper.enableDebugLogging(true);
		}

		// Start setup. This is asynchronous and the specified listener will be called once setup completes.
		Log.d(TAG, "Starting IAB setup");
		this.iabHelper.startSetup(this.setupFinishedListener);
	}

	/**
	 * We're being destroyed. It's important to dispose of the helper here.
	 *
	 * @throws IabHelper.IabAsyncInProgressException exception
	 */
	public void onDestroy() throws IabHelper.IabAsyncInProgressException
	{
		// broadcast receiver
		if (IABAdapter.this.broadcastReceiver != null)
		{
			this.activity.unregisterReceiver(IABAdapter.this.broadcastReceiver);
		}

		// iab helper
		Log.d(TAG, "Destroying IAB helper");
		if (this.iabHelper != null)
		{
			this.iabHelper.dispose();
			this.iabHelper = null;
		}
	}

	// E X T E R N A L  L I S T E N E R S

	/**
	 * Result listener
	 *
	 * @param requestCode request code
	 * @param resultCode  result code
	 * @param data        result data
	 * @return true if handled
	 */
	public boolean onActivityResult(final int requestCode, final int resultCode, final Intent data)
	{
		// Pass on the activity result to the helper for handling
		if (this.iabHelper != null && this.iabHelper.handleActivityResult(requestCode, resultCode, data))
		{
			Log.d(TAG, "onActivityResult handled by IAB helper");
			return true;
		}

		// Not handled, so handle it ourselves (here's where you'd perform any handling of activity results not related to in-app billing...
		return false;
	}

	@Override
	public void receivedBroadcast()
	{
		// Received a broadcast notification that the inventory of items has changed
		Log.d(TAG, "Received broadcast notification");
		Log.d(TAG, "Querying inventory");
		try
		{
			assert this.iabHelper != null;
			this.iabHelper.queryInventoryAsync(this.queryInventoryListener);
		}
		catch (IabHelper.IabAsyncInProgressException e)
		{
			complain("Error querying inventory : another async operation in progress");
		}
	}

	// A C T I O N S

	/**
	 * Query inventory (result returned with receivedBroadcast
	 */
	private void queryInventory() throws IabHelper.IabAsyncInProgressException
	{
		Log.d(TAG, "Querying inventory");
		IABAdapter.this.iabListener.onStart(IABListener.Op.INVENTORY);
		assert this.iabHelper != null;
		this.iabHelper.queryInventoryAsync(this.queryInventoryListener);
	}

	/**
	 * Purchase product
	 *
	 * @param sku purchase sku
	 * @throws IabHelper.IabAsyncInProgressException exception
	 */
	public void buy(String sku) throws IabHelper.IabAsyncInProgressException
	{
		// sku = SKU_TEST;
		Log.d(TAG, "Launching purchase flow for " + sku);
		IABAdapter.this.iabListener.onStart(IABListener.Op.BUY);
		try
		{
			// Production apps should carefully generate this
			final String payload = "sqlunet donation";
			assert this.iabHelper != null;
			this.iabHelper.launchPurchaseFlow(this.activity, sku, RC_REQUEST, this.purchaseFinishedListener, payload);
		}
		catch (IllegalStateException e)
		{
			Log.e(TAG, "IAB: can't buy product", e);
			Toast.makeText(this.activity, R.string.error_cant_buy, Toast.LENGTH_LONG).show();
			IABAdapter.this.iabListener.onFinish(false, IABListener.Op.BUY);
		}
		Log.d(TAG, "Launched buying product");
	}

	/**
	 * Consume purchase
	 *
	 * @param purchase purchase to consume
	 * @throws IabHelper.IabAsyncInProgressException exception
	 */
	private void consume(@NonNull final Purchase purchase) throws IabHelper.IabAsyncInProgressException
	{
		Log.d(TAG, "Consuming product: " + purchase.getSku());
		assert this.iabHelper != null;
		this.iabHelper.consumeAsync(purchase, this.consumeFinishedListener);
	}

	// A C T I O N   L I S T E N E R S

	/**
	 * Callback for when we finish setting up
	 */
	@Nullable
	private final IabHelper.OnIabSetupFinishedListener setupFinishedListener = result -> {
		Log.d(TAG, "Setup finished");

		if (!result.isSuccess())
		{
			// There was a problem.
			Log.e(TAG, "Set up failed with result: " + result);
			complain("Failed to set up in-app billing: " + result);
			IABAdapter.this.iabListener.onFinish(false, IABListener.Op.SETUP);
			return;
		}

		// Have we been disposed of in the meantime? If so, quit.
		if (IABAdapter.this.iabHelper == null)
		{
			return;
		}

		// IAB is fully set up
		Log.d(TAG, "Setup successful");
		IABAdapter.this.iabListener.onFinish(true, IABListener.Op.SETUP);

		// Important: Dynamically register for broadcast messages about updated purchases.
		// We register the receiver here instead of as a <receiver> in the Manifest
		// because we always call getPurchases() at startup, so therefore we can ignore
		// any broadcasts sent while the app isn't running.
		// Note: registering this listener in an Activity is a bad idea, but is done here
		// because this is a SAMPLE. Regardless, the receiver must be registered after
		// IabHelper is setup, but before first call to getPurchases().
		IABAdapter.this.broadcastReceiver = new IabBroadcastReceiver(IABAdapter.this);
		IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
		IABAdapter.this.activity.registerReceiver(IABAdapter.this.broadcastReceiver, broadcastFilter);

		// Get an inventory of stuff we own.
		try
		{
			queryInventory();
		}
		catch (IabHelper.IabAsyncInProgressException e)
		{
			Log.e(TAG, "Query inventory failed", e);
		}
	};

	/**
	 * Callback for when we finish querying the items and subscriptions we own
	 */
	@Nullable
	private final IabHelper.QueryInventoryFinishedListener queryInventoryListener = (result, inventory) -> {
		Log.d(TAG, "Query inventory finished");

		// Have we been disposed of in the meantime? If so, quit.
		if (IABAdapter.this.iabHelper == null)
		{
			return;
		}

		/*
		if (inventory.hasPurchase(SKU_TEST))
		{
			try
			{
				consume(inventory.getPurchase(SKU_TEST));
			}
			catch (IabHelper.IabAsyncInProgressException e)
			{
				e.printStackTrace();
			}
		}
		*/

		// Is it a failure?
		if (result.isFailure())
		{
			Log.e(TAG, "Query inventory failed with result: " + result);
			complain("Failed to query inventory: " + result);
			IABAdapter.this.iabListener.onFinish(false, IABListener.Op.INVENTORY);
			return;
		}

		Log.d(TAG, "Query inventory was successful");

		// Scan inventory
		IABAdapter.this.hasDonated = false;
		for (String sku : SKU_DONATES)
		{
			final Purchase purchase = inventory.getPurchase(sku);
			if (purchase != null)
			{
				if (verifyDeveloperPayload(purchase))
				{
					IABAdapter.this.hasDonated = true;
				}

				// The time the product was purchased, in milliseconds since the epoch (Jan 1, 1970).
				long purchaseTime = purchase.getPurchaseTime();
				long now = System.currentTimeMillis();
				long diff = now - purchaseTime;
				//      s      m    h    d
				diff /= 1000 * 60 * 60 * 24;
				if (diff > 30)
				{
					try
					{
						consume(purchase);
					}
					catch (Exception e)
					{
						Log.e(TAG, "Cannot consume");
					}
				}
			}
		}

		Log.d(TAG, "User has " + (IABAdapter.this.hasDonated ? "donated" : "not donated"));

		// Fire finish signal
		IABAdapter.this.iabListener.onFinish(IABAdapter.this.hasDonated, IABListener.Op.INVENTORY);
	};

	/**
	 * Callback for when a purchase is finished
	 */
	@Nullable
	private final IabHelper.OnIabPurchaseFinishedListener purchaseFinishedListener = (result, purchase) -> {
		Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

		// if we were disposed of in the meantime, quit.
		if (IABAdapter.this.iabHelper == null)
		{
			return;
		}

		if (result.isFailure())
		{
			Log.e(TAG, "Purchase failed with result: " + result);
			complain("Error purchasing: " + result);
			IABAdapter.this.iabListener.onFinish(false, IABListener.Op.BUY);
			return;
		}

		if (!verifyDeveloperPayload(purchase))
		{
			Log.e(TAG, "Semantikos IAB failed to verify purchase: " + result);
			complain("Error while donating : authenticity verification failed");
			IABAdapter.this.iabListener.onFinish(false, IABListener.Op.BUY);
			return;
		}

		Log.d(TAG, "Purchase successful " + purchase.getSku());

		for (String donate : SKU_DONATES)
		{
			if (donate.equals(purchase.getSku()))
			{
				// donated
				Log.d(TAG, "Purchase is successful: donation is owned " + donate);
				alert("Thank you for donating!");
				IABAdapter.this.hasDonated = true;
				IABAdapter.this.iabListener.onFinish(true, IABListener.Op.BUY);
			}
		}
	};

	/**
	 * Callback for when consumption is complete
	 */
	@Nullable
	private final IabHelper.OnConsumeFinishedListener consumeFinishedListener = (purchase, result) -> {
		Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

		// if we were disposed of in the meantime, quit.
		if (IABAdapter.this.iabHelper == null)
		{
			return;
		}

		// We know this is the X sku because it's the only one we consume, so we don't check which sku was consumed. If you have more than one sku, you probably should check...
		if (result.isSuccess())
		{
			// successfully consumed, so we apply the effects of the item in our world's logic
			Log.d(TAG, "Consumption successful");
			// ...
			alert("Consuming!");
		}
		else
		{
			complain("Error while consuming: " + result);
		}
		IABAdapter.this.iabListener.onFinish(IABAdapter.this.hasDonated, IABListener.Op.BUY);
		Log.d(TAG, "End consumption flow");
	};

	// P A Y L O A D    V E R I F I C A T I O N

	/**
	 * Verifies the developer payload of a purchase.
	 */
	static private boolean verifyDeveloperPayload(@NonNull Purchase purchase)
	{
		/*
		 * Verify that the developer payload of the purchase is correct. It will be the same one that you sent when initiating the purchase.
		 *
		 * WARNING: Locally generating a random string when starting a purchase and verifying it here might seem like a good approach, but this will fail in the
		 * case where the user purchases an item on one device and then uses your app on a different device, because on the other device you will not have
		 * access to the random string you originally generated.
		 *
		 * So a good developer payload has these characteristics:
		 *
		 * 1. If two different users purchase an item, the payload is different between them, so that one user's purchase can't be replayed to another user.
		 *
		 * 2. The payload must be such that you can verify it even when the app wasn't the one who initiated the purchase flow (so that items purchased by the
		 * user on one device work on other devices owned by the user).
		 *
		 * Using your own server to store and verify developer payloads across app installations is recommended.
		 */
		final String payload = purchase.getDeveloperPayload();
		return PAYLOAD.equals(payload);
	}

	// I N T E R A C T

	/**
	 * Complain
	 *
	 * @param message message
	 */
	private void complain(String message)
	{
		alert("Error: " + message);
	}

	/**
	 * Alert
	 *
	 * @param message message
	 */
	private void alert(String message)
	{
		final AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
		builder.setMessage(message);
		builder.setNeutralButton("OK", null);
		Log.d(TAG, "Alert dialog: " + message);
		builder.create().show();
	}
}
