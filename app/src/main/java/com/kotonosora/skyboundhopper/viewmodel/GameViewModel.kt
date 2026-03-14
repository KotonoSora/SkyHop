package com.kotonosora.skyboundhopper.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kotonosora.skyboundhopper.audio.AudioManager
import com.kotonosora.skyboundhopper.audio.SfxType
import com.kotonosora.skyboundhopper.data.ScoreRepository
import com.kotonosora.skyboundhopper.data.SettingsRepository
import com.kotonosora.skyboundhopper.model.BirdState
import com.kotonosora.skyboundhopper.model.GamePhysics
import com.kotonosora.skyboundhopper.model.GameState
import com.kotonosora.skyboundhopper.model.PowerUpType
import com.kotonosora.skyboundhopper.model.SkinIds
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(
    private val scoreRepository: ScoreRepository,
    private val settingsRepository: SettingsRepository,
    private val audioManager: AudioManager
) : ViewModel() {
    
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _selectedSkinId = MutableStateFlow(SkinIds.SKIN_DEFAULT_ID)
    val selectedSkinId: StateFlow<String> = _selectedSkinId.asStateFlow()

    private var currentMusicEnabled: Boolean = true
    private var currentSfxEnabled: Boolean = true

    private var gameJob: Job? = null

    init {
        observeRepositories()
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

        // Task 4.1: Observe Audio Preferences using combine
        viewModelScope.launch {
            combine(
                settingsRepository.musicEnabledFlow,
                settingsRepository.sfxEnabledFlow
            ) { musicEnabled, sfxEnabled ->
                currentMusicEnabled = musicEnabled
                currentSfxEnabled = sfxEnabled
                Pair(musicEnabled, sfxEnabled)
            }.collect { (musicEnabled, sfxEnabled) ->
                audioManager.updateSettings(musicEnabled = musicEnabled, sfxEnabled = sfxEnabled)
            }
        }
    }

    fun onScreenSizeChanged(width: Float, height: Float) {
        _gameState.update { state ->
            if (state.screenWidth == width && state.screenHeight == height) return@update state
            
            var newState = state.copy(screenWidth = width, screenHeight = height)
            
            if (state.screenHeight <= 0f || !state.isGameStarted || state.isStartSequenceActive) {
                newState = newState.copy(
                    bird = state.bird.copy(position = Offset(100f, height / 2))
                )
            }
            newState
        }
    }

    fun startGame() {
        if (currentMusicEnabled) audioManager.playBgm()
        if (currentSfxEnabled) audioManager.playSfx(SfxType.START)

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

        if (currentState.isGameStarted && !currentState.isStartSequenceActive) {
            if (currentSfxEnabled) audioManager.playSfx(SfxType.TOUCH)
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
            if (currentSfxEnabled) audioManager.playSfx(SfxType.GAMEOVER)
            audioManager.stopBgm()

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
                        if (currentMusicEnabled) {
                            audioManager.playBgm()
                        }
                        newState = newState.copy(isGameOver = false, bird = getRevivedBirdState(state))
                    }
                    newState
                }
                if (gameJob?.isActive != true) runGameLoop()
            }
        }
    }

    fun onGameScreenHidden() {
        gameJob?.cancel()
        audioManager.stopBgm()
    }

    fun onGameScreenVisible() {
        val state = _gameState.value
        if (!state.isGameStarted || state.isGameOver) return

        if (gameJob?.isActive != true) {
            runGameLoop()
        }
        if (currentMusicEnabled) {
            audioManager.playBgm()
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioManager.stopBgm()
    }
}

class GameViewModelFactory(
    private val scoreRepository: ScoreRepository,
    private val settingsRepository: SettingsRepository,
    private val audioManager: AudioManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(scoreRepository, settingsRepository, audioManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}