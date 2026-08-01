package com.kotonosora.flappybird.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kotonosora.flappybird.data.mapper.toRawString
import com.kotonosora.flappybird.data.mapper.toScoreEntry
import com.kotonosora.flappybird.domain.model.ScoreEntry
import com.kotonosora.flappybird.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.max

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ScoreRepositoryImpl(private val context: Context) : ScoreRepository {

    companion object {
        private val HIGH_SCORE_KEY = intPreferencesKey("high_score")
        private val SCORE_HISTORY_KEY = stringPreferencesKey("score_history")
        private const val MAX_HISTORY_SIZE = 20
    }

    override val highScoreFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[HIGH_SCORE_KEY] ?: 0
        }

    override val scoreHistoryFlow: Flow<List<ScoreEntry>> = context.dataStore.data
        .map { preferences ->
            preferences[SCORE_HISTORY_KEY]
                ?.split(";")
                ?.mapNotNull { it.toScoreEntry() }
                ?: emptyList()
        }

    override suspend fun updateHighScore(score: Int) {
        context.dataStore.edit { preferences ->
            val currentHighScore = preferences[HIGH_SCORE_KEY] ?: 0
            preferences[HIGH_SCORE_KEY] = max(currentHighScore, score)
        }
    }

    override suspend fun saveScoreEntry(entry: ScoreEntry) {
        if (entry.score <= 0) return
        context.dataStore.edit { preferences ->
            val existingRaw = preferences[SCORE_HISTORY_KEY] ?: ""
            val existingEntries = existingRaw.split(";")
                .filter { it.isNotBlank() }

            val updatedEntries = (listOf(entry.toRawString()) + existingEntries)
                .take(MAX_HISTORY_SIZE)

            preferences[SCORE_HISTORY_KEY] = updatedEntries.joinToString(";")
        }
    }
}
