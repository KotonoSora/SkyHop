package com.jn.flagfang.model

import kotlin.random.Random

object GamePhysics {
    const val GRAVITY = 0.8f
    const val JUMP_VELOCITY = -15f
    const val FRAME_RATE_MS = 16L
    const val DELTA_TIME = FRAME_RATE_MS / 1000f

    private const val PIPE_GAP_HEIGHT = 550f
    private const val PIPE_WIDTH = 55f
    private const val PIPE_SPACING = 660f
    private const val BASE_PIPE_SPEED = 5f
    private const val SPEED_INCREMENT = 0.5f
    private const val PIPES_PER_LEVEL = 10
    private const val CENTS_PER_LEVEL = 3

    fun updatePowerUpTimers(state: GameState): GameState {
        if (state.isStartSequenceActive) {
            val newTime = (state.startSequenceTimeLeft - DELTA_TIME).coerceAtLeast(0f)
            return state.copy(
                startSequenceTimeLeft = newTime,
                isStartSequenceActive = newTime > 0f
            )
        }

        val shieldTime = (state.shieldTimeLeft - DELTA_TIME).coerceAtLeast(0f)
        val autoPlayTime = (state.autoPlayTimeLeft - DELTA_TIME).coerceAtLeast(0f)
        val multiplierTime = (state.multiplierTimeLeft - DELTA_TIME).coerceAtLeast(0f)

        return state.copy(
            shieldTimeLeft = shieldTime,
            shieldActive = shieldTime > 0f,
            autoPlayTimeLeft = autoPlayTime,
            isAutoPlayActive = autoPlayTime > 0f,
            multiplierTimeLeft = multiplierTime,
            multiplierActive = multiplierTime > 0f
        )
    }

    fun updatePhysics(state: GameState): GameState {
        val shouldAutoJump = when {
            state.isStartSequenceActive -> {
                val targetY = if (state.screenHeight > 0) state.screenHeight / 2 else 500f
                state.Animal.position.y > targetY && state.Animal.velocity >= 0
            }

            state.isAutoPlayActive || state.multiplierActive -> {
                val nextPipe = state.pipes.firstOrNull { it.x + it.width > state.Animal.position.x }
                val targetY =
                    nextPipe?.let { it.gapTop + it.gapHeight / 2 } ?: (state.screenHeight / 2)
                state.Animal.position.y > targetY && state.Animal.velocity >= 0
            }

            else -> false
        }

        val newVelocity = if (shouldAutoJump) JUMP_VELOCITY else state.Animal.velocity + GRAVITY
        val newY = state.Animal.position.y + newVelocity

        return state.copy(
            Animal = state.Animal.copy(
                position = state.Animal.position.copy(y = newY),
                velocity = newVelocity
            )
        )
    }

    fun updatePipes(state: GameState): GameState {
        val pipeSpeed = BASE_PIPE_SPEED + ((state.pipesPassed / PIPES_PER_LEVEL) * SPEED_INCREMENT)
        val updatedPipes = state.pipes
            .map { it.copy(x = it.x - pipeSpeed) }
            .filter { it.x + it.width > 0 }
            .toMutableList()

        if (state.screenWidth > 0 && (updatedPipes.isEmpty() || updatedPipes.last().x < state.screenWidth - PIPE_SPACING)) {
            val availableHeight = (state.screenHeight - 600).coerceAtLeast(100f)
            val reducedAmplitude = availableHeight * 0.68f
            val amplitudeOffset = 100f + (availableHeight * 0.1f)

            val gapTop = Random.nextFloat() * reducedAmplitude + amplitudeOffset
            updatedPipes.add(
                PipeState(
                    x = state.screenWidth,
                    gapTop = gapTop,
                    gapHeight = PIPE_GAP_HEIGHT,
                    width = PIPE_WIDTH
                )
            )
        }

        return state.copy(pipes = updatedPipes)
    }

    fun updateScoring(state: GameState): GameState {
        var newScore = state.score
        var newCoins = state.cents
        var newPipesPassed = state.pipesPassed

        val finalPipes = state.pipes.map { pipe ->
            if (!pipe.scored && pipe.x + pipe.width < state.Animal.position.x) {
                val scoreGain = if (state.multiplierActive) 2 else 1
                newScore += scoreGain
                newPipesPassed += 1

                if (newPipesPassed % PIPES_PER_LEVEL == 0) newCoins += CENTS_PER_LEVEL

                pipe.copy(scored = true)
            } else pipe
        }

        val newLevel = (newPipesPassed / PIPES_PER_LEVEL) + 1
        return state.copy(
            pipes = finalPipes,
            score = newScore,
            cents = newCoins,
            pipesPassed = newPipesPassed,
            level = newLevel
        )
    }

    fun checkCollision(state: GameState): Boolean {
        if (state.screenHeight <= 0f) return false
        val Animal = state.Animal

        // Screen bounds collision
        if (Animal.position.y < 0 || Animal.position.y + Animal.size.height > state.screenHeight) return true

        val AnimalRect = AnimalRect(Animal)

        return state.pipes.any { pipe ->
            val pipeLeft = pipe.x
            val pipeRight = pipe.x + pipe.width

            if (AnimalRect.right > pipeLeft && AnimalRect.left < pipeRight) {
                AnimalRect.top < pipe.gapTop || AnimalRect.bottom > pipe.gapTop + pipe.gapHeight
            } else {
                false
            }
        }
    }

    private class AnimalRect(Animal: AnimalState) {
        val left = Animal.position.x
        val right = Animal.position.x + Animal.size.width
        val top = Animal.position.y
        val bottom = Animal.position.y + Animal.size.height
    }
}