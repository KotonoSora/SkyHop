package com.kotonosora.skyboundhopper.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kotonosora.skyboundhopper.data.ScoreRepository
import com.kotonosora.skyboundhopper.data.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val scoreRepository = ScoreRepository(application)
    private val settingsRepository = SettingsRepository(application)
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _selectedSkinId = MutableStateFlow("default")
    val selectedSkinId: StateFlow<String> = _selectedSkinId.asStateFlow()

    private val _shieldCount = MutableStateFlow(0)
    val shieldCount: StateFlow<Int> = _shieldCount.asStateFlow()

    private val _multiplierCount = MutableStateFlow(0)
    val multiplierCount: StateFlow<Int> = _multiplierCount.asStateFlow()

    private var gameJob: Job? = null

    init {
        viewModelScope.launch {
            scoreRepository.highScoreFlow.collect { highScore ->
                _gameState.update { it.copy(highScore = highScore) }
            }
        }
        viewModelScope.launch {
            settingsRepository.selectedSkinFlow.collect { skinId ->
                _selectedSkinId.value = skinId
            }
        }
        viewModelScope.launch {
            settingsRepository.coinsFlow.collect { coins ->
                _gameState.update { it.copy(coins = coins) }
            }
        }
        viewModelScope.launch {
            settingsRepository.shieldCountFlow.collect { count ->
                _shieldCount.value = count
            }
        }
        viewModelScope.launch {
            settingsRepository.multiplierCountFlow.collect { count ->
                _multiplierCount.value = count
            }
        }
    }

    fun onScreenSizeChanged(width: Float, height: Float) {
        _gameState.update { it.copy(screenWidth = width, screenHeight = height) }
    }

    fun startGame() {
        if (_gameState.value.isGameOver) {
            resetGame()
        }
        _gameState.update { 
            it.copy(
                isGameStarted = true, 
                isGameOver = false,
                isStartSequenceActive = true,
                startSequenceTimeLeft = 10f,
                shieldActive = true,
                shieldTimeLeft = 5f,
                isAutoPlayActive = false // Will be true after 5s
            ) 
        }
        runGameLoop()
    }

    private fun resetGame() {
        _gameState.update { 
            GameState(
                screenWidth = it.screenWidth, 
                screenHeight = it.screenHeight,
                highScore = it.highScore,
                coins = it.coins,
                level = 1,
                bird = BirdState(position = androidx.compose.ui.geometry.Offset(100f, it.screenHeight / 2))
            ) 
        }
    }

    fun jump() {
        if (!_gameState.value.isGameStarted) {
            startGame()
        }
        if (!_gameState.value.isGameOver && !_gameState.value.isAutoPlayActive && !_gameState.value.isStartSequenceActive) {
            _gameState.update { it.copy(bird = it.bird.copy(velocity = -15f)) }
        }
    }

    private fun runGameLoop() {
        gameJob?.cancel()
        gameJob = viewModelScope.launch {
            while (!_gameState.value.isGameOver && _gameState.value.isGameStarted) {
                updateGame()
                delay(16) // ~60 FPS
            }
        }
    }

    private fun updateGame() {
        _gameState.update { state ->
            val deltaTime = 16f / 1000f
            
            // Start Sequence Logic
            var startSeqTime = state.startSequenceTimeLeft
            var startSeqActive = state.isStartSequenceActive
            var shieldActive = state.shieldActive
            var shieldTime = state.shieldTimeLeft
            var autoPlayActive = state.isAutoPlayActive
            var autoPlayTime = state.autoPlayTimeLeft

            if (startSeqActive) {
                startSeqTime -= deltaTime
                if (startSeqTime > 5f) {
                    // Phase 1: Shield (First 5 seconds)
                    shieldActive = true
                    shieldTime = startSeqTime - 5f
                    autoPlayActive = true // Automate flight during start sequence
                } else if (startSeqTime > 0f) {
                    // Phase 2: Boost (Next 5 seconds)
                    shieldActive = false
                    shieldTime = 0f
                    autoPlayActive = true
                    autoPlayTime = startSeqTime
                } else {
                    startSeqActive = false
                    autoPlayActive = false
                    autoPlayTime = 0f
                }
            } else {
                // Normal Power-up Timers
                shieldTime = (state.shieldTimeLeft - deltaTime).coerceAtLeast(0f)
                autoPlayTime = (state.autoPlayTimeLeft - deltaTime).coerceAtLeast(0f)
                shieldActive = shieldTime > 0f
                autoPlayActive = autoPlayTime > 0f
            }

            val multiplierTime = (state.multiplierTimeLeft - deltaTime).coerceAtLeast(0f)
            val multiplierActive = multiplierTime > 0f

            // Update level based on score
            val newLevel = (state.score / 10) + 1

            // Auto Play Logic: Determine if bird should jump
            var shouldAutoJump = false
            if (autoPlayActive) {
                val nextPipe = state.pipes.firstOrNull { it.x + it.width > state.bird.position.x }
                val targetY = if (nextPipe != null) {
                    nextPipe.gapTop + nextPipe.gapHeight / 2
                } else {
                    state.screenHeight / 2
                }
                
                if (state.bird.position.y > targetY && state.bird.velocity >= 0) {
                    shouldAutoJump = true
                }
            }

            // Update bird
            val gravity = 0.8f
            var newVelocity = state.bird.velocity + gravity
            if (shouldAutoJump) {
                newVelocity = -15f
            }
            
            val newY = state.bird.position.y + newVelocity
            val newBirdState = state.bird.copy(
                position = state.bird.position.copy(y = newY),
                velocity = newVelocity
            )

            // Update pipes
            val pipeSpeed = 5f + (newLevel - 1) * 0.5f
            val updatedPipes = state.pipes.map { it.copy(x = it.x - pipeSpeed) }
                .filter { it.x + it.width > 0 }
                .toMutableList()

            // Spawn new pipe
            if (updatedPipes.isEmpty() || updatedPipes.last().x < state.screenWidth - 400) {
                val gapTop = Random.nextFloat() * (state.screenHeight - 600) + 100
                updatedPipes.add(PipeState(x = state.screenWidth, gapTop = gapTop))
            }

            // Check scoring
            var newScore = state.score
            var newCoins = state.coins
            val finalPipes = updatedPipes.map { pipe ->
                if (!pipe.scored && pipe.x + pipe.width < state.bird.position.x) {
                    val scoreGain = if (multiplierActive) 2 else 1
                    newScore += scoreGain
                    newCoins += 10
                    pipe.copy(scored = true)
                } else {
                    pipe
                }
            }

            // Check collisions
            var isGameOver = checkCollision(newBirdState, finalPipes, state.screenHeight)
            
            if (isGameOver && (shieldActive || autoPlayActive)) {
                isGameOver = false
                // If it's a normal shield (not start sequence), it might get consumed. 
                // But let's keep it simple: power-ups give invincibility for their duration.
            }

            if (isGameOver) {
                viewModelScope.launch {
                    scoreRepository.updateHighScore(newScore)
                    settingsRepository.addCoins(newCoins - state.coins)
                }
            }

            state.copy(
                bird = newBirdState,
                pipes = finalPipes,
                score = newScore,
                coins = newCoins,
                level = newLevel,
                isGameOver = isGameOver,
                shieldActive = shieldActive,
                multiplierActive = multiplierActive,
                shieldTimeLeft = shieldTime,
                multiplierTimeLeft = multiplierTime,
                isAutoPlayActive = autoPlayActive,
                autoPlayTimeLeft = autoPlayTime,
                isStartSequenceActive = startSeqActive,
                startSequenceTimeLeft = startSeqTime
            )
        }
    }

    private fun checkCollision(bird: BirdState, pipes: List<PipeState>, screenHeight: Float): Boolean {
        if (bird.position.y < 0 || bird.position.y + bird.size.height > screenHeight) {
            return true
        }

        for (pipe in pipes) {
            val birdLeft = bird.position.x
            val birdRight = bird.position.x + bird.size.width
            val birdTop = bird.position.y
            val birdBottom = bird.position.y + bird.size.height

            val pipeLeft = pipe.x
            val pipeRight = pipe.x + pipe.width

            if (birdRight > pipeLeft && birdLeft < pipeRight) {
                if (birdTop < pipe.gapTop || birdBottom > pipe.gapTop + pipe.gapHeight) {
                    return true
                }
            }
        }
        return false
    }

    fun purchasePowerUp(type: String, cost: Int, onSuccess: () -> Unit, onInsufficient: () -> Unit) {
        viewModelScope.launch {
            if (settingsRepository.spendCoins(cost)) {
                _gameState.update { state ->
                    when (type) {
                        "shield" -> state.copy(isGameOver = false, shieldActive = true, shieldTimeLeft = 10f)
                        "boost" -> state.copy(isGameOver = false, isAutoPlayActive = true, autoPlayTimeLeft = 10f)
                        else -> state
                    }
                }
                onSuccess()
                if (gameJob?.isActive != true) {
                    runGameLoop()
                }
            } else {
                onInsufficient()
            }
        }
    }

    fun usePowerUp(type: String) {
        viewModelScope.launch {
            if (settingsRepository.usePowerUp(type)) {
                _gameState.update { state ->
                    when (type) {
                        "shield" -> state.copy(shieldActive = true, shieldTimeLeft = 10f)
                        "multiplier" -> state.copy(multiplierActive = true, multiplierTimeLeft = 10f)
                        "autoplay" -> state.copy(isAutoPlayActive = true, autoPlayTimeLeft = 10f)
                        else -> state
                    }
                }
            }
        }
    }

    fun triggerAutoPlay() {
        if (!_gameState.value.isGameOver && _gameState.value.isGameStarted) {
            _gameState.update { it.copy(isAutoPlayActive = true, autoPlayTimeLeft = 10f) }
        }
    }
}
