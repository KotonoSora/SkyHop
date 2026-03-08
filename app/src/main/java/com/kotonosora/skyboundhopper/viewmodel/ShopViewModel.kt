package com.kotonosora.skyboundhopper.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.kotonosora.skyboundhopper.billing.BillingManager
import com.kotonosora.skyboundhopper.billing.BillingStatus
import com.kotonosora.skyboundhopper.data.SettingsRepository
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
    val billingStatus: StateFlow<BillingStatus> = billingManager.status

    init {
        viewModelScope.launch {
            settingsRepository.selectedSkinFlow.collect { skinId ->
                _selectedSkinId.value = skinId
            }
        }
    }

    val coinPacks = products.map { productList ->
        productList.filter { it.productId.startsWith("coins_") }
            .map { product -> ShopData.mapToCoinPack(product) }
            .sortedBy { it.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    fun buyCoinPack(activity: Activity, productDetails: ProductDetails) {
        billingManager.launchPurchaseFlow(activity, productDetails)
    }

    fun retryConnection() {
        billingManager.startConnection()
    }
}
