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
package com.bbou.donate.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.bbou.donate.R
import java.io.IOException

/**
 * Handles all the interactions with Play Store (via Billing library), maintains connection to it through BillingClient and caches temporary states/data if needed
 */
class BillingManager(activity: Activity, listener: BillingListener) {

    /**
     * A reference to BillingClient
     */
    private var client: BillingClient?

    /**
     * Listener to fire to
     */
    private val listener: BillingListener

    /**
     * Activity
     */
    private val activity: Activity

    /**
     * List of verifiedPurchases
     */
    private val verifiedPurchases: MutableList<Purchase> = ArrayList()

    // status

    /**
     * True if billing service is connected now.
     */
    private var isServiceConnected = false

    /**
     * Service connection response code
     * The value Billing client response code or BillingResponseCode.SERVICE_DISCONNECTED if the client connection response was not received yet.
     */
    @BillingResponseCode
    var billingClientResponseCode = BillingResponseCode.SERVICE_DISCONNECTED
        private set

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
    private val base64EncodedPublicKey: String

    /**
     * Listener to the updates that happen when verifiedPurchases list was updated or consumption of the item was finished
     */
    interface BillingListener {
        fun onBillingClientSetupFinished()
        fun onPurchaseFinished(purchase: Purchase)
        fun onConsumeFinished(purchase: Purchase)
        fun onPurchaseList(purchases: List<Purchase>)
    }

    // C O N S T R U C T O R / D E S T R U C T O R

    /**
     * Constructor
     */
    init {
        Log.d(TAG, "Creating billing client.")
        this.activity = activity
        this.listener = listener
        base64EncodedPublicKey = this.activity.getString(R.string.license_key)
        client = BillingClient.newBuilder(this.activity)
            .enablePendingPurchases()
            .setListener { billingResult: BillingResult, purchases: List<Purchase>? -> handleNewPurchases(billingResult, purchases) }
            .build()

        // Start setup.
        // This is asynchronous and the specified listener will be called once setup completes.
        // It also starts to report all the new verifiedPurchases through onPurchasesUpdated() callback.
        Log.d(TAG, "Starting setup.")
        startServiceConnection {

            // Notifying the listener that billing client is ready
            this.listener.onBillingClientSetupFinished()

            // IAB is fully set up. Now, let's get an inventory of stuff we own.
            Log.d(TAG, "Querying inventory.")
            queryPurchases()
        }
    }

    /**
     * Start service connection
     *
     * @param executeOnSuccess runnable to be executed on success
     */
    private fun startServiceConnection(executeOnSuccess: Runnable?) {
        // guard against destroyed client
        if (client == null) {
            Log.e(TAG, "Start service connection failed. Null billing client.")
            return
        }
        Log.d(TAG, "Setting up client.")
        client!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                billingClientResponseCode = billingResult.responseCode
                if (BillingResponseCode.OK == billingClientResponseCode) {
                    Log.d(TAG, "Setup succeeded.")

                    // Flag success
                    isServiceConnected = true

                    // Execute success tail
                    executeOnSuccess?.run()
                } else {
                    Log.e(TAG, "Setup failed. Response: " + billingResult.responseCode + " " + billingResult.debugMessage)
                    Toast.makeText(context, billingResult.debugMessage, Toast.LENGTH_LONG).show()
                }
            }

            override fun onBillingServiceDisconnected() {
                isServiceConnected = false
            }
        })
    }

    /**
     * Clear the resources
     */
    fun destroy() {
        Log.d(TAG, "Destroying the billing client.")
        if (client != null && client!!.isReady) {
            client!!.endConnection()
            client = null
        }
    }

    // A C C E S S

    private val context: Context
        /**
         * Get context
         *
         * @return context
         */
        get() = activity

    /**
     * Purchases to product ids
     *
     * @param purchases purchases
     * @return product ids
     */
    fun purchasesToProductIds(purchases: List<Purchase?>?): List<String> {
        val productIds: MutableList<String> = ArrayList()
        if (purchases != null) {
            for (purchase in purchases) {
                productIds.addAll(purchaseToProductIds(purchase))
            }
        }
        return productIds
    }

    /**
     * Purchase to product ids
     *
     * @param purchase purchase
     * @return product ids
     */
    private fun purchaseToProductIds(purchase: Purchase?): List<String> {
        return purchase?.products ?: ArrayList()
    }

    /**
     * Product id to purchase
     *
     * @param productId0 product id
     * @return purchase
     */
    fun productIdToPurchase(productId0: String): Purchase? {
        for (purchase in verifiedPurchases) {
            val productIds = purchase.products
            for (productId in productIds) {
                if (productId == productId0) {
                    return purchase
                }
            }
        }
        return null
    }

    /**
     * Product id to purchase
     *
     * @param purchaseToken0 purchase token
     * @return purchase
     */
    private fun purchaseTokenToPurchase(purchaseToken0: String?): Purchase? {
        for (purchase in verifiedPurchases) {
            val purchaseToken = purchase.purchaseToken
            if (purchaseToken == purchaseToken0) {
                return purchase
            }
        }
        return null
    }

    //  P U R C H A S E

    /**
     * Start a purchase flow
     *
     * @param productId product id
     */
    fun initiatePurchaseFlow(productId: String) {
        executeServiceRequest {


            // guard against destroyed client
            if (client == null) {
                Log.e(TAG, "Initiate purchase flow failed. Null billing client.")
                return@executeServiceRequest
            }

            // query details params
            Log.d(TAG, "Getting product details for $productId")
            val productList: MutableList<QueryProductDetailsParams.Product> = ArrayList()
            productList.add(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            )
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            // query details
            client!!.queryProductDetailsAsync(params) { billingResult: BillingResult, productDetailsList: List<ProductDetails> ->
                val response = billingResult.responseCode
                if (BillingResponseCode.OK != response) {
                    Log.e(TAG, "Getting product details failed. $response")
                    return@queryProductDetailsAsync
                }
                if (productDetailsList.isEmpty()) {
                    Log.e(TAG, "Getting product details yielded no details for product $productId")
                    return@queryProductDetailsAsync
                }

                // purchase
                val productDetails = productDetailsList[0]
                initiatePurchaseFlow(productDetails)
            }
        }
    }

    /**
     * Start a purchase or subscription replace flow
     *
     * @param productDetails product details
     */
    private fun initiatePurchaseFlow(productDetails: ProductDetails) {
        executeServiceRequest {


            // guard against destroyed client
            if (client == null) {
                Log.e(TAG, "Initiate purchase flow failed. Null billing client.")
                return@executeServiceRequest
            }

            // purchase params
            Log.d(TAG, "Launching purchase flow.")
            val productDetailsParamsList: MutableList<ProductDetailsParams> = ArrayList()
            productDetailsParamsList.add(
                ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .build()
            )
            val params = BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList).build()

            // purchase
            /* BillingResult billingResult = */client!!.launchBillingFlow(activity, params)
        }
    }

    /**
     * Handle new purchases
     */
    private fun handleNewPurchases(billingResult: BillingResult, purchases: List<Purchase>?) {
        val responseCode = billingResult.responseCode
        if (BillingResponseCode.OK == responseCode) {
            Log.i(TAG, "Purchases: success.")
            if (purchases != null) {
                for (purchase in purchases) {
                    handleNewPurchase(purchase)
                }
            }
        } else if (responseCode == BillingResponseCode.USER_CANCELED) {
            Log.i(TAG, "Purchases: User-cancelled.")
        } else if (responseCode == BillingResponseCode.ITEM_ALREADY_OWNED) {
            Log.i(TAG, "Purchases: Item already owned.")
        } else {
            Log.w(TAG, "Purchases: Unexpected response: $responseCode")
        }
    }

    /**
     * Handles the purchase
     *
     * Note: Notice that for each purchase, we check if signature is valid on the client.
     * It's recommended to move this check into your backend.
     * See [Security.verifyPurchase]
     *
     *
     * @param purchase Purchase to be handled
     */
    private fun handleNewPurchase(purchase: Purchase) {
        if (!verifyValidSignature(purchase.originalJson, purchase.signature)) {
            Log.e(TAG, "Got a purchase: $purchase but signature is bad.")
            return
        }
        Log.d(TAG, "Got a verified purchase: $purchase")
        verifiedPurchases.add(purchase)

        // Acknowledge the purchase if it hasn't already been acknowledged.
        acknowledgePurchase(purchase)

        // Fire
        listener.onPurchaseFinished(purchase)
    }

    /**
     * Acknowledge purchase
     *
     * @param purchase purchase
     */
    private fun acknowledgePurchase(purchase: Purchase) {
        // guard against destroyed client
        if (client == null) {
            Log.e(TAG, "Acknowledge purchase failed. Null billing client.")
            return
        }

        // Acknowledge the purchase if it hasn't already been acknowledged.
        if (!purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            client!!.acknowledgePurchase(acknowledgePurchaseParams) { billingResult: BillingResult -> Log.d(TAG, "Acknowledged purchase: " + purchase + " response: " + billingResult.responseCode) }
        }
    }
    // C O N S U M E
    /**
     * Consume all purchases
     */
    fun consumeAll() {
        for (purchase in verifiedPurchases) {
            consume(purchase)
        }
    }

    /**
     * Consume purchase
     *
     * @param purchase purchase
     */
    private fun consume(purchase: Purchase) {
        executeServiceRequest {


            // guard against destroyed client
            if (client == null) {
                Log.e(TAG, "Consume failed. Null billing client.")
                return@executeServiceRequest
            }

            // consume purchase params
            val purchaseToken = purchase.purchaseToken
            val params = ConsumeParams.newBuilder().setPurchaseToken(purchaseToken).build()

            // consume purchase
            client!!.consumeAsync(params) { billingResult: BillingResult, consumedToken: String? -> consumed(billingResult, consumedToken) }
        }
    }

    private fun consumed(billingResult: BillingResult, consumedToken: String?) {
        val responseCode = billingResult.responseCode
        if (BillingResponseCode.OK == responseCode) {
            val purchase = purchaseTokenToPurchase(consumedToken)!!
            listener.onConsumeFinished(purchase)
        } else {
            Log.w(TAG, "Query purchases: Unexpected response: $responseCode")
        }
    }

    // I N V E N T O R Y

    /**
     * Query verifiedPurchases across various use cases and deliver the result in a formalized way through a listener
     */
    fun queryPurchases() {
        executeServiceRequest {


            // guard against destroyed client
            if (client == null) {
                Log.e(TAG, "Query purchases failed. Null billing client.")
                return@executeServiceRequest
            }
            // query purchases params
            val params = QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build()

            // query purchases
            client!!.queryPurchasesAsync(params) { billingResult: BillingResult, purchases: List<Purchase>? -> collectPurchases(billingResult, purchases) }
        }
    }

    private fun collectPurchases(billingResult: BillingResult, purchases: List<Purchase>?) {
        val responseCode = billingResult.responseCode
        if (BillingResponseCode.OK == responseCode) {
            makeList(purchases)
        } else {
            Log.w(TAG, "Query purchases: Unexpected response: $responseCode")
        }
    }

    @Synchronized
    private fun makeList(purchases: List<Purchase>?) {
        verifiedPurchases.clear()
        if (purchases != null) {
            Log.i(TAG, "Querying purchases count: " + purchases.size)
            for (purchase in purchases) {
                if (!verifyValidSignature(purchase.originalJson, purchase.signature)) {
                    Log.e(TAG, "Ignoring purchase: $purchase whose signature is bad.")
                    return
                }
                verifiedPurchases.add(purchase)
            }
        }

        // fire update
        listener.onPurchaseList(verifiedPurchases)
    }

    // D E T A I L S

    /**
     * Query details async op
     *
     * @param productList product list
     * @param listener    response listener
     */
    fun queryDetailsProductDetails(productList: List<QueryProductDetailsParams.Product?>, listener: ProductDetailsResponseListener) {
        // Creating a runnable from the request to use it inside our connection retry policy below
        executeServiceRequest {


            // guard against destroyed client
            if (client == null) {
                Log.e(TAG, "Query sku details failed. Null billing client.")
                return@executeServiceRequest
            }

            // query product details params
            val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()

            // query product details
            client!!.queryProductDetailsAsync(params, listener)
        }
    }

    // S E R V I C E   R E Q U E S T

    /**
     * Execute service request
     */
    private fun executeServiceRequest(runnable: Runnable) {
        if (isServiceConnected) {
            runnable.run()
        } else {
            // If billing service was disconnected, we try to reconnect 1 time.
            // (feel free to introduce your retry policy here).
            startServiceConnection(runnable)
        }
    }

    // V A L I D I T Y

    /**
     * Verifies that the purchase was signed correctly for this developer's public key.
     *
     * Note: It's strongly recommended to perform such check on your backend since hackers can
     * replace this method with "constant true" if they decompile/rebuild your app.
     *
     */
    private fun verifyValidSignature(signedData: String, signature: String): Boolean {
        // Some sanity checks to see if the developer (that's you!) really followed the instructions to run this sample (don't put these checks on your app!)
        return try {
            Security.verifyPurchase(base64EncodedPublicKey, signedData, signature)
        } catch (e: IOException) {
            Log.e(TAG, "Got an exception trying to validate a purchase: $e")
            false
        }
    }

    companion object {
        private const val TAG = "BillingManager"
    }
}
