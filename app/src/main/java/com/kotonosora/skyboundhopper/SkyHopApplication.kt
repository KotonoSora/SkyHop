package com.kotonosora.skyboundhopper

import android.app.Application
import android.content.pm.PackageManager
import com.kotonosora.skyboundhopper.ads.AdManager
import com.kotonosora.skyboundhopper.audio.AudioManager
import com.kotonosora.skyboundhopper.billing.BillingManager
import com.kotonosora.skyboundhopper.data.DataStoreAdRewardRepository
import com.kotonosora.skyboundhopper.data.ScoreRepository
import com.kotonosora.skyboundhopper.data.SettingsRepository
import com.kotonosora.skyboundhopper.domain.repository.AdRewardRepository

/**
 * Application class that provides app-scoped singletons for infrastructure objects.
 * Repositories, AudioManager, and BillingManager are created here so that their
 * lifecycle is tied to the process, not to a single Composable or ViewModel.
 */
class SkyHopApplication : Application() {

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

    override fun onCreate() {
        super.onCreate()
    }
}
