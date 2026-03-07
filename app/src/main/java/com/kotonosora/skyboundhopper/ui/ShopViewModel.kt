package com.kotonosora.skyboundhopper.ui

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.kotonosora.skyboundhopper.R
import com.kotonosora.skyboundhopper.billing.BillingManager
import com.kotonosora.skyboundhopper.billing.BillingStatus
import com.kotonosora.skyboundhopper.data.SettingsRepository
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
            .map { product ->
                CoinPackItem(
                    id = product.productId,
                    name = when (product.productId) {
                        "coins_100" -> "100 Coins"
                        "coins_500" -> "500 Coins"
                        "coins_1000" -> "1000 Coins"
                        else -> product.name
                    },
                    price = product.oneTimePurchaseOfferDetails?.formattedPrice ?: "---",
                    imageRes = when (product.productId) {
                        "coins_100" -> R.drawable.img_coins_100
                        "coins_500" -> R.drawable.img_coins_500
                        "coins_1000" -> R.drawable.img_coins_1000
                        else -> R.drawable.placeholder
                    },
                    productDetails = product
                )
            }.sortedBy { it.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val skinItems = purchasedItems.map { purchasedIds ->
        listOf(
            ShopItem(
                id = "default",
                name = "Blue Bird",
                price = 0,
                imageRes = R.drawable.img_bird_hero,
                isUnlocked = true
            ),
            ShopItem(
                id = "skin_space",
                name = "Space Voyager",
                price = 300,
                imageRes = R.drawable.img_skin_space_voyager,
                isUnlocked = purchasedIds.contains("skin_space")
            ),
            ShopItem(
                id = "skin_golden",
                name = "Golden Phoenix",
                price = 500,
                imageRes = R.drawable.img_skin_golden_phoenix,
                isUnlocked = purchasedIds.contains("skin_golden")
            ),
            ShopItem(
                id = "skin_steampunk",
                name = "Steam-Powered Flyer",
                price = 400,
                imageRes = R.drawable.img_skin_steampunk_flyer,
                isUnlocked = purchasedIds.contains("skin_steampunk")
            )
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val powerUpItems = listOf(
        ShopItem(
            id = "powerup_shield",
            name = "Energy Shield",
            description = "Invincible for 10s",
            price = 150,
            imageRes = R.drawable.img_powerup_shield_icon,
            isUnlocked = false
        ),
        ShopItem(
            id = "powerup_multiplier",
            name = "Score Booster",
            description = "Double points for one run",
            price = 200,
            imageRes = R.drawable.img_powerup_multiplier_icon,
            isUnlocked = false
        )
    )

    fun selectSkin(id: String) {
        viewModelScope.launch {
            settingsRepository.updateSelectedSkin(id)
        }
    }

    fun buyItem(item: ShopItem): Boolean {
        var success = false
        viewModelScope.launch {
            if (settingsRepository.spendCoins(item.price)) {
                if (item.id.startsWith("skin")) {
                    settingsRepository.purchaseItem(item.id)
                }
                success = true
            }
        }
        return success
    }

    fun buyCoinPack(activity: Activity, productDetails: ProductDetails) {
        billingManager.launchPurchaseFlow(activity, productDetails)
    }

    fun retryConnection() {
        billingManager.startConnection()
    }
}

data class ShopItem(
    val id: String,
    val name: String,
    val description: String = "",
    val price: Int,
    val imageRes: Int,
    val isUnlocked: Boolean = false
)

data class CoinPackItem(
    val id: String,
    val name: String,
    val price: String,
    val imageRes: Int,
    val productDetails: ProductDetails
)
