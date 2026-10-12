package local.core.shop.coins

import local.core.billing.DomainProduct
import java.util.Locale

data class CoinPackDefinition(
    val productId: String,
    val coinAmount: Int,
    val debugPrice: String,
    val description: String
)

/**
 * Pure coin-pack catalog. This file has no `R` imports; the drawable mapping
 * for coin amounts lives in CoinPackItems.kt in this same module.
 */
object CoinPacks {
    private const val COMMON_DESC =
        "A pack of %d coins used to unlock powerful boosts like Extra Time, Hint, and Undo."

    private val definitions = listOf(
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

    fun getCoinAmount(productId: String): Int? {
        return definitions.firstOrNull { it.productId == productId }?.coinAmount
            ?: productId.removePrefix("coins_").toIntOrNull()
    }

    fun definitionFor(productId: String): CoinPackDefinition? {
        return definitions.firstOrNull { it.productId == productId }
    }

    fun formatCoinPackName(coinAmount: Int): String =
        String.format(Locale.US, "%,d Coins", coinAmount)

    val mockProducts: List<DomainProduct> = definitions.map { def ->
        DomainProduct(
            productId = def.productId,
            name = formatCoinPackName(def.coinAmount),
            formattedPrice = def.debugPrice
        )
    }
}
