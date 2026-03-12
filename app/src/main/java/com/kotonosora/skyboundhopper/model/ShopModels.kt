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
        CoinPackDefinition(productId = "coins_100", coinAmount = 100, debugPrice = "$0.29"),
        CoinPackDefinition(productId = "coins_500", coinAmount = 500, debugPrice = "$0.49"),
        CoinPackDefinition(productId = "coins_1000", coinAmount = 1000, debugPrice = "$0.69"),
        CoinPackDefinition(productId = "coins_1500", coinAmount = 1500, debugPrice = "$0.99"),
        CoinPackDefinition(productId = "coins_2000", coinAmount = 2000, debugPrice = "$1.99"),
        CoinPackDefinition(productId = "coins_2500", coinAmount = 2500, debugPrice = "$3.99"),
        CoinPackDefinition(productId = "coins_3000", coinAmount = 3000, debugPrice = "$4.99"),
        CoinPackDefinition(productId = "coins_3500", coinAmount = 3500, debugPrice = "$7.99"),
        CoinPackDefinition(productId = "coins_4000", coinAmount = 4000, debugPrice = "$9.99")
    )

    val coinProductIds: List<String> = coinPackDefinitions.map { it.productId }

    fun getCoinAmount(productId: String): Int? =
        coinPackDefinitions.firstOrNull { it.productId == productId }?.coinAmount

    fun getDebugCoinPacks(): List<CoinPackItem> =
        coinPackDefinitions.map { definition ->
            CoinPackItem(
                id = "mock_${definition.coinAmount}",
                name = formatCoinPackName(definition.coinAmount),
                price = definition.debugPrice,
                imageRes = getCoinPackImageRes(definition.coinAmount),
                productDetails = null
            )
        }

    fun getPowerUpItems(): List<ShopItem> {
        return listOf(
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
    }

    fun getSkinItems(purchasedIds: Set<String>): List<ShopItem> {
        return listOf(
            ShopItem("default", "Blue Bird", "", 0, R.drawable.img_bird_hero, true),
            ShopItem("skin_pirate", "Pirate", "", 50, R.drawable.img_skin_pirate, purchasedIds.contains("skin_pirate")),
            ShopItem("skin_ninja", "Ninja", "", 50, R.drawable.img_skin_ninja, purchasedIds.contains("skin_ninja")),
            ShopItem("skin_robot", "Robot", "", 50, R.drawable.img_skin_robot, purchasedIds.contains("skin_robot")),
            ShopItem("skin_space", "Space Voyager", "", 300, R.drawable.img_skin_space_voyager, purchasedIds.contains("skin_space")),
            ShopItem("skin_golden", "Golden Phoenix", "", 500, R.drawable.img_skin_golden_phoenix, purchasedIds.contains("skin_golden")),
            ShopItem("skin_steampunk", "Steam-Powered Flyer", "", 400, R.drawable.img_skin_steampunk_flyer, purchasedIds.contains("skin_steampunk"))
        )
    }

    fun mapToCoinPack(product: ProductDetails): CoinPackItem {
        val coinAmount = getCoinAmount(product.productId)
        return CoinPackItem(
            id = product.productId,
            name = coinAmount?.let(::formatCoinPackName) ?: product.name,
            price = product.oneTimePurchaseOfferDetails?.formattedPrice ?: "---",
            imageRes = coinAmount?.let(::getCoinPackImageRes) ?: R.drawable.placeholder,
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
