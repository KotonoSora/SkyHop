package com.kotonosora.skyboundhopper.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.kotonosora.skyboundhopper.data.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class BillingStatus {
    IDLE, CONNECTING, CONNECTED, ERROR, EMPTY
}

class BillingManager(private val context: Context, private val externalScope: CoroutineScope) {

    private val settingsRepository = SettingsRepository(context)

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

    private val _purchasedProductIds = MutableStateFlow<Set<String>>(emptySet())
    val purchasedProductIds = _purchasedProductIds.asStateFlow()

    private val productIds = listOf(
        "coins_100", "coins_500", "coins_1000"
    )

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
                val list = productDetailsResult.productDetailsList ?: emptyList()
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
                val ids = purchases.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                    .flatMap { it.products }
                    .toSet()
                _purchasedProductIds.value = ids
                
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
                externalScope.launch {
                    purchase.products.forEach { productId ->
                        when (productId) {
                            "coins_100" -> settingsRepository.addCoins(100)
                            "coins_500" -> settingsRepository.addCoins(500)
                            "coins_1000" -> settingsRepository.addCoins(1000)
                        }
                    }
                }
                queryPurchases()
            }
        }
    }
}