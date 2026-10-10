package local.flappybird.shop

object PowerUps {
    fun getPowerUpItems(): List<ShopItem> {
        return listOf(
            PowerUpShopItem(
                id = PowerUpIds.POWERUP_SHIELD_ID,
                // Must match the engine's PowerUpType id ("shield").
                inventoryId = "shield",
                name = "Energy Shield",
                description = "Invincible for 10s",
                price = 200,
                imageRes = R.drawable.img_powerup_shield_icon,
                isUnlocked = false
            ),
            PowerUpShopItem(
                id = PowerUpIds.POWERUP_MULTIPLIER_ID,
                // Must match the engine's PowerUpType id ("multiplier").
                inventoryId = "multiplier",
                name = "Score Booster",
                description = "Double points for one run",
                price = 200,
                imageRes = R.drawable.img_powerup_multiplier_icon,
                isUnlocked = false
            )
        )
    }
}
