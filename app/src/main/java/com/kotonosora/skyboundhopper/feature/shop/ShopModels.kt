package com.kotonosora.skyboundhopper.model

import com.android.billingclient.api.ProductDetails
import com.kotonosora.skyboundhopper.R
import java.util.Locale

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
    val productDetails: ProductDetails?
)

data class CoinPackDefinition(
    val productId: String,
    val coinAmount: Int,
    val debugPrice: String
)

object ShopData {
    private val coinPackDefinitions = listOf(
        CoinPackDefinition(productId = CoinPackIds.COINS_100, coinAmount = 100, debugPrice = "$0.29~"),
        CoinPackDefinition(productId = CoinPackIds.COINS_500, coinAmount = 500, debugPrice = "$0.49~"),
        CoinPackDefinition(productId = CoinPackIds.COINS_1000, coinAmount = 1000, debugPrice = "$0.69~"),
        CoinPackDefinition(productId = CoinPackIds.COINS_1500, coinAmount = 1500, debugPrice = "$0.99~"),
        CoinPackDefinition(productId = CoinPackIds.COINS_2000, coinAmount = 2000, debugPrice = "$1.99~"),
        CoinPackDefinition(productId = CoinPackIds.COINS_2500, coinAmount = 2500, debugPrice = "$3.99~"),
        CoinPackDefinition(productId = CoinPackIds.COINS_3000, coinAmount = 3000, debugPrice = "$4.99~"),
        CoinPackDefinition(productId = CoinPackIds.COINS_3500, coinAmount = 3500, debugPrice = "$7.99~"),
        CoinPackDefinition(productId = CoinPackIds.COINS_4000, coinAmount = 4000, debugPrice = "$9.99~")
    )

    val coinProductIds: List<String> = CoinPackIds.ALL

    fun getCoinAmount(productId: String): Int? {
        val defined = coinPackDefinitions.firstOrNull { it.productId == productId }?.coinAmount
        if (defined != null) return defined
        
        // Fallback: try to parse amount from ID like "coins_123"
        return productId.removePrefix("coins_").toIntOrNull()
    }

    fun getDebugCoinPacks(productIds: List<String> = coinProductIds): List<CoinPackItem> =
        productIds.mapNotNull { id ->
            val amount = getCoinAmount(id) ?: return@mapNotNull null
            val def = coinPackDefinitions.firstOrNull { it.productId == id }
            val price = def?.debugPrice ?: "$0.99"
            CoinPackItem(
                id = "mock_$amount",
                name = formatCoinPackName(amount),
                price = price,
                imageRes = getCoinPackImageRes(amount),
                productDetails = null
            )
        }

    fun getPowerUpItems(): List<ShopItem> {
        return listOf(
            ShopItem(
                id = PowerUpIds.POWERUP_SHIELD_ID,
                name = "Energy Shield",
                description = "Invincible for 10s",
                price = 150,
                imageRes = R.drawable.img_powerup_shield_icon,
                isUnlocked = false
            ),
            ShopItem(
                id = PowerUpIds.POWERUP_MULTIPLIER_ID,
                name = "Score Booster",
                description = "Double points for one run",
                price = 200,
                imageRes = R.drawable.img_powerup_multiplier_icon,
                isUnlocked = false
            )
        )
    }

    fun getSkinItems(purchasedIds: Set<String>): List<ShopItem> {
        return listOf(
            ShopItem(SkinIds.SKIN_DEFAULT_ID, "Blue Bird", "", 0, R.drawable.img_bird_hero, true),
            ShopItem(SkinIds.SKIN_PIRATE_ID, "Pirate", "", 50, R.drawable.img_skin_pirate, purchasedIds.contains(SkinIds.SKIN_PIRATE_ID)),
            ShopItem(SkinIds.SKIN_NINJA_ID, "Ninja", "", 50, R.drawable.img_skin_ninja, purchasedIds.contains(SkinIds.SKIN_NINJA_ID)),
            ShopItem(SkinIds.SKIN_ROBOT_ID, "Robot", "", 50, R.drawable.img_skin_robot, purchasedIds.contains(SkinIds.SKIN_ROBOT_ID)),
            ShopItem(SkinIds.SKIN_SPACE_ID, "Space Voyager", "", 300, R.drawable.img_skin_space_voyager, purchasedIds.contains(SkinIds.SKIN_SPACE_ID)),
            ShopItem(SkinIds.SKIN_GOLDEN_ID, "Golden Phoenix", "", 500, R.drawable.img_skin_golden_phoenix, purchasedIds.contains(SkinIds.SKIN_GOLDEN_ID)),
            ShopItem(SkinIds.SKIN_STEAMPUNK_ID, "Steam-Powered Flyer", "", 400, R.drawable.img_skin_steampunk_flyer, purchasedIds.contains(SkinIds.SKIN_STEAMPUNK_ID))
        )
    }

    fun mapToCoinPack(product: ProductDetails): CoinPackItem {
        val coinAmount = getCoinAmount(product.productId)
        return CoinPackItem(
            id = product.productId,
            name = coinAmount?.let(::formatCoinPackName) ?: product.name,
            price = product.oneTimePurchaseOfferDetails?.formattedPrice ?: "---",
            imageRes = coinAmount?.let(::getCoinPackImageRes) ?: R.drawable.img_coins_500,
            productDetails = product
        )
    }

    private fun formatCoinPackName(coinAmount: Int): String =
        String.format(Locale.US, "%,d Coins", coinAmount)

    private fun getCoinPackImageRes(coinAmount: Int): Int = when {
        coinAmount <= 100 -> R.drawable.img_coins_100
        coinAmount <= 500 -> R.drawable.img_coins_500
        else -> R.drawable.img_coins_1000
    }
}
