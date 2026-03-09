package com.kotonosora.skyboundhopper.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kotonosora.skyboundhopper.data.ScoreRepository
import com.kotonosora.skyboundhopper.data.SettingsRepository
import com.kotonosora.skyboundhopper.model.*
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

    private var gameJob: Job? = null
    private val frameRateMs = 16L
    private val deltaTime = frameRateMs / 1000f

    init {
        observeRepositories()
        // Initialize the game in a "Ready" state with a 2s auto-fly period
        resetGame()
        runGameLoop()
    }

    private fun observeRepositories() {
        viewModelScope.launch { scoreRepository.highScoreFlow.collect { highScore -> _gameState.update { it.copy(highScore = highScore) } } }
        viewModelScope.launch { settingsRepository.selectedSkinFlow.collect { skinId -> _selectedSkinId.value = skinId } }
        viewModelScope.launch { settingsRepository.coinsFlow.collect { coins -> _gameState.update { it.copy(coins = coins) } } }
        viewModelScope.launch { settingsRepository.shieldCountFlow.collect { count -> _gameState.update { it.copy(shieldCount = count) } } }
        viewModelScope.launch { settingsRepository.multiplierCountFlow.collect { count -> _gameState.update { it.copy(multiplierCount = count) } } }
        viewModelScope.launch { settingsRepository.autoPlayCountFlow.collect { count -> _gameState.update { it.copy(autoPlayCount = count) } } }
    }

    fun onScreenSizeChanged(width: Float, height: Float) {
        _gameState.update { it.copy(screenWidth = width, screenHeight = height) }
        // Adjust bird position after screen size is known
        _gameState.update { state ->
            if (state.bird.position.y == 0f || state.bird.position.y == state.screenHeight / 2) {
                state.copy(bird = state.bird.copy(position = androidx.compose.ui.geometry.Offset(100f, height / 2)))
            } else state
        }
    }

    fun startGame() {
        resetGame()
        runGameLoop()
    }

    private fun resetGame() {
        _gameState.update { state ->
            GameState(
                screenWidth = state.screenWidth, 
                screenHeight = state.screenHeight,
                highScore = state.highScore,
                coins = state.coins,
                shieldCount = state.shieldCount,
                multiplierCount = state.multiplierCount,
                autoPlayCount = state.autoPlayCount,
                level = 1,
                isGameStarted = true,
                isStartSequenceActive = true,
                startSequenceTimeLeft = 2f, // 2-second auto-fly period
                bird = BirdState(position = androidx.compose.ui.geometry.Offset(100f, if (state.screenHeight > 0) state.screenHeight / 2 else 500f))
            ) 
        }
    }

    fun jump() {
        val state = _gameState.value
        if (state.isGameOver) {
            startGame()
            return
        }
        
        // If user jumps during auto-fly, take control immediately
        if (state.isStartSequenceActive) {
            _gameState.update { it.copy(isStartSequenceActive = false, bird = it.bird.copy(velocity = -15f)) }
            return
        }

        if (state.isGameStarted && !state.isAutoPlayActive && !state.multiplierActive) {
            _gameState.update { it.copy(bird = it.bird.copy(velocity = -15f)) }
        }
    }

    private fun runGameLoop() {
        gameJob?.cancel()
        gameJob = viewModelScope.launch {
            while (!_gameState.value.isGameOver && _gameState.value.isGameStarted) {
                updateGame()
                delay(frameRateMs)
            }
        }
    }

    private fun updateGame() {
        _gameState.update { currentState ->
            var state = updatePowerUpTimers(currentState)
            
            // Note: Start sequence active means "auto-fly" period, we DO update physics/pipes
            
            state = updatePhysics(state)
            state = updatePipes(state)
            state = updateScoring(state)
            state = checkAndHandleGameOver(state)
            state
        }
    }

    private fun updatePowerUpTimers(state: GameState): GameState {
        var startSeqTime = state.startSequenceTimeLeft
        var startSeqActive = state.isStartSequenceActive
        var shieldActive = state.shieldActive
        var shieldTime = state.shieldTimeLeft
        var autoPlayActive = state.isAutoPlayActive
        var autoPlayTime = state.autoPlayTimeLeft

        if (startSeqActive) {
            startSeqTime = (startSeqTime - deltaTime).coerceAtLeast(0f)
            if (startSeqTime <= 0f) {
                startSeqActive = false
            }
        } else {
            shieldTime = (state.shieldTimeLeft - deltaTime).coerceAtLeast(0f)
            autoPlayTime = (state.autoPlayTimeLeft - deltaTime).coerceAtLeast(0f)
            shieldActive = shieldTime > 0f
            autoPlayActive = autoPlayTime > 0f
        }

        val multiplierTime = (state.multiplierTimeLeft - deltaTime).coerceAtLeast(0f)
        val multiplierActive = multiplierTime > 0f

        return state.copy(
            isStartSequenceActive = startSeqActive,
            startSequenceTimeLeft = startSeqTime,
            shieldActive = shieldActive,
            shieldTimeLeft = shieldTime,
            isAutoPlayActive = autoPlayActive,
            autoPlayTimeLeft = autoPlayTime,
            multiplierActive = multiplierActive,
            multiplierTimeLeft = multiplierTime
        )
    }

    private fun updatePhysics(state: GameState): GameState {
        var shouldAutoJump = false
        
        // Multiplier also helps bird fly pass pipe (acts like AutoPlay)
        if (state.isStartSequenceActive) {
            // During auto-fly, maintain height near the middle
            val targetY = if (state.screenHeight > 0) state.screenHeight / 2 else 500f
            if (state.bird.position.y > targetY && state.bird.velocity >= 0) {
                shouldAutoJump = true
            }
        } else if (state.isAutoPlayActive || state.multiplierActive) {
            val nextPipe = state.pipes.firstOrNull { it.x + it.width > state.bird.position.x }
            val targetY = nextPipe?.let { it.gapTop + it.gapHeight / 2 } ?: (state.screenHeight / 2)
            
            if (state.bird.position.y > targetY && state.bird.velocity >= 0) {
                shouldAutoJump = true
            }
        }

        val gravity = 0.8f
        val newVelocity = if (shouldAutoJump) -15f else state.bird.velocity + gravity
        val newY = state.bird.position.y + newVelocity
        
        val newBirdState = state.bird.copy(
            position = state.bird.position.copy(y = newY),
            velocity = newVelocity
        )
        return state.copy(bird = newBirdState)
    }

    private fun updatePipes(state: GameState): GameState {
        // Pipes move normally even during auto-fly
        val pipeSpeed = 5f + ((state.pipesPassed / 10) * 0.5f)
        val updatedPipes = state.pipes
            .map { it.copy(x = it.x - pipeSpeed) }
            .filter { it.x + it.width > 0 }
            .toMutableList()

        if (state.screenWidth > 0 && (updatedPipes.isEmpty() || updatedPipes.last().x < state.screenWidth - 460)) {
            val availableHeight = (state.screenHeight - 600).coerceAtLeast(100f)
            val reducedAmplitude = availableHeight * 0.68f
            val amplitudeOffset = 100f + (availableHeight * 0.1f)
            
            val gapTop = Random.nextFloat() * reducedAmplitude + amplitudeOffset
            updatedPipes.add(PipeState(x = state.screenWidth, gapTop = gapTop))
        }

        return state.copy(pipes = updatedPipes)
    }

    private fun updateScoring(state: GameState): GameState {
        var newScore = state.score
        var newCoins = state.coins
        var newPipesPassed = state.pipesPassed
        
        val finalPipes = state.pipes.map { pipe ->
            if (!pipe.scored && pipe.x + pipe.width < state.bird.position.x) {
                val scoreGain = if (state.multiplierActive) 2 else 1
                newScore += scoreGain
                newPipesPassed += 1
                
                if (newPipesPassed % 10 == 0) newCoins += 3
                
                pipe.copy(scored = true)
            } else pipe
        }
        
        val newLevel = (newPipesPassed / 10) + 1
        return state.copy(
            pipes = finalPipes,
            score = newScore,
            coins = newCoins,
            pipesPassed = newPipesPassed,
            level = newLevel
        )
    }

    private fun checkAndHandleGameOver(state: GameState): GameState {
        // Invincible during auto-fly start sequence or if screen size not set
        if (state.isStartSequenceActive || state.screenHeight <= 0f) return state
        
        val hasCollided = checkCollision(state.bird, state.pipes, state.screenHeight)
        
        // Multiplier also grants invincibility (helps bird fly pass pipe)
        val isGameOver = hasCollided && !state.shieldActive && !state.isAutoPlayActive && !state.multiplierActive

        if (isGameOver) {
            viewModelScope.launch {
                scoreRepository.updateHighScore(state.score)
                settingsRepository.addCoins(state.coins - _gameState.value.coins) // Only add delta
            }
        }
        return state.copy(isGameOver = isGameOver)
    }

    private fun checkCollision(bird: BirdState, pipes: List<PipeState>, screenHeight: Float): Boolean {
        // Extra guard for screenHeight
        if (screenHeight <= 0f) return false

        if (bird.position.y < 0 || bird.position.y + bird.size.height > screenHeight) return true

        val birdLeft = bird.position.x
        val birdRight = bird.position.x + bird.size.width
        val birdTop = bird.position.y
        val birdBottom = bird.position.y + bird.size.height

        for (pipe in pipes) {
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

    private fun getRevivedBirdState(state: GameState): BirdState {
        val safeY = state.bird.position.y.coerceIn(100f, (state.screenHeight - 200f).coerceAtLeast(100f))
        return state.bird.copy(
            position = state.bird.position.copy(y = safeY), 
            velocity = -5f
        )
    }

    fun purchasePowerUp(type: String, cost: Int, onSuccess: () -> Unit, onInsufficient: () -> Unit) {
        viewModelScope.launch {
            if (settingsRepository.spendCoins(cost)) {
                settingsRepository.addPowerUp(type)
                
                _gameState.update { state ->
                    var newState = state.copy(isGameOver = false, bird = getRevivedBirdState(state))

                    // Grant immediate temporary effect upon purchase (Shield/Boost/AutoPlay)
                    when (type) {
                        "shield" -> newState = newState.copy(shieldActive = true, shieldTimeLeft = 10f)
                        "multiplier" -> newState = newState.copy(multiplierActive = true, multiplierTimeLeft = 10f)
                        "autoplay", "boost" -> newState = newState.copy(isAutoPlayActive = true, autoPlayTimeLeft = 10f)
                        else -> newState
                    }
                    newState
                }
                onSuccess()
                if (gameJob?.isActive != true) runGameLoop()
            } else {
                onInsufficient()
            }
        }
    }

    fun usePowerUp(type: String) {
        viewModelScope.launch {
            if (settingsRepository.usePowerUp(type)) {
                _gameState.update { state ->
                    var newState = when (type) {
                        "shield" -> state.copy(shieldActive = true, shieldTimeLeft = 10f)
                        "multiplier" -> state.copy(multiplierActive = true, multiplierTimeLeft = 10f)
                        "autoplay", "boost" -> state.copy(isAutoPlayActive = true, autoPlayTimeLeft = 10f)
                        else -> state
                    }
                    // Revive bird if game over and power-up is used to continue
                    if (state.isGameOver) {
                        newState = newState.copy(isGameOver = false, bird = getRevivedBirdState(state))
                    }
                    newState
                }
                if (gameJob?.isActive != true) runGameLoop()
            }
        }
    }

    fun triggerAutoPlay() {
        val state = _gameState.value
        if (!state.isGameOver && state.isGameStarted) {
            _gameState.update { it.copy(isAutoPlayActive = true, autoPlayTimeLeft = 10f) }
        }
    }
}