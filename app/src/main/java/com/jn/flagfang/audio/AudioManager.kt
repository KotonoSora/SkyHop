package com.jn.flagfang.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.annotation.RawRes
import com.jn.flagfang.R

enum class SfxType(@param:RawRes val rawResId: Int) {
    START(R.raw.sfx_start),
    TOUCH(R.raw.sfx_touch),
    GAMEOVER(R.raw.sfx_game_over)
}

class AudioManager(context: Context) {

    private var bgmPlayer: MediaPlayer? = null
    private var soundPool: SoundPool? = null
    private val loadedSfx = mutableMapOf<SfxType, Int>()

    private var musicEnabled: Boolean = true
    private var sfxEnabled: Boolean = true

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        try {
            bgmPlayer = MediaPlayer().apply {
                setAudioAttributes(audioAttributes)
                val afd = context.resources.openRawResourceFd(R.raw.bg_music)
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                isLooping = true
                setVolume(0.5f, 0.5f)
                prepare()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback
            bgmPlayer = MediaPlayer.create(context, R.raw.bg_music)?.apply {
                isLooping = true
                setVolume(0.5f, 0.5f)
            }
        }

        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttributes)
            .build()

        loadedSfx[SfxType.START] = soundPool?.load(context, SfxType.START.rawResId, 1) ?: 0
        loadedSfx[SfxType.TOUCH] = soundPool?.load(context, SfxType.TOUCH.rawResId, 1) ?: 0
        loadedSfx[SfxType.GAMEOVER] = soundPool?.load(context, SfxType.GAMEOVER.rawResId, 1) ?: 0
    }

    fun playBgm() {
        if (musicEnabled && bgmPlayer?.isPlaying == false) {
            bgmPlayer?.start()
        }
    }

    fun pauseBgm() {
        bgmPlayer?.pause()
    }

    fun stopBgm() {
        bgmPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
            }
            player.seekTo(0)
        }
    }

    fun playSfx(type: SfxType) {
        if (!sfxEnabled) return
        val sampleId = loadedSfx[type] ?: return
        soundPool?.play(sampleId, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    fun release() {
        bgmPlayer?.release()
        bgmPlayer = null
        soundPool?.release()
        soundPool = null
        loadedSfx.clear()
    }

    fun updateSettings(musicEnabled: Boolean, sfxEnabled: Boolean) {
        val wasPlaying = bgmPlayer?.isPlaying == true

        this.musicEnabled = musicEnabled
        this.sfxEnabled = sfxEnabled

        if (!musicEnabled) {
            if (wasPlaying) {
                pauseBgm()
            }
        }
    }
}