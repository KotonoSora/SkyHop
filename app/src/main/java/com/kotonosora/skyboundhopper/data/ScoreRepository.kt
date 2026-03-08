package com.kotonosora.skyboundhopper.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.max

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

private val HIGH_SCORE_KEY = intPreferencesKey("high_score")

class ScoreRepository(private val context: Context) {

    val highScoreFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[HIGH_SCORE_KEY] ?: 0
        }

    suspend fun updateHighScore(score: Int) {
        context.dataStore.edit { preferences ->
            val currentHighScore = preferences[HIGH_SCORE_KEY] ?: 0
            preferences[HIGH_SCORE_KEY] = max(currentHighScore, score)
        }
    }
}