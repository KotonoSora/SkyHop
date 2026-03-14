package com.kotonosora.skyboundhopper.model

import com.kotonosora.skyboundhopper.R

object SkinData {
    fun getBirdSkinResource(selectedSkinId: String): Int {
        return when (selectedSkinId) {
            SkinIds.SKIN_PIRATE_ID -> R.drawable.img_skin_pirate
            SkinIds.SKIN_NINJA_ID -> R.drawable.img_skin_ninja
            SkinIds.SKIN_ROBOT_ID -> R.drawable.img_skin_robot
            SkinIds.SKIN_SPACE_ID -> R.drawable.img_skin_space_voyager
            SkinIds.SKIN_GOLDEN_ID -> R.drawable.img_skin_golden_phoenix
            SkinIds.SKIN_STEAMPUNK_ID -> R.drawable.img_skin_steampunk_flyer
            else -> R.drawable.img_bird_hero
        }
    }
}
