package com.kotonosora.skyboundhopper.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

data class BirdState(
    val position: Offset = Offset(100f, 500f),
    val velocity: Float = 0f,
    val size: Size = Size(50f, 50f)
)

data class PipeState(
    val x: Float,
    val gapTop: Float,
    val gapHeight: Float = 250f,
    val width: Float = 80f,
    val scored: Boolean = false
)

data class GameState(
    val bird: BirdState = BirdState(),
    val pipes: List<PipeState> = emptyList(),
    val score: Int = 0,
    val highScore: Int = 0,
    val coins: Int = 0,
    val level: Int = 1,
    val isGameOver: Boolean = false,
    val isGameStarted: Boolean = false,
    val screenWidth: Float = 0f,
    val screenHeight: Float = 0f,
    val shieldActive: Boolean = false,
    val multiplierActive: Boolean = false,
    val multiplierTimeLeft: Float = 0f,
    val shieldTimeLeft: Float = 0f,
    val isAutoPlayActive: Boolean = false,
    val autoPlayTimeLeft: Float = 0f,
    val startSequenceTimeLeft: Float = 0f,
    val isStartSequenceActive: Boolean = false
)
