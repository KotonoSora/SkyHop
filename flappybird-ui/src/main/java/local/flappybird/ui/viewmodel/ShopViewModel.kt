package local.flappybird.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import local.core.ui.audio.IAudioManager
import local.flappybird.ui.audio.SfxType
import local.core.billing.BillingRepository
import local.core.billing.BillingStatus
import local.core.billing.DomainProduct
import local.core.ui.data.CoinAccount
import local.core.ui.data.InventoryStore
import local.core.ui.data.SkinLibrary
import local.core.shop.coins.CoinPackIds
import local.core.shop.coins.CoinPacks
import local.core.shop.coins.CoinPackItem
import local.core.shop.coins.mapToCoinPack
import local.flappybird.engine.model.PowerUpType
import local.flappybird.shop.PowerUpShopItem
import local.flappybird.shop.PowerUps
import local.flappybird.shop.ShopItem
import local.flappybird.shop.SkinData
import local.flappybird.shop.SkinIds
import local.flappybird.shop.SkinShopItem
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
    private val coinAccount: CoinAccount,
    private val skins: SkinLibrary,
    private val inventory: InventoryStore,
    private val billingRepository: BillingRepository,
    private val audioManager: IAudioManager
) : ViewModel() {

    val coins: StateFlow<Int> = coinAccount.coinsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    val purchasedItems: StateFlow<Set<String>> = skins.purchasedItemsFlow.stateIn(
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
    val shieldCount: StateFlow<Int> = inventory.inventoryCountFlow(PowerUpType.SHIELD.id).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )
    val multiplierCount: StateFlow<Int> = inventory.inventoryCountFlow(PowerUpType.MULTIPLIER.id).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    init {
        viewModelScope.launch {
            skins.selectedSkinFlow.collect { skinId ->
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
        productList.filter { CoinPacks.getCoinAmount(it.productId) != null }
            .map { product -> mapToCoinPack(product) }
            .sortedBy {
                val index = allowedIds.indexOf(it.id)
                if (index >= 0) index else Int.MAX_VALUE
            }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val skinItems = purchasedItems.map { purchasedIds ->
        SkinData.getSkinItems(purchasedIds)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val powerUpItems = PowerUps.getPowerUpItems()

    private fun selectSkin(id: String) {
        viewModelScope.launch {
            skins.updateSelectedSkin(id)
        }
    }

    private fun buyItem(item: ShopItem) {
        viewModelScope.launch {
            if (!coinAccount.spendCoins(item.price)) return@launch
            audioManager.playSfx(SfxType.COLLECT.rawResId)
            // Exhaustive: a new ShopItem subtype fails compilation here instead
            // of silently granting nothing after charging coins.
            when (item) {
                is SkinShopItem -> skins.purchaseItem(item.id)
                is PowerUpShopItem -> inventory.addToInventory(item.inventoryId)
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
