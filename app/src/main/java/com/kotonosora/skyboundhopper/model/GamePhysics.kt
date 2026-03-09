package com.kotonosora.skyboundhopper.model

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
    private const val COINS_PER_LEVEL = 3

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
                state.bird.position.y > targetY && state.bird.velocity >= 0
            }
            state.isAutoPlayActive || state.multiplierActive -> {
                val nextPipe = state.pipes.firstOrNull { it.x + it.width > state.bird.position.x }
                val targetY = nextPipe?.let { it.gapTop + it.gapHeight / 2 } ?: (state.screenHeight / 2)
                state.bird.position.y > targetY && state.bird.velocity >= 0
            }
            else -> false
        }

        val newVelocity = if (shouldAutoJump) JUMP_VELOCITY else state.bird.velocity + GRAVITY
        val newY = state.bird.position.y + newVelocity
        
        return state.copy(
            bird = state.bird.copy(
                position = state.bird.position.copy(y = newY),
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
            updatedPipes.add(PipeState(x = state.screenWidth, gapTop = gapTop, gapHeight = PIPE_GAP_HEIGHT, width = PIPE_WIDTH))
        }

        return state.copy(pipes = updatedPipes)
    }

    fun updateScoring(state: GameState): GameState {
        var newScore = state.score
        var newCoins = state.coins
        var newPipesPassed = state.pipesPassed
        
        val finalPipes = state.pipes.map { pipe ->
            if (!pipe.scored && pipe.x + pipe.width < state.bird.position.x) {
                val scoreGain = if (state.multiplierActive) 2 else 1
                newScore += scoreGain
                newPipesPassed += 1
                
                if (newPipesPassed % PIPES_PER_LEVEL == 0) newCoins += COINS_PER_LEVEL
                
                pipe.copy(scored = true)
            } else pipe
        }
        
        val newLevel = (newPipesPassed / PIPES_PER_LEVEL) + 1
        return state.copy(
            pipes = finalPipes,
            score = newScore,
            coins = newCoins,
            pipesPassed = newPipesPassed,
            level = newLevel
        )
    }

    fun checkCollision(state: GameState): Boolean {
        if (state.screenHeight <= 0f) return false
        val bird = state.bird
        
        // Screen bounds collision
        if (bird.position.y < 0 || bird.position.y + bird.size.height > state.screenHeight) return true

        val birdRect = BirdRect(bird)

        return state.pipes.any { pipe ->
            val pipeLeft = pipe.x
            val pipeRight = pipe.x + pipe.width

            if (birdRect.right > pipeLeft && birdRect.left < pipeRight) {
                birdRect.top < pipe.gapTop || birdRect.bottom > pipe.gapTop + pipe.gapHeight
            } else {
                false
            }
        }
    }
    
    private class BirdRect(bird: BirdState) {
        val left = bird.position.x
        val right = bird.position.x + bird.size.width
        val top = bird.position.y
        val bottom = bird.position.y + bird.size.height
    }
}