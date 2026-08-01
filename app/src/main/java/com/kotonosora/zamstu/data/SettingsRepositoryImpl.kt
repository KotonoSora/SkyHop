package com.kotonosora.zamstu.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kotonosora.zamstu.core.AppConstants
import com.kotonosora.zamstu.domain.model.PowerUpType
import com.kotonosora.zamstu.domain.repository.SettingsRepository
import com.kotonosora.zamstu.feature.shop.SkinIds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    companion object {
        private const val INITIAL_COINS = AppConstants.DEFAULT_INITIAL_COINS
        private val SELECTED_SKIN_KEY = stringPreferencesKey("selected_skin")
        private val COINS_KEY = intPreferencesKey("coins")
        private val PURCHASED_ITEMS_KEY = stringSetPreferencesKey("purchased_items")
        private val SHIELD_COUNT_KEY = intPreferencesKey("shield_count")
        private val MULTIPLIER_COUNT_KEY = intPreferencesKey("multiplier_count")
        private val AUTOPLAY_COUNT_KEY = intPreferencesKey("autoplay_count")
        private val MUSIC_ENABLED_KEY = booleanPreferencesKey("music_enabled")
        private val SFX_ENABLED_KEY = booleanPreferencesKey("sfx_enabled")
    }

    private val POWER_UP_KEYS = mapOf(
        PowerUpType.SHIELD to SHIELD_COUNT_KEY,
        PowerUpType.MULTIPLIER to MULTIPLIER_COUNT_KEY,
        PowerUpType.AUTO_PLAY to AUTOPLAY_COUNT_KEY,
        PowerUpType.BOOST to AUTOPLAY_COUNT_KEY
    )

    override val selectedSkinFlow: Flow<String> = context.settingsDataStore.data
        .map { preferences ->
            preferences[SELECTED_SKIN_KEY] ?: SkinIds.SKIN_DEFAULT_ID
        }

    override val coinsFlow: Flow<Int> = context.settingsDataStore.data
        .map { preferences ->
            preferences[COINS_KEY] ?: INITIAL_COINS
        }

    override val purchasedItemsFlow: Flow<Set<String>> = context.settingsDataStore.data
        .map { preferences ->
            preferences[PURCHASED_ITEMS_KEY] ?: setOf(SkinIds.SKIN_DEFAULT_ID)
        }

    override val shieldCountFlow: Flow<Int> = context.settingsDataStore.data
        .map { preferences -> preferences[SHIELD_COUNT_KEY] ?: 0 }

    override val multiplierCountFlow: Flow<Int> = context.settingsDataStore.data
        .map { preferences -> preferences[MULTIPLIER_COUNT_KEY] ?: 0 }

    override val autoPlayCountFlow: Flow<Int> = context.settingsDataStore.data
        .map { preferences -> preferences[AUTOPLAY_COUNT_KEY] ?: 0 }

    override val musicEnabledFlow: Flow<Boolean> = context.settingsDataStore.data
        .map { preferences ->
            preferences[MUSIC_ENABLED_KEY] ?: true
        }

    override val sfxEnabledFlow: Flow<Boolean> = context.settingsDataStore.data
        .map { preferences ->
            preferences[SFX_ENABLED_KEY] ?: true
        }

    override suspend fun updateSelectedSkin(skinId: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[SELECTED_SKIN_KEY] = skinId
        }
    }

    override suspend fun addCoins(amount: Int) {
        context.settingsDataStore.edit { preferences ->
            val current = preferences[COINS_KEY] ?: INITIAL_COINS
            preferences[COINS_KEY] = current + amount
        }
    }

    override suspend fun spendCoins(amount: Int): Boolean {
        var success = false
        context.settingsDataStore.edit { preferences ->
            val current = preferences[COINS_KEY] ?: INITIAL_COINS
            if (current >= amount) {
                preferences[COINS_KEY] = current - amount
                success = true
            }
        }
        return success
    }

    override suspend fun purchaseItem(itemId: String) {
        context.settingsDataStore.edit { preferences ->
            val current = preferences[PURCHASED_ITEMS_KEY] ?: setOf(SkinIds.SKIN_DEFAULT_ID)
            preferences[PURCHASED_ITEMS_KEY] = current + itemId
        }
    }

    override suspend fun addPowerUp(typeId: String) {
        val type = PowerUpType.fromId(typeId) ?: return
        val key = POWER_UP_KEYS[type] ?: return
        context.settingsDataStore.edit { preferences ->
            val current = preferences[key] ?: 0
            preferences[key] = current + 1
        }
    }

    override suspend fun usePowerUp(typeId: String): Boolean {
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

    override suspend fun toggleMusic(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[MUSIC_ENABLED_KEY] = enabled
        }
    }

    override suspend fun toggleSfx(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[SFX_ENABLED_KEY] = enabled
        }
    }
}
