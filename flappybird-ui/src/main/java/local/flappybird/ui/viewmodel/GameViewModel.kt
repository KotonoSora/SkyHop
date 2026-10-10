package local.flappybird.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import local.core.ui.audio.IAudioManager
import local.flappybird.ui.audio.SfxType
import local.flappybird.engine.game.GameConfig
import local.flappybird.engine.game.GamePhysics
import local.flappybird.engine.game.RewardPolicy
import local.flappybird.engine.game.newGame
import local.flappybird.engine.model.AnimalState
import local.flappybird.engine.model.GameState
import local.flappybird.engine.model.Point
import local.flappybird.engine.model.PowerUpType
import local.core.ui.data.AudioSettings
import local.core.ui.data.CoinAccount
import local.core.ui.data.InventoryStore
import local.core.ui.data.SkinLibrary
import local.flappybird.engine.usecase.GetHighScoreUseCase
import local.flappybird.engine.usecase.UpdateHighScoreUseCase
import local.flappybird.engine.usecase.UsePowerUpUseCase
import local.flappybird.shop.SkinIds
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class GameIntent {
    data class StartGame(
        val level: Int = 1,
        val isEndless: Boolean = true,
        val isDailyChallenge: Boolean = false
    ) : GameIntent()

    object Jump : GameIntent()
    data class UsePowerUp(val typeId: String) : GameIntent()
    data class ScreenSizeChanged(val width: Float, val height: Float) : GameIntent()
    object ScreenHidden : GameIntent()
    object ScreenVisible : GameIntent()
}

class GameViewModel(
    private val getHighScoreUseCase: GetHighScoreUseCase,
    private val updateHighScoreUseCase: UpdateHighScoreUseCase,
    private val usePowerUpUseCase: UsePowerUpUseCase,
    private val coins: CoinAccount,
    private val inventory: InventoryStore,
    private val skins: SkinLibrary,
    private val audio: AudioSettings,
    private val audioManager: IAudioManager
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

    fun onIntent(intent: GameIntent) {
        when (intent) {
            is GameIntent.StartGame -> startGame(
                intent.level,
                intent.isEndless,
                intent.isDailyChallenge
            )

            GameIntent.Jump -> jump()
            is GameIntent.UsePowerUp -> usePowerUp(intent.typeId)
            is GameIntent.ScreenSizeChanged -> onScreenSizeChanged(intent.width, intent.height)
            GameIntent.ScreenHidden -> onGameScreenHidden()
            GameIntent.ScreenVisible -> onGameScreenVisible()
        }
    }

    private fun observeRepositories() {
        viewModelScope.launch {
            getHighScoreUseCase().collect { highScore ->
                _gameState.update { it.copy(highScore = highScore) }
            }
        }
        viewModelScope.launch {
            skins.selectedSkinFlow.collect { skinId ->
                _selectedSkinId.value = skinId
            }
        }
        viewModelScope.launch {
            coins.coinsFlow.collect { coinBalance ->
                _gameState.update { it.copy(coins = coinBalance) }
            }
        }
        viewModelScope.launch {
            inventory.inventoryCountFlow(PowerUpType.SHIELD.id).collect { count ->
                _gameState.update { it.copy(shieldCount = count) }
            }
        }
        viewModelScope.launch {
            inventory.inventoryCountFlow(PowerUpType.MULTIPLIER.id).collect { count ->
                _gameState.update { it.copy(multiplierCount = count) }
            }
        }
        viewModelScope.launch {
            inventory.inventoryCountFlow(PowerUpType.AUTO_PLAY.id).collect { count ->
                _gameState.update { it.copy(autoPlayCount = count) }
            }
        }

        // Observe Audio Preferences using combine
        viewModelScope.launch {
            combine(
                audio.musicEnabledFlow,
                audio.sfxEnabledFlow
            ) { musicEnabled, sfxEnabled ->
                currentMusicEnabled = musicEnabled
                currentSfxEnabled = sfxEnabled
                Pair(musicEnabled, sfxEnabled)
            }.collect { (musicEnabled, sfxEnabled) ->
                audioManager.updateSettings(musicEnabled = musicEnabled, sfxEnabled = sfxEnabled)
            }
        }
    }

    private fun onScreenSizeChanged(width: Float, height: Float) {
        _gameState.update { state ->
            if (state.screenWidth == width && state.screenHeight == height) return@update state

            var newState = state.copy(screenWidth = width, screenHeight = height)

            if (state.screenHeight <= 0f || !state.isGameStarted || state.isStartSequenceActive) {
                newState = newState.copy(
                    animal = state.animal.copy(position = Point(100f, height / 2))
                )
            }
            newState
        }
    }

    private fun startGame(
        level: Int = 1,
        isEndless: Boolean = true,
        isDailyChallenge: Boolean = false
    ) {
        if (currentMusicEnabled) audioManager.playBgm()
        if (currentSfxEnabled) audioManager.playSfx(SfxType.START.rawResId)

        resetGame(level, isEndless, isDailyChallenge)
        runGameLoop()
    }

    private fun resetGame(level: Int, isEndless: Boolean, isDailyChallenge: Boolean) {
        _gameState.update { state ->
            newGame(state, GameConfig(level, isEndless, isDailyChallenge))
        }
    }

    private fun jump() {
        val currentState = _gameState.value
        if (currentState.isGameOver) {
            return
        }

        _gameState.update { state ->
            if (state.isStartSequenceActive) {
                state.copy(
                    isStartSequenceActive = false,
                    animal = state.animal.copy(velocity = GamePhysics.JUMP_VELOCITY)
                )
            } else if (state.isGameStarted && !state.isAutoPlayActive && !state.multiplierActive) {
                state.copy(animal = state.animal.copy(velocity = GamePhysics.JUMP_VELOCITY))
            } else {
                state
            }
        }

        if (currentState.isGameStarted && !currentState.isStartSequenceActive) {
            if (currentSfxEnabled) audioManager.playSfx(SfxType.TOUCH.rawResId)
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
        val oldScore = _gameState.value.score
        val oldCoins = _gameState.value.coins
        _gameState.update { currentState ->
            var state = GamePhysics.updatePowerUpTimers(currentState)
            state = GamePhysics.updatePhysics(state)
            state = GamePhysics.updatePipes(state)
            state = GamePhysics.updateScoring(state)
            state = checkAndHandleWin(state)
            state = checkAndHandleGameOver(state)
            state
        }
        val newScore = _gameState.value.score
        val newCoins = _gameState.value.coins

        if (newScore > 0 && oldScore / 100 != newScore / 100) {
            if (currentSfxEnabled) audioManager.playSfx(SfxType.MILESTONE.rawResId)
        }

        if (newCoins > oldCoins) {
            if (currentSfxEnabled) audioManager.playSfx(SfxType.COLLECT.rawResId)
        }
    }

    private fun checkAndHandleWin(state: GameState): GameState {
        if (state.isEndless || state.isGameOver || state.isWin) return state
        if (state.score >= state.targetScore) {
            if (currentMusicEnabled) audioManager.stopBgm()
            if (currentSfxEnabled) audioManager.playSfx(SfxType.WIN.rawResId)

            val reward = RewardPolicy.winReward(state.level, state.isDailyChallenge)
            viewModelScope.launch {
                coins.addCoins(reward)
                updateHighScoreUseCase(state.score, reward)
            }
            return state.copy(isWin = true, rewardCoins = reward)
        }
        return state
    }

    private fun checkAndHandleGameOver(state: GameState): GameState {
        if (state.isStartSequenceActive || state.screenHeight <= 0f || state.isGameOver || state.isWin) return state

        val hasCollided = GamePhysics.checkCollision(state)
        val shouldDie =
            hasCollided && !state.shieldActive && !state.isAutoPlayActive && !state.multiplierActive

        if (shouldDie) {
            if (currentSfxEnabled) audioManager.playSfx(SfxType.LOSE.rawResId)
            audioManager.stopBgm()

            val consolation = RewardPolicy.gameOverReward(state.score)
            viewModelScope.launch {
                updateHighScoreUseCase(state.score, consolation)
                coins.addCoins(consolation)
            }
            return state.copy(isGameOver = true)
        }
        return state
    }

    private fun getRevivedAnimalState(state: GameState): AnimalState {
        val safeY =
            state.animal.position.y.coerceIn(100f, (state.screenHeight - 200f).coerceAtLeast(100f))
        return state.animal.copy(
            position = state.animal.position.copy(y = safeY),
            velocity = -5f
        )
    }

    private fun usePowerUp(typeId: String) {
        val type = PowerUpType.fromId(typeId) ?: return
        val currentState = _gameState.value

        if (type == PowerUpType.SHIELD && currentState.shieldDisabled) return

        viewModelScope.launch {
            if (usePowerUpUseCase(type.id)) {
                _gameState.update { state ->
                    var newState = when (type) {
                        PowerUpType.SHIELD -> state.copy(shieldActive = true, shieldTimeLeft = 10f)
                        PowerUpType.MULTIPLIER -> state.copy(
                            multiplierActive = true,
                            multiplierTimeLeft = 10f
                        )

                        PowerUpType.AUTO_PLAY, PowerUpType.BOOST -> state.copy(
                            isAutoPlayActive = true,
                            autoPlayTimeLeft = 10f
                        )
                    }
                    if (state.isGameOver) {
                        if (currentMusicEnabled) {
                            audioManager.playBgm()
                        }
                        newState =
                            newState.copy(isGameOver = false, animal = getRevivedAnimalState(state))
                    }
                    newState
                }
                if (gameJob?.isActive != true) runGameLoop()
            }
        }
    }

    private fun onGameScreenHidden() {
        gameJob?.cancel()
        audioManager.stopBgm()
    }

    private fun onGameScreenVisible() {
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
