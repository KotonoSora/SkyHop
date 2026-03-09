package com.kotonosora.skyboundhopper.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kotonosora.skyboundhopper.model.PowerUpType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class SettingsRepository(private val context: Context) {
    private val SELECTED_SKIN_KEY = stringPreferencesKey("selected_skin")
    private val COINS_KEY = intPreferencesKey("coins")
    private val PURCHASED_ITEMS_KEY = stringSetPreferencesKey("purchased_items")
    private val SHIELD_COUNT_KEY = intPreferencesKey("shield_count")
    private val MULTIPLIER_COUNT_KEY = intPreferencesKey("multiplier_count")
    private val AUTOPLAY_COUNT_KEY = intPreferencesKey("autoplay_count")

    private val POWER_UP_KEYS = mapOf(
        PowerUpType.SHIELD to SHIELD_COUNT_KEY,
        PowerUpType.MULTIPLIER to MULTIPLIER_COUNT_KEY,
        PowerUpType.AUTO_PLAY to AUTOPLAY_COUNT_KEY,
        PowerUpType.BOOST to AUTOPLAY_COUNT_KEY
    )

    val selectedSkinFlow: Flow<String> = context.settingsDataStore.data
        .map { preferences ->
            preferences[SELECTED_SKIN_KEY] ?: "default"
        }

    val coinsFlow: Flow<Int> = context.settingsDataStore.data
        .map { preferences ->
            preferences[COINS_KEY] ?: 10000
        }

    val purchasedItemsFlow: Flow<Set<String>> = context.settingsDataStore.data
        .map { preferences ->
            preferences[PURCHASED_ITEMS_KEY] ?: setOf("default")
        }

    val shieldCountFlow: Flow<Int> = context.settingsDataStore.data
        .map { preferences -> preferences[SHIELD_COUNT_KEY] ?: 0 }

    val multiplierCountFlow: Flow<Int> = context.settingsDataStore.data
        .map { preferences -> preferences[MULTIPLIER_COUNT_KEY] ?: 0 }

    val autoPlayCountFlow: Flow<Int> = context.settingsDataStore.data
        .map { preferences -> preferences[AUTOPLAY_COUNT_KEY] ?: 0 }

    suspend fun updateSelectedSkin(skinId: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[SELECTED_SKIN_KEY] = skinId
        }
    }

    suspend fun addCoins(amount: Int) {
        context.settingsDataStore.edit { preferences ->
            val current = preferences[COINS_KEY] ?: 10000
            preferences[COINS_KEY] = current + amount
        }
    }

    suspend fun spendCoins(amount: Int): Boolean {
        var success = false
        context.settingsDataStore.edit { preferences ->
            val current = preferences[COINS_KEY] ?: 10000
            if (current >= amount) {
                preferences[COINS_KEY] = current - amount
                success = true
            }
        }
        return success
    }

    suspend fun purchaseItem(itemId: String) {
        context.settingsDataStore.edit { preferences ->
            val current = preferences[PURCHASED_ITEMS_KEY] ?: setOf("default")
            preferences[PURCHASED_ITEMS_KEY] = current + itemId
        }
    }

    suspend fun addPowerUp(typeId: String) {
        val type = PowerUpType.fromId(typeId) ?: return
        val key = POWER_UP_KEYS[type] ?: return
        context.settingsDataStore.edit { preferences ->
            val current = preferences[key] ?: 0
            preferences[key] = current + 1
        }
    }

    suspend fun usePowerUp(typeId: String): Boolean {
        val type = PowerUpType.fromId(typeId) ?: return false
        val key = POWER_UP_KEYS[type] ?: return false
        var success = false
        context.settingsDataStore.edit { preferences ->
            val current = preferences[key] ?: 0
            if (current > 0) {
                preferences[key] = current - 1
                success = true
            }
        }
        return success
    }
}