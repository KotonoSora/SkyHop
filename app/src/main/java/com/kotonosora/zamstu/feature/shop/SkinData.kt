package com.kotonosora.zamstu.feature.shop

import com.kotonosora.zamstu.R

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
}
