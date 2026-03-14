package com.kotonosora.skyboundhopper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.kotonosora.skyboundhopper.analytics.CoinPackRevenueEvent
import com.kotonosora.skyboundhopper.analytics.RevenueAnalyticsLogger
import com.kotonosora.skyboundhopper.BuildConfig
import com.kotonosora.skyboundhopper.billing.BillingManager
import com.kotonosora.skyboundhopper.billing.BillingStatus
import com.kotonosora.skyboundhopper.data.SettingsRepository
import com.kotonosora.skyboundhopper.model.CoinPackItem
import com.kotonosora.skyboundhopper.model.ShopData
import com.kotonosora.skyboundhopper.model.ShopItem
import com.kotonosora.skyboundhopper.model.SkinIds
import com.kotonosora.skyboundhopper.remoteconfig.RemoteConfigManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class ShopViewModel(
    private val settingsRepository: SettingsRepository,
    private val billingManager: BillingManager,
    private val remoteConfigManager: RemoteConfigManager,
    private val isDebug: Boolean = BuildConfig.DEBUG
) : ViewModel() {

    val coins: StateFlow<Int> = settingsRepository.coinsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    val purchasedItems: StateFlow<Set<String>> = settingsRepository.purchasedItemsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), setOf(SkinIds.SKIN_DEFAULT_ID)
    )

    private val _selectedSkinId = MutableStateFlow(SkinIds.SKIN_DEFAULT_ID)
    val selectedSkinId = _selectedSkinId.asStateFlow()

    private val _purchaseLaunchRequests = MutableSharedFlow<ProductDetails>(extraBufferCapacity = 1)
    val purchaseLaunchRequests: SharedFlow<ProductDetails> = _purchaseLaunchRequests.asSharedFlow()

    val products: StateFlow<List<ProductDetails>> = billingManager.products

    val billingStatus: StateFlow<BillingStatus> = billingManager.status.map { status ->
        if (isDebug) {
            when (status) {
                BillingStatus.EMPTY, BillingStatus.ERROR, BillingStatus.IDLE -> BillingStatus.CONNECTED
                else -> status
            }
        } else {
            status
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        if (isDebug) BillingStatus.CONNECTED else BillingStatus.IDLE
    )

    // Expose power-up counts for the shop UI
    val shieldCount: StateFlow<Int> = settingsRepository.shieldCountFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )
    val multiplierCount: StateFlow<Int> = settingsRepository.multiplierCountFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    private val _isFetchingConfig = MutableStateFlow(false)
    val isFetchingConfig = _isFetchingConfig.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.selectedSkinFlow.collect { skinId ->
                _selectedSkinId.value = skinId
            }
        }
    }

    fun fetchRemoteConfig() {
        viewModelScope.launch {
            _isFetchingConfig.value = true
            remoteConfigManager.fetchAndActivate()
            _isFetchingConfig.value = false
        }
    }

    val coinPacks: StateFlow<List<CoinPackItem>> = combine(
        products,
        remoteConfigManager.coinProductIds
    ) { productList, allowedIds ->
        val packs = productList.filter { it.productId.startsWith("coins_") }
            .map { product -> ShopData.mapToCoinPack(product) }
            .sortedBy { allowedIds.indexOf(it.id) }

        if (isDebug && packs.isEmpty()) {
            ShopData.getDebugCoinPacks(allowedIds)
        } else {
            packs
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        if (isDebug) ShopData.getDebugCoinPacks(remoteConfigManager.coinProductIds.value) else emptyList()
    )

    val skinItems = purchasedItems.map { purchasedIds ->
        ShopData.getSkinItems(purchasedIds)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val powerUpItems = ShopData.getPowerUpItems()

    fun selectSkin(id: String) {
        viewModelScope.launch {
            settingsRepository.updateSelectedSkin(id)
        }
    }

    fun buyItem(item: ShopItem) {
        Firebase.analytics.logEvent("buy_action") {
            param(FirebaseAnalytics.Param.ITEM_ID, item.id)
            param(FirebaseAnalytics.Param.ITEM_NAME, item.name)
            param(FirebaseAnalytics.Param.PRICE, item.price.toLong())
            param(FirebaseAnalytics.Param.CURRENCY, "virtual_coins")
        }
        viewModelScope.launch {
            if (settingsRepository.spendCoins(item.price)) {
                if (item.id.startsWith("skin")) {
                    settingsRepository.purchaseItem(item.id)
                } else if (item.id.startsWith("powerup_")) {
                    val type = item.id.removePrefix("powerup_")
                    settingsRepository.addPowerUp(type)
                }
            }
        }
    }

    fun buyCoinPack(item: CoinPackItem) {
        Firebase.analytics.logEvent("buy_action") {
            param(FirebaseAnalytics.Param.ITEM_ID, item.id)
        }
        if (item.id.startsWith("mock_")) {
            val amountStr = item.id.removePrefix("mock_")
            val amount = amountStr.toIntOrNull() ?: 0
            if (isDebug) {
                logDebugMockRevenue(item = item, coinAmount = amount)
            }
            addMockCoins(amount)
        } else {
            item.productDetails?.let { details ->
                _purchaseLaunchRequests.tryEmit(details)
            }
        }
    }

    private fun logDebugMockRevenue(item: CoinPackItem, coinAmount: Int) {
        val normalizedPrice = item.price.replace(',', '.')
        val parsedValue = normalizedPrice
            .filter { it.isDigit() || it == '.' }
            .toDoubleOrNull() ?: 0.0
        val transactionId = "debug_${item.id}_${UUID.randomUUID()}"

        RevenueAnalyticsLogger.logCoinPackRevenue(
            CoinPackRevenueEvent(
                eventName = "debug_mock_purchase",
                transactionId = transactionId,
                itemId = item.id,
                itemName = item.name,
                value = parsedValue,
                currency = "USD",
                paymentType = "debug_mock",
                purchaseSource = "debug_mock",
                coinAmount = coinAmount.toLong()
            )
        )
    }

    private fun addMockCoins(amount: Int) {
        viewModelScope.launch {
            settingsRepository.addCoins(amount)
        }
    }

    fun retryConnection() {
        billingManager.startConnection()
    }

}

class ShopViewModelFactory(
    private val settingsRepository: SettingsRepository,
    private val billingManager: BillingManager,
    private val remoteConfigManager: RemoteConfigManager,
    private val isDebug: Boolean = BuildConfig.DEBUG
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopViewModel::class.java)) {
            return ShopViewModel(settingsRepository, billingManager, remoteConfigManager, isDebug) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}