package com.jn.flagfang.feature.shop

import com.android.billingclient.api.ProductDetails
import com.jn.flagfang.R
import com.jn.flagfang.model.PowerUpIds
import java.util.Locale

data class ShopItem(
    val id: String,
    val name: String,
    val description: String = "",
    val price: Int,
    val imageRes: Int,
    val isUnlocked: Boolean = false
)

data class CentPackItem(
    val id: String,
    val name: String,
    val price: String,
    val imageRes: Int,
    val productDetails: ProductDetails?
)

data class CentPackDefinition(
    val productId: String,
    val centAmount: Int,
    val debugPrice: String
)

object ShopData {
    private val CentPackDefinitions = listOf(
        CentPackDefinition(
            productId = CentPackIds.CENTS_100,
            centAmount = 100,
            debugPrice = "$0.39~"
        ),
        CentPackDefinition(
            productId = CentPackIds.CENTS_500,
            centAmount = 500,
            debugPrice = "$0.59~"
        ),
        CentPackDefinition(
            productId = CentPackIds.CENTS_1000,
            centAmount = 1000,
            debugPrice = "$0.79~"
        ),
        CentPackDefinition(
            productId = CentPackIds.CENTS_1500,
            centAmount = 1500,
            debugPrice = "$0.89~"
        ),
        CentPackDefinition(
            productId = CentPackIds.CENTS_2000,
            centAmount = 2000,
            debugPrice = "$0.99~"
        ),
        CentPackDefinition(
            productId = CentPackIds.CENTS_2500,
            centAmount = 2500,
            debugPrice = "$1.99~"
        ),
        CentPackDefinition(
            productId = CentPackIds.CENTS_3000,
            centAmount = 3000,
            debugPrice = "$3.99~"
        ),
        CentPackDefinition(
            productId = CentPackIds.CENTS_3500,
            centAmount = 3500,
            debugPrice = "$5.99~"
        ),
        CentPackDefinition(
            productId = CentPackIds.CENTS_4000,
            centAmount = 4000,
            debugPrice = "$7.99~"
        )
    )

    val centProductIds: List<String> = CentPackIds.ALL

    fun getCoinAmount(productId: String): Int? {
        val defined = CentPackDefinitions.firstOrNull { it.productId == productId }?.centAmount
        if (defined != null) return defined

        // Fallback: try to parse amount from ID like "cents_123"
        return productId.removePrefix("cents_").toIntOrNull()
    }

    fun getDebugCoinPacks(productIds: List<String> = centProductIds): List<CentPackItem> =
        productIds.mapNotNull { id ->
            val amount = getCoinAmount(id) ?: return@mapNotNull null
            val def = CentPackDefinitions.firstOrNull { it.productId == id }
            val price = def?.debugPrice ?: "$0.99"
            CentPackItem(
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
                price = 200,
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
            ShopItem(
                SkinIds.SKIN_DEFAULT_ID,
                "Cute Bat",
                "",
                0, R.drawable.img_idle_bat_normal, true
            ),
            ShopItem(
                SkinIds.SKIN_SYNTH_SCREECHER,
                "Synth-Screecher",
                "",
                400,
                R.drawable.img_idle_bat_synth_screecher,
                purchasedIds.contains(SkinIds.SKIN_SYNTH_SCREECHER)
            ),
            ShopItem(
                SkinIds.SKIN_SONAR_MECH,
                "Sonar-Mech",
                "",
                600,
                R.drawable.img_idle_bat_sonar_mech,
                purchasedIds.contains(SkinIds.SKIN_SONAR_MECH)
            ),
            ShopItem(
                SkinIds.SKIN_SIR_A_LOT,
                "Sir Bat-a-lot",
                "",
                800,
                R.drawable.img_idle_bat_sir_a_lot,
                purchasedIds.contains(SkinIds.SKIN_SIR_A_LOT)
            ),
        )
    }

    fun mapToCoinPack(product: ProductDetails): CentPackItem {
        val centAmount = getCoinAmount(product.productId)
        return CentPackItem(
            id = product.productId,
            name = centAmount?.let(::formatCoinPackName) ?: product.name,
            price = product.oneTimePurchaseOfferDetails?.formattedPrice ?: "---",
            imageRes = centAmount?.let(::getCoinPackImageRes) ?: R.drawable.img_cents_500,
            productDetails = product
        )
    }

    private fun formatCoinPackName(centAmount: Int): String =
        String.format(Locale.US, "%,d Cents", centAmount)

    private fun getCoinPackImageRes(centAmount: Int): Int = when {
        centAmount <= 100 -> R.drawable.img_cents_100
        centAmount <= 500 -> R.drawable.img_cents_500
        centAmount <= 1000 -> R.drawable.img_cents_1000
        centAmount <= 1500 -> R.drawable.img_cents_1000
        centAmount <= 2000 -> R.drawable.img_cents_2000
        else -> R.drawable.img_cents_2000
    }
}
