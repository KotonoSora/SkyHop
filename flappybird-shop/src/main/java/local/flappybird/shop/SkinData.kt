package local.flappybird.shop

object SkinData {
    fun getIDLEAnimalSkinResource(selectedSkinId: String): Int {
        return when (selectedSkinId) {
            SkinIds.SKIN_SYNTH_SCREECHER -> R.drawable.img_idle_bat_synth_screecher
            SkinIds.SKIN_SONAR_MECH -> R.drawable.img_idle_bat_sonar_mech
            SkinIds.SKIN_SIR_A_LOT -> R.drawable.img_idle_bat_sir_a_lot
            else -> R.drawable.img_idle_bat_normal
        }
    }

    fun getFlyAnimalSkinResource(selectedSkinId: String): Int {
        return when (selectedSkinId) {
            SkinIds.SKIN_SYNTH_SCREECHER -> R.drawable.img_fly_bat_synth_screecher
            SkinIds.SKIN_SONAR_MECH -> R.drawable.img_fly_bat_sonar_mech
            SkinIds.SKIN_SIR_A_LOT -> R.drawable.img_fly_bat_sir_a_lot
            else -> R.drawable.img_fly_bat_normal
        }
    }

    fun getSkinItems(purchasedIds: Set<String>): List<ShopItem> {
        return listOf(
            SkinShopItem(
                SkinIds.SKIN_DEFAULT_ID,
                "Cute Bat",
                "",
                0, R.drawable.img_idle_bat_normal, true
            ),
            SkinShopItem(
                SkinIds.SKIN_SYNTH_SCREECHER,
                "Synth-Screecher",
                "",
                400,
                R.drawable.img_idle_bat_synth_screecher,
                purchasedIds.contains(SkinIds.SKIN_SYNTH_SCREECHER)
            ),
            SkinShopItem(
                SkinIds.SKIN_SONAR_MECH,
                "Sonar-Mech",
                "",
                600,
                R.drawable.img_idle_bat_sonar_mech,
                purchasedIds.contains(SkinIds.SKIN_SONAR_MECH)
            ),
            SkinShopItem(
                SkinIds.SKIN_SIR_A_LOT,
                "Sir Bat-a-lot",
                "",
                800,
                R.drawable.img_idle_bat_sir_a_lot,
                purchasedIds.contains(SkinIds.SKIN_SIR_A_LOT)
            ),
        )
    }
}
