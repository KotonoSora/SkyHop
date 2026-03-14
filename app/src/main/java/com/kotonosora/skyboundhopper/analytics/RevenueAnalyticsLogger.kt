package com.kotonosora.skyboundhopper.analytics

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent

data class CoinPackRevenueEvent(
    val eventName: String,
    val transactionId: String,
    val itemId: String,
    val itemName: String,
    val itemCategory: String = "coin_pack",
    val value: Double,
    val currency: String,
    val quantity: Long = 1L,
    val paymentType: String,
    val purchaseSource: String,
    val purchaseState: String? = null,
    val coinAmount: Long? = null
)

object RevenueAnalyticsLogger {
    fun logCoinPackRevenue(event: CoinPackRevenueEvent) {
        Firebase.analytics.logEvent(event.eventName) {
            param(FirebaseAnalytics.Param.TRANSACTION_ID, event.transactionId)
            param(FirebaseAnalytics.Param.ITEM_ID, event.itemId)
            param(FirebaseAnalytics.Param.ITEM_NAME, event.itemName)
            param(FirebaseAnalytics.Param.ITEM_CATEGORY, event.itemCategory)
            param(FirebaseAnalytics.Param.PRICE, event.value)
            param(FirebaseAnalytics.Param.VALUE, event.value)
            param(FirebaseAnalytics.Param.CURRENCY, event.currency)
            param(FirebaseAnalytics.Param.QUANTITY, event.quantity)
            param(FirebaseAnalytics.Param.PAYMENT_TYPE, event.paymentType)
            param("purchase_source", event.purchaseSource)

            event.purchaseState?.let { state ->
                param("purchase_state", state)
            }
            event.coinAmount?.let { amount ->
                param("coin_amount", amount)
            }
        }
    }
}