package local.flappybird.ui.adapters

import local.core.billing.CoinSink
import local.core.ui.data.CoinAccount
import local.core.ui.data.InventoryStore
import local.core.ui.data.ScoreEntry as CoreScoreEntry
import local.core.ui.data.ScoreStore
import local.flappybird.engine.model.ScoreEntry as EngineScoreEntry
import local.flappybird.engine.repo.PowerUpStore
import local.flappybird.engine.repo.ScoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Glue between the generic core persistence layer and the game engine's ports.
 * This is the only place where core types and engine types meet.
 */

fun CoinAccount.asCoinSink(): CoinSink =
    CoinSink { amount -> addCoins(amount) }

fun InventoryStore.asPowerUpStore(): PowerUpStore =
    object : PowerUpStore {
        override suspend fun usePowerUp(typeId: String): Boolean =
            consumeFromInventory(typeId)
    }

fun CoreScoreEntry.toEngine(): EngineScoreEntry =
    EngineScoreEntry(score = score, reward = reward, timestamp = timestamp)

fun EngineScoreEntry.toCore(): CoreScoreEntry =
    CoreScoreEntry(score = score, reward = reward, timestamp = timestamp)

fun ScoreStore.asScoreRepository(): ScoreRepository =
    object : ScoreRepository {
        override val highScoreFlow: Flow<Int> =
            this@asScoreRepository.highScoreFlow
        override val scoreHistoryFlow: Flow<List<EngineScoreEntry>> =
            this@asScoreRepository.scoreHistoryFlow.map { list -> list.map { it.toEngine() } }

        override suspend fun updateHighScore(score: Int) =
            this@asScoreRepository.updateHighScore(score)

        override suspend fun saveScoreEntry(entry: EngineScoreEntry) =
            this@asScoreRepository.saveScoreEntry(entry.toCore())
    }
