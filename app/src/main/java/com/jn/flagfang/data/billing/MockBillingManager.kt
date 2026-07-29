package com.jn.flagfang.data.billing

import android.app.Activity
import com.jn.flagfang.domain.repository.BillingRepository
import com.jn.flagfang.domain.repository.BillingStatus
import com.jn.flagfang.domain.repository.DomainProduct
import com.jn.flagfang.domain.repository.SettingsRepository
import com.jn.flagfang.feature.shop.ShopData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Mock implementation of [BillingRepository] for testing and debug builds.
 */
class MockBillingManager(
    private val settingsRepository: SettingsRepository
) : BillingRepository {

    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _status = MutableStateFlow(BillingStatus.IDLE)
    override val status = _status.asStateFlow()

    private val _products = MutableStateFlow<List<DomainProduct>>(emptyList())
    override val products = _products.asStateFlow()

    override fun startConnection() {
        managerScope.launch {
            _status.value = BillingStatus.CONNECTING
            delay(500) // Simulate network delay
            _status.value = BillingStatus.CONNECTED
            loadMockProducts()
        }
    }

    private fun loadMockProducts() {
        _products.value = ShopData.mockProducts
    }

    override fun launchPurchaseFlow(activity: Activity, productId: String) {
        managerScope.launch {
            // Simulate successful purchase
            val amount = ShopData.getCoinAmount(productId) ?: 0
            settingsRepository.addCoins(amount)
        }
    }
}
