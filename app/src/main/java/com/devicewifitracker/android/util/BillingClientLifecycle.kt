/*
 * Copyright 2018 Google LLC. All rights reserved.
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

package com.devicewifitracker.android.util

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow

class BillingClientLifecycle private constructor(
    private val applicationContext: Context,
    private val externalScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
) : DefaultLifecycleObserver, PurchasesUpdatedListener, BillingClientStateListener,
    ProductDetailsResponseListener, PurchasesResponseListener {

    /**
     * ProductDetails for all known products.
     */
    val productsWithProductDetails = HashMap<String, ProductDetails>()
    var productsWithUserCanceled = mutableListOf<String>()

    /**
     * Instantiate a new BillingClient instance.
     */
    private lateinit var billingClient: BillingClient

    val subscribeStatus = MutableLiveData(false)

    override fun onCreate(owner: LifecycleOwner) {
        Log.e(TAG, "ON_CREATE")
        billingClient = BillingClient.newBuilder(applicationContext)
            .setListener(this)
            .enablePendingPurchases() // Not used for subscriptions.
            .build()
        if (!billingClient.isReady) {
            Log.e(TAG, "BillingClient: Start connection...")
            billingClient.startConnection(this)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Log.e(TAG, "ON_DESTROY")
        if (billingClient.isReady) {
            Log.e(TAG, "BillingClient can only be used once -- closing connection")
            billingClient.endConnection()
        }
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Log.e(TAG, "onBillingSetupFinished: $responseCode $debugMessage")
        if (responseCode == BillingClient.BillingResponseCode.OK) {
            queryProductDetails()
            queryPurchases()
        }
    }

    override fun onBillingServiceDisconnected() {
        Log.e(TAG, "onBillingServiceDisconnected")
        // billingClient.startConnection(this)
    }

    private fun queryProductDetails() {
        Log.e(TAG, "queryProductDetails")
        val params = QueryProductDetailsParams.newBuilder()

        val productList: MutableList<QueryProductDetailsParams.Product> = arrayListOf()
        for (product in LIST_OF_PRODUCTS) {
            productList.add(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(product)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            )
        }

        params.setProductList(productList).let { productDetailsParams ->
            Log.e(TAG, "queryProductDetailsAsync")
            billingClient.queryProductDetailsAsync(productDetailsParams.build(), this)
        }

    }

    override fun onProductDetailsResponse(
        billingResult: BillingResult,
        productDetailsList: MutableList<ProductDetails>
    ) {
        val response = BillingResponse(billingResult.responseCode)
        val debugMessage = billingResult.debugMessage
        when {
            response.isOk -> {
                val expectedProductDetailsCount = LIST_OF_PRODUCTS.size
                if (productDetailsList.isNullOrEmpty()) {
                    Log.e(
                        TAG, "onProductDetailsResponse: " +
                                "Expected ${expectedProductDetailsCount}, " +
                                "Found null ProductDetails. " +
                                "Check to see if the products you requested are correctly published " +
                                "in the Google Play Console."
                    )
                } else {
                    for (productDetails in productDetailsList) {
                        productsWithProductDetails.put(productDetails.productId, productDetails)
                    }
                }
            }
            response.isTerribleFailure -> {
                // These response codes are not expected.
                Log.e(TAG, "onProductDetailsResponse: ${response.code} $debugMessage")
            }
            else -> {
                Log.e(TAG, "onProductDetailsResponse: ${response.code} $debugMessage")
            }

        }
    }


    fun queryPurchases() {
        if (!billingClient.isReady) {
            Log.e(TAG, "queryPurchases: BillingClient is not ready")
            billingClient.startConnection(this)
        }
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build(), this
        )
    }

    /**
     * Callback from the billing library when queryPurchasesAsync is called.
     */
    override fun onQueryPurchasesResponse(
        billingResult: BillingResult,
        purchasesList: MutableList<Purchase>
    ) {
        processPurchases(purchasesList)
    }

    /**
     * Called by the Billing Library when new purchases are detected.
     */
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Log.e(TAG, "onPurchasesUpdated: $responseCode $debugMessage")
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (purchases == null) {
                    Log.e(TAG, "onPurchasesUpdated: null purchase list")
                    processPurchases(null)
                } else {
                    processPurchases(purchases)
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Log.e(TAG, "onPurchasesUpdated: User canceled the purchase")
                if(!purchases.isNullOrEmpty()){
                    for (purchase in purchases) {
                        if(!purchase.products.isNullOrEmpty()){
                            if(purchase.products.contains(skuMonth)){
                                if (!productsWithUserCanceled.contains(skuMonth)){
                                    productsWithUserCanceled.contains(skuMonth)
                                }
                            }else if(purchase.products.contains(skuYear)){
                                if (!productsWithUserCanceled.contains(skuYear)){
                                    productsWithUserCanceled.contains(skuYear)
                                }
                            }
                        }
                    }
                }
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                Log.e(TAG, "onPurchasesUpdated: The user already owns this item")
            }
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
                Log.e(
                    TAG, "onPurchasesUpdated: Developer error means that Google Play " +
                            "does not recognize the configuration. If you are just getting started, " +
                            "make sure you have configured the application correctly in the " +
                            "Google Play Console. The product ID must match and the APK you " +
                            "are using must be signed with release keys."
                )
            }
        }
    }

    /**
     * Send purchase to StateFlow, which will trigger network call to verify the subscriptions
     * on the sever.
     */
    private fun processPurchases(purchasesList: List<Purchase>?) {
        Log.e(TAG, "processPurchases: ${purchasesList?.size} purchase(s)")
        if (purchasesList == null || purchasesList.isEmpty()) {
            Log.e(TAG, "processPurchases: Purchase list is null or empty")
            return
        }
        externalScope.launch {
            for (purchase in purchasesList) {
                if(purchase.purchaseState == Purchase.PurchaseState.PURCHASED){
                    if(!purchase.products.isNullOrEmpty()){
                        if(purchase.products.contains(skuMonth) || purchase.products.contains(skuYear)){
                            subscribeStatus.value = true
                            if(!purchase.isAcknowledged){
                                acknowledgePurchase(purchase.purchaseToken)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Launching the billing flow.
     *
     * Launching the UI to make a purchase requires a reference to the Activity.
     */
    fun launchBillingFlow(activity: Activity, sku: String): Int {
        if (!billingClient.isReady) {
            Log.e(TAG, "launchBillingFlow: BillingClient is not ready")
        }
        val skuDetails = productsWithProductDetails[sku]
        if (null != skuDetails) {
            val billingFlowParamsBuilder = BillingFlowParams.newBuilder()
            var p : BillingFlowParams.ProductDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(skuDetails)
                .build()
            billingFlowParamsBuilder.setProductDetailsParamsList(listOf(p))

            val billingResult = billingClient.launchBillingFlow(
                activity!!,
                billingFlowParamsBuilder.build()
            )
            val responseCode = billingResult.responseCode
            val debugMessage = billingResult.debugMessage
            if (responseCode == BillingClient.BillingResponseCode.OK) {
                subscribeStatus.value = true
            } else {
                Log.e(TAG, "Billing failed: + $debugMessage")
            }
            return responseCode
        } else {
            Log.e(TAG, "SkuDetails not found for: $sku")
            return  BillingClient.BillingResponseCode.ERROR
        }
    }

    suspend fun acknowledgePurchase(purchaseToken: String): Boolean {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        for (trial in 1..MAX_RETRY_ATTEMPT) {
            var response = BillingResponse(500)
            var bResult: BillingResult? = null
            billingClient.acknowledgePurchase(params) { billingResult ->
                response = BillingResponse(billingResult.responseCode)
                bResult = billingResult
            }

            when {
                response.isOk -> {
                    Log.e(TAG, "Acknowledge success - token: $purchaseToken")
                    return true
                }
                response.canFailGracefully -> {
                    // Ignore the error
                    Log.e(TAG, "Token $purchaseToken is already owned.")
                    return true
                }
                response.isRecoverableError -> {
                    // Retry to ack because these errors may be recoverable.
                    val duration = 500L * 2.0.pow(trial).toLong()
                    delay(duration)
                    if (trial < MAX_RETRY_ATTEMPT) {
                        Log.e(
                            TAG,
                            "Retrying($trial) to acknowledge for token $purchaseToken - code: ${bResult!!.responseCode}, message: ${bResult!!.debugMessage}"
                        )
                    }
                }
                response.isNonrecoverableError || response.isTerribleFailure -> {
                    Log.e(
                        TAG,
                        "Failed to acknowledge for token $purchaseToken - code: ${bResult!!.responseCode}, message: ${bResult!!.debugMessage}"
                    )
                    break
                }
            }
        }
        throw Exception("Failed to acknowledge the purchase!")
    }

    companion object {
        private const val TAG = "BillingLifecycle"
        private const val MAX_RETRY_ATTEMPT = 3
        val skuMonth = "com.bluetoothdevices.finder.month"
        val skuYear = "com.bluetoothdevices.finder.year"
        private val LIST_OF_PRODUCTS = listOf(
            skuMonth,
            skuYear,
        )

        @Volatile
        private var INSTANCE: BillingClientLifecycle? = null

        fun getInstance(applicationContext: Context): BillingClientLifecycle =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: BillingClientLifecycle(applicationContext).also { INSTANCE = it }
            }
    }
}

@JvmInline
private value class BillingResponse(val code: Int) {
    val isOk: Boolean
        get() = code == BillingClient.BillingResponseCode.OK
    val canFailGracefully: Boolean
        get() = code == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED
    val isRecoverableError: Boolean
        get() = code in setOf(
            BillingClient.BillingResponseCode.ERROR,
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
        )
    val isNonrecoverableError: Boolean
        get() = code in setOf(
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
            BillingClient.BillingResponseCode.DEVELOPER_ERROR,
        )
    val isTerribleFailure: Boolean
        get() = code in setOf(
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE,
            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
            BillingClient.BillingResponseCode.ITEM_NOT_OWNED,
            BillingClient.BillingResponseCode.USER_CANCELED,
        )
}