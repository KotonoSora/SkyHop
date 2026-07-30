package com.kotonosora.zamstu.feature.shop

import com.kotonosora.zamstu.R
import com.kotonosora.zamstu.domain.model.PowerUpIds
import com.kotonosora.zamstu.domain.repository.DomainProduct
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
    val description: String,
    val price: String,
    val coinAmount: Int,
    val imageRes: Int
)

data class CoinPackDefinition(
    val productId: String,
    val coinAmount: Int,
    val debugPrice: String,
    val description: String
)

object ShopData {
    private const val COMMON_DESC =
        "A pack of %d coins used to unlock powerful boosts like Extra Time, Hint, and Undo."

    private val CoinPackDefinitions = listOf(
        CoinPackDefinition(
            productId = CoinPackIds.COINS_100,
            coinAmount = 100,
            debugPrice = "$0.29",
            description = String.format(Locale.US, COMMON_DESC, 100)
        ),
        CoinPackDefinition(
            productId = CoinPackIds.COINS_500,
            coinAmount = 500,
            debugPrice = "$0.49",
            description = String.format(Locale.US, COMMON_DESC, 500)
        ),
        CoinPackDefinition(
            productId = CoinPackIds.COINS_1000,
            coinAmount = 1000,
            debugPrice = "$0.69",
            description = String.format(Locale.US, COMMON_DESC, 1000)
        ),
        CoinPackDefinition(
            productId = CoinPackIds.COINS_1500,
            coinAmount = 1500,
            debugPrice = "$0.99",
            description = String.format(Locale.US, COMMON_DESC, 1500)
        ),
        CoinPackDefinition(
            productId = CoinPackIds.COINS_2000,
            coinAmount = 2000,
            debugPrice = "$1.99",
            description = String.format(Locale.US, COMMON_DESC, 2000)
        ),
        CoinPackDefinition(
            productId = CoinPackIds.COINS_2500,
            coinAmount = 2500,
            debugPrice = "$3.99",
            description = String.format(Locale.US, COMMON_DESC, 2500)
        ),
        CoinPackDefinition(
            productId = CoinPackIds.COINS_3000,
            coinAmount = 3000,
            debugPrice = "$4.99",
            description = String.format(Locale.US, COMMON_DESC, 3000)
        ),
        CoinPackDefinition(
            productId = CoinPackIds.COINS_3500,
            coinAmount = 3500,
            debugPrice = "$7.99",
            description = String.format(Locale.US, COMMON_DESC, 3500)
        ),
        CoinPackDefinition(
            productId = CoinPackIds.COINS_4000,
            coinAmount = 4000,
            debugPrice = "$9.99",
            description = String.format(Locale.US, COMMON_DESC, 4000)
        )
    )

    val coinProductIds: List<String> = CoinPackIds.ALL

    fun getCoinAmount(productId: String): Int? {
        return CoinPackDefinitions.firstOrNull { it.productId == productId }?.coinAmount
            ?: productId.removePrefix("coins_").toIntOrNull()
    }

    val mockProducts: List<DomainProduct> = CoinPackDefinitions.map { def ->
        DomainProduct(
            productId = def.productId,
            name = formatCoinPackName(def.coinAmount),
            formattedPrice = def.debugPrice
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

    fun mapToCoinPack(product: DomainProduct): CoinPackItem {
        val def = CoinPackDefinitions.firstOrNull { it.productId == product.productId }
        val coinAmount = def?.coinAmount ?: getCoinAmount(product.productId) ?: 0

        return CoinPackItem(
            id = product.productId,
            name = def?.let { formatCoinPackName(it.coinAmount) } ?: product.name,
            description = def?.description ?: "",
            price = product.formattedPrice,
            coinAmount = coinAmount,
            imageRes = getCoinPackImageRes(coinAmount)
        )
    }

    private fun formatCoinPackName(coinAmount: Int): String =
        String.format(Locale.US, "%,d Coins", coinAmount)

    private fun getCoinPackImageRes(coinAmount: Int): Int = when {
        coinAmount <= 100 -> R.drawable.img_coins_100
        coinAmount <= 500 -> R.drawable.img_coins_500
        coinAmount <= 1000 -> R.drawable.img_coins_1000
        coinAmount <= 1500 -> R.drawable.img_coins_1000
        coinAmount <= 2000 -> R.drawable.img_coins_2000
        else -> R.drawable.img_coins_2000
    }
}
