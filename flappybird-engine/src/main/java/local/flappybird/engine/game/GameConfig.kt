package local.flappybird.engine.game

import local.flappybird.engine.model.AnimalState
import local.flappybird.engine.model.GameState
import local.flappybird.engine.model.Point

data class GameConfig(
    val level: Int = 1,
    val isEndless: Boolean = true,
    val isDailyChallenge: Boolean = false
) {
    val targetScore: Int = if (isDailyChallenge) 20 else if (isEndless) 0 else level * 10
    val gravityMultiplier: Float = if (isDailyChallenge) 2.0f else 1f
    val shieldDisabled: Boolean = isDailyChallenge
}

/**
 * Pure factory for a fresh [GameState]. Moved out of the Android ViewModel so
 * other games can reuse run setup without copying ViewModel logic.
 * `previous` carries over screen size and persisted counters only.
 */
fun newGame(previous: GameState, config: GameConfig): GameState {
    return GameState(
        screenWidth = previous.screenWidth,
        screenHeight = previous.screenHeight,
        highScore = previous.highScore,
        coins = previous.coins,
        shieldCount = previous.shieldCount,
        multiplierCount = previous.multiplierCount,
        autoPlayCount = previous.autoPlayCount,
        level = config.level,
        targetScore = config.targetScore,
        isEndless = config.isEndless,
        isDailyChallenge = config.isDailyChallenge,
        gravityMultiplier = config.gravityMultiplier,
        shieldDisabled = config.shieldDisabled,
        isGameStarted = true,
        isStartSequenceActive = true,
        startSequenceTimeLeft = 2f,
        animal = AnimalState(
            position = Point(
                100f,
                if (previous.screenHeight > 0) previous.screenHeight / 2 else 500f
            )
        )
    )
}
