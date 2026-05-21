package com.jn.flagfang.feature.shop

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.max

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

private val HIGH_SCORE_KEY = intPreferencesKey("high_score")
private val TOP_SCORES_KEY = stringPreferencesKey("top_scores")
private const val MAX_LEADERBOARD_SIZE = 10

class ScoreRepository(private val context: Context) {

    val highScoreFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[HIGH_SCORE_KEY] ?: 0
        }

    /** Emits the top-10 scores sorted descending. */
    val topScoresFlow: Flow<List<Int>> = context.dataStore.data
        .map { preferences ->
            preferences[TOP_SCORES_KEY]
                ?.split(",")
                ?.mapNotNull { it.trim().toIntOrNull() }
                ?: emptyList()
        }

    suspend fun updateHighScore(score: Int) {
        context.dataStore.edit { preferences ->
            val currentHighScore = preferences[HIGH_SCORE_KEY] ?: 0
            preferences[HIGH_SCORE_KEY] = max(currentHighScore, score)
        }
    }

    /** Saves a game score to the top-10 leaderboard. */
    suspend fun saveScore(score: Int) {
        if (score <= 0) return
        context.dataStore.edit { preferences ->
            val existing = preferences[TOP_SCORES_KEY]
                ?.split(",")
                ?.mapNotNull { it.trim().toIntOrNull() }
                ?: emptyList()
            val updated = (existing + score)
                .sortedDescending()
                .take(MAX_LEADERBOARD_SIZE)
            preferences[TOP_SCORES_KEY] = updated.joinToString(",")
        }
    }
}