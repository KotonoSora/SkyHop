package com.kotonosora.skyboundhopper.viewmodel

import android.app.Activity
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.kotonosora.skyboundhopper.BuildConfig
import com.kotonosora.skyboundhopper.R
import com.kotonosora.skyboundhopper.billing.BillingManager
import com.kotonosora.skyboundhopper.billing.BillingStatus
import com.kotonosora.skyboundhopper.data.SettingsRepository
import com.kotonosora.skyboundhopper.model.CoinPackItem
import com.kotonosora.skyboundhopper.model.ShopData
import com.kotonosora.skyboundhopper.model.ShopItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ShopViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsRepository = SettingsRepository(application)
    private val billingManager = BillingManager(application, viewModelScope)

    val coins: StateFlow<Int> = settingsRepository.coinsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    val purchasedItems: StateFlow<Set<String>> = settingsRepository.purchasedItemsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), setOf("default")
    )

    private val _selectedSkinId = MutableStateFlow("default")
    val selectedSkinId = _selectedSkinId.asStateFlow()

    val products: StateFlow<List<ProductDetails>> = billingManager.products
    
    // Always force CONNECTED in debug mode to ensure mock shop is visible
    val billingStatus: StateFlow<BillingStatus> = billingManager.status.map { status ->
        if (BuildConfig.DEBUG) {
            when (status) {
                BillingStatus.EMPTY, BillingStatus.ERROR, BillingStatus.IDLE -> BillingStatus.CONNECTED
                else -> status
            }
        } else {
            status
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), if (BuildConfig.DEBUG) BillingStatus.CONNECTED else BillingStatus.IDLE)

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

    // Show mock packs in debug mode if no real products are found
    val coinPacks: StateFlow<List<CoinPackItem>> = products.map { productList ->
        val packs = productList.filter { it.productId.startsWith("coins_") }
            .map { product -> ShopData.mapToCoinPack(product) }
            .sortedBy { it.id }
        
        if (BuildConfig.DEBUG && packs.isEmpty()) {
            getMockPacks()
        } else {
            packs
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), if (BuildConfig.DEBUG) getMockPacks() else emptyList())

    private fun getMockPacks() = listOf(
        CoinPackItem("mock_100", "100 COINS", "$0.99", R.drawable.img_coins_100, null),
        CoinPackItem("mock_500", "500 COINS", "$3.99", R.drawable.img_coins_500, null),
        CoinPackItem("mock_1000", "1000 COINS", "$6.99", R.drawable.img_coins_1000, null)
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
                if (item.id.startsWith("skin")) {
                    settingsRepository.purchaseItem(item.id)
                } else if (item.id.startsWith("powerup_")) {
                    val type = item.id.removePrefix("powerup_")
                    settingsRepository.addPowerUp(type)
                }
            }
        }
    }

    fun buyCoinPack(activity: Activity, item: CoinPackItem) {
        if (item.id.startsWith("mock_")) {
            val amountStr = item.id.removePrefix("mock_")
            val amount = amountStr.toIntOrNull() ?: 0
            addMockCoins(amount)
            Toast.makeText(getApplication(), "Purchased $amount coins (Mock)", Toast.LENGTH_SHORT).show()
        } else {
            item.productDetails?.let { details ->
                billingManager.launchPurchaseFlow(activity, details)
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