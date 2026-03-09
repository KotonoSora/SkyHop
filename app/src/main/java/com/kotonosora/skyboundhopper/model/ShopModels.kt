package com.kotonosora.skyboundhopper.model

import com.android.billingclient.api.ProductDetails
import com.kotonosora.skyboundhopper.R

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

object ShopData {
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
        return CoinPackItem(
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
                else -> R.drawable.placeholder // Assume exists
            },
            productDetails = product
        )
    }
}
