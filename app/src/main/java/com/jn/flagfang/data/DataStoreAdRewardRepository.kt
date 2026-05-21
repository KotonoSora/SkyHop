package com.jn.flagfang.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jn.flagfang.domain.repository.AdRewardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

private val Context.adRewardDataStore: DataStore<Preferences> by preferencesDataStore(name = "ad_reward_settings")

class DataStoreAdRewardRepository(
    context: Context,
    private val dataStore: DataStore<Preferences> = context.adRewardDataStore
) : AdRewardRepository {
    private val lastWatchedTimestampKey = longPreferencesKey("last_watched_timestamp")

    override val canWatchAdFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            val lastWatched = preferences[lastWatchedTimestampKey] ?: 0L
            val lastWatchedDate = Calendar.getInstance().apply { timeInMillis = lastWatched }
            val now = Calendar.getInstance()

            // Check if it's a different day
            (lastWatchedDate[Calendar.YEAR] != now[Calendar.YEAR]) ||
                    (lastWatchedDate[Calendar.DAY_OF_YEAR] != now[Calendar.DAY_OF_YEAR])
        }

    override suspend fun recordAdWatched() {
        dataStore.edit { preferences ->
            preferences[lastWatchedTimestampKey] = System.currentTimeMillis()
        }
    }
}
