package local.flappybird.ui.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import local.flappybird.ui.viewmodel.GameViewModel
import local.flappybird.ui.viewmodel.LeaderboardViewModel
import local.flappybird.ui.viewmodel.SettingsViewModel
import local.flappybird.ui.viewmodel.ShopViewModel

class AppViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(GameViewModel::class.java) -> {
                GameViewModel(
                    getHighScoreUseCase = container.getHighScoreUseCase,
                    recordRunUseCase = container.recordRunUseCase,
                    updateRunUseCase = container.updateRunUseCase,
                    usePowerUpUseCase = container.usePowerUpUseCase,
                    coins = container.coinAccount,
                    inventory = container.inventoryStore,
                    skins = container.skinLibrary,
                    audio = container.audioSettings,
                    audioManager = container.audioManager
                ) as T
            }

            modelClass.isAssignableFrom(ShopViewModel::class.java) -> {
                ShopViewModel(
                    coinAccount = container.coinAccount,
                    skins = container.skinLibrary,
                    inventory = container.inventoryStore,
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
