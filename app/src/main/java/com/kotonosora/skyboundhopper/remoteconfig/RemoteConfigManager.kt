package com.kotonosora.skyboundhopper.remoteconfig

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.kotonosora.skyboundhopper.BuildConfig
import com.kotonosora.skyboundhopper.model.CoinPackIds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class RemoteConfigManager {

    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    // Initialize with empty list so BillingManager waits until fetch completes
    private val _coinProductIds = MutableStateFlow<List<String>>(emptyList())
    val coinProductIds = _coinProductIds.asStateFlow()

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(mapOf(
            KEY_COIN_PRODUCT_IDS to CoinPackIds.ALL.joinToString(",")
        ))
    }

    suspend fun fetchAndActivate() {
        try {
            val activated = remoteConfig.fetchAndActivate().await()
            if (activated || remoteConfig.info.lastFetchStatus == FirebaseRemoteConfig.LAST_FETCH_STATUS_SUCCESS) {
                updateConfigs()
            }
        } catch (e: Exception) {
            Log.e("RemoteConfigManager", "Error fetching remote config", e)
        } finally {
            // Always update configs so we apply defaults if network fails
            if (_coinProductIds.value.isEmpty()) {
                updateConfigs()
            }
        }
    }

    private fun updateConfigs() {
        val idsString = remoteConfig.getString(KEY_COIN_PRODUCT_IDS)
        if (idsString.isNotEmpty()) {
            val ids = idsString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            if (ids.isNotEmpty()) {
                _coinProductIds.value = ids
            }
        }
    }

    companion object {
        // Adding a project-specific prefix to differentiate this app's keys
        // from other projects sharing the same Firebase instance
        private const val PROJECT_PREFIX = "skyhop_"
        private const val KEY_COIN_PRODUCT_IDS = "${PROJECT_PREFIX}coin_product_ids"
    }
}
