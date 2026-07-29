package com.jn.flagfang.audio

/**
 * Interface for managing background music and sound effects.
 */
interface IAudioManager {
    fun playBgm()
    fun pauseBgm()
    fun stopBgm()
    fun playSfx(type: SfxType)
    fun release()
    fun updateSettings(musicEnabled: Boolean, sfxEnabled: Boolean)
}
