package com.kotonosora.zamstu.di

import android.content.Context
import android.content.pm.PackageManager
import com.kotonosora.zamstu.BuildConfig
import com.kotonosora.zamstu.audio.AudioManager
import com.kotonosora.zamstu.audio.IAudioManager
import com.kotonosora.zamstu.data.ScoreRepositoryImpl
import com.kotonosora.zamstu.data.SettingsRepositoryImpl
import com.kotonosora.zamstu.data.billing.BillingManagerImpl
import com.kotonosora.zamstu.data.billing.MockBillingManager
import com.kotonosora.zamstu.domain.repository.BillingRepository
import com.kotonosora.zamstu.domain.repository.ScoreRepository
import com.kotonosora.zamstu.domain.repository.SettingsRepository
import com.kotonosora.zamstu.domain.usecase.GetAudioSettingsUseCase
import com.kotonosora.zamstu.domain.usecase.GetCoinsUseCase
import com.kotonosora.zamstu.domain.usecase.GetHighScoreUseCase
import com.kotonosora.zamstu.domain.usecase.GetScoreHistoryUseCase
import com.kotonosora.zamstu.domain.usecase.ToggleAudioUseCase
import com.kotonosora.zamstu.domain.usecase.UpdateHighScoreUseCase
import com.kotonosora.zamstu.domain.usecase.UsePowerUpUseCase

/**
 * Dependency Injection container for the application.
 */
interface AppContainer {
    val scoreRepository: ScoreRepository
    val settingsRepository: SettingsRepository
    val audioManager: IAudioManager
    val billingRepository: BillingRepository
    val appVersionName: String

    val getHighScoreUseCase: GetHighScoreUseCase
    val getScoreHistoryUseCase: GetScoreHistoryUseCase
    val updateHighScoreUseCase: UpdateHighScoreUseCase
    val getAudioSettingsUseCase: GetAudioSettingsUseCase
    val getCoinsUseCase: GetCoinsUseCase
    val toggleAudioUseCase: ToggleAudioUseCase
    val usePowerUpUseCase: UsePowerUpUseCase
}

/**
 * Default implementation of the [AppContainer] interface.
 */
class DefaultAppContainer(private val context: Context) : AppContainer {

    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(context)
    }

    override val scoreRepository: ScoreRepository by lazy {
        ScoreRepositoryImpl(context)
    }

    override val audioManager: IAudioManager by lazy {
        AudioManager(context)
    }

    override val billingRepository: BillingRepository by lazy {
        if (BuildConfig.DEBUG) {
            MockBillingManager(settingsRepository)
        } else {
            BillingManagerImpl(context, settingsRepository)
        }
    }

    override val appVersionName: String by lazy {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
        } catch (_: PackageManager.NameNotFoundException) {
            "1.0"
        }
    }

    override val getHighScoreUseCase: GetHighScoreUseCase by lazy {
        GetHighScoreUseCase(scoreRepository)
    }

    override val getScoreHistoryUseCase: GetScoreHistoryUseCase by lazy {
        GetScoreHistoryUseCase(scoreRepository)
    }

    override val updateHighScoreUseCase: UpdateHighScoreUseCase by lazy {
        UpdateHighScoreUseCase(scoreRepository)
    }

    override val getAudioSettingsUseCase: GetAudioSettingsUseCase by lazy {
        GetAudioSettingsUseCase(settingsRepository)
    }

    override val getCoinsUseCase: GetCoinsUseCase by lazy {
        GetCoinsUseCase(settingsRepository)
    }

    override val toggleAudioUseCase: ToggleAudioUseCase by lazy {
        ToggleAudioUseCase(settingsRepository)
    }

    override val usePowerUpUseCase: UsePowerUpUseCase by lazy {
        UsePowerUpUseCase(settingsRepository)
    }
}
