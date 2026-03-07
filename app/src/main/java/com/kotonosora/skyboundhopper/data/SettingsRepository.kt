package com.kotonosora.skyboundhopper.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class SettingsRepository(private val context: Context) {
    private val SELECTED_SKIN_KEY = stringPreferencesKey("selected_skin")
    private val COINS_KEY = intPreferencesKey("coins")
    private val PURCHASED_ITEMS_KEY = stringSetPreferencesKey("purchased_items")
    private val SHIELD_COUNT_KEY = intPreferencesKey("shield_count")
    private val MULTIPLIER_COUNT_KEY = intPreferencesKey("multiplier_count")

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

    suspend fun addPowerUp(type: String) {
        context.settingsDataStore.edit { preferences ->
            if (type == "shield") {
                val current = preferences[SHIELD_COUNT_KEY] ?: 0
                preferences[SHIELD_COUNT_KEY] = current + 1
            } else if (type == "multiplier") {
                val current = preferences[MULTIPLIER_COUNT_KEY] ?: 0
                preferences[MULTIPLIER_COUNT_KEY] = current + 1
            }
        }
    }

    suspend fun usePowerUp(type: String): Boolean {
        var success = false
        context.settingsDataStore.edit { preferences ->
            if (type == "shield") {
                val current = preferences[SHIELD_COUNT_KEY] ?: 0
                if (current > 0) {
                    preferences[SHIELD_COUNT_KEY] = current - 1
                    success = true
                }
            } else if (type == "multiplier") {
                val current = preferences[MULTIPLIER_COUNT_KEY] ?: 0
                if (current > 0) {
                    preferences[MULTIPLIER_COUNT_KEY] = current - 1
                    success = true
                }
            }
        }
        return success
    }
}
