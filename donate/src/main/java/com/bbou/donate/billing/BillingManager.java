/*
 * Copyright (c) 2022. Bernard Bou
 */
package com.bbou.donate.billing;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.bbou.donate.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Handles all the interactions with Play Store (via Billing library), maintains connection to it through BillingClient and caches temporary states/data if needed
 */
public class BillingManager
{
	private static final String TAG = "BillingManager";

	/**
	 * A reference to BillingClient
	 **/
	@Nullable
	private BillingClient client;

	/**
	 * Listener to fire to
	 */
	private final BillingListener listener;

	/**
	 * Activity
	 */
	private final Activity activity;

	/**
	 * List of verifiedPurchases
	 */
	private final List<Purchase> verifiedPurchases = new ArrayList<>();

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
	public interface BillingListener
	{
		void onBillingClientSetupFinished();

		void onPurchaseFinished(final Purchase purchase);

		void onConsumeFinished(final Purchase purchase);

		void onPurchaseList(@Nullable List<Purchase> purchases);
	}

	// C O N S T R U C T O R / D E S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param activity activity
	 * @param listener updates listener
	 */
	public BillingManager(final Activity activity, final BillingListener listener)
	{
		Log.d(TAG, "Creating billing client.");
		this.activity = activity;
		this.listener = listener;
		this.base64EncodedPublicKey = this.activity.getString(R.string.license_key);
		client = BillingClient.newBuilder(this.activity) //
				.enablePendingPurchases() //
				.setListener(this::handleNewPurchases) //
				.build();

		// Start setup.
		// This is asynchronous and the specified listener will be called once setup completes.
		// It also starts to report all the new verifiedPurchases through onPurchasesUpdated() callback.
		Log.d(TAG, "Starting setup.");
		startServiceConnection(() -> {

			// Notifying the listener that billing client is ready
			this.listener.onBillingClientSetupFinished();

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
		if (client == null)
		{
			Log.e(TAG, "Start service connection failed. Null billing client.");
			return;
		}
		Log.d(TAG, "Setting up client.");
		client.startConnection(new BillingClientStateListener()
		{
			@Override
			public void onBillingSetupFinished(@NonNull final BillingResult billingResult)
			{
				BillingManager.this.billingClientResponseCode = billingResult.getResponseCode();
				if (BillingResponseCode.OK == BillingManager.this.billingClientResponseCode)
				{
					Log.d(TAG, "Setup succeeded.");

					// Flag success
					isServiceConnected = true;

					//
					if (executeOnSuccess != null)
					{
						executeOnSuccess.run();
					}
				}
				else
				{
					Log.e(TAG, "Setup failed. Response: " + billingResult.getResponseCode() + " " + billingResult.getDebugMessage());
					Toast.makeText(getContext(), billingResult.getDebugMessage(), Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onBillingServiceDisconnected()
			{
				isServiceConnected = false;
			}
		});
	}

	/**
	 * Clear the resources
	 */
	public void destroy()
	{
		Log.d(TAG, "Destroying the billing client.");
		if (client != null && client.isReady())
		{
			client.endConnection();
			client = null;
		}
	}

	// A C C E S S

	/**
	 * Get context
	 *
	 * @return context
	 */
	private Context getContext()
	{
		return activity;
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

	/**
	 * Purchases to product ids
	 *
	 * @param purchases purchases
	 * @return product ids
	 */
	@NonNull
	public List<String> purchasesToProductIds(@Nullable List<Purchase> purchases)
	{
		final List<String> productIds = new ArrayList<>();
		if (purchases != null)
		{
			for (Purchase purchase : purchases)
			{
				productIds.addAll(purchaseToProductIds(purchase));
			}
		}
		return productIds;
	}

	/**
	 * Purchase to product ids
	 *
	 * @param purchase purchase
	 * @return product ids
	 */
	@NonNull
	public List<String> purchaseToProductIds(@Nullable Purchase purchase)
	{
		if (purchase == null)
		{
			return new ArrayList<>();
		}
		return purchase.getProducts();
	}

	/**
	 * Product id to purchase
	 *
	 * @param productId0 product id
	 * @return purchase
	 */
	@Nullable
	public Purchase productIdToPurchase(final String productId0)
	{
		for (Purchase purchase : verifiedPurchases)
		{
			List<String> productIds = purchase.getProducts();
			for (String productId : productIds)
			{
				if (productId.equals(productId0))
				{
					return purchase;
				}
			}
		}
		return null;
	}

	/**
	 * Product id to purchase
	 *
	 * @param purchaseToken0 purchase token
	 * @return purchase
	 */
	@Nullable
	public Purchase purchaseTokenToPurchase(final String purchaseToken0)
	{
		for (Purchase purchase : verifiedPurchases)
		{
			String purchaseToken = purchase.getPurchaseToken();
			if (purchaseToken.equals(purchaseToken0))
			{
				return purchase;
			}
		}
		return null;
	}

	//  P U R C H A S E

	/**
	 * Start a purchase flow
	 *
	 * @param productId product id
	 */
	public void initiatePurchaseFlow(final String productId)
	{
		executeServiceRequest(() -> {

			// guard against destroyed client
			if (client == null)
			{
				Log.e(TAG, "Initiate purchase flow failed. Null billing client.");
				return;
			}

			// query details params
			Log.d(TAG, "Getting product details for " + productId);
			final List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
			productList.add(QueryProductDetailsParams.Product.newBuilder() //
					.setProductId(productId) //
					.setProductType(BillingClient.ProductType.INAPP) //
					.build());
			final QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder() //
					.setProductList(productList) //
					.build();

			// query details
			client.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {

				int response = billingResult.getResponseCode();
				if (BillingResponseCode.OK != response)
				{
					Log.e(TAG, "Getting product details failed. " + response);
					return;
				}
				if (productDetailsList.isEmpty())
				{
					Log.e(TAG, "Getting product details yielded no details for product " + productId);
					return;
				}

				// purchase
				final ProductDetails productDetails = productDetailsList.get(0);
				initiatePurchaseFlow(productDetails);
			});
		});
	}

	/**
	 * Start a purchase or subscription replace flow
	 *
	 * @param productDetails product details
	 */
	private void initiatePurchaseFlow(@NonNull final ProductDetails productDetails)
	{
		executeServiceRequest(() -> {

			// guard against destroyed client
			if (client == null)
			{
				Log.e(TAG, "Initiate purchase flow failed. Null billing client.");
				return;
			}

			// purchase params
			Log.d(TAG, "Launching purchase flow.");
			final List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = new ArrayList<>();
			productDetailsParamsList.add(BillingFlowParams.ProductDetailsParams.newBuilder() //
					.setProductDetails(productDetails) //
					.build());
			final BillingFlowParams params = BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList).build();

			// purchase
			/* BillingResult billingResult = */
			client.launchBillingFlow(activity, params);
		});
	}

	/**
	 * Handle new purchases
	 */
	private void handleNewPurchases(@NonNull final BillingResult billingResult, @Nullable final List<Purchase> purchases)
	{
		int responseCode = billingResult.getResponseCode();
		if (BillingResponseCode.OK == responseCode)
		{
			Log.i(TAG, "Purchases: success.");
			if (purchases != null)
			{
				for (Purchase purchase : purchases)
				{
					handleNewPurchase(purchase);
				}
			}
		}
		else if (responseCode == BillingResponseCode.USER_CANCELED)
		{
			Log.i(TAG, "Purchases: User-cancelled.");
		}
		else if (responseCode == BillingResponseCode.ITEM_ALREADY_OWNED)
		{
			Log.i(TAG, "Purchases: Item already owned.");
		}
		else
		{
			Log.w(TAG, "Purchases: Unexpected response: " + responseCode);
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
	private void handleNewPurchase(@NonNull final Purchase purchase)
	{
		if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature()))
		{
			Log.e(TAG, "Got a purchase: " + purchase + " but signature is bad.");
			return;
		}

		Log.d(TAG, "Got a verified purchase: " + purchase);
		verifiedPurchases.add(purchase);

		// Acknowledge the purchase if it hasn't already been acknowledged.
		acknowledgePurchase(purchase);

		// Fire
		listener.onPurchaseFinished(purchase);
	}

	/**
	 * Acknowledge purchase
	 *
	 * @param purchase purchase
	 */
	private void acknowledgePurchase(@NonNull final Purchase purchase)
	{
		// guard against destroyed client
		if (client == null)
		{
			Log.e(TAG, "Acknowledge purchase failed. Null billing client.");
			return;
		}

		// Acknowledge the purchase if it hasn't already been acknowledged.
		if (!purchase.isAcknowledged())
		{
			final AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder() //
					.setPurchaseToken(purchase.getPurchaseToken()) //
					.build();
			client.acknowledgePurchase(acknowledgePurchaseParams, (billingResult) -> Log.d(TAG, "Acknowledged purchase: " + purchase + " response: " + billingResult.getResponseCode()));
		}
	}

	// C O N S U M E

	/**
	 * Consume all purchases
	 */
	public void consumeAll()
	{
		for (Purchase purchase : verifiedPurchases)
		{
			consume(purchase);
		}
	}

	/**
	 * Consume purchase
	 *
	 * @param purchase purchase
	 */
	public void consume(@NonNull final Purchase purchase)
	{
		executeServiceRequest(() -> {

			// guard against destroyed client
			if (client == null)
			{
				Log.e(TAG, "Consume failed. Null billing client.");
				return;
			}

			// consume purchase params
			final String purchaseToken = purchase.getPurchaseToken();
			final ConsumeParams params = ConsumeParams.newBuilder().setPurchaseToken(purchaseToken).build();

			// consume purchase
			client.consumeAsync(params, this::consumed);
		});
	}

	private void consumed(@NonNull final BillingResult billingResult, @Nullable final String consumedToken)
	{
		int responseCode = billingResult.getResponseCode();
		if (BillingResponseCode.OK == responseCode)
		{
			final Purchase purchase = purchaseTokenToPurchase(consumedToken);
			listener.onConsumeFinished(purchase);
		}
		else
		{
			Log.w(TAG, "Query purchases: Unexpected response: " + responseCode);
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
			if (client == null)
			{
				Log.e(TAG, "Query purchases failed. Null billing client.");
				return;
			}
			// query purchases params
			final QueryPurchasesParams params = QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build();

			// query purchases
			client.queryPurchasesAsync(params, this::collectPurchases);
		});
	}

	private void collectPurchases(@NonNull final BillingResult billingResult, @Nullable final List<Purchase> purchases)
	{
		int responseCode = billingResult.getResponseCode();
		if (BillingResponseCode.OK == responseCode)
		{
			makeList(purchases);
		}
		else
		{
			Log.w(TAG, "Query purchases: Unexpected response: " + responseCode);
		}
	}

	synchronized private void makeList(@Nullable final List<Purchase> purchases)
	{
		verifiedPurchases.clear();

		if (purchases != null)
		{
			Log.i(TAG, "Querying purchases count: " + purchases.size());
			for (Purchase purchase : purchases)
			{
				if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature()))
				{
					Log.e(TAG, "Ignoring purchase: " + purchase + " whose signature is bad.");
					return;
				}

				verifiedPurchases.add(purchase);
			}
		}

		// fire update
		listener.onPurchaseList(verifiedPurchases);
	}

	// D E T A I L S

	/**
	 * Query details async op
	 *
	 * @param productList product list
	 * @param listener    response listener
	 */
	public void queryDetailsProductDetails(@NonNull final List<QueryProductDetailsParams.Product> productList, @NonNull final ProductDetailsResponseListener listener)
	{
		// Creating a runnable from the request to use it inside our connection retry policy below
		executeServiceRequest(() -> {

			// guard against destroyed client
			if (client == null)
			{
				Log.e(TAG, "Query sku details failed. Null billing client.");
				return;
			}

			// query product details params
			final QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder().setProductList(productList).build();

			// query product details
			client.queryProductDetailsAsync(params, listener);
		});
	}

	// S E R V I C E   R E Q U E S T

	/**
	 * Execute service request
	 */
	private void executeServiceRequest(@NonNull final Runnable runnable)
	{
		if (isServiceConnected)
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

	// V A L I D I T Y

	/**
	 * Verifies that the purchase was signed correctly for this developer's public key.
	 * <p>Note: It's strongly recommended to perform such check on your backend since hackers can
	 * replace this method with "constant true" if they decompile/rebuild your app.
	 * </p>
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
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

