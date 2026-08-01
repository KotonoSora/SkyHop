package com.kotonosora.zamstu.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kotonosora.zamstu.viewmodel.GameViewModel
import com.kotonosora.zamstu.viewmodel.LeaderboardViewModel
import com.kotonosora.zamstu.viewmodel.SettingsViewModel
import com.kotonosora.zamstu.viewmodel.ShopViewModel

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
