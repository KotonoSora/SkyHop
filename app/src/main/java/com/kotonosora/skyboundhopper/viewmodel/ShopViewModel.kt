package com.kotonosora.skyboundhopper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.kotonosora.skyboundhopper.BuildConfig
import com.kotonosora.skyboundhopper.billing.BillingManager
import com.kotonosora.skyboundhopper.billing.BillingStatus
import com.kotonosora.skyboundhopper.data.SettingsRepository
import com.kotonosora.skyboundhopper.model.CoinPackItem
import com.kotonosora.skyboundhopper.model.ShopData
import com.kotonosora.skyboundhopper.model.ShopItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShopViewModel(
    private val settingsRepository: SettingsRepository,
    private val billingManager: BillingManager,
    private val isDebug: Boolean = BuildConfig.DEBUG
) : ViewModel() {

    val coins: StateFlow<Int> = settingsRepository.coinsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    val purchasedItems: StateFlow<Set<String>> = settingsRepository.purchasedItemsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), setOf("default")
    )

    private val _selectedSkinId = MutableStateFlow("default")
    val selectedSkinId = _selectedSkinId.asStateFlow()

    private val _purchaseLaunchRequests = MutableSharedFlow<ProductDetails>(extraBufferCapacity = 1)
    val purchaseLaunchRequests: SharedFlow<ProductDetails> = _purchaseLaunchRequests.asSharedFlow()

    val products: StateFlow<List<ProductDetails>> = billingManager.products

    // Force CONNECTED in debug mode so mock packs stay visible during local testing.
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

    init {
        viewModelScope.launch {
            settingsRepository.selectedSkinFlow.collect { skinId ->
                _selectedSkinId.value = skinId
            }
        }
    }

    // Show mock packs in debug mode if no real products are found.
    val coinPacks: StateFlow<List<CoinPackItem>> = products.map { productList ->
        val packs = productList.filter { it.productId.startsWith("coins_") }
            .map { product -> ShopData.mapToCoinPack(product) }
            .sortedBy { it.id }

        if (isDebug && packs.isEmpty()) {
            getMockPacks()
        } else {
            packs
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        if (isDebug) getMockPacks() else emptyList()
    )

    private fun getMockPacks() = ShopData.getDebugCoinPacks()

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
        if (item.id.startsWith("mock_")) {
            val amountStr = item.id.removePrefix("mock_")
            val amount = amountStr.toIntOrNull() ?: 0
            addMockCoins(amount)
        } else {
            item.productDetails?.let { details ->
                _purchaseLaunchRequests.tryEmit(details)
            }
        }
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
    private val isDebug: Boolean = BuildConfig.DEBUG
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopViewModel::class.java)) {
            return ShopViewModel(settingsRepository, billingManager, isDebug) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}