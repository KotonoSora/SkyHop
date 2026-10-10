package local.flappybird.ui.adapters

import local.core.billing.CoinSink
import local.core.ui.data.CoinAccount
import local.core.ui.data.InventoryStore
import local.core.ui.data.RunRecord as CoreRunRecord
import local.core.ui.data.RunStore
import local.flappybird.engine.model.RunEndCause
import local.flappybird.engine.model.RunRecord as EngineRunRecord
import local.flappybird.engine.repo.PowerUpStore
import local.flappybird.engine.repo.RunHistoryRepository
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

fun CoreRunRecord.toEngine(): EngineRunRecord =
    EngineRunRecord(
        mode = mode,
        level = level,
        score = score,
        reward = reward,
        durationMs = durationMs,
        pipesPassed = pipesPassed,
        // Core stores the cause as the enum name; fall back instead of crashing
        // the flow if a row ever holds an unknown value.
        endCause = runCatching { RunEndCause.valueOf(endCause) }
            .getOrDefault(RunEndCause.COLLISION),
        shieldUses = shieldUses,
        multiplierUses = multiplierUses,
        autoPlayUses = autoPlayUses,
        deathX = deathX,
        deathY = deathY,
        startedAt = startedAt,
        endedAt = endedAt
    )

fun EngineRunRecord.toCore(): CoreRunRecord =
    CoreRunRecord(
        mode = mode,
        level = level,
        score = score,
        reward = reward,
        durationMs = durationMs,
        pipesPassed = pipesPassed,
        endCause = endCause.name,
        shieldUses = shieldUses,
        multiplierUses = multiplierUses,
        autoPlayUses = autoPlayUses,
        deathX = deathX,
        deathY = deathY,
        startedAt = startedAt,
        endedAt = endedAt
    )

fun RunStore.asRunHistoryRepository(): RunHistoryRepository =
    object : RunHistoryRepository {
        override val highScoreFlow: Flow<Int> =
            this@asRunHistoryRepository.highScoreFlow

        override fun recentRunsFlow(limit: Int): Flow<List<EngineRunRecord>> =
            this@asRunHistoryRepository.recentRunsFlow(limit)
                .map { list -> list.map { it.toEngine() } }

        override suspend fun recordRun(record: EngineRunRecord): Long =
            this@asRunHistoryRepository.recordRun(record.toCore())

        override suspend fun updateRun(id: Long, record: EngineRunRecord) =
            this@asRunHistoryRepository.updateRun(id, record.toCore())
    }
