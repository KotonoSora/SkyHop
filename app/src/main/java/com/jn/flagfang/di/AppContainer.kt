package com.jn.flagfang.di

import android.content.Context
import android.content.pm.PackageManager
import com.jn.flagfang.audio.AudioManager
import com.jn.flagfang.data.ScoreRepositoryImpl
import com.jn.flagfang.data.SettingsRepositoryImpl
import com.jn.flagfang.data.billing.BillingManagerImpl
import com.jn.flagfang.domain.repository.BillingRepository
import com.jn.flagfang.domain.repository.ScoreRepository
import com.jn.flagfang.domain.repository.SettingsRepository
import com.jn.flagfang.domain.usecase.GetAudioSettingsUseCase
import com.jn.flagfang.domain.usecase.GetCoinsUseCase
import com.jn.flagfang.domain.usecase.GetHighScoreUseCase
import com.jn.flagfang.domain.usecase.GetScoreHistoryUseCase
import com.jn.flagfang.domain.usecase.ToggleAudioUseCase
import com.jn.flagfang.domain.usecase.UpdateHighScoreUseCase
import com.jn.flagfang.domain.usecase.UsePowerUpUseCase

/**
 * Dependency Injection container for the application.
 */
interface AppContainer {
    val scoreRepository: ScoreRepository
    val settingsRepository: SettingsRepository
    val audioManager: AudioManager
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

    override val audioManager: AudioManager by lazy {
        AudioManager(context)
    }

    override val billingRepository: BillingRepository by lazy {
        BillingManagerImpl(context, settingsRepository)
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
