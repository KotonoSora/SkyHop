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
import androidx.compose.ui.geometry.Offset

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val scoreRepository = ScoreRepository(application)
    private val settingsRepository = SettingsRepository(application)
    
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _selectedSkinId = MutableStateFlow("default")
    val selectedSkinId: StateFlow<String> = _selectedSkinId.asStateFlow()

    private var gameJob: Job? = null

    init {
        observeRepositories()
        resetGame()
        runGameLoop()
    }

    private fun observeRepositories() {
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
                _gameState.update { it.copy(shieldCount = count) } 
            }
        }
        viewModelScope.launch {
            settingsRepository.multiplierCountFlow.collect { count -> 
                _gameState.update { it.copy(multiplierCount = count) } 
            }
        }
        viewModelScope.launch {
            settingsRepository.autoPlayCountFlow.collect { count -> 
                _gameState.update { it.copy(autoPlayCount = count) } 
            }
        }
    }

    fun onScreenSizeChanged(width: Float, height: Float) {
        _gameState.update { state ->
            if (state.screenWidth == width && state.screenHeight == height) return@update state
            
            var newState = state.copy(screenWidth = width, screenHeight = height)
            
            // If screen size was previously unknown, or if the bird is at a default/centered position,
            // we center it for the new screen dimensions.
            if (state.screenHeight <= 0f || !state.isGameStarted || state.isStartSequenceActive) {
                newState = newState.copy(
                    bird = state.bird.copy(position = Offset(100f, height / 2))
                )
            }
            newState
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
                startSequenceTimeLeft = 2f,
                bird = BirdState(position = Offset(100f, if (state.screenHeight > 0) state.screenHeight / 2 else 500f))
            ) 
        }
    }

    fun jump() {
        val currentState = _gameState.value
        if (currentState.isGameOver) {
            return
        }

        _gameState.update { state ->
            if (state.isStartSequenceActive) {
                state.copy(isStartSequenceActive = false, bird = state.bird.copy(velocity = GamePhysics.JUMP_VELOCITY))
            } else if (state.isGameStarted && !state.isAutoPlayActive && !state.multiplierActive) {
                state.copy(bird = state.bird.copy(velocity = GamePhysics.JUMP_VELOCITY))
            } else {
                state
            }
        }
    }

    private fun runGameLoop() {
        gameJob?.cancel()
        gameJob = viewModelScope.launch {
            while (!_gameState.value.isGameOver && _gameState.value.isGameStarted) {
                updateGame()
                delay(GamePhysics.FRAME_RATE_MS)
            }
        }
    }

    private fun updateGame() {
        _gameState.update { currentState ->
            var state = GamePhysics.updatePowerUpTimers(currentState)
            state = GamePhysics.updatePhysics(state)
            state = GamePhysics.updatePipes(state)
            state = GamePhysics.updateScoring(state)
            state = checkAndHandleGameOver(state)
            state
        }
    }

    private fun checkAndHandleGameOver(state: GameState): GameState {
        if (state.isStartSequenceActive || state.screenHeight <= 0f || state.isGameOver) return state
        
        val hasCollided = GamePhysics.checkCollision(state)
        val shouldDie = hasCollided && !state.shieldActive && !state.isAutoPlayActive && !state.multiplierActive

        if (shouldDie) {
            viewModelScope.launch {
                scoreRepository.updateHighScore(state.score)
                settingsRepository.addCoins(state.score / 2)
            }
            return state.copy(isGameOver = true)
        }
        return state
    }

    private fun getRevivedBirdState(state: GameState): BirdState {
        val safeY = state.bird.position.y.coerceIn(100f, (state.screenHeight - 200f).coerceAtLeast(100f))
        return state.bird.copy(
            position = state.bird.position.copy(y = safeY), 
            velocity = -5f
        )
    }

    fun usePowerUp(typeId: String) {
        val type = PowerUpType.fromId(typeId) ?: return
        
        viewModelScope.launch {
            if (settingsRepository.usePowerUp(type.id)) {
                _gameState.update { state ->
                    var newState = when (type) {
                        PowerUpType.SHIELD -> state.copy(shieldActive = true, shieldTimeLeft = 10f)
                        PowerUpType.MULTIPLIER -> state.copy(multiplierActive = true, multiplierTimeLeft = 10f)
                        PowerUpType.AUTO_PLAY, PowerUpType.BOOST -> state.copy(isAutoPlayActive = true, autoPlayTimeLeft = 10f)
                    }
                    if (state.isGameOver) {
                        newState = newState.copy(isGameOver = false, bird = getRevivedBirdState(state))
                    }
                    newState
                }
                if (gameJob?.isActive != true) runGameLoop()
            }
        }
    }
}