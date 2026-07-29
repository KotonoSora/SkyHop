package com.jn.flagfang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jn.flagfang.BuildConfig
import com.jn.flagfang.audio.AudioManager
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


class ShopViewModel(
    private val settingsRepository: SettingsRepository,
    private val billingRepository: BillingRepository,
    private val audioManager: AudioManager,
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

    private val _purchaseLaunchRequests = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val purchaseLaunchRequests: SharedFlow<String> = _purchaseLaunchRequests.asSharedFlow()

    val products: StateFlow<List<DomainProduct>> = billingRepository.products.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val billingStatus: StateFlow<BillingStatus> = billingRepository.status.map { status ->
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

    val coinPacks: StateFlow<List<CoinPackItem>> = products.map { productList ->
        val allowedIds = CoinPackIds.ALL
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
        if (isDebug) ShopData.getDebugCoinPacks(CoinPackIds.ALL) else emptyList()
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

    fun buyCoinPack(item: CoinPackItem) {
        if (item.id.startsWith("mock_")) {
            val amountStr = item.id.removePrefix("mock_")
            val amount = amountStr.toIntOrNull() ?: 0
            addMockCoins(amount)
        } else {
            _purchaseLaunchRequests.tryEmit(item.id)
        }
    }

    private fun addMockCoins(amount: Int) {
        viewModelScope.launch {
            settingsRepository.addCoins(amount)
            audioManager.playSfx(SfxType.COLLECT)
        }
    }

    fun retryConnection() {
        billingRepository.startConnection()
    }

}
