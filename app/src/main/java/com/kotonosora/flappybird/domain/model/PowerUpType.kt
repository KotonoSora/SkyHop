package com.kotonosora.flappybird.domain.model

enum class PowerUpType(val id: String) {
    SHIELD("shield"),
    MULTIPLIER("multiplier"),
    AUTO_PLAY("autoplay"),
    BOOST("boost");

    companion object {
        fun fromId(id: String) = entries.find { it.id == id }
    }
}
