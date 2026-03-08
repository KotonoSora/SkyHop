package com.kotonosora.skyboundhopper.model

import com.kotonosora.skyboundhopper.R

object SkinData {
    fun getBirdSkinResource(selectedSkinId: String): Int {
        return when (selectedSkinId) {
            "skin_pirate" -> R.drawable.img_skin_pirate
            "skin_ninja" -> R.drawable.img_skin_ninja
            "skin_robot" -> R.drawable.img_skin_robot
            "skin_space" -> R.drawable.img_skin_space_voyager
            "skin_golden" -> R.drawable.img_skin_golden_phoenix
            "skin_steampunk" -> R.drawable.img_skin_steampunk_flyer
            else -> R.drawable.img_bird_hero
        }
    }
}
