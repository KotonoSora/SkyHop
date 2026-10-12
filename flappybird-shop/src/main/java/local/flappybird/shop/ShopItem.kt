package local.flappybird.shop

/**
 * A purchasable shop entry. Sealed so grant routing (`when`) is exhaustive:
 * adding a new item kind fails compilation at every router instead of
 * silently granting nothing after charging coins.
 */
sealed interface ShopItem {
    val id: String
    val name: String
    val description: String
    val price: Int
    val imageRes: Int
    val isUnlocked: Boolean
}

data class SkinShopItem(
    override val id: String,
    override val name: String,
    override val description: String = "",
    override val price: Int,
    override val imageRes: Int,
    override val isUnlocked: Boolean = false
) : ShopItem

data class PowerUpShopItem(
    override val id: String,
    /**
     * Inventory slot this purchase grants (e.g. "shield"). Plain string so
     * `:flappybird-shop` stays decoupled from the engine; values must match
     * the game's power-up type IDs.
     */
    val inventoryId: String,
    override val name: String,
    override val description: String = "",
    override val price: Int,
    override val imageRes: Int,
    override val isUnlocked: Boolean = false
) : ShopItem
