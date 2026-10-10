package local.flappybird.ui.di

import android.content.Context
import android.content.pm.PackageManager
import local.flappybird.ui.R
import local.flappybird.ui.audio.SfxType
import local.core.ui.audio.AudioManager
import local.core.ui.audio.IAudioManager
import local.core.ui.data.ScoreRepositoryImpl
import local.core.ui.data.SettingsRepositoryImpl
import local.core.ui.data.CoinAccount
import local.core.ui.data.InventoryStore
import local.core.ui.data.SkinLibrary
import local.core.ui.data.AudioSettings
import local.core.billing.BillingConfig
import local.core.billing.BillingManagerImpl
import local.core.billing.MockBillingManager
import local.core.billing.BillingRepository
import local.core.shop.coins.CoinPackIds
import local.core.shop.coins.CoinPacks
import local.flappybird.engine.repo.ScoreRepository
import local.core.ui.usecase.GetAudioSettingsUseCase
import local.core.ui.usecase.GetCoinsUseCase
import local.flappybird.engine.usecase.GetHighScoreUseCase
import local.flappybird.engine.usecase.GetScoreHistoryUseCase
import local.core.ui.usecase.ToggleAudioUseCase
import local.flappybird.engine.usecase.UpdateHighScoreUseCase
import local.flappybird.engine.usecase.UsePowerUpUseCase
import local.flappybird.shop.SkinIds
import local.flappybird.ui.adapters.asCoinSink
import local.flappybird.ui.adapters.asPowerUpStore
import local.flappybird.ui.adapters.asScoreRepository

/**
 * Dependency Injection container for the application.
 */
interface AppContainer {
    val scoreRepository: ScoreRepository
    val coinAccount: CoinAccount
    val inventoryStore: InventoryStore
    val skinLibrary: SkinLibrary
    val audioSettings: AudioSettings
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
class DefaultAppContainer(
    private val context: Context,
    private val isDebug: Boolean
) : AppContainer {

    /**
     * Single DataStore-backed store, exposed through narrow ports so each
     * consumer depends only on what it uses.
     */
    private val settingsStore: SettingsRepositoryImpl by lazy {
        SettingsRepositoryImpl(context, SkinIds.SKIN_DEFAULT_ID)
    }

    override val coinAccount: CoinAccount by lazy { settingsStore }
    override val inventoryStore: InventoryStore by lazy { settingsStore }
    override val skinLibrary: SkinLibrary by lazy { settingsStore }
    override val audioSettings: AudioSettings by lazy { settingsStore }

    override val scoreRepository: ScoreRepository by lazy {
        ScoreRepositoryImpl(context).asScoreRepository()
    }

    override val audioManager: IAudioManager by lazy {
        AudioManager(
            context,
            R.raw.bg_music,
            SfxType.entries.map { it.rawResId }
        )
    }

    override val billingRepository: BillingRepository by lazy {
        val wallet = coinAccount.asCoinSink()
        if (isDebug) {
            MockBillingManager(wallet, CoinPacks.mockProducts, CoinPacks::getCoinAmount)
        } else {
            BillingManagerImpl(
                context,
                wallet,
                BillingConfig(CoinPackIds.ALL, CoinPacks::getCoinAmount)
            )
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
        GetAudioSettingsUseCase(audioSettings)
    }

    override val getCoinsUseCase: GetCoinsUseCase by lazy {
        GetCoinsUseCase(coinAccount)
    }

    override val toggleAudioUseCase: ToggleAudioUseCase by lazy {
        ToggleAudioUseCase(audioSettings)
    }

    override val usePowerUpUseCase: UsePowerUpUseCase by lazy {
        UsePowerUpUseCase(inventoryStore.asPowerUpStore())
    }
}
