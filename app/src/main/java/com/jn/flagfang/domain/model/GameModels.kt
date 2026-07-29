package com.jn.flagfang.domain.model

data class AnimalState(
    val position: Point = Point(100f, 500f),
    val velocity: Float = 0f,
    val size: Size = Size(110f, 110f)
)

data class PipeState(
    val x: Float,
    val gapTop: Float,
    val gapHeight: Float = 350f,
    val width: Float = 100f,
    val scored: Boolean = false
)

data class GameState(
    val animal: AnimalState = AnimalState(),
    val pipes: List<PipeState> = emptyList(),
    val score: Int = 0,
    val pipesPassed: Int = 0,
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
    val isStartSequenceActive: Boolean = false,
    val shieldCount: Int = 0,
    val multiplierCount: Int = 0,
    val autoPlayCount: Int = 0,
    val isEndless: Boolean = true,
    val targetScore: Int = 0,
    val isWin: Boolean = false,
    val rewardCoins: Int = 0,
    val gravityMultiplier: Float = 1f,
    val shieldDisabled: Boolean = false,
    val isDailyChallenge: Boolean = false
)
