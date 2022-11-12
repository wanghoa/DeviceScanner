package com.devicewifitracker.android.util

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils

class SubscribeManager {
    val TAG = "SubscribeManager"
    var isSubscribed = false

    private lateinit var billingClient: BillingClient

    companion object {
        val instance: SubscribeManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            SubscribeManager()
        }
    }

    private constructor() {
    }

    private lateinit var billingClientLifecycle: BillingClientLifecycle
    val skuMonth = "com.devicewifitracker.android.month"
    val skuYear = "com.devicewifitracker.android.year"
    private val skuList = listOf(skuMonth, skuYear)
    private var billingListener: BillingListener? = null
    private var skuDetailList: List<ProductDetails>? = null
    var productsWithUserCanceled = mutableListOf<String>()
    fun initBillingLifecycle(lifecycle: BillingClientLifecycle) {
        billingClientLifecycle = lifecycle
    }


    fun setBillingListener(billingListener: BillingListener?) {
        this.billingListener = billingListener
    }

    fun initBilling(context: Context) {
        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()  //支持待处理的交易
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(p0: BillingResult) {
                LogUtils.file(TAG, "onBillingSetupFinished: code= ${p0.responseCode}")
                LogUtils.file(TAG, "onBillingSetupFinished: message = ${p0.debugMessage}")
                if (p0.responseCode == BillingClient.BillingResponseCode.OK) {//连接成功 可以进行查询商品等操作
                    querySkuDetails()
                } else {
                    disConnect("Error trying to connect to the Google play store:startConnection failed")
                }
            }

            override fun onBillingServiceDisconnected() {
                LogUtils.file(TAG, "onBillingServiceDisconnected: ")
                disConnect("Error trying to connect to the Google play store:service disconnected")
            }
        })
    }

    private fun querySkuDetails() {
        var list = mutableListOf<QueryProductDetailsParams.Product>()
        list.add(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(skuMonth)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )
        list.add(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(skuYear)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(list)
                .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult,
                                                                            productDetailsList ->
            LogUtils.file(TAG, "queryProductDetailsAsync==${billingResult.responseCode}==${billingResult.debugMessage}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (productDetailsList != null && productDetailsList.size > 0) {
                    skuDetailList = productDetailsList
                    queryPurchases()
                } else {
                    ToastUtils.showShort("no product")
                }
            } else {
                ToastUtils.showShort("query product failed")
            }
        }
    }

    fun isSubscribe(): Boolean {
        return isSubscribed
//        return billingClientLifecycle?.subscribeStatus?.value?:false
    }

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            var code = billingResult.responseCode
            LogUtils.file(TAG, "PurchasesUpdatedListener==$code==${billingResult.debugMessage}")
            //购买成功
            if (code == BillingClient.BillingResponseCode.OK) {
                LogUtils.file(TAG, "purchasesUpdatedListener==OK")
                if (purchases == null) {
                    processPurchases(null, false)
                } else {
                    processPurchases(purchases, false)
                }
                // 取消购买
            } else if (code == BillingClient.BillingResponseCode.USER_CANCELED) {
                LogUtils.file(TAG, "purchasesUpdatedListener==USER_CANCELED")
                if (!purchases.isNullOrEmpty()) {
                    for (purchase in purchases) {
                        if (!purchase.products.isNullOrEmpty()) {
                            if (purchase.products.contains(BillingClientLifecycle.skuMonth)) {
                                if (!productsWithUserCanceled.contains(BillingClientLifecycle.skuMonth)) {
                                    productsWithUserCanceled.contains(BillingClientLifecycle.skuMonth)
                                }
                            } else if (purchase.products.contains(BillingClientLifecycle.skuYear)) {
                                if (!productsWithUserCanceled.contains(BillingClientLifecycle.skuYear)) {
                                    productsWithUserCanceled.contains(BillingClientLifecycle.skuYear)
                                }
                            }
                        }
                    }
                }
            }
        }


    private fun disConnect(msg: String) {
        LogUtils.file(TAG, "disConnect==$msg")
//        ToastUtils.showShort("Google Service Disconnected")
    }


    fun restore(activity: Activity,listener: BillingLaunchCallback?) {
        LogUtils.file(TAG, "restore==${productsWithUserCanceled}")
//        ToastUtils.showLong("${productsWithUserCanceled}")
        if (productsWithUserCanceled.contains(skuYear)) {
            subscribe(activity, skuYear, listener)
        } else if (productsWithUserCanceled.contains(skuMonth)) {
            subscribe(activity, skuMonth, listener)
        }
    }

    fun queryPurchases() {
        LogUtils.file(TAG, "queryPurchases")
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
        billingClient.queryPurchasesAsync(
            params.build()
        ) { billingResult, purchasesList ->
            LogUtils.file(TAG, "queryPurchases==${billingResult.responseCode}==${billingResult.debugMessage}")
            processPurchases(purchasesList, true) }
    }

//    fun subscribe(activity: Activity, sku: String) : Int{
//        return billingClientLifecycle?.launchBillingFlow(activity,sku)
//    }

    fun subscribe(activity: Activity, sku: String, listener: BillingLaunchCallback?) {
        LogUtils.file(TAG, "subscribe sku=$sku")
        if (isSubscribed) {
            billingListener?.isSubscribe(true)
            listener?.callback()
        } else {
            var currentSku: ProductDetails? = null
            if (skuDetailList != null && skuDetailList!!.isNotEmpty()) {
                for (skuDetail in skuDetailList!!) {
                    if (skuDetail.productId == sku) {
                        currentSku = skuDetail
                        break
                    }
                }
            }
            if (currentSku != null) {
                val offerToken = currentSku.subscriptionOfferDetails?.get(0)?.offerToken?:""
                val productDetailsParamsList = listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(currentSku)
                        .setOfferToken(offerToken)
                        .build()
                )

                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()
                val responseCode: Int =
                    billingClient.launchBillingFlow(activity, billingFlowParams).responseCode
                LogUtils.file(TAG, "launchBillingFlow==$responseCode")
                if (responseCode == BillingClient.BillingResponseCode.OK) {
                    LogUtils.file(
                        TAG,
                        "launchBillingFlow success"
                    )
                    ToastUtils.showShort("Success")
                    billingListener?.launchBillingResponse(true)
                    listener?.callback()
                } else {
                    listener?.callback()
                    billingListener?.launchBillingResponse(false)
                    subscribeError(
                        4,
                        "Error trying to connect to the Google play store:launch failed,Please try again"
                    )
                    ToastUtils.showShort("Please try again")
                }
            } else {
                LogUtils.file(TAG, "no list")
                ToastUtils.showShort("no list")
                listener?.callback()
                querySkuDetails()
                billingListener?.launchBillingResponse(false)
                subscribeError(
                    5,
                    "Error trying to connect to the Google play store:no list,Please try again"
                )
            }
        }
    }

    private fun subscribeError(code: Int, error: String) {
        LogUtils.file(TAG, "subscribeError = $code  $error")
        ToastUtils.showShort("Subscribe Error:$code")
        billingListener?.subscribeError(code, error)
    }

    private fun processPurchases(purchasesList: List<Purchase>?, fromHistory: Boolean) {
        LogUtils.file(TAG, "processPurchases==$fromHistory")
        if (purchasesList != null) {
            LogUtils.file(
                TAG,
                "processPurchases: " + purchasesList.size + " purchase(s)"
            )
        } else {
            LogUtils.file(
                TAG,
                "processPurchases: with no purchases"
            )
        }
        if (purchasesList != null) {
            for (purchase in purchasesList) {
                val purchaseState = purchase.purchaseState
                if (purchaseState == Purchase.PurchaseState.PURCHASED) {
                    if (!purchase.products.isNullOrEmpty()) {
                        if (purchase.products.contains(BillingClientLifecycle.skuMonth) || purchase.products.contains(
                                BillingClientLifecycle.skuYear
                            )
                        ) {
                            productsWithUserCanceled.remove(skuMonth)
                            productsWithUserCanceled.remove(skuYear)
                            isSubscribed = true
                            if (!purchase.isAcknowledged) {
                                val purchaseToken = purchase.purchaseToken
                                LogUtils.file(
                                    TAG,
                                    "knowledge purchase with sku: ${purchase.products}, token: $purchaseToken"
                                )
                                billingClient.acknowledgePurchase(
                                    AcknowledgePurchaseParams.newBuilder()
                                        .setPurchaseToken(purchaseToken)
                                        .build()
                                ) { billingResult ->
                                    LogUtils.file(TAG, "acknowledgePurchase==${billingResult.responseCode}==${billingResult.debugMessage}")
                                    if (billingResult.responseCode
                                        == BillingClient.BillingResponseCode.OK
                                    ) {

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (fromHistory) {
//            purchaseQueryFlag = 3
        }
        if (isSubscribed) {
            if (fromHistory) {
                billingListener?.isSubscribe(true)
            } else {
                billingListener?.subscribeSuccess()
            }
        }
    }


}