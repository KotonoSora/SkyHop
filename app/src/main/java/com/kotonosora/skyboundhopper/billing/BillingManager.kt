package com.kotonosora.skyboundhopper.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.kotonosora.skyboundhopper.analytics.CoinPackRevenueEvent
import com.kotonosora.skyboundhopper.analytics.RevenueAnalyticsLogger
import com.google.firebase.analytics.FirebaseAnalytics
import com.kotonosora.skyboundhopper.data.SettingsRepository
import com.kotonosora.skyboundhopper.model.ShopData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Collections

enum class BillingStatus {
    IDLE, CONNECTING, CONNECTED, ERROR, EMPTY
}

class BillingManager(
    context: Context,
    private val settingsRepository: SettingsRepository
) {

    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _status = MutableStateFlow(BillingStatus.IDLE)
    val status = _status.asStateFlow()

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
    }

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    private val _products = MutableStateFlow<List<ProductDetails>>(emptyList())
    val products = _products.asStateFlow()
    private val trackedPurchaseKeys = Collections.synchronizedSet(mutableSetOf<String>())

    private val productIds = ShopData.coinProductIds

    init {
        startConnection()
    }

    fun startConnection() {
        if (_status.value == BillingStatus.CONNECTING || _status.value == BillingStatus.CONNECTED) return
        
        _status.value = BillingStatus.CONNECTING
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _status.value = BillingStatus.CONNECTED
                    queryProducts()
                    queryPurchases()
                } else {
                    _status.value = BillingStatus.ERROR
                }
            }

            override fun onBillingServiceDisconnected() {
                _status.value = BillingStatus.IDLE
            }
        })
    }

    private fun queryProducts() {
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                productIds.map {
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(it)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                }
            )
            .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val list = productDetailsResult.productDetailsList
                _products.value = list
                if (list.isEmpty()) {
                    _status.value = BillingStatus.EMPTY
                }
            } else {
                _status.value = BillingStatus.ERROR
            }
        }
    }

    private fun queryPurchases() {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases.forEach { purchase ->
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        handlePurchase(purchase)
                    }
                }
            }
        }
    }

    fun launchPurchaseFlow(activity: Activity, productDetails: ProductDetails) {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            val isCoinPack = purchase.products.any { it.startsWith("coins_") }
            
            if (isCoinPack) {
                consumeCoinPack(purchase)
            } else if (!purchase.isAcknowledged) {
                acknowledgePurchaseIfNeeded(purchase)
            }
        }
    }

    private fun acknowledgePurchaseIfNeeded(purchase: Purchase) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                queryPurchases()
            }
        }
    }

    private fun consumeCoinPack(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
            
        billingClient.consumeAsync(consumeParams) { billingResult, _ ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                managerScope.launch {
                    purchase.products.forEach { productId ->
                        logRevenueIfNeeded(purchase, productId)
                        ShopData.getCoinAmount(productId)?.let { coinAmount ->
                            settingsRepository.addCoins(coinAmount)
                        }
                    }
                }
                queryPurchases()
            }
        }
    }

    private fun logRevenueIfNeeded(purchase: Purchase, productId: String) {
        val productDetails = _products.value.firstOrNull { it.productId == productId }
        val oneTimeDetails = productDetails?.oneTimePurchaseOfferDetails ?: return
        val priceMicros = oneTimeDetails.priceAmountMicros
        if (priceMicros <= 0L) return

        val purchaseKey = "${purchase.purchaseToken}:$productId"
        if (!trackedPurchaseKeys.add(purchaseKey)) return

        val value = priceMicros / 1_000_000.0
        val currency = oneTimeDetails.priceCurrencyCode
        val transactionId = purchase.orderId ?: purchase.purchaseToken

        RevenueAnalyticsLogger.logCoinPackRevenue(
            CoinPackRevenueEvent(
                eventName = FirebaseAnalytics.Event.PURCHASE,
                transactionId = transactionId,
                itemId = productId,
                itemName = productDetails.name,
                value = value,
                currency = currency,
                paymentType = "google_play",
                purchaseSource = "in_app_billing",
                purchaseState = purchase.purchaseState.toString()
            )
        )
    }

    fun release() {
        billingClient.endConnection()
        managerScope.cancel()
    }
}