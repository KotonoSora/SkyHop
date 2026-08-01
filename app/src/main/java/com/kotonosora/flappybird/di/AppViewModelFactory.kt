package com.kotonosora.flappybird.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kotonosora.flappybird.viewmodel.GameViewModel
import com.kotonosora.flappybird.viewmodel.LeaderboardViewModel
import com.kotonosora.flappybird.viewmodel.SettingsViewModel
import com.kotonosora.flappybird.viewmodel.ShopViewModel

class AppViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(GameViewModel::class.java) -> {
                GameViewModel(
                    getHighScoreUseCase = container.getHighScoreUseCase,
                    updateHighScoreUseCase = container.updateHighScoreUseCase,
                    usePowerUpUseCase = container.usePowerUpUseCase,
                    settingsRepository = container.settingsRepository,
                    audioManager = container.audioManager
                ) as T
            }

            modelClass.isAssignableFrom(ShopViewModel::class.java) -> {
                ShopViewModel(
                    settingsRepository = container.settingsRepository,
                    billingRepository = container.billingRepository,
                    audioManager = container.audioManager
                ) as T
            }

            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(
                    getAudioSettingsUseCase = container.getAudioSettingsUseCase,
                    toggleAudioUseCase = container.toggleAudioUseCase,
                    getCoinsUseCase = container.getCoinsUseCase,
                    appVersionName = container.appVersionName
                ) as T
            }

            modelClass.isAssignableFrom(LeaderboardViewModel::class.java) -> {
                LeaderboardViewModel(
                    getScoreHistoryUseCase = container.getScoreHistoryUseCase,
                    getCoinsUseCase = container.getCoinsUseCase
                ) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
