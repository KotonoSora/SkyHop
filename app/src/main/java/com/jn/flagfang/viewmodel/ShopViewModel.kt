package com.jn.flagfang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jn.flagfang.audio.IAudioManager
import com.jn.flagfang.audio.SfxType
import com.jn.flagfang.domain.repository.BillingRepository
import com.jn.flagfang.domain.repository.BillingStatus
import com.jn.flagfang.domain.repository.DomainProduct
import com.jn.flagfang.domain.repository.SettingsRepository
import com.jn.flagfang.feature.shop.CoinPackIds
import com.jn.flagfang.feature.shop.CoinPackItem
import com.jn.flagfang.feature.shop.ShopData
import com.jn.flagfang.feature.shop.ShopItem
import com.jn.flagfang.feature.shop.SkinIds
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

sealed class ShopIntent {
    data class SelectSkin(val id: String) : ShopIntent()
    data class BuyItem(val item: ShopItem) : ShopIntent()
    data class BuyCoinPack(val item: CoinPackItem) : ShopIntent()
    object RetryConnection : ShopIntent()
}

class ShopViewModel(
    private val settingsRepository: SettingsRepository,
    private val billingRepository: BillingRepository,
    private val audioManager: IAudioManager
) : ViewModel() {

    val coins: StateFlow<Int> = settingsRepository.coinsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    val purchasedItems: StateFlow<Set<String>> = settingsRepository.purchasedItemsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), setOf(SkinIds.SKIN_DEFAULT_ID)
    )

    private val _selectedSkinId = MutableStateFlow(SkinIds.SKIN_DEFAULT_ID)
    val selectedSkinId = _selectedSkinId.asStateFlow()

    private val _purchaseLaunchRequests = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val purchaseLaunchRequests: SharedFlow<String> = _purchaseLaunchRequests.asSharedFlow()

    val products: StateFlow<List<DomainProduct>> = billingRepository.products.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val billingStatus: StateFlow<BillingStatus> = billingRepository.status.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        BillingStatus.IDLE
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

    fun onIntent(intent: ShopIntent) {
        when (intent) {
            is ShopIntent.SelectSkin -> selectSkin(intent.id)
            is ShopIntent.BuyItem -> buyItem(intent.item)
            is ShopIntent.BuyCoinPack -> buyCoinPack(intent.item)
            ShopIntent.RetryConnection -> retryConnection()
        }
    }

    val coinPacks: StateFlow<List<CoinPackItem>> = products.map { productList ->
        val allowedIds = CoinPackIds.ALL
        productList.filter { it.productId.startsWith("coins_") }
            .map { product -> ShopData.mapToCoinPack(product) }
            .sortedBy { allowedIds.indexOf(it.id) }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val skinItems = purchasedItems.map { purchasedIds ->
        ShopData.getSkinItems(purchasedIds)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val powerUpItems = ShopData.getPowerUpItems()

    private fun selectSkin(id: String) {
        viewModelScope.launch {
            settingsRepository.updateSelectedSkin(id)
        }
    }

    private fun buyItem(item: ShopItem) {
        viewModelScope.launch {
            if (settingsRepository.spendCoins(item.price)) {
                audioManager.playSfx(SfxType.COLLECT)
                if (item.id.startsWith("skin")) {
                    settingsRepository.purchaseItem(item.id)
                } else if (item.id.startsWith("powerup_")) {
                    val type = item.id.removePrefix("powerup_")
                    settingsRepository.addPowerUp(type)
                }
            }
        }
    }

    private fun buyCoinPack(item: CoinPackItem) {
        _purchaseLaunchRequests.tryEmit(item.id)
    }

    private fun retryConnection() {
        billingRepository.startConnection()
    }
}
