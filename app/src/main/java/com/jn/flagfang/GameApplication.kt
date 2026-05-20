package com.jn.flagfang

import android.app.Application
import android.content.pm.PackageManager
import com.jn.flagfang.ads.AdManager
import com.jn.flagfang.audio.AudioManager
import com.jn.flagfang.billing.BillingManager
import com.jn.flagfang.data.DataStoreAdRewardRepository
import com.jn.flagfang.feature.shop.ScoreRepository
import com.jn.flagfang.feature.shop.SettingsRepository
import com.jn.flagfang.domain.repository.AdRewardRepository

/**
 * Application class that provides app-scoped singletons for infrastructure objects.
 * Repositories, AudioManager, and BillingManager are created here so that their
 * lifecycle is tied to the process, not to a single Composable or ViewModel.
 */
class GameApplication : Application() {

    val appVersionName: String by lazy {
        try {
            packageManager.getPackageInfo(packageName, 0).versionName ?: "1.0"
        } catch (_: PackageManager.NameNotFoundException) {
            "1.0"
        }
    }

    val settingsRepository: SettingsRepository by lazy { SettingsRepository(this) }
    val scoreRepository: ScoreRepository by lazy { ScoreRepository(this) }
    val audioManager: AudioManager by lazy { AudioManager(this) }
    val adManager: AdManager by lazy { AdManager(this) }
    val adRewardRepository: AdRewardRepository by lazy { DataStoreAdRewardRepository(this) }
    val billingManager: BillingManager by lazy {
        BillingManager(this, settingsRepository)
    }

}
